package at.course_service.at.course_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** recommendations for exception handling: https://datatracker.ietf.org/doc/html/rfc7807
     * triggered when the service throws a [MenuServiceException]
     */
    @ExceptionHandler(CourseServiceException.class)
    public ProblemDetail handleCourseServiceException(CourseServiceException ex, WebRequest request) {
        log.error("Intercepted CourseServiceException. Message: {}. Status: {}", ex.getMessage(), ex.getStatus());
        return createProblemDetail(ex.getMessage(), ex.getStatus(), request);
    }

    //The handler is triggered when the body of the incoming request cannot be read.

    //The handler is triggered when there are validation errors in the request body.

    //The handler is triggered when methods of a RestController have parameters annotated with @Valid, for example:

    //@PathVariable("id") @Positive(message = "id must be > 0.") @Valid Long id, but these parameters fail validation.

    private static ProblemDetail createProblemDetail(String message, HttpStatus status, WebRequest request) {
        var pd = ProblemDetail.forStatusAndDetail(status, message);
        pd.setProperty("timestamp", Instant.now());
        pd.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return pd;
    }
}
