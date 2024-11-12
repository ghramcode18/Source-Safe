package Geeks.Source.Safe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // This sets the HTTP status to 401
public class UnauthorizedException extends RuntimeException {

    public  UnauthorizedException(String message) {
        super(message);
    }
}
