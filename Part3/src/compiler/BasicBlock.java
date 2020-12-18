package compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a basic block of code.
 */
public class BasicBlock {
    final private String label;
    final private List<String> lines;

    /**
     * Construct the basic block with the specified label.
     *
     * @param label The label of the basic block
     */
    public BasicBlock(String label) {
        this.label = label;
        this.lines = new ArrayList<>();
    }

    /**
     * Add a line to the basic block.
     *
     * @param line The line to add
     */
    public void add(String line) {
        this.lines.add(line);
    }

    /**
     * Return the code contained in the basic block.
     *
     * @return The code contained in the basic block
     */
    public String toString() {
        StringBuilder code = new StringBuilder();
        code.append("\t").append(this.label).append(":\n");

        for (String line : lines) {
            code.append("\t\t").append(line).append("\n");
        }

        return code.toString();
    }
}
