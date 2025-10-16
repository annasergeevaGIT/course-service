package at.course_service.service;

import at.course_service.dto.*;
import at.course_service.model.Category;

import java.util.List;

public interface CourseService {
    CourseDto createCourse(CreateCourseRequest course);
    void deleteCourse(Long id);
    CourseDto updateCourse(Long id, UpdateCourseRequest course);
    CourseDto getCourse(Long id);
    List<CourseDto> getCoursesFor(Category category, SortBy sortBy);
    OrderCourseResponse getCoursesForOrder(OrderCourseRequest request);
}
