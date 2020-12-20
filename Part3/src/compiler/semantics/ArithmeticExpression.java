package compiler.semantics;

import compiler.BinaryTree;
import compiler.LexicalUnit;
import compiler.ParseTree;
import compiler.Symbol;


/**
 * This class represents an arithmetic expression.
 */
public class ArithmeticExpression {

    final private BinaryTree<Symbol> root;

    /**
     * Construct the arithmetic expression with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public ArithmeticExpression(ParseTree parseTree) {
        this.root = new BinaryTree<>();
        handleAddition(parseTree, this.root);
    }

    /**
     * Construct the specified binary tree from the specified parse tree of label expr.
     *
     * @param parseTree The parse tree
     * @param binaryTree The binary tree
     */
    private void handleAddition(ParseTree parseTree, BinaryTree<Symbol> binaryTree) {
        BinaryTree<Symbol> lastBinaryTree = new BinaryTree<>();
        BinaryTree<Symbol> currentBinaryTree;

        handleMultiplication(parseTree.getChildren().get(0), lastBinaryTree);
        parseTree = parseTree.getChildren().get(1);

        while (parseTree != null) {
            currentBinaryTree = new BinaryTree<>();
            currentBinaryTree.setData(parseTree.getChildren().get(0).getLabel());
            currentBinaryTree.setLeftChild(lastBinaryTree);
            currentBinaryTree.setRightChild(new BinaryTree<>());

            handleMultiplication(parseTree.getChildren().get(1), currentBinaryTree.getRightChild());

            lastBinaryTree = currentBinaryTree;
            parseTree = parseTree.getChildren().get(2);
        }

        binaryTree.setData(lastBinaryTree.getData());
        binaryTree.setLeftChild(lastBinaryTree.getLeftChild());
        binaryTree.setRightChild(lastBinaryTree.getRightChild());
    }

    /**
     * Construct the specified binary tree from the specified parse tree of label prod.
     *
     * @param parseTree The parse tree
     * @param binaryTree The binary tree
     */
    private void handleMultiplication(ParseTree parseTree, BinaryTree<Symbol> binaryTree) {
        BinaryTree<Symbol> lastBinaryTree = new BinaryTree<>();
        BinaryTree<Symbol> currentBinaryTree;

        handleAtom(parseTree.getChildren().get(0), lastBinaryTree);
        parseTree = parseTree.getChildren().get(1);

        while (parseTree != null) {
            currentBinaryTree = new BinaryTree<>();
            currentBinaryTree.setData(parseTree.getChildren().get(0).getLabel());
            currentBinaryTree.setLeftChild(lastBinaryTree);
            currentBinaryTree.setRightChild(new BinaryTree<>());

            handleAtom(parseTree.getChildren().get(1), currentBinaryTree.getRightChild());

            lastBinaryTree = currentBinaryTree;
            parseTree = parseTree.getChildren().get(2);
        }

        binaryTree.setData(lastBinaryTree.getData());
        binaryTree.setLeftChild(lastBinaryTree.getLeftChild());
        binaryTree.setRightChild(lastBinaryTree.getRightChild());
    }

    /**
     * Construct the specified binary tree from the specified parse tree of label atom.
     *
     * @param parseTree The parse tree
     * @param binaryTree The binary tree
     */
    private void handleAtom(ParseTree parseTree, BinaryTree<Symbol> binaryTree) {
        switch (parseTree.getChildren().get(0).getLabel().getType()) {
            case MINUS:
                Symbol symbol = new Symbol(LexicalUnit.NUMBER, "-1");
                Symbol operator = new Symbol(LexicalUnit.TIMES);
                binaryTree.setLeftChild(new BinaryTree<>());
                binaryTree.setRightChild(new BinaryTree<>());
                binaryTree.setData(operator);
                handleAtom(parseTree.getChildren().get(1), binaryTree.getLeftChild());
                binaryTree.getRightChild().setData(symbol);
                break;
            case NUMBER:
            case VARNAME:
                binaryTree.setData(parseTree.getChildren().get(0).getLabel());
                break;
            case LPAREN:
                handleAddition(parseTree.getChildren().get(1), binaryTree);
                break;
        }
    }

    /**
     * Get the root.
     *
     * @return The root
     */
    public BinaryTree<Symbol> getRoot() {
        return root;
    }
}
