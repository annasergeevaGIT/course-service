package at.course_service.at.course_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CourseServiceException extends RuntimeException {
    private final HttpStatus status;
    public CourseServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
