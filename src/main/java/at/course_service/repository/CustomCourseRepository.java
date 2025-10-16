package at.course_service.repository;

import at.course_service.dto.SortBy;
import at.course_service.dto.UpdateCourseRequest;
import at.course_service.model.Category;
import at.course_service.model.Course;

import java.util.List;

public interface CustomCourseRepository {
    int updateCourse(Long id, UpdateCourseRequest dto);
    List<Course> getCoursesFor(Category category, SortBy sortBy);
}
