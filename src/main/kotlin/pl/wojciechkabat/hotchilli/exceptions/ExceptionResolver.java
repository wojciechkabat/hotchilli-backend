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

    private final String GUEST_VOTE_LIMIT_EXCEEDED_MESSAGE = "The limit of guest vote per day was exceeded";

    @ExceptionHandler(GuestVoteLimitExceededException.class)
    public ResponseEntity<Error> guestVoteLimitExceededException(GuestVoteLimitExceededException e) throws IOException {
        Error error = new Error(GUEST_VOTE_LIMIT_EXCEEDED_CODE, GUEST_VOTE_LIMIT_EXCEEDED_MESSAGE);
        return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
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