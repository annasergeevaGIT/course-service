package at.course_service.testutils;

import at.course_service.dto.CreateCourseRequest;
import at.course_service.dto.UpdateCourseRequest;
import at.course_service.model.Category;
import at.course_service.model.Difficulty;
import at.course_service.model.Module;
import at.course_service.model.ModuleCollection;

import java.math.BigDecimal;
import java.util.List;

import static at.course_service.testutils.TestConstants.*;

public class TestData {
    public static ModuleCollection javaBasicsModules() {
        return new ModuleCollection(
                List.of(
                        new Module(JAVA_BASICS_FIRST_MODULE, JAVA_BASICS_FIRST_MODULE_DURATION),
                        new Module(JAVA_BASICS_SECOND_MODULE, JAVA_BASICS_SECOND_MODULE_DURATION)
                )
        );
    }

    public static UpdateCourseRequest updateCourseFullRequest() {
        return UpdateCourseRequest.builder()
                .name("Advanced Java")
                .description("Advanced Java Description")
                .price(BigDecimal.valueOf(100.01))
                .duration(2000L)
                .imageUrl("http://images.com/advanced-java.png")
                .build();
    }

    public static CreateCourseRequest createCourseRequest() {
        return CreateCourseRequest.builder()
                .name(JAVA_BASICS_NAME)
                .description(JAVA_BASICS_DESCRIPTION)
                .price(JAVA_BASICS_PRICE)
                .category(Category.ENGINEERING)
                .duration(JAVA_BASICS_DURATION_MINUTES)
                .difficulty(Difficulty.BEGINNER)
                .imageUrl(JAVA_BASICS_IMAGE_URL)
                .moduleCollection(javaBasicsModules())
                .build();
    }
}
