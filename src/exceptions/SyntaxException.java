package exceptions;

/**
 * The exception for syntax problems
 */
public class SyntaxException extends Exception{
    /**
     * Constructs a new SyntaxException with a specified detail message
     *
     * @param message the detail message
     */
    public SyntaxException(String message) {
        super(message);
    }
}
