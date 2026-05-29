package tests.e2e;

import base.BaseTest;
import drivers.DriverManager;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.AddNotePage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.Excelreader;
import utils.ScreenshotUtil;
import utils.Waits;

import static io.restassured.RestAssured.given;

public class Hybridtest extends BaseTest {

    private static String apiToken;

    @BeforeClass
public void getApiToken() {

    RestAssured.baseURI = ConfigReader.get("api.base.url");

    String requestBody = "{\n" +
            "\"email\":\"" + ConfigReader.get("valid.email") + "\",\n" +
            "\"password\":\"" + ConfigReader.get("valid.password") + "\"\n" +
            "}";

    Response response = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/users/login")
            .then()
            .extract()
            .response();

    System.out.println("Hybrid Login Status: " + response.statusCode());
    System.out.println("Hybrid Login Response:");
    System.out.println(response.asPrettyString());

    apiToken = response.jsonPath().getString("data.token");

    System.out.println("Hybrid API Token: " + apiToken);
}

    private void loginAsValidUser() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(
                ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password")
        );
        Waits.waitForUrlContains("/notes/app");
    }

    // ─── TC-14 ───────────────────────────────────────────────────────────────

    @Test(priority = 1, testName = "TC-14: UI Created Note Appears In API Response")
    public void verifyUICreatedNoteAppearsInAPI() {

        Excelreader.loadExcel("NoteData");
        String category    = Excelreader.getCellData(1, 0);
        String title       = Excelreader.getCellData(1, 1);
        String description = Excelreader.getCellData(1, 2);

        loginAsValidUser();

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.clickAddNote();

        AddNotePage addNotePage = new AddNotePage();
        addNotePage.selectCategory(category);
        addNotePage.enterTitle(title);
        addNotePage.enterDescription(description);
        addNotePage.clickCreate();

        Assert.assertTrue(
                dashboardPage.isNotePresent(title),
                "TC-14 FAILED: Note not visible in UI"
        );

        System.out.println("TC-14: Note created in UI");

        Response apiResponse = given()
                .header("x-auth-token", apiToken)
                .when()
                .get("/notes")
                .then()
                .statusCode(200)
                .extract()
                .response();

        boolean foundInApi = apiResponse.jsonPath()
                .getList("data.title")
                .stream()
                .anyMatch(t -> t.toString().equalsIgnoreCase(title));

        Assert.assertTrue(foundInApi, "TC-14 FAILED: Note not found in API");
        System.out.println("TC-14 PASSED");
    }

    // ─── TC-15 ───────────────────────────────────────────────────────────────

   @Test(
        priority = 2,
        testName = "TC-15: UI And API Note Details Match"
)

public void verifyUIAndAPIDetailsMatch() {

    // ─── Load Expected Data ─────────────────────────────────────

    Excelreader.loadExcel("NoteData");
    String expectedTitle =
            Excelreader.getCellData(1, 1);
    String expectedDescription =
            Excelreader.getCellData(1, 2);

    // ─── Login ──────────────────────────────────────────────────

    loginAsValidUser();

    DashboardPage dashboardPage =
            new DashboardPage();

    dashboardPage.waitForDashboardToLoad();

    // ─── Read UI Data ───────────────────────────────────────────

    String uiTitle =
            dashboardPage.getNoteTitle(expectedTitle);

    String uiDescription =
            dashboardPage.getNoteDescription(expectedTitle);

    String uiCategory =
            dashboardPage.getNoteCategory(expectedTitle);

    // ─── API Request ────────────────────────────────────────────

    Response apiResponse = given()

            .header("x-auth-token", apiToken)

            .when()

            .get("/notes")

            .then()

            .statusCode(200)

            .extract()

            .response();

    // ─── Find Matching Note ─────────────────────────────────────

    int noteIndex = -1;

    java.util.List<String> apiTitles =
            apiResponse.jsonPath().getList("data.title");

    for (int i = 0; i < apiTitles.size(); i++) {

        if (apiTitles.get(i)
                .equalsIgnoreCase(expectedTitle)) {

            noteIndex = i;
            break;
        }
    }

    Assert.assertTrue(
            noteIndex >= 0,
            "TC-15 FAILED: Note not found in API response"
    );

    // ─── Extract API Data ───────────────────────────────────────

    String apiTitle =
            apiResponse.jsonPath()
                    .getString("data[" + noteIndex + "].title");

    String apiDescription =
            apiResponse.jsonPath()
                    .getString("data[" + noteIndex + "].description");

    String apiCategory =
            apiResponse.jsonPath()
                    .getString("data[" + noteIndex + "].category");

    // ─── Logs ───────────────────────────────────────────────────

    System.out.println(
            "UI Title: " + uiTitle +
            " | API Title: " + apiTitle
    );

    System.out.println(
            "UI Description: " + uiDescription +
            " | API Description: " + apiDescription
    );

    System.out.println(
            "UI Category: " + uiCategory +
            " | API Category: " + apiCategory
    );

    // ─── Assertions ─────────────────────────────────────────────

    Assert.assertEquals(
            uiTitle,
            apiTitle,
            "TC-15 FAILED: Title mismatch"
    );

    Assert.assertEquals(
            uiDescription,
            apiDescription,
            "TC-15 FAILED: Description mismatch"
    );

  

    System.out.println(
            "TC-15 PASSED: UI and API data match."
    );
}
    // ─── TC-18 ───────────────────────────────────────────────────────────────

   @Test(priority = 3, testName = "TC-18: API Deleted Note Disappears From UI")
