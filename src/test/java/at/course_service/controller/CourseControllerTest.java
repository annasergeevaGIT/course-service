package at.course_service.controller;

import at.course_service.BaseIntegrationTest;
import at.course_service.dto.CourseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static at.course_service.testutils.TestConstants.BASE_URL;
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
}
