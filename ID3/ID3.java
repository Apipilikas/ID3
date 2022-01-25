import java.util.ArrayList;

//package ID3;

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
    private int numberOfFeatures;
    private int numberOfExamples;
    private String[] attributes;
    private char[][] examples;
    private char[] usedFeatures;

    public ID3(String featureFileName, String dataFileName) {
        ReadFile readFile = new ReadFile();
        

        this.numberOfFeatures = readFile.getNumberOfLines(featureFileName);
        this.numberOfExamples = readFile.getNumberOfLines(dataFileName);

        this.attributes = readFile.extractFeatureData(featureFileName, this.numberOfFeatures);
        this.examples = readFile.extractData(dataFileName, this.numberOfFeatures, this.numberOfExamples);
    }

    public double calculateEntropy(int[] numberOfRows) {
        double prob;
        double entropy = 0.0f;
        
        for (int i = 0; i < 2; i++) {
            prob = numberOfRows[i] / numberOfRows[2];
            entropy += -prob * log2(prob);
        }
        
        return entropy;
    }

    public int[] calculateNumberOfRowsWithClass(char[][] examples, int feature, char cls) {
        // results = [class '0', class '1']
        int nExamples = 0;
        int class0 = 0;
        int class1 = 0;

        for (int i = 0; i < this.numberOfExamples; i++) {
            if (examples[i][feature] == cls || cls == 'A') {
                if (examples[i][0] == '0') {
                    class0++;
                }
                nExamples++;
            }
        }
        class1 = nExamples - class0;
        int[] results = {class0, class1, nExamples};
        return results;
    }

    public double calculateInformationGain(char[][] examples, int feature, int[] dataset, double entropy) {
        // datasetFeature = {entropy in dataset for class 0, entropy in dataset for class 1}
        double[] entropyFeature = new double[2];
        
        int[] datasetFeatureClass0 = calculateNumberOfRowsWithClass(examples, feature + 1, '0');
        entropyFeature[0] = calculateEntropy(datasetFeatureClass0);
        
        int[] datasetFeatureClass1 = calculateNumberOfRowsWithClass(examples, feature + 1, '1');
        entropyFeature[1] = calculateEntropy(datasetFeatureClass1);

        double prob;
        double informationGain = entropy;

        for (int i = 0; i < 2; i++) {
            prob = dataset[i] / dataset[2];
            informationGain += -prob * entropyFeature[i];
        }
        
        return informationGain;
    }

    public static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    public void start() {
        // choose max infogain
        Boolean flag = true;

        while (flag) {
            // calculate all the infogains

        }
        // root treenode = bestFeature
    }

    public void initializeTable() {
        for (int i = 0; i < this.usedFeatures.length; i++) {
            this.usedFeatures[i] = '0';
        }
    }

    public Boolean isFeatureUsed(int feature) {
        return (this.usedFeatures[feature] == '1');
    }

    public int findMaxInformationGain(double[] informationGains) {
        int bestFeature = 0;
        double max = informationGains[0];

        for (int feature = 1;feature < this.numberOfFeatures; feature++) {
            if (max < informationGains[feature]) {
                max = informationGains[feature];
                bestFeature = feature;
            }
        }
        return bestFeature;
    }

    public double[] getInformationGains(char[][] examples, char cls) {
        double[] informationGains = new double[this.numberOfFeatures];
        int[] dataset = calculateNumberOfRowsWithClass(examples, 0, 'A');
        if (isSameClass(dataset, '0')) {
            // leaf node with final result 0
        }
        else if (isSameClass(dataset, '1')) {
            // leaf node with final result 1
        }
        else {
            double entropy = calculateEntropy(dataset);
            for (int feature = 0; feature < this.numberOfFeatures; feature++) {
                if (! isFeatureUsed(feature)) {
                    informationGains[feature] = calculateInformationGain(examples, feature, dataset, entropy);
                }
            }
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
}
