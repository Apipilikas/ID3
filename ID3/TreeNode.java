//package ID3;

public class TreeNode {

    private int feature;
    // YES Choise
    private TreeNode leftChild;
    // NO Choice
    private TreeNode rightChild;

    public TreeNode(int feature, TreeNode leftChild, TreeNode rightChild) {
        this.feature = feature;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public TreeNode() {
        this.feature = -1;
        this.leftChild = null;
        this.rightChild = null;
    }

    public int getFeature() {
        return this.feature;
    }

    public TreeNode getLeftChild() {
        return this.leftChild;
    }

    public TreeNode getRightChild() {
        return this.rightChild;
    }

    public void setFeature(int newFeature) {
        this.feature = newFeature;
    }

    public void setLeftChild(TreeNode newLeftChild) {
        this.leftChild = newLeftChild;
    }

    public void setRightChild(TreeNode newRightChild) {
        this.rightChild = newRightChild;
    }

    public Boolean isLeafNode() {
        return (this.leftChild == null && this.rightChild == null);
    }

    /*
      We use the attribute feature to determine in which class
      a leaf node belongs. This happens to avoid using an extra
      attribute decision, which probably is not going to be used
      in most of the treeNodes that we will create.
     */
    public void setAsLeafNode(char decision) {
        if (decision == '0') {
            this.feature = -2;
        }
        else {
            this.feature = -3;
        }
    }

    /*
      The opposite of the above function. It is actually decrypt in
      which class a leaf node has concluded.
    */
    public char getLeafNodeClass() {
        if (this.feature == -2) {
            return '0';
        }
        else {
            return '1';
        }
    }
}