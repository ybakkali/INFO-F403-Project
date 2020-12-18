package compiler.semantics;

import compiler.ParseTree;

public class Condition {
    ArithmeticExpression leftMember;
    ArithmeticExpression rightMember;
    String operator;

    public Condition(ParseTree parseTree) {
        this.leftMember = new ArithmeticExpression(parseTree.getChildren().get(0));
        this.rightMember = new ArithmeticExpression(parseTree.getChildren().get(2));
        this.operator = (String) parseTree.getChildren().get(1).getLabel().getValue();
    }

    public ArithmeticExpression getLeftMember() {
        return leftMember;
    }

    public ArithmeticExpression getRightMember() {
        return rightMember;
    }

    public String getOperator() {
        return operator;
    }
}
