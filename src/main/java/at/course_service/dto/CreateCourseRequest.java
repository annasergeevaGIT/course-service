package at.course_service.dto;

import at.course_service.dto.validation.NullOrNotBlank;
import at.course_service.model.Category;
import at.course_service.model.Difficulty;
import at.course_service.model.ModuleCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateCourseRequest { //received incoming data, dto for creating a new course
    @NotBlank(message = "Title should not be empty")
    private String name;

    @NotBlank(message = "Description should not be empty")
    private String description;

    @NullOrNotBlank(message = "Price should not be empty")
    private BigDecimal price;

    @NotNull(message = "Category should not be null")
    private Category category;

    @Positive(message = "Duration in minutes must be > 0.")
    private long duration; //in minutes

    @NotNull(message = "Difficulty level must not be null.")
    private Difficulty difficulty;

    @NotBlank(message = "Image URL must not be empty.")
    private String imageUrl;

    @NotNull(message = "Module collection must not be null.")
    private ModuleCollection moduleCollection;

}
