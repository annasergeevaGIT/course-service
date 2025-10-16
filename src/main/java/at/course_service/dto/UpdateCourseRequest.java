package at.course_service.dto;

import at.course_service.dto.validation.NullOrNotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //Prevents sending unnecessary null values
public class UpdateCourseRequest {
    @NullOrNotBlank(message = "Course name must not be empty.")
    private String name;
    @NullOrNotBlank(message = "Course description must not be empty.")
    private String description;
    @Positive(message = "Course price must be > 0.")
    private BigDecimal price;
    @Positive (message = "The duration time must be > 0.")
    private Long duration;
    @NullOrNotBlank(message = "Image URL must not be empty.")
    private String imageUrl;
}
