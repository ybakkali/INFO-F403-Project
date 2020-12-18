package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.LexicalUnit;
import compiler.ParseTree;
import compiler.exceptions.SemanticException;

public class If implements Instruction {
    Condition condition;
    Code codeTrue;
    Code codeFalse;

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

    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) throws SemanticException {
        return codeGenerator.handleIf(this, basicBlock);
    }

    public Code getCodeTrue() {
        return codeTrue;
    }

    public Code getCodeFalse() {
        return codeFalse;
    }

    public Condition getCondition() {
        return condition;
    }
}
