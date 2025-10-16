package at.course_service.repository;

import at.course_service.model.Course;
import at.course_service.model.CourseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CourseRepository extends JpaRepository<Course, Long>, CustomCourseRepository {
    @Query("""
            select new at.course_service.model.CourseProjection(
                c.name,
                c.price
            ) from Course c where c.name in :names
            """)
    List<CourseProjection> getCourseInfoForNames(@Param("names") Set<String> names);
}
