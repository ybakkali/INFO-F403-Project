package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.Symbol;
import compiler.exceptions.SemanticException;

/**
 * This class represent a print instruction.
 */
public class Print implements Instruction {
    Symbol variable;

    /**
     * Construct the print instruction with specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Print(ParseTree parseTree) {
        this.variable = parseTree.getChildren().get(2).getLabel();
    }

    /**
     * Dispatch to the right function of the code generator.
     *
     * @param codeGenerator The code generator
     * @param basicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException {
        return codeGenerator.handlePrint(this, basicBlock);
    }

    /**
     * Return the variable.
     *
     * @return The variable
     */
    public Symbol getVariable() {
        return variable;
    }
}
