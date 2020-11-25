package compiler;

public class Option {
    private final String label;
    private String argument;

    public Option(String label) {
        this.label = label;
    }

    public Option(String label, String argument) {
        this.label = label;
        this.argument = argument;
    }

    public String getLabel() {
        return label;
    }

    public String getArgument() {
        return argument;
    }
}
