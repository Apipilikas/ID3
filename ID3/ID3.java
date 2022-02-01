//EVANGELOS PIPILIKAS | 3180157

public class ID3 {
    
    private ReadFile readFile;
    private int numberOfFeatures;
    private String[] features;
    private int[] usedFeatures;
    private TreeNode root;
    private double pruningParameter;
    private int n;

    public ID3(double pruningParameter, int nParameter) {
        this.readFile = new ReadFile();
        this.pruningParameter = pruningParameter;
        this.n = nParameter;
    }

    public void prepareFeaturesData(String featureFileName) {
        this.numberOfFeatures = readFile.getNumberOfLines(featureFileName);
        //this.numberOfExamples = readFile.getNumberOfLines(dataFileName);

        this.features = readFile.extractFeatureData(featureFileName, this.numberOfFeatures, this.n);
        //this.examples = readFile.extractData(dataFileName, this.numberOfFeatures, this.numberOfExamples, n);
        //this.n = n;
        
        this.usedFeatures = new int[this.numberOfFeatures - this.n];
        this.initializeTable();
    }

    public char[][] getExamplesData(String dataFileName) {
        int numberOfExamples = readFile.getNumberOfLines(dataFileName);
        char[][] examples = readFile.extractData(dataFileName, this.numberOfFeatures, numberOfExamples, this.n);
        return examples;
    }

    public int getNumberOfFeatures() {
        return this.numberOfFeatures;
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
        //System.out.println("Entropy is: " + entropy);
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
        // System.out.println("Number of rows with class " + cls + 
        //                    " has: class 0-> " + results[0] + 
        //                    ", class 1-> " + results[1] + 
        //                    ", sum-> " + results[2]);
        return results;
    }

    public double calculateInformationGain(char[][] examples, int feature, int[] dataset, double entropy) {
        // datasetFeature = {entropy in dataset for class 0, entropy in dataset for class 1}
        double[] entropyFeature = new double[2];
        
        //System.out.println("Feature " + feature + " with class 0");
        int[] datasetFeatureClass0 = calculateNumberOfRowsWithClass(examples, feature, '0');
        entropyFeature[0] = calculateEntropy(datasetFeatureClass0);
        
        //System.out.println("Feature " + feature + " with class 1");
        int[] datasetFeatureClass1 = calculateNumberOfRowsWithClass(examples, feature, '1');
        entropyFeature[1] = calculateEntropy(datasetFeatureClass1);

        double prob;
        double informationGain = entropy;

        for (int i = 0; i < 2; i++) {
            prob = dataset[i] / (double) dataset[2];
            informationGain += -prob * entropyFeature[i];
        }
        //System.out.println("IG for feature " + feature + " is: " + informationGain);
        return Math.abs(informationGain);
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
        double[] informationGains = new double[examples[0].length - 1];
        double entropy = calculateEntropy(dataset);
        for (int feature = 1; feature < examples[0].length; feature++) {
            informationGains[feature - 1] = calculateInformationGain(examples, feature, dataset, entropy);
        }
        return informationGains;
    }

