package at.course_service.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
public @interface NullOrNotBlank { // own annotation type
    String message() default "Field must be null or not blank.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
