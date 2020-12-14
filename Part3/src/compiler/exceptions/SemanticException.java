package compiler.exceptions;

/**
 * The exception for semantic problems
 */
public class SemanticException extends Exception{
    /**
     * Constructs a new SemanticException with a specified detail message
     *
     * @param message the detail message
     */
    public SemanticException(String message) {
        super(message);
    }
}
