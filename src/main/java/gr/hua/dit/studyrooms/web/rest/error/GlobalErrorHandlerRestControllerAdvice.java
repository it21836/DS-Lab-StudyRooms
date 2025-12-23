package gr.hua.dit.studyrooms.web.rest.error;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@RestControllerAdvice(basePackages = "gr.hua.dit.studyrooms.web.rest")
@Order(1)
public class GlobalErrorHandlerRestControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handle(Exception ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof NoResourceFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof SecurityException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof AuthorizationDeniedException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof ResponseStatusException rse) {
            try {
                status = HttpStatus.valueOf(rse.getStatusCode().value());
            } catch (Exception ignored) {}
        }

        ApiError err = new ApiError(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            req.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}
