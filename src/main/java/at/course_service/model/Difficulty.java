package at.course_service.model;

import at.course_service.at.course_service.exception.CourseServiceException;
import org.springframework.http.HttpStatus;

public enum Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED;

    public static Difficulty fromString(String level) {
        try {
            return Difficulty.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            var msg = "Failed to create CourseLevel from string: %s".formatted(level);
            throw new CourseServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