public void verifyAPIDeletedNoteDisappearsFromUI() {

    // ─────────────────────────────────────────────────────────────
    // STEP 1 : Create note via API
    // ─────────────────────────────────────────────────────────────

    Response createResponse = given()
            .header("x-auth-token", apiToken)
            .contentType(ContentType.JSON)
            .body("{\n" +
                    "\"title\":\"TC-18 Delete Test Note\",\n" +
                    "\"description\":\"Created for TC-18\",\n" +
                    "\"category\":\"Home\"\n" +
                    "}")
            .when()
            .post("/notes")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String noteId =
            createResponse.jsonPath().getString("data.id");

    String noteTitle =
            createResponse.jsonPath().getString("data.title");

    System.out.println(
            "TC-18: Note created via API — ID: "
                    + noteId
    );

    // ─────────────────────────────────────────────────────────────
    // STEP 2 : Login and verify note visible in UI
    // ─────────────────────────────────────────────────────────────

    loginAsValidUser();

    DashboardPage dashboardPage =
            new DashboardPage();

    dashboardPage.waitForDashboardToLoad();

    Assert.assertTrue(
            dashboardPage.isNotePresentWithoutModalCheck(noteTitle),
            "TC-18 FAILED: Note not visible before deletion"
    );

    // ─────────────────────────────────────────────────────────────
    // STEP 3 : Delete note via API
    // ─────────────────────────────────────────────────────────────

    given()
            .header("x-auth-token", apiToken)
            .when()
            .delete("/notes/" + noteId)
            .then()
            .statusCode(200);

    System.out.println(
            "TC-18: Note deleted via API"
    );

    // ─────────────────────────────────────────────────────────────
    // STEP 4 : CHECK UI BEFORE REFRESH
    // ─────────────────────────────────────────────────────────────

    boolean visibleBeforeRefresh =
            dashboardPage.isNotePresentWithoutModalCheck(noteTitle);

    ScreenshotUtil.attachScreenshot(
            "TC18_Before_Refresh_Note_Still_Visible"
    );

    System.out.println(
            "OBSERVATION BEFORE REFRESH: "
                    + "Deleted note still visible in UI = "
                    + visibleBeforeRefresh
    );

    // ALLURE LOG

    Allure.step(
            "Observation: UI does not automatically sync "
                    + "after API deletion. Manual refresh required."
    );

    // ─────────────────────────────────────────────────────────────
    // STEP 5 : Refresh UI manually
    // ─────────────────────────────────────────────────────────────

    DriverManager.getDriver()
            .navigate()
            .refresh();

    dashboardPage.waitForDashboardToLoad();

    ScreenshotUtil.attachScreenshot(
            "TC18_After_Refresh_Note_Removed"
    );

    // ─────────────────────────────────────────────────────────────
    // STEP 6 : Validate note removed after refresh
    // ─────────────────────────────────────────────────────────────

    boolean stillPresent =
            dashboardPage.isNotePresentWithoutModalCheck(noteTitle);

    Assert.assertFalse(
            stillPresent,
            "TC-18 FAILED: Note still visible after API deletion and refresh"
    );

    System.out.println(
            "TC-18 PASSED"
    );

    System.out.println(
            "OBSERVATION: UI requires refresh "
                    + "to reflect API deletion."
    );
}
    // ─── TC-19 ───────────────────────────────────────────────────────────────

    @Test(priority = 4, testName = "TC-19: Deleted Note Cannot Be Accessed Again")
    public void verifyDeletedNoteCannotBeAccessed() {

Response createResponse = given()
        .header("x-auth-token", apiToken)
        .contentType(ContentType.JSON)
        .body("{\n" +
                "\"title\":\"TC-19 Access Test Note\",\n" +
                "\"description\":\"Created for TC-19\",\n" +
                "\"category\":\"Work\"\n" +
                "}")
        .when()
        .post("/notes")
        .then()
        .statusCode(200)
        .extract()
        .response();

        String noteId    = createResponse.jsonPath().getString("data.id");
        String noteTitle = createResponse.jsonPath().getString("data.title");

        given()
                .header("x-auth-token", apiToken)
                .when()
                .delete("/notes/" + noteId)
                .then()
                .statusCode(200);

        Response getResponse = given()
                .header("x-auth-token", apiToken)
                .when()
                .get("/notes/" + noteId)
                .then()
                .statusCode(404)
                .extract()
                .response();

        Assert.assertFalse(
                getResponse.jsonPath().getBoolean("success"),
                "TC-19 FAILED: Deleted note still accessible via API"
        );

        loginAsValidUser();

        DashboardPage dashboardPage = new DashboardPage();
        DriverManager.getDriver().navigate().refresh();
        dashboardPage.waitForDashboardToLoad();

        Assert.assertFalse(
                dashboardPage.isNotePresentWithoutModalCheck(noteTitle),
                "TC-19 FAILED: Deleted note still visible in UI"
        );

        System.out.println("TC-19 PASSED");
    }
}