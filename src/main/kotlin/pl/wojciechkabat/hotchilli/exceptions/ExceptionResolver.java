package pl.wojciechkabat.hotchilli.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ExceptionResolver {

    private final int GUEST_VOTE_LIMIT_EXCEEDED_CODE = 521;
    private final int USER_WITH_LOGIN_ALREADY_EXISTS_CODE = 421;

    private final String GUEST_VOTE_LIMIT_EXCEEDED_MESSAGE = "The limit of guest vote per day was exceeded";
    private final String USER_WITH_LOGIN_ALREADY_EXISTS_MESSAGE = "User with email provided already exists";

    @ExceptionHandler(GuestVoteLimitExceededException.class)
    public ResponseEntity<Error> guestVoteLimitExceededException(GuestVoteLimitExceededException e) throws IOException {
        Error error = new Error(GUEST_VOTE_LIMIT_EXCEEDED_CODE, GUEST_VOTE_LIMIT_EXCEEDED_MESSAGE);
        return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserWithLoginAlreadyExistsException.class)
    public ResponseEntity<Error> userWithSuchLoginAlreadyExistsException() {
        Error error = new Error(USER_WITH_LOGIN_ALREADY_EXISTS_CODE, USER_WITH_LOGIN_ALREADY_EXISTS_MESSAGE);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @Getter
    @Setter
    public class Error {
        private int statusCode;
        private String message;

        public Error(int status, String message) {
            this.statusCode = status;
            this.message = message;
        }
    }
}