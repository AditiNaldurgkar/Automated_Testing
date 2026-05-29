package utils;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
public class allure_api_util {
    public static void attachResponse(Response response) {
        Allure.addAttachment(
                "API Response",
                "application/json",
                response.asPrettyString()
        );
    }
} 