package compiler;

/**
 * This class is an option with 0 or 1 argument.
 */
public class Option {
    private final String label;
    private String argument;

    /**
     * Create a new option.
     *
     * @param label The label of the option
     * @param argument The argument of the option
     */
    public Option(String label, String argument) {
        this.label = label;
        this.argument = argument;
    }

    /**
     * Create a new option without argument.
     *
     * @param label The label of the option
     */
    public Option(String label) {
        this.label = label;
    }

    /**
     * Get the label of the option.
     *
     * @return The label of the option
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the argument of the option.
     *
     * @return The argument of the option
     */
    public String getArgument() {
        return argument;
    }
}
