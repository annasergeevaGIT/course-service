package at.course_service.at.course_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("Intercepted HttpMessageNotReadableException. Message: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(createProblemDetail(ex.getMessage(), HttpStatus.BAD_REQUEST, request));
    }

    //The handler is triggered when there are validation errors in the request body.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var pd = ex.getBody();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getGlobalErrors().forEach(e -> {
            errors.put(e.getObjectName(), e.getDefaultMessage());
        });
        ex.getBindingResult().getFieldErrors().forEach(e -> {
            errors.put(e.getField(), e.getDefaultMessage());
        });
        log.error("Intercepted MethodArgumentNotValidException. Errors: {}", errors);
        pd.setProperty("invalid_params", errors);
        pd.setStatus(HttpStatus.BAD_REQUEST);
        pd.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return handleExceptionInternal(ex, pd, headers, HttpStatus.BAD_REQUEST, request);
    }

    //The handler is triggered when methods of a RestController have parameters annotated with @Valid, for example:
    //@PathVariable("id") @Positive(message = "id must be > 0.") @Valid Long id, but these parameters fail validation.
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var pd = ex.getBody();
        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> { //getAllValidationResults() is deprecated
            String paramName = result.getMethodParameter().getParameterName();
            // For each error on that parameter
            result.getResolvableErrors().forEach(err ->
                errors.put(paramName, err.getDefaultMessage())
            );
        });
        log.error("Intercepted HandlerMethodValidationException. Errors: {}", errors);
        pd.setProperty("invalid_params", errors);
        pd.setStatus(HttpStatus.BAD_REQUEST);
        pd.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return handleExceptionInternal(ex, pd, headers, HttpStatus.BAD_REQUEST, request);
    }

    private static ProblemDetail createProblemDetail(String message, HttpStatus status, WebRequest request) {
        var pd = ProblemDetail.forStatusAndDetail(status, message);
        pd.setProperty("timestamp", Instant.now());
        pd.setInstance(URI.create(((ServletWebRequest) request).getRequest().getRequestURI()));
        return pd;
    }
}
