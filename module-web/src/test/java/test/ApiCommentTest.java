package test;

import com.google.gson.Gson;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.CommentDTO;
import com.mjc.school.service.dto.NewsDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ApiCommentTest {
    private Long CommentId;
    private static Long newsId;

    @BeforeAll
    public static void addNews() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("name");
        String jsonBody = new Gson().toJson(authorDTO);

        Long authorId = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/author")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setContent("content");
        newsDTO.setTitle("title");
        newsDTO.setAuthorId(authorId);
        jsonBody = new Gson().toJson(newsDTO);

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

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testCreateComment() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("content");
        commentDTO.setNewsId(newsId);
        String jsonBody = new Gson().toJson(commentDTO);

        CommentId = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
        .when()
                .post("/comment")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    public void testGetCommentById() {
        given()
                .pathParam("id", CommentId)
        .when()
                .get("/comment/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateComment() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent("updateContent");
        String jsonBody = new Gson().toJson(commentDTO);

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .pathParam("id", CommentId)
        .when()
                .patch("comment/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllComment() {
        given()
        .when()
                .get("/comment/all")
        .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteComment() {
        given()
                .pathParam("id", CommentId)
        .when()
                .delete("comment/{id}")
        .then()
                .statusCode(204);
    }
}
