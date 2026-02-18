package mod4.jpaapi.exceptionhandling;

import mod4.jpaapi.exceptionhandling.exceptions.NotValidUserInputException;
import mod4.jpaapi.exceptionhandling.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UsersAPIExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UsersAPIExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        logger.error("An error occured: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse =  new ErrorResponse("User not found", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotValidUserInputException.class)
    public ResponseEntity<ErrorResponse> handleNotValidUserInput(NotValidUserInputException ex) {
        logger.error("An error occured: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse =  new ErrorResponse("Not valid User data provided", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("An error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Internal server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
