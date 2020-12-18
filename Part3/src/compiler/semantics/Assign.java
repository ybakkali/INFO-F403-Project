package compiler.semantics;

import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

/**
 * This class represents an assign instruction.
 */
public class Assign implements Instruction {

    final private String variable;
    final private ArithmeticExpression arithmeticExpression;

    /**
     * Construct the assign instruction with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Assign(ParseTree parseTree) {
        this.variable = (String) parseTree.getChildren().get(0).getLabel().getValue();
        this.arithmeticExpression = new ArithmeticExpression(parseTree.getChildren().get(2));
    }

    /**
     * Dispatch to the right function of the code generator.
     *
     * @param codeGenerator The code generator
     * @throws SemanticException When a semantic problem is encountered
     */
    @Override
    public void dispatch(CodeGenerator codeGenerator) throws SemanticException {
        codeGenerator.handleAssign(this);
    }

    /**
     * Return the variable.
     *
     * @return The variable
     */
    public String getVariable() {
        return variable;
    }

    /**
     * Return the arithmetic expression.
     *
     * @return The arithmetic expression
     */
    public ArithmeticExpression getArithmeticExpression() {
        return arithmeticExpression;
    }
}
