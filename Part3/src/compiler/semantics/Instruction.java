package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.exceptions.SemanticException;

public interface Instruction {
    BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException;
}