    public Boolean isSameClass(int[] dataset) {
        double rate = dataset[0] / (double) dataset[2];

        if (rate == 1.0 || rate == 0.0) {
            return true;
        }
        return false;
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

        //System.out.println("Rate is " + rate);
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

    public char[][] getPercentageOfExamples(char[][] examples, double percentage) {
        int upperLimit = (int) (examples.length * percentage);
        System.out.println(upperLimit);
        char[][] data = new char[upperLimit][this.usedFeatures.length];

        for (int example = 0; example < upperLimit; example++) {
            data[example] = examples[example];
        }
        return data;
    }

    public void trainID3(char[][] examples) {
        // root tree node
        this.root = buildID3Tree(examples, this.usedFeatures, 'I');
    }

    public TreeNode buildID3Tree(char[][] subExamples, int[] subFeatures, char cls) {
        TreeNode currentTreeNode = new TreeNode();
        //printExamples(subExamples);
        //printFeatures(subFeatures);
        int[] dataset = calculateNumberOfRowsWithClass(subExamples, 0, 'T');
        if (subExamples.length == 0) {
            // No more examples. Set leaf node with decision = branch class
            currentTreeNode.setAsLeafNode(cls);
            //System.out.println("No examples: Setting leaf node with decision: " + cls);
        }
        else if (isSameClass(dataset) || subFeatures.length == 0 || pruneTree(dataset)) {
            // leaf node with final result
            char decision = mostCommonClass(dataset);
            currentTreeNode.setAsLeafNode(decision);
            // if (isSameClass(dataset)) {
            //     System.out.print("Same class: ");
            // }
            // else if (subFeatures.length == 0) {
            //     System.out.print("No features: ");
            // }
            // else {
            //     System.out.print("Pruning tree: ");
            // }
            // System.out.println("Setting leaf node with decision: " + decision);
        }
        else {
            int highestInfoGainFeature = 0;
            if (subFeatures.length != 1) {
                // Get for every feature the Information Gain (IG), so that we find the highest one.
                double[] informationGains = getInformationGains(subExamples, dataset);
                // Highest Information Gain doesn't contain target value
                highestInfoGainFeature = findMaxInformationGain(informationGains);
            }

            currentTreeNode.setFeature(subFeatures[highestInfoGainFeature]);

            //System.out.println("Best feature is: " + this.features[subFeatures[highestInfoGainFeature]]);
            
            // Get a subset of features without the highest information gain feature
            int[] newSubFeatures = getSubFeatures(subFeatures, highestInfoGainFeature);

            int nExamplesClass0 = calculateNumberOfRowsWithClass(subExamples, highestInfoGainFeature + 1, '0')[2];
            char[][] subExamplesClass0 = getSubExamples(subExamples, nExamplesClass0, highestInfoGainFeature + 1, '0');
            
            int nExamplesClass1 = calculateNumberOfRowsWithClass(subExamples, highestInfoGainFeature + 1, '1')[2];
            char[][] subExamplesClass1 = getSubExamples(subExamples, nExamplesClass1, highestInfoGainFeature + 1, '1');
            
            //System.out.println("----------------- New left TreeNode -----------------");
            //System.out.println("----------> Feature " + this.features[subFeatures[highestInfoGainFeature]] + " and class 1 <----------");
            //  Left child for the class 1
            currentTreeNode.setLeftChild(buildID3Tree(subExamplesClass1, newSubFeatures, '1'));
            
            //System.out.println("----------------- New right TreeNode -----------------");
            //System.out.println("----------> Feature " + this.features[subFeatures[highestInfoGainFeature]] + " and class 0 <----------");
            // Right child for the class 0
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
        // Arguments should have the folowing format: java ID3 featurefilename.txt traindatafile.txt testdatafilename.txt pruningParameter(double) nParameter(int)
        // Example of arguments: java ID3 imdb.vocal trainlabeledBow.feat testlabeledBow.feat 0.90 1000
        
        // featureFileName: file containing the vocaulary(features)
        String featureFileName = args[0];
        // trainDataFileName: file containg the training example set
        String trainDataFileName = args[1];
        // testDataFileName: file contains the test example set
        String testDataFileName = args[2];
        // pruningParameter: the parameter we use to prune the tree
        double pruningParameter = Double.parseDouble(args[3]);
        // nParameter: the first n most common words
        int nParameter = Integer.parseInt(args[4]);

        // if (pruningParameter < 0.80) {
        //     pruningParameter = 0.90;
        // }

        ID3 id3 = new ID3(pruningParameter, nParameter);
        // featureFileName = "t_feat.txt";
        // dataFileName = "test.txt";
        featureFileName = "imdb.vocab";
        trainDataFileName = "labeledBow.feat";
        testDataFileName = "testlabeledBow.feat";
        id3.prepareFeaturesData(featureFileName);
        //id3.printExamples(id3.examples);
        //id3.printFeatures(id3.usedFeatures);
        char[][] trainExamples = id3.getExamplesData(trainDataFileName);
        char[][] trainPerEx = id3.getPercentageOfExamples(trainExamples, 1.0);
        id3.trainID3(trainPerEx);
        //id3.traverse(id3.getTrainedID3());
        char[] predictions1 = id3.getPredictions(trainPerEx);
        double accuracy = id3.calculateAccuracy(trainPerEx, predictions1);
        System.out.println("Accuracy score is: " + accuracy);
        double precision = id3.calculatePrecision(trainPerEx, predictions1, '1');
        System.out.println("Precision score is: " + precision);
        double recall = id3.calculateRecall(trainPerEx, predictions1, '1');
        System.out.println("Recall score is: " + recall);
        System.out.println("F1 score is: " + id3.calculateF1(precision, recall));

        
        char[][] testExamples = id3.getExamplesData(testDataFileName);
        char[] testpredictions = id3.getPredictions(testExamples);
        double testAccuracy = id3.calculateAccuracy(testExamples, testpredictions);
        System.out.println("Accuracy score is: " + testAccuracy);
        double testPrecision = id3.calculatePrecision(testExamples, testpredictions, '1');
        System.out.println("Precision score is: " + testPrecision);
        double testRecall = id3.calculateRecall(testExamples, testpredictions, '1');
        System.out.println("Recall score is: " + testRecall);
        System.out.println("F1 score is: " + id3.calculateF1(testPrecision, testRecall));
    }
}