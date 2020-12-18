package compiler.semantics;

import compiler.BasicBlock;
import compiler.CodeGenerator;
import compiler.ParseTree;

public class Read implements Instruction {
    String variable;

    public Read(ParseTree parseTree) {
        this.variable = (String) parseTree.getChildren().get(2).getLabel().getValue();
    }

    @Override
    public BasicBlock dispatch(CodeGenerator codeGenerator, BasicBlock basicBlock) {
        return codeGenerator.handleRead(this, basicBlock);
    }

    public String getVariable() {
        return variable;
    }
}
