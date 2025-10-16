package at.course_service.repository;

import at.course_service.dto.UpdateCourseRequest;
import at.course_service.model.Course;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public class CourseAttrUpdater<V> { // Generic class to update attributes of Course entity, avoiding multiple null checks in the repository
    SingularAttribute<Course, V> attr;
    Function<UpdateCourseRequest, V> dtoValueExtractor;

    public void updateAttr(CriteriaUpdate<Course> criteria, UpdateCourseRequest dto) {
        V dtoValue = dtoValueExtractor.apply(dto);
        if (dtoValue != null) { // no unnecessary DB update
            criteria.set(attr, dtoValue);
        }
    }
}
