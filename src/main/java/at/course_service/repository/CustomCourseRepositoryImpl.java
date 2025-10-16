package at.course_service.repository;

import at.course_service.dto.SortBy;
import at.course_service.dto.UpdateCourseRequest;
import at.course_service.model.Course;
import at.course_service.model.Category;
import at.course_service.model.Course_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CustomCourseRepositoryImpl implements CustomCourseRepository {
    private final EntityManager em;
    private final List<CourseAttrUpdater<?>> updaters;

    public CustomCourseRepositoryImpl(EntityManager em, List <CourseAttrUpdater<?>> updaters) {
        this.em = em;
        this.updaters = updaters;
    }

    @Transactional
    @Override
    public int updateCourse(Long id, UpdateCourseRequest dto) {
        CriteriaBuilder cb = em.getCriteriaBuilder(); //Creates a CriteriaBuilder instance to construct criteria queries, compound selections, expressions, predicates, orderings
        CriteriaUpdate<Course> criteriaUpdate = cb.createCriteriaUpdate(Course.class); //Creates a criteria update object similar to UPDATE Course SET ...
        Root<Course> root = criteriaUpdate.from(Course.class); //Defines the entity root (like the FROM menu_item part in SQL).
        updaters.forEach(updater -> updater.updateAttr(criteriaUpdate, dto)); //Iterates over a list of CourseAttrUpdater instances to update the attributes of the Course entity based on the provided UpdateCourseRequest DTO.
        criteriaUpdate.where(cb.equal(root.get(Course_.id), id)); //Adds a WHERE clause to the update query to target the specific Course entity by its id.
        return em.createQuery(criteriaUpdate).executeUpdate(); //Executes the update query and returns the number of entities updated.
    }

    @Override
    public List<Course> getCoursesFor(Category category, SortBy sortBy) {
        CriteriaBuilder cb = em.getCriteriaBuilder(); //Creates a CriteriaBuilder instance to construct criteria queries, compound selections, expressions, predicates, orderings
        CriteriaQuery<Course> query = cb.createQuery(Course.class); //Creates a query object similar to SELECT * FROM Course
        Root<Course> root = query.from(Course.class); //Defines the entity root (like the FROM menu_item part in SQL).
        query.orderBy(sortBy.getOrder(cb, root)); //Orders the results based on the specified sortBy parameter.
        query.where(cb.equal(root.get(Course_.category), category)); //Adds a WHERE clause to filter results based on the specified category.
        CriteriaQuery<Course> select = query.select(root); //Specifies the selection of the query, which is the root entity (Course).
        TypedQuery<Course> typedQuery = em.createQuery(select); //Creates a typed query from the criteria query.
        return typedQuery.getResultList(); //Executes the query and returns the list of Course entities that match the criteria.
    }
}
