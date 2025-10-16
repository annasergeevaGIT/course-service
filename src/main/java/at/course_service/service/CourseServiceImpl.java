package at.course_service.service;

import at.course_service.at.course_service.exception.CourseServiceException;
import at.course_service.dto.*;
import at.course_service.mapper.CourseMapper;
import at.course_service.model.Category;
import at.course_service.model.CourseProjection;
import at.course_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseMapper mapper;
    private final CourseRepository repository;

    @Override
    public CourseDto createCourse(CreateCourseRequest dto) {
        var course = mapper.toDomain(dto);
        try{
            return mapper.toDto(repository.save(course));
        } catch (DataIntegrityViolationException e){
            var msg = String.format("Failed to create Course: %s. Reason: Item with name %s already exists.", dto, dto.getName());
            throw new CourseServiceException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public void deleteCourse(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public CourseDto updateCourse(Long id, UpdateCourseRequest update) {
        try {
            int updateCount = repository.updateCourse(id, update);
            if (updateCount == 0) {
                var msg = String.format("Course with id=%d not found.", id);
                throw new CourseServiceException(msg, HttpStatus.NOT_FOUND);
            }
            return this.getCourse(id);
        } catch (DataIntegrityViolationException ex) {
            var msg = String.format("Failed to update Course with ID: %d. Reason: Item with name %s already exists.", id, update.getName());
            throw new CourseServiceException(msg, HttpStatus.CONFLICT);
    }
}

    @Override
    public CourseDto getCourse(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)// lambda expression x -> mapper.toDto(x)
                .orElseThrow(() -> new CourseServiceException(
                        String.format("Course with id=%d not found.", id), HttpStatus.NOT_FOUND));
    }

    @Override
    public List<CourseDto> getCoursesFor(Category category, SortBy sortBy) {
        return mapper.toDtoList(repository.getCoursesFor(category, sortBy));
    }

    /**
     * Builds a response with detailed information for the requested courses.
     * For each course name in the {@link OrderCourseRequest}, this method checks
     * if it exists in the database and collects its name, price, and availability status.
     * The response preserves the order of requested courses and includes missing ones
     * with {@code null} prices and {@code available = false}.
     *
     * @param request the request containing the list of course names
     * @return an {@link OrderCourseResponse} with course information and availability
     */
    @Override
    public OrderCourseResponse getCoursesForOrder(OrderCourseRequest request) {
        Map<String, CourseProjection> nameToProjection = repository.getCourseInfoForNames(request.getCourseNames())
                .stream()
                .collect(Collectors.toMap(CourseProjection::getName, Function.identity())); // map of course name to its projection
        List<CourseInfo> courseInfos = new ArrayList<>();
        for (String courseName : request.getCourseNames()) {
            if (nameToProjection.containsKey(courseName)) {
                var projection = nameToProjection.get(courseName);
                courseInfos.add(new CourseInfo(projection.getName(), projection.getPrice(), true));
            } else {
                courseInfos.add(new CourseInfo(courseName, null, false));
            }
        }
        return OrderCourseResponse.builder().courseInfos(courseInfos).build();
    }
}
