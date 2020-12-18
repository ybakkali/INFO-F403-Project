package compiler.semantics;

import compiler.ParseTree;

/**
 * This class represents a condition.
 */
public class Condition {
    ArithmeticExpression leftMember;
    ArithmeticExpression rightMember;
    String operator;

    /**
     * Construct the condition with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Condition(ParseTree parseTree) {
        this.leftMember = new ArithmeticExpression(parseTree.getChildren().get(0));
        this.rightMember = new ArithmeticExpression(parseTree.getChildren().get(2));
        this.operator = (String) parseTree.getChildren().get(1).getLabel().getValue();
    }

    /**
     * Return the left member.
     *
     * @return The left member
     */
    public ArithmeticExpression getLeftMember() {
        return leftMember;
    }

    /**
     * Return the right member.
     *
     * @return The right member
     */
    public ArithmeticExpression getRightMember() {
        return rightMember;
    }

    /**
     * Return the operator.
     *
     * @return The operator
     */
    public String getOperator() {
        return operator;
    }
}
