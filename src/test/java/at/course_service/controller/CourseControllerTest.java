package at.course_service.controller;

import at.course_service.BaseIntegrationTest;
import at.course_service.dto.CourseDto;
import at.course_service.dto.CourseInfo;
import at.course_service.dto.EnrollmentCourseRequest;
import at.course_service.dto.EnrollmentCourseResponse;
import at.course_service.testutils.AuthToken;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

import static at.course_service.testutils.TestConstants.BASE_URL;
import static at.course_service.testutils.TestData.createCourseRequest;
import static at.course_service.testutils.TestData.updateCourseFullRequest;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

public class CourseControllerTest extends BaseIntegrationTest {

    private static final KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:24.0")
            .withRealmImportFile("/cloud-java-realm.json");

    static {
        KEYCLOAK.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> KEYCLOAK.getAuthServerUrl() + "/realms/cloud-java");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> KEYCLOAK.getAuthServerUrl() + "/realms/cloud-java/protocol/openid-connect/certs");
    }

    @Autowired
    private WebTestClient webTestClient;
    private static AuthToken admin;
    private static AuthToken user;

    @BeforeAll
    static void setup() {
        WebClient webClient = WebClient.builder()
                .baseUrl(KEYCLOAK.getAuthServerUrl() + "/realms/cloud-java/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        admin = createToken(webClient, "alex", "password");
        user = createToken(webClient, "max", "password");
    }

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
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
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
    void createCourse_returnsConflict_whenCourseWithThatNameInDb() {
        var dto = createCourseRequest();
        dto.setName("Java Basics");

        webTestClient.post()
                .uri(BASE_URL)
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createCourse_returnsUnauthorized_whenNoAccessToken() {
        var dto = createCourseRequest();

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createCourse_returnsForbidden_forSimpleUser() {
        var dto = createCourseRequest();

        webTestClient.post()
                .uri(BASE_URL)
                .headers(h -> h.setBearerAuth(user.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void deleteCourse_deletesItem() {
        var id = getIdByName("Java Basics");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteCourse_returnsUnauthorized_whenNoAccessToken() {
        var id = getIdByName("Java Basics");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void deleteCourse_returnsForbidden_forSimpleUser() {
        var id = getIdByName("Java Basics");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(user.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void updateCourse_updatesCourse() {
        var update = updateCourseFullRequest();
        var id = getIdByName("Java Basics");

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
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
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateCourse_returnsUnauthorized_whenNoAccessToken() {
        var update = updateCourseFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void updateCourse_returnsForbidden_forSimpleUser() {
        var update = updateCourseFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(user.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void getCourseForEnrollment_returnsCorrectCourseInfo(){
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
    private static AuthToken createToken(WebClient webClient, String username, String password) {
        return webClient.post()
                .body(fromFormData("grant_type", "password")
                        .with("client_id", "cloud-java-gateway")
                        .with("username", username)
                        .with("password", password)
                        .with("client_secret", "RleFn4MVPDKtGTXIZv4Opyfuwfx2fFLL")
                )
                .retrieve()
                .bodyToMono(AuthToken.class)
                .block();
    }
}
