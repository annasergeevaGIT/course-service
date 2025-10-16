package at.course_service.model;

import at.course_service.at.course_service.exception.CourseServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum Category {
    BUSINESS,
    DESIGN,
    LANGUAGES,
    SCIENCE,
    ARTS,
    ENGINEERING;

    @JsonCreator
    public static Category fromString(String str) {
        try {
            return Category.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            var msg = "Failed to create CourseCategory from string: %s".formatted(str);
            throw new CourseServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
