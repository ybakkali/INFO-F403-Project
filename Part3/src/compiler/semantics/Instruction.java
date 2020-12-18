package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.exceptions.SemanticException;

/**
 * This interface represents an instruction.
 */
public interface Instruction {
    /**
     * Dispatch to the right function of the code generator.
     *
     * @param codeGenerator The code generator
     * @param basicBlock The current basic block
     * @return The last basic block
     * @throws SemanticException When a semantic problem is encountered
     */
    BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException;
}
