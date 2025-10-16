package at.course_service.mapper;

import at.course_service.dto.CourseDto;
import at.course_service.dto.CreateCourseRequest;
import at.course_service.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
/**
 * We use the MapStruct library to simplify the process of mapping DTOs to models and vice versa.
 * mapper as spring bean, fail compilation if a property is not mapped
 * @link <a href="https://mapstruct.org/">MapStruct</a>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CourseMapper {
    CourseDto toDto(Course domain);

    // id, createdAt, or updatedAt, they are automatically set by Hibernate; tell MapStruct to ignore them.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course toDomain(CreateCourseRequest dto);

    List<CourseDto> toDtoList(List<Course> domains);
}
