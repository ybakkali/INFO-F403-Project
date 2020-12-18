package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

/**
 * This class represents the read instruction.
 */
public class Read implements Instruction {
    String variable;

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
     * @param basicBlock The current basic block
     * @return The last basic block
     */
    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) {
        return codeGenerator.handleRead(this, basicBlock);
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
