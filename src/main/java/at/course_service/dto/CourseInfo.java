package at.course_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CourseInfo {
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
}
