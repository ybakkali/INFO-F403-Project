package compiler.exceptions;

/**
 * The exception for lexical problems
 */
public class LexicalException extends Exception {
    /**
     * Constructs a new LexicalException with a specified detail message
     *
     * @param message the detail message
     */
    public LexicalException(String message) {
        super(message);
    }
}