package marketplace.orderservice.exception;

public class ValidationException extends OrderException {
    public ValidationException(String message) {
        super("Validation error: " + message);
    }
}
