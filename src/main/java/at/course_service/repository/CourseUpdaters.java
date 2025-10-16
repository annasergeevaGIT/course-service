package at.course_service.repository;

import at.course_service.dto.UpdateCourseRequest;
import at.course_service.model.Course_;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CourseUpdaters {
    @Bean
    CourseAttrUpdater<String> name() {
        return new CourseAttrUpdater<>(Course_.name, UpdateCourseRequest::getName);
    }

    @Bean
    CourseAttrUpdater<String> description() {
        return new CourseAttrUpdater<>(Course_.description, UpdateCourseRequest::getDescription);
    }

    @Bean
    CourseAttrUpdater<BigDecimal> price() {
        return new CourseAttrUpdater<>(Course_.price, UpdateCourseRequest::getPrice);
    }

    @Bean
    CourseAttrUpdater<Long> duration() {
        return new CourseAttrUpdater<>(Course_.duration, UpdateCourseRequest::getDuration);
    }
    @Bean
    CourseAttrUpdater<String> imageUrl() {
        return new CourseAttrUpdater<>(Course_.imageUrl, UpdateCourseRequest::getImageUrl);
    }
}
