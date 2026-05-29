package tests.api;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;
import utils.allure_api_util;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class ApiBaseTest {

    protected static String token;

    @BeforeClass
    public void apiSetup() {

        // Base URI

        RestAssured.baseURI =
                "https://practice.expandtesting.com/notes/api";

        // Login request body

        Map<String, String> loginBody =
                new HashMap<>();

        loginBody.put(
                "email",
                ConfigReader.get("valid.email")
        );

        loginBody.put(
                "password",
                ConfigReader.get("valid.password")
        );

        // Send login request

        Response response = given()

                .contentType(ContentType.JSON)

                .body(loginBody)

                .when()

                .post("/users/login")

                .then()

                .extract()

                .response();

        // Print response

        System.out.println(
                "Login Status Code : "
                        + response.getStatusCode()
        );

        System.out.println(
                "Login Response : "
                        + response.getBody().asString()
        );
        allure_api_util.attachResponse(response);

        // Extract token

        token =
                response.jsonPath()
                        .getString("data.token");

        System.out.println(
                "Generated Token : "
                        + token
        );
    }
}