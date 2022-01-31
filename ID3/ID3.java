//package ID3;
// https://en.wikipedia.org/wiki/ID3_algorithm

public class ID3 {
    
    /*
    Data input:

    -> Attributes in voc file
    [good, bad, very, not]

    -> Attribute number
    4

    -> Data from labeledBow.feat
    [rate | word 2 from voc appeared 3 times]
    [9 | 2:3 | 657:2 ...]

    Maybe there should be a small edit at the data:
    rate > 5 -> 1
    rate < 5 -> 0
    */
    private ReadFile readFile;
    private int numberOfFeatures;
    private int numberOfExamples;
    private String[] features;
    private char[][] examples;
    private int[] usedFeatures;
    private TreeNode root;
    private double pruningParameter;

    public ID3(double pruningParameter) {
        this.readFile = new ReadFile();
        this.pruningParameter = pruningParameter;
    }

    public void prepareData(String featureFileName, String dataFileName) {
        this.numberOfFeatures = readFile.getNumberOfLines(featureFileName);
        this.numberOfExamples = readFile.getNumberOfLines(dataFileName);

        this.features = readFile.extractFeatureData(featureFileName, this.numberOfFeatures);
        this.examples = readFile.extractData(dataFileName, this.numberOfFeatures, this.numberOfExamples);
        
        this.usedFeatures = new int[this.numberOfFeatures];
    }

    public double calculateEntropy(int[] numberOfRows) {
        double prob;
        double entropy = 0.0d;
        double subEntropy;
        
        for (int i = 0; i < 2; i++) {
            prob = numberOfRows[i] / (double) numberOfRows[2];
            subEntropy = -prob * log2(prob);
            if (! Double.isNaN(subEntropy)) {
                entropy += subEntropy;
            }
        }
        System.out.println("Entropy is: " + entropy);
        return entropy;
    }

    public int[] calculateNumberOfRowsWithClass(char[][] examples, int feature, char cls) {
        // results = [class '0', class '1']
        int nExamples = 0;
        int class0 = 0;
        int class1 = 0;

        for (int i = 0; i < examples.length; i++) {
            if (examples[i][feature] == cls || cls == 'T') {
                if (examples[i][0] == '0') {
                    class0++;
                }
                nExamples++;
            }
        }
        class1 = nExamples - class0;
        int[] results = {class0, class1, nExamples};
        System.out.println("Number of rows with class " + cls + 
                           " has: class 0-> " + results[0] + 
                           ", class 1-> " + results[1] + 
                           ", sum-> " + results[2]);
        return results;
    }

    public double calculateInformationGain(char[][] examples, int feature, int[] dataset, double entropy) {
        // datasetFeature = {entropy in dataset for class 0, entropy in dataset for class 1}
        double[] entropyFeature = new double[2];
        
        System.out.println("Feature " + feature + " with class 0");
        int[] datasetFeatureClass0 = calculateNumberOfRowsWithClass(examples, feature, '0');
        entropyFeature[0] = calculateEntropy(datasetFeatureClass0);
        
        System.out.println("Feature " + feature + " with class 1");
        int[] datasetFeatureClass1 = calculateNumberOfRowsWithClass(examples, feature, '1');
        entropyFeature[1] = calculateEntropy(datasetFeatureClass1);

        double prob;
        double informationGain = entropy;

        for (int i = 0; i < 2; i++) {
            prob = dataset[i] / (double) dataset[2];
            informationGain += -prob * entropyFeature[i];
        }
        System.out.println("IG for feature " + feature + " is: " + informationGain);
        return informationGain;
    }

    public static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    public void initializeTable() {
        for (int i = 0; i < this.usedFeatures.length; i++) {
            this.usedFeatures[i] = i;
        }
    }

    public int findMaxInformationGain(double[] informationGains) {
        int bestFeature = 0;
        double max = informationGains[0];

        for (int feature = 1; feature < informationGains.length; feature++) {
            if (max < informationGains[feature]) {
                max = informationGains[feature];
                bestFeature = feature;
            }
        }
        return bestFeature;
    }

    public double[] getInformationGains(char[][] examples, int[] dataset) {
        double[] informationGains = new double[examples[0].length];
        double entropy = calculateEntropy(dataset);
        for (int feature = 1; feature < examples[0].length; feature++) {
            informationGains[feature - 1] = calculateInformationGain(examples, feature, dataset, entropy);
        }
        return informationGains;
    }

    public Boolean isSameClass(int[] dataset, char cls) {
        if (cls == '0') {
            return (dataset[2] - dataset[0]) == 0;
        }
        else {
            return (dataset[2] - dataset[1]) == 0;
        }
    }

