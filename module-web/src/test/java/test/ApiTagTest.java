package test;

import com.google.gson.Gson;
import com.mjc.school.service.dto.AuthorDTO;
import com.mjc.school.service.dto.TagDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class ApiTagTest {
    private Long tagId;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testCreateTag() {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("name");
        String jsonBody = new Gson().toJson(tagDTO);

        tagId = given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
        .when()
                .post("/tag")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    @Test
    public void testGetTagById() {
        given()
                .pathParam("id", tagId)
        .when()
        .get("/tag/{id}")
                .then()
                .statusCode(200);
    }

    @Test
    public void testUpdateTag() {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setName("updateName");
        String jsonBody = new Gson().toJson(tagDTO);

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .pathParam("id", tagId)
        .when()
                .patch("tag/{id}")
        .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllTags() {
        given()
        .when()
                .get("/tags/all")
        .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteTag() {
        given()
                .pathParam("id", tagId)
        .when()
                .delete("tag/{id}")
        .then()
                .statusCode(204);
    }
}
