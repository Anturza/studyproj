package mod4.jpaapi.exceptionhandling.exceptions;

public class NotValidUserInputException extends RuntimeException {
    public NotValidUserInputException() {
        super();
    }

    public NotValidUserInputException(String message) {
        super(message);
    }

    public NotValidUserInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidUserInputException(Throwable cause) {
        super(cause);
    }
}
