package compiler.exceptions;

/**
 * The exception for command line problems
 */
public class CommandLineException extends Exception{
    /**
     * Constructs a new compiler.exceptions.CommandLineException with a specified detail message
     *
     * @param message the detail message
     */
    public CommandLineException(String message) {
        super(message);
    }
}
