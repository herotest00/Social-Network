package socialnetwork.domain.validators;

/**
 *  type of exception class
 */
public class ValidationException extends RuntimeException {

    /**
     *  constructor
     * @param message - message of type String
     */
    public ValidationException(String message) {
        super(message);
    }
}
