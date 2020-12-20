package compiler;

/**
 * This class represents a binary tree that contains data of the specified type.
 *
 * @param <T> The type of the data it contains
 */
public class BinaryTree<T> {
    T data;
    BinaryTree<T> leftChild;
    BinaryTree<T> rightChild;

    /**
     * Construct the binary tree.
     */
    public BinaryTree() {
        this.data = null;
        this.leftChild = null;
        this.rightChild = null;
    }

    /**
     * Verify that the node is a leaf.
     *
     * @return True if the node is a leaf else False
     */
    public boolean isLeaf() {
        return this.leftChild == null && this.rightChild == null;
    }

    /**
     * Get the data.
     *
     * @return The data
     */
    public T getData() {
        return data;
    }

    /**
     * Get the left child.
     *
     * @return The left child
     */
    public BinaryTree<T> getLeftChild() {
        return leftChild;
    }

    /**
     * Get the right child.
     *
     * @return The right child
     */
    public BinaryTree<T> getRightChild() {
        return rightChild;
    }

    /**
     * Set the data.
     *
     * @param data The data
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Set the left child.
     *
     * @param leftChild The left child
     */
    public void setLeftChild(BinaryTree<T> leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * Set the right child.
     *
     * @param rightChild The right child
     */
    public void setRightChild(BinaryTree<T> rightChild) {
        this.rightChild = rightChild;
    }
}
