package compiler.semantics;

import compiler.CodeGenerator;
import compiler.ParseTree;

/**
 * This class represents the read instruction.
 */
public class Read implements Instruction {

    final private String variable;

    /**
     * Construct the read instruction with the specified parse tree.
     *
     * @param parseTree The parse tree.
     */
    public Read(ParseTree parseTree) {
        this.variable = (String) parseTree.getChildren().get(2).getLabel().getValue();
    }

    /**
     * Dispatch to the right function of the code generator.
     *
     * @param codeGenerator The code generator
     */
    @Override
    public void dispatch(CodeGenerator codeGenerator) {
        codeGenerator.handleRead(this);
    }

    /**
     * Return the variable.
     *
     * @return The variable
     */
    public String getVariable() {
        return variable;
    }
}
