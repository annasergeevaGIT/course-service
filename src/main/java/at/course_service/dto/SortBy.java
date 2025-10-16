package at.course_service.dto;

import at.course_service.at.course_service.exception.CourseServiceException;
import at.course_service.model.Course;
import at.course_service.model.Course_;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.http.HttpStatus;

public enum SortBy {
    AZ{
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<Course> root) {
            return cb.asc(root.get(Course_.name));
        }
    },
    ZA{
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<Course> root) {
            return cb.desc(root.get(Course_.name));
        }
    },
    PRICE_ASC{
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<Course> root) {
            return cb.asc(root.get(Course_.price));
        }
    },
    PRICE_DESC{
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<Course> root) {
            return cb.desc(root.get(Course_.price));
        }
    },
    DATE_ASC{
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<Course> root) {
            return cb.asc(root.get(Course_.createdAt));
        }
    },
    DATE_DESC{
        @Override
        public Order getOrder(CriteriaBuilder cb, Root<Course> root) {
            return cb.desc(root.get(Course_.createdAt));
        }
    };

    public abstract Order getOrder(CriteriaBuilder cb, Root<Course> root);

    @JsonCreator
    public static SortBy fromString(String str) {
        try {
            return SortBy.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            var msg = "Failed to create SortBy from string: %s".formatted(str);
            throw new CourseServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }
}