    public char[][] getSubExamples(char[][] iExamples, int nExamplesInClass, int feature, char cls) {
        char[][] subExamples = new char[nExamplesInClass][iExamples[0].length - 1];
        int line = 0;

        for (int i = 0; i < iExamples.length; i++) {
            int column = 0;
            if (iExamples[i][feature] == cls) {
                for (int j = 0; j < iExamples[i].length; j++) {
                    if (j != feature) {
                        subExamples[line][column] = iExamples[i][j];
                        column++;
                    }
                }
                line++;
            }
        }
        return subExamples;
    }

    public int[] getSubFeatures(int[] currentFeatures, int usedFeature) {
        int[] subFeatures = new int[currentFeatures.length - 1];
        int line = 0;

        for (int i = 0; i < currentFeatures.length; i++) {
            if (i != usedFeature) {
                subFeatures[line] = currentFeatures[i];
                line++;
            }
        }
        return subFeatures;
    }

    public void printExamples(char[][] examples) {
        for (int i = 0; i < examples.length; i++) {
            for (int j = 0; j < examples[i].length; j++) {
                System.out.print(examples[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public void printFeatures(int[] features) {
        for (int i = 0; i < features.length; i++) {
            System.out.println("Feature index: " + i + ", Content: " + features[i]);
            System.out.println("----------> " + this.features[features[i]] + " <----------");
        }
    }

    public char mostCommonClass(int[] dataset) {
        if (dataset[0] > dataset[1]) {
            return '0';
        }
        return '1';
    }

    public Boolean pruneTree(int[] dataset) {
        double rate = dataset[0] / (double) dataset[2];

        System.out.println("Rate is " + rate);
        if (rate >= this.pruningParameter || rate <= (1 - this.pruningParameter)) {
            return true;
        }
        return false;
    }

    public void traverse(TreeNode treeNode) {
        if (treeNode.isLeafNode()) {
            System.out.println("Decision: " + treeNode.getLeafNodeClass());
        }
        else {
            System.out.println(this.features[treeNode.getFeature()]);
            
            // left child traverse
            System.out.println("Left child");
            traverse(treeNode.getLeftChild());

            // right child traverse
            System.out.println("Right child");
            traverse(treeNode.getRightChild());
        }
    }

    public void trainID3() {
        // choose max infogain
        this.root = buildID3Tree(this.examples, this.usedFeatures, 'I');
    }

    public TreeNode buildID3Tree(char[][] subExamples, int[] subFeatures, char cls) {
        TreeNode currentTreeNode = new TreeNode();
        printExamples(subExamples);
        int[] dataset = calculateNumberOfRowsWithClass(subExamples, 0, 'T');
        if (subExamples.length == 0) {
            // leaf node with final cls, depends on the child
            currentTreeNode.setAsLeafNode(cls);
            System.out.println("No examples: Setting leaf node with decision: " + cls);
        }
        else if (isSameClass(dataset, '0')) {
            // leaf node with final result 0
            currentTreeNode.setAsLeafNode('0');
            System.out.println("Same class: Setting leaf node with decision: 0");
        }
        else if (isSameClass(dataset, '1')) {
            // leaf node with final result 1
            currentTreeNode.setAsLeafNode('1');
            System.out.println("Same class: Setting leaf node with decision: 1");
        }
        else if (pruneTree(dataset)) {
            // prune tree
            char decision = mostCommonClass(dataset);
            currentTreeNode.setAsLeafNode(decision);
            System.out.println("Pruning tree: Setting leaf node with decision: " + decision);
        }
        else if (subFeatures.length == 0) {
            // leaf result with most common class
            char decision = mostCommonClass(dataset);
            currentTreeNode.setAsLeafNode(decision);
            System.out.println("No features: Setting leaf node with decision: " + decision);
        }
        else {
            int highestInfoGainFeature = 0;
            if (subFeatures.length != 1) {
                // find max = f1
                double[] informationGains = getInformationGains(subExamples, dataset);
                // without target value
                highestInfoGainFeature = findMaxInformationGain(informationGains);
            }

            currentTreeNode.setFeature(subFeatures[highestInfoGainFeature]);

            System.out.println("Best feature is: " + this.features[subFeatures[highestInfoGainFeature]]);
            
            // TODO: remember to mark this feature as USED
            int[] newSubFeatures = getSubFeatures(subFeatures, highestInfoGainFeature);

            int nExamplesClass0 = calculateNumberOfRowsWithClass(subExamples, highestInfoGainFeature + 1, '0')[2];
            char[][] subExamplesClass0 = getSubExamples(subExamples, nExamplesClass0, highestInfoGainFeature + 1, '0');
            
            int nExamplesClass1 = calculateNumberOfRowsWithClass(subExamples, highestInfoGainFeature + 1, '1')[2];
            char[][] subExamplesClass1 = getSubExamples(subExamples, nExamplesClass1, highestInfoGainFeature + 1, '1');
            
            System.out.println("----------------- New left TreeNode -----------------");
            System.out.println("----------> Feature " + this.features[subFeatures[highestInfoGainFeature]] + " and class 1 <----------");
            //  left child and create a tree for the subset with f1 = yes, return
            currentTreeNode.setLeftChild(buildID3Tree(subExamplesClass1, newSubFeatures, '1'));
            
            System.out.println("----------------- New right TreeNode -----------------");
            System.out.println("----------> Feature " + this.features[subFeatures[highestInfoGainFeature]] + " and class 0 <----------");
            // right child and create a tree for the subset with f1 = no, return
            currentTreeNode.setRightChild(buildID3Tree(subExamplesClass0, newSubFeatures, '0'));
        }
        return currentTreeNode;
    }

    public TreeNode getTrainedID3() {
        return this.root;
    }

    public char[] getPredictions(char[][] examples) {
        TreeNode currentTreeNode;
        int feature;
        char cls;
        char decision;
        char[] predictions = new char[examples.length];

        for (int example = 0; example < examples.length; example++) {
            currentTreeNode = getTrainedID3();
            while (! currentTreeNode.isLeafNode()) {
                feature = currentTreeNode.getFeature();
                cls = examples[example][feature + 1];

                if (cls == '1') {
                    currentTreeNode = currentTreeNode.getLeftChild();
                }
                else {
                    currentTreeNode = currentTreeNode.getRightChild();
                }
            }
            decision = currentTreeNode.getLeafNodeClass();
            predictions[example] = decision;
        }
        return predictions;
    }

    public double calculateAccuracy(char[][] examples, char[] predictions) {
        int successfulPredictions = 0;
        double accuracy;

        for (int example = 0; example < examples.length; example++) {
            if (examples[example][0] == predictions[example]) {
                successfulPredictions++;
            }
        }
        //System.out.println("Successful predictions are: " + successfulPredictions);
        accuracy = successfulPredictions / (double) examples.length;
        return accuracy;
    }

    public double calculatePrecision(char[][] examples, char[] predictions, char cls) {
        int truePositives = 0;
        int falsePositives = 0;
        double precision;

        for (int example = 0; example < examples.length; example++) {
            if (predictions[example] == cls) {
                if (examples[example][0] == predictions[example]) {
                    truePositives++;
                }
                else {
                    falsePositives++;
                }
            }
        }
        //System.out.println("True positives are: " + truePositives + " and false positives are: " + falsePositives);
        precision = truePositives / (double) (truePositives + falsePositives);
        return precision;
    }

    public double calculateRecall(char[][] examples, char[] predictions, char cls) {
        int truePositives = 0;
        int falseNegatives = 0;
        double recall;

        for (int example = 0; example < examples.length; example++) {
            if (predictions[example] == cls) {
                if (examples[example][0] == predictions[example]) {
                    truePositives++;
                }
            }
            else {
                if (examples[example][0] == cls) {
                    falseNegatives++;
                }
            }
        }
        recall = truePositives / (double) (truePositives + falseNegatives);
        return recall;
    }

    public double calculateF1(double precision, double recall) {
        double f1 = 2 * (precision * recall) / (precision + recall);
        return f1;
    }

    public static void main(String[] args) {
        double pruningParameter = Double.parseDouble(args[0]);

        // if (pruningParameter < 0.85) {
        //     pruningParameter = 0.90;
        // }

        ID3 id3 = new ID3(pruningParameter);
        String filename1 = "test.txt";
        String filename2 = "t_feat.txt";
        id3.prepareData(filename2, filename1);
        //id3.printExamples(id3.examples);
        id3.initializeTable();
        //id3.printFeatures(id3.usedFeatures);
        id3.trainID3();
        id3.traverse(id3.root);
        char[] predictions1 = id3.getPredictions(id3.examples);
        double accuracy = id3.calculateAccuracy(id3.examples, predictions1);
        System.out.println("Accuracy score is: " + accuracy);
        double precision = id3.calculatePrecision(id3.examples, predictions1, '1');
        System.out.println("Precision score is: " + precision);
        double recall = id3.calculateRecall(id3.examples, predictions1, '1');
        System.out.println("Recall score is: " + recall);
        System.out.println("F1 score is: " + id3.calculateF1(precision, recall));
    }
}