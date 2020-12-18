package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

/**
 * This class represents a while instruction.
 */
public class While implements Instruction{
    Condition condition;
    Code code;

    /**
     * Construct the while instruction with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public While(ParseTree parseTree) {
        this.condition = new Condition(parseTree.getChildren().get(2));
        this.code = new Code(parseTree.getChildren().get(6));
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
        return codeGenerator.handleWhile(this, basicBlock);
    }

    /**
     * Return the condition.
     *
     * @return The condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Return the code.
     *
     * @return The code
     */
    public Code getCode() {
        return code;
    }
}
