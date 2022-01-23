package ID3;
public class TreeNode {

    private int value;
    // YES Choise
    private TreeNode leftChild;
    // NO Choice
    private TreeNode rightChild;

    public TreeNode(int value, TreeNode leftChild, TreeNode rightChild) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public int getValue() {
        return this.value;
    }

    public TreeNode getLeftChild() {
        return this.leftChild;
    }

    public TreeNode getRightChild() {
        return this.rightChild;
    }

    public void setNewValue(int newValue) {
        this.value = newValue;
    }

    public void setLeftChild(TreeNode newLeftChild) {
        this.leftChild = newLeftChild;
    }

    public void setRightChild(TreeNode newRightChild) {
        this.rightChild = newRightChild;
    }
}