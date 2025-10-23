package at.course_service.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class CourseInfo {
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
}
