package at.course_service.repository;

import at.course_service.BaseTest;
import at.course_service.dto.SortBy;
import at.course_service.dto.UpdateCourseRequest;
import at.course_service.model.Category;
import at.course_service.model.Course;
import at.course_service.testutils.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;

@DataJpaTest
@Import(CourseUpdaters.class)
@Transactional(propagation = Propagation.NEVER) //https://dev.to/henrykeys/don-t-use-transactional-in-tests-
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // don't replace with in memory db, run the test against the db from application-test.yml
class CourseRepositoryImplTest extends BaseTest {
    @Autowired
    private CourseRepository courseRepository;

    @Test
    void updateCourse_WhenAllUpdateFieldsAreSet() {
        var dto = TestData.updateCourseFullRequest();
        var id = getIdByName("Java Basics");
        int updateCount = courseRepository.updateCourse(id, dto);
        assertThat(updateCount).isEqualTo(1);
        Course updated = courseRepository.findById(id).get();
        assertFieldsEquality(updated, dto, "name", "description", "price", "duration", "imageUrl");
    }

    @Test
    void updateCourse_WhenSomeUpdateFieldsAreSet() {
        var dto = UpdateCourseRequest.builder()
                .price(BigDecimal.valueOf(100.01))
                .description("New Java Basics Description")
                .imageUrl("http://images.com/new_javabasics.png")
                .build();
        var id = getIdByName("Java Basics");
        int updateCount = courseRepository.updateCourse(id, dto);
        assertThat(updateCount).isEqualTo(1); // one row should be updated
        Course updated = courseRepository.findById(id).get();
        assertFieldsEquality(updated, dto, "price", "description", "imageUrl");
    }

    @Test
    void updateCourse_throws_whenUpdateRequestHasNotUniqueName() {
        var dto = UpdateCourseRequest.builder()
                .name("Physics Made Simple") // name already exists in test data
                .price(BigDecimal.valueOf(100.01))
                .description("New Java Basics Description")
                .build();
        var id = getIdByName("Java Basics"); // get id of an existing course
        assertThrows(DataIntegrityViolationException.class, // if name is not unique, a constraint violation exception is thrown
                () -> courseRepository.updateCourse(id, dto)
        );
    }

    @Test
    void updateCourse_updatesNothing_whenNoMCoursePresentInDB() {
        var dto = TestData.updateCourseFullRequest();
        int updateCount = courseRepository.updateCourse(1000L, dto);
        assertThat(updateCount).isEqualTo(0);
    }

    @Test
    void getCourseFor_returnsCorrectListForCategory_sortedByPriceDesc() {
        var courses = courseRepository.getCoursesFor(Category.ENGINEERING, SortBy.PRICE_DESC);
        assertThat(courses).hasSize(3); // there are 3 courses in the ENGINEERING category in the test data
        assertElementsInOrder(courses, Course::getName,
                java.util.List.of("Java Basics", "Python for Developers", "Web Development with JavaScript"));
    }

    @Test
    void getCoursesFor_returnsCorrectListForCategory_sortedByNameAsc() {
        var courses = courseRepository.getCoursesFor(Category.ENGINEERING, SortBy.AZ);
        assertThat(courses).hasSize(3); // there are 3 courses in the ENGINEERING category in the test data
        assertElementsInOrder(courses, Course::getName, List.of("Java Basics", "Python for Developers", "Web Development with JavaScript"));
    }
}