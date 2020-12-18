package compiler.semantics;

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
     * @throws SemanticException When a semantic problem is encountered
     */
    void dispatch(CodeGenerator codeGenerator) throws SemanticException;
}
