package at.course_service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentCourseRequest {
    @NotEmpty(message = "The list of course names must not be empty.")
    private Set<String> courseNames;
}
