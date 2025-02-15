package compiler.semantics;

import compiler.LexicalUnit;
import compiler.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * The class represents a code.
 */
public class Code {

    final private List<Instruction> instructions;

    /**
     * Construct the code with the specified parse tree.
     *
     * @param parseTree The parse tree
     */
    public Code(ParseTree parseTree) {
        this.instructions = new ArrayList<>();

        while (parseTree != null) {
            if (parseTree.getChildren().get(0).getLabel().getType() == LexicalUnit.ENDLINE) {
                parseTree = parseTree.getChildren().get(1);
            } else {
                ParseTree child = parseTree.getChildren().get(0).getChildren().get(0);
                switch ((String) child.getLabel().getValue()) {
                    case "Assign": this.instructions.add(new Assign(child)); break;
                    case "If": this.instructions.add(new If(child)); break;
                    case "While": this.instructions.add(new While(child)); break;
                    case "Print": this.instructions.add(new Print(child)); break;
                    case "Read": this.instructions.add(new Read(child)); break;
                }
                parseTree = parseTree.getChildren().get(2);
            }
        }
    }

    /**
     * Return all the instructions.
     *
     * @return The instructions
     */
    public List<Instruction> getInstructions() {
        return instructions;
    }
}
