package test;

import com.google.gson.Gson;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.NewsDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ApiNewsTest {
    private Long newsId;
    private static Long authorId;

    @BeforeAll
    public static void addAuthor() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        String jsonBody = new Gson().toJson(authorDTO);

        authorId = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/author")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testCreateNews() {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setContent("content");
        newsDTO.setTitle("title");
        newsDTO.setAuthorId(authorId);
        String jsonBody = new Gson().toJson(newsDTO);

        newsId = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
        .when()
                .post("/news")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    public void testGetNewsById() {
        given()
                .pathParam("id", newsId)
        .when()
                .get("/news/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateNews() {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setContent("updateContent");
        newsDTO.setTitle("updateTitle");
        newsDTO.setAuthorId(authorId);
        String jsonBody = new Gson().toJson(newsDTO);

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .pathParam("id", newsId)
        .when()
                .patch("news/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllNews() {
        given()
        .when()
                .get("/news/all")
        .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteNews() {
        given()
                .pathParam("id", newsId)
        .when()
                .delete("news/{id}")
        .then()
                .statusCode(204);
    }
}
