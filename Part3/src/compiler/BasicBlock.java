package compiler;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    String label;
    List<String> lines;

    public BasicBlock(String label) {
        this.label = label;
        this.lines = new ArrayList<>();
    }

    public void add(String line) {
        this.lines.add(line);
    }

    public String toString() {
        StringBuilder code = new StringBuilder();
        code.append("\t").append(this.label).append(":\n");

        for (String line : lines) {
            code.append("\t\t").append(line).append("\n");
        }

        return code.toString();
    }

}
