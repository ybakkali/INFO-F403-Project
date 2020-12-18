package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

public class Assign implements Instruction {
    String variableName;
    ArithmeticExpression arithmeticExpression;

    public Assign(ParseTree parseTree) {
        this.variableName = (String) parseTree.getChildren().get(0).getLabel().getValue();
        this.arithmeticExpression = new ArithmeticExpression(parseTree.getChildren().get(2));
    }

    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException {
        return codeGenerator.handleAssign(this, basicBlock);
    }

    public String getVariableName() {
        return variableName;
    }

    public ArithmeticExpression getArithmeticExpression() {
        return arithmeticExpression;
    }
}
