package at.course_service.dto;

import at.course_service.model.Category;
import at.course_service.model.Difficulty;
import at.course_service.model.ModuleCollection;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON serialization
public class CourseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private long duration; // in minutes
    private Difficulty difficulty;
    private String imageUrl;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private ModuleCollection moduleCollection;
}
