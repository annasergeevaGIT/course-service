package at.course_service.service;

import at.course_service.BaseIntegrationTest;
import at.course_service.at.course_service.exception.CourseServiceException;
import at.course_service.dto.CourseDto;
import at.course_service.dto.SortBy;
import at.course_service.model.Category;
import at.course_service.repository.CourseRepository;
import at.course_service.testutils.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class CourseServiceImplTest extends BaseIntegrationTest {

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    void getCourseFor_Category_retrunSortedList() { // get list of courses by category and sorted by name
        List<CourseDto> courses = courseService.getCoursesFor(Category.ENGINEERING, SortBy.AZ);
        assertThat(courses).hasSize(3);
        assertElementsInOrder(courses, CourseDto::getName, List.of("Java Basics", "Python for Developers", "Web Development with JavaScript"));
    }

    @Test
    void getCourse_returnsCourse_whenCourseInDB() {
        var id = getIdByName("Java Basics");
        var course = courseService.getCourse(id);
        assertThat(course).isNotNull();
        assertThat(course.getId()).isNotNull();
        assertThat(course.getName()).isEqualTo("Java Basics");
        assertThat(course.getCreatedAt()).isNotNull();
        assertThat(course.getUpdatedAt()).isNotNull();
    }

    @Test
    void getCourse_throws_whenNoCourseInDb() { // id not present in test data
        assertThrows(
                CourseServiceException.class,
                () -> courseService.getCourse(1000L)
        );
    }

    @Test
    void deleteCourse_deletesCourse() { // delete existing course
        var id = getIdByName("Java Basics");
        courseService.deleteCourse(id);
        var deleted = courseRepository.findById(id);
        assertThat(deleted).isEmpty(); // a value that might be present or absent, avoiding null
    }

    @Test
    void createCourse_createsCourse() { //  create new course
        var dto = TestData.createCourseRequest();
        var now = LocalDateTime.now().minusNanos(1000); // to avoid precision issues
        CourseDto created = courseService.createCourse(dto);
        assertThat(created.getId()).isNotNull();
        assertFieldsEquality(created, dto, "name", "description", "price", "imageUrl", "duration");
        assertThat(created.getCreatedAt()).isAfter(now);
        assertThat(created.getUpdatedAt()).isAfter(now);
    }

    @Test
    void createCourse_throws_whenCourseNameNotUnique() { // name already exists in test data
        var dto = TestData.createCourseRequest();
        dto.setName("Java Basics"); // first creation should succeed
        assertThrows(
                CourseServiceException.class,
                () -> courseService.createCourse(dto) // second creation with the same name should throw
        );
    }

    @Test
    void updateCourse_updatesCourse_whenNoCourseInDb(){ // no course with such id
        var id = 1000L;
        var dto = TestData.updateCourseFullRequest();
        assertThrows(
                CourseServiceException.class,
                () -> courseService.updateCourse(id, dto)
        );
    }

    @Test
    void updateCourse_throws_whenUpdateRequestContainsNotUniqueName(){ // name is not unique
        var id = getIdByName("Java Basics");
        var dto = TestData.updateCourseFullRequest();
        dto.setName("Physics Made Simple");
        assertThrows(
                CourseServiceException.class,
                () -> courseService.updateCourse(id, dto)
        );
    }
}
