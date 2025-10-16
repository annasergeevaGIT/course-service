package at.course_service.at.course_service.controller;

import at.course_service.dto.*;
import at.course_service.model.Category;
import at.course_service.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/courses")
@Tag(name = "CourseController", description = "REST API for managing courses.")
public class CourseController {

    private final CourseService courseService;
    /**
     * ObjectMapper from Jackson library does the deserialization
     * Maps the request body JSON into a Java object (CreateCourseRequest).
     * POST has a body, server reads that body using @RequestBody
     * “posting” data outward (from client to server) deserializes the incoming to server JSON
     */
    @Operation(
            summary = "${api.course-create.summary}",
            description = "${api.course-create.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "${api.response.createOk}"),
            @ApiResponse(responseCode = "409", description = "${api.response.createConflict}",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "${api.response.createBadRequest}",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDto createCourse(@RequestBody @Valid CreateCourseRequest dto) {
        log.info("Received POST request to create Course: {}", dto);
        return courseService.createCourse(dto);
    }

    /**
     * binds the {id} part from the URL to the Long id.
     */
    @Operation(
            summary = "${api.course-delete.summary}",
            description = "${api.course-delete.description}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "${api.response.deleteNoContent}")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable("id") @Positive(message = "ID must be positive.") Long id) {
        log.info("Received DELETE request for Course with id: {}", id);
        courseService.deleteCourse(id);
    }

    /**
     * PATCH is for partial updates, PUT is for full updates.
     * Here we use PATCH because we want to update only certain fields of the Course entity.
     */
    @Operation(
            summary = "${api.course-update.summary}",
            description = "${api.course-update.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.updateOk}"),
            @ApiResponse(
                    responseCode = "404",
                    description = "${api.response.notFound}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.updateBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
    })
    @PatchMapping("/{id}")
    public CourseDto updateCourse(@PathVariable("id") @Positive(message = "ID must be positive.") Long id,
                                  @RequestBody @Valid UpdateCourseRequest update) {
        log.info("Received PATCH request to update Course with id: {}. Update data: {}", id, update);
        return courseService.updateCourse(id, update);
    }

    @Operation(
            summary = "${api.course-get.summary}",
            description = "${api.course-get.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getOk}"),
            @ApiResponse(
                    responseCode = "404",
                    description = "${api.response.notFound}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public CourseDto getCourse(@PathVariable("id") @Positive(message = "ID must be positive.") Long id) {
        log.info("Received GET request for Course with id: {}", id);
        return courseService.getCourse(id);
    }

    /**
     * @RequestParam binds the query parameters from the URL to the method parameters.
     * Query string: /v1/courses?category=PROGRAMMING&sort=az
     * category and sort are query parameters.
     */
    @Operation(
            summary = "${api.course-list-get.summary}",
            description = "${api.course-list-get.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getListOk}"),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.getListBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping
    public List<CourseDto> getCourses(@RequestParam("category") @NotBlank(message = "Category must not be blank.") String category,
                                         @RequestParam(value = "sort", defaultValue = "az") @NotBlank (message="Sorting parameter should not be empty") String sortBy) {
        log.info("Received GET request for list of Courses in category: {} with sort: {}", category, sortBy);
        return courseService.getCoursesFor(Category.fromString(category), SortBy.fromString(sortBy));
    }

    @Operation(
            summary = "${api.course-info.summary}",
            description = "${api.course-info.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getCourseInfoOk}"),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.getCourseInfoBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @PostMapping("/course-info") // A payload (body) within a GET request message has no defined semantics; sending a payload body on a GET request might cause some existing implementations to reject the request.
    public OrderCourseResponse getCoursesForOrder(@RequestBody @Valid OrderCourseRequest request) {
        log.info("Received request to GET course info with names: {}", request.getCourseNames());
        return courseService.getCoursesForOrder(request);
    }
}
