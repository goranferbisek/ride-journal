package si.ferbisek.ride_journal.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import si.ferbisek.ride_journal.exception.UsernameAlreadyExistsException;

@ControllerAdvice
@Slf4j
public class ErrorController {


    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException() {
        log.error("Username already exists");

        // should return an error object with field specific messages
        return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
    }
}
