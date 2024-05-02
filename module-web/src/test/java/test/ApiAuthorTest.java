package test;

import com.google.gson.Gson;
import com.mjc.school.service.dto.AuthorDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ApiAuthorTest {
    private Long authorId;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testCreateAuthor() {
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

    @Test
    public void testGetAuthorById() {
        given()
                .pathParam("id", authorId)
        .when()
                .get("/author/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateAuthor() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setName("updateName");
        String jsonBody = new Gson().toJson(authorDTO);

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .pathParam("id", authorId)
        .when()
                .patch("author/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllAuthor() {
        given()
        .when()
                .get("/authors/all")
        .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteAuthor() {
        given()
                .pathParam("id", authorId)
        .when()
                .delete("author/{id}")
        .then()
                .statusCode(204);
    }
}
