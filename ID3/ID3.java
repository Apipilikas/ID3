package ID3;

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
    private int numberOfAttributes;
    private String[] attributes;
    private int[][] data;

    public int calculateEntropy() {
        float entropy = 0.0f;
        
        return 1;
    }
}
