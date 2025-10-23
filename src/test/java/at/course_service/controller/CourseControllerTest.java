package at.course_service.controller;

import at.course_service.BaseIntegrationTest;
import at.course_service.dto.CourseDto;
import at.course_service.dto.CourseInfo;
import at.course_service.dto.EnrollmentCourseRequest;
import at.course_service.dto.EnrollmentCourseResponse;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

import static at.course_service.testutils.TestConstants.BASE_URL;
import static at.course_service.testutils.TestData.createCourseRequest;
import static at.course_service.testutils.TestData.updateCourseFullRequest;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CourseControllerTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getCourse_returnsCourse_whenItExists() {
        var id = getIdByName("Java Basics");
        webTestClient.get()
                .uri(BASE_URL+"/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CourseDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo("Java Basics");
                });
    }

    @Test
    void getCourse_returnsEmptyListForCategoryNotPresentInDB() {
        webTestClient.get()
                .uri(BASE_URL + "?category=languages&sort=az")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CourseDto.class)
                .value(response -> assertThat(response).isEmpty()); // No courses were found for the category engineering
    }

    @Test
    void getCourse_returnsNotFound_whenCourseNotExists() {
        var id = 1000L;
        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createCourse_createItem(){
        var dto = createCourseRequest();
        var now = LocalDateTime.now();

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CourseDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo(dto.getName());
                    assertThat(response.getDescription()).isEqualTo(dto.getDescription());
                    assertThat(response.getPrice()).isEqualTo(dto.getPrice());
                    assertThat(response.getDuration()).isEqualTo(dto.getDuration());
                    assertThat(response.getImageUrl()).isEqualTo(dto.getImageUrl());
                    assertThat(response.getModuleCollection()).isEqualTo(dto.getModuleCollection());
                    assertThat(response.getCreatedAt()).isAfter(now);
                    assertThat(response.getUpdatedAt()).isAfter(now);
                });
    }

    @Test
    void createCourse_returnsConflict_whenMenuWithThatNameInDb() {
        var dto = createCourseRequest();
        dto.setName("Java Basics");

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deleteCourse_deletesItem() {
        var id = getIdByName("Java Basics");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateCourse_updatesCOurse() {
        var update = updateCourseFullRequest();
        var id = getIdByName("Java Basics");

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CourseDto.class)
                .value(response -> {
                    assertThat(response.getName()).isEqualTo(update.getName());
                    assertThat(response.getDescription()).isEqualTo(update.getDescription());
                    assertThat(response.getPrice()).isEqualTo(update.getPrice());
                    assertThat(response.getDuration()).isEqualTo(update.getDuration());
                    assertThat(response.getImageUrl()).isEqualTo(update.getImageUrl());
                });
    }

    @Test
    void updateCourse_returnsNotFound_WhenCourseNotInDb() {
        var id = 1000L;
        var update = updateCourseFullRequest();

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getCourseForOrder_returnsCorrectCourseInfo(){
        var request = EnrollmentCourseRequest.builder()
                .courseNames(Set.of("Java Basics", "Python for Developers", "Unknown"))
                .build();
        webTestClient.post()
                .uri(BASE_URL + "/course-info")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EnrollmentCourseResponse.class)
                .value(response -> {
                    var infos = response.getCourseInfos();
                    infos.sort(Comparator.comparing(CourseInfo::getName));
                    assertThat(infos).hasSize(request.getCourseNames().size());
                    assertThat(infos.get(0).getName()).isEqualTo("Java Basics");
                    assertThat(infos.get(0).getPrice()).isNotNull();
                    assertThat(infos.get(0).getIsAvailable()).isTrue();

                    assertThat(infos.get(1).getName()).isEqualTo("Python for Developers");
                    AssertionsForClassTypes.assertThat(infos.get(1).getPrice()).isNotNull();
                    AssertionsForClassTypes.assertThat(infos.get(1).getIsAvailable()).isTrue();

                    assertThat(infos.get(2).getName()).isEqualTo("Unknown");
                    assertThat(infos.get(2).getPrice()).isNull();
                    assertThat(infos.get(2).getIsAvailable()).isFalse();
                });
    }
}
