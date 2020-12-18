package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.LexicalUnit;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

/**
 * This class represents an if instruction.
 */
public class If implements Instruction {
    Condition condition;
    Code codeTrue;
    Code codeFalse;

    /**
     * Construct the if instruction with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public If(ParseTree parseTree) {
        this.condition = new Condition(parseTree.getChildren().get(2));
        this.codeTrue = new Code(parseTree.getChildren().get(6));

        parseTree = parseTree.getChildren().get(7);
        if (parseTree.getChildren().get(0).getLabel().getType() == LexicalUnit.ELSE) {
            this.codeFalse = new Code(parseTree.getChildren().get(2));
        } else {
            this.codeFalse = null;
        }
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
        return codeGenerator.handleIf(this, basicBlock);
    }

    /**
     * Return the code if the condition is true.
     *
     * @return The code if the condition is true
     */
    public Code getCodeTrue() {
        return codeTrue;
    }

    /**
     * Return the code if the condition is false.
     *
     * @return The code if the condition is false
     */
    public Code getCodeFalse() {
        return codeFalse;
    }

    /**
     * Return the condition.
     *
     * @return The condition
     */
    public Condition getCondition() {
        return condition;
    }
}
