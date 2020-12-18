package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

public class While implements Instruction{
    Condition condition;
    Code code;

    public While(ParseTree parseTree) {
        this.condition = new Condition(parseTree.getChildren().get(2));
        this.code = new Code(parseTree.getChildren().get(6));
    }

    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException {
        return codeGenerator.handleWhile(this, basicBlock);
    }

    public Condition getCondition() {
        return condition;
    }

    public Code getCode() {
        return code;
    }
}
