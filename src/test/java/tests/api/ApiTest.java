package tests.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utils.allure_api_util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ApiTest extends ApiBaseTest {

    private static String noteIdToDelete;

    // ─────────────────────────────────────────────────────────────
    // TC-12 : GET /notes returns notes list
    // ─────────────────────────────────────────────────────────────

    @Test(
            priority = 1,
            testName =
                    "TC-12: GET /notes Returns Notes List"
    )

    public void verifyGetNotesReturnsNotesList() {

        Response response = given()

                .header(
                        "x-auth-token",
                        token
                )

                .when()

                .get("/notes")

                .then()

                .statusCode(200)

                .extract()

                .response();
                       allure_api_util.attachResponse(response);

        boolean success =
                response.jsonPath()
                        .getBoolean("success");

        int status =
                response.jsonPath()
                        .getInt("status");

        String message =
                response.jsonPath()
                        .getString("message");

        Assert.assertTrue(
                success,
                "TC-12 FAILED : success field is false"
        );

        Assert.assertEquals(
                status,
                200,
                "TC-12 FAILED : status is not 200"
        );

        Assert.assertNotNull(
                message,
                "TC-12 FAILED : message is null"
        );

        Assert.assertNotNull(
                response.jsonPath().getList("data"),
                "TC-12 FAILED : data array is null"
        );

        // Performance validation

        Assert.assertTrue(
                response.getTime() < 2000,
                "TC-12 FAILED : Response time exceeded 2 seconds"
        );

        System.out.println(
                "TC-12 PASSED : GET /notes returned successfully in "
                        + response.getTime()
                        + " ms"
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TC-13 : Validate response schema
    // ─────────────────────────────────────────────────────────────

    @Test(
            priority = 2,
            testName =
                    "TC-13: GET /notes Response Structure Validation"
    )

    public void verifyGetNotesResponseStructure() {

        given()

                .header(
                        "x-auth-token",
                        token
                )

                .when()

                .get("/notes")

                .then()

                .statusCode(200)

                .body(
                        matchesJsonSchemaInClasspath(
                                "schemas/schema.json"
                        )
                );
                       

        System.out.println(
                "TC-13 PASSED : Response schema validated"
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TC-16 : DELETE note via API
    // ─────────────────────────────────────────────────────────────

    @Test(
            priority = 3,
            testName =
                    "TC-16: DELETE Note Via API"
    )

    public void verifySuccessfulNoteDeletion() {

        // Create note request body

        Map<String, String> noteBody =
                new HashMap<>();

        noteBody.put(
                "title",
                "Note to Delete"
        );

        noteBody.put(
                "description",
                "Created for deletion test"
        );

        noteBody.put(
                "category",
                "Home"
        );

        // Create note

        Response createResponse = given()

                .header(
                        "x-auth-token",
                        token
                )

                .contentType(ContentType.JSON)

                .body(noteBody)

                .when()

                .post("/notes")

                .then()

                .statusCode(200)

                .extract()

                .response();
        allure_api_util.attachResponse(createResponse);

        noteIdToDelete =
                createResponse.jsonPath()
                        .getString("data.id");

        System.out.println(
                "Created Note ID : "
                        + noteIdToDelete
        );

        // Delete note

        Response deleteResponse = given()

                .header(
                        "x-auth-token",
                        token
                )

                .when()

                .delete("/notes/" + noteIdToDelete)

                .then()

                .statusCode(200)

                .extract()

                .response();
                allure_api_util.attachResponse(deleteResponse);
        

        boolean success =
                deleteResponse.jsonPath()
                        .getBoolean("success");

        int status =
                deleteResponse.jsonPath()
                        .getInt("status");

        Assert.assertTrue(
                success,
                "TC-16 FAILED : success is false"
        );

        Assert.assertEquals(
                status,
                200,
                "TC-16 FAILED : status is not 200"
        );

        System.out.println(
                "TC-16 PASSED : Note deleted successfully"
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TC-17 : DELETE invalid note
    // ─────────────────────────────────────────────────────────────

    @Test(
            priority = 4,
            testName =
                    "TC-17: DELETE Invalid Note"
    )

    public void verifyDeletionOfNonExistingNote() {

        String invalidNoteId =
                utils.ConfigReader.get(
                        "invalid.note.id"
                );

        Response response = given()

                .header(
                        "x-auth-token",
                        token
                )

                .when()

                .delete("/notes/" + invalidNoteId)

                .then()

                .statusCode(404)

                .extract()

                .response();
allure_api_util.attachResponse(response);
        boolean success =
                response.jsonPath()
                        .getBoolean("success");

        int status =
                response.jsonPath()
                        .getInt("status");

        String message =
                response.jsonPath()
                        .getString("message");

        Assert.assertFalse(
                success,
                "TC-17 FAILED : success should be false"
        );

        Assert.assertEquals(
                status,
                404,
                "TC-17 FAILED : Expected 400 status"
        );

        Assert.assertNotNull(
                message,
                "TC-17 FAILED : message is null"
        );

        System.out.println(
                "TC-17 PASSED : Proper error returned for invalid note ID"
        );
    }
    @Test(priority = 5,
        testName = "TC-20: Verify GET Notes Response Time")
public void verifyGetNotesResponseTime() {

    Response response = given()
            .header("x-auth-token", token)
            .when()
            .get("/notes")
            .then()
            .statusCode(200)
            .extract()
            .response();
        allure_api_util.attachResponse(response);

    long responseTime = response.getTime();

    Assert.assertTrue(
            responseTime < 2000,
            "TC-20 FAILED : Response time exceeded 2 seconds"
    );

    System.out.println(
            "TC-20 PASSED : Response received in "
                    + responseTime + " ms"
    );
}
    @Test(priority = 6,
        testName = "TC-21: Verify API Stability For Multiple Requests")
public void verifyApiStabilityForMultipleRequests() {

    for (int i = 1; i <= 5; i++) {

        Response response = given()
                .header("x-auth-token", token)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .extract()
                .response();
        

        long responseTime = response.getTime();

        Assert.assertTrue(
                responseTime < 2000,
                "TC-21 FAILED : Response time exceeded 2 seconds in iteration " + i
        );

        System.out.println(
                "Request " + i +
                        " completed successfully in " +
                        responseTime + " ms"
        );
    }

    System.out.println(
            "TC-21 PASSED : API remained stable for multiple requests"
    );
}
@Test(priority = 7,
        testName = "TC-22: Verify API With Invalid Authentication Token")
public void verifyApiWithInvalidToken() {

    String invalidToken = "invalid_token_123";

    Response response = given()
            .header("x-auth-token", invalidToken)
            .when()
            .get("/notes")
            .then()
            .statusCode(401)
            .extract()
            .response();
        allure_api_util.attachResponse(response);

    boolean success =
            response.jsonPath().getBoolean("success");

    int status =
            response.jsonPath().getInt("status");

    String message =
            response.jsonPath().getString("message");

    Assert.assertFalse(
            success,
            "TC-22 FAILED : success should be false"
    );

    Assert.assertEquals(
            status,
            401,
            "TC-22 FAILED : Status code mismatch"
    );

    Assert.assertNotNull(
            message,
            "TC-22 FAILED : Error message is null"
    );

    System.out.println(
            "TC-22 PASSED : Invalid token validation successful"
    );

    System.out.println(
            "Error Message : " + message
    );
}
}