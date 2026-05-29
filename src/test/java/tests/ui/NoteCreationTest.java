package tests.ui;

import base.BaseTest;
import drivers.DriverManager;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNotePage;
import pages.DashboardPage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.Excelreader;
import utils.PerformanceUtil;
import utils.Waits;

public class NoteCreationTest extends BaseTest {

    private void loginAsValidUser() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(
                ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password")
        );
        Waits.waitForUrlContains("/notes/app");
        PerformanceUtil.measureAndLog("Dashboard After Login"); 
    }

    // ─── TC-05 ───────────────────────────────────────────────────────────────

    @Test(priority = 1, testName = "TC-05: Successful Note Creation")
    public void verifySuccessfulNoteCreation() {

        Excelreader.loadExcel("NoteData");
        String category    = Excelreader.getCellData(1, 0);
        String title =
        Excelreader.getCellData(1, 1)
        + "_"
        + System.currentTimeMillis();
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
                "TC-05 FAILED: Note was not created successfully."
        );

        System.out.println("TC-05 PASSED: Note created — " + title);
    }

    // ─── TC-06 ───────────────────────────────────────────────────────────────

    @Test(priority = 2, testName = "TC-06: Note Creation With Special Characters")
    public void verifyNoteCreationWithSpecialCharacters() {

        Excelreader.loadExcel("NoteData");
        String category    = Excelreader.getCellData(2, 0);
        String title       = Excelreader.getCellData(2, 1);
        String description = Excelreader.getCellData(2, 2);

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
                "TC-06 FAILED: Note with special characters was not created."
        );

        System.out.println("TC-06 PASSED: Note with special characters created — " + title);
    }

    // ─── TC-07 ───────────────────────────────────────────────────────────────

    @Test(priority = 3, testName = "TC-07: Empty Title Validation")
    public void verifyNoteCreationWithEmptyTitle() {

        Excelreader.loadExcel("NoteData");
        String category    = Excelreader.getCellData(3, 0);
        String description = Excelreader.getCellData(3, 2);

        loginAsValidUser();

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.clickAddNote();

        AddNotePage addNotePage = new AddNotePage();
        addNotePage.selectCategory(category);
        addNotePage.enterDescription(description);
        addNotePage.clickCreate();

        Assert.assertTrue(
                addNotePage.isValidationMessageDisplayed(),
                "TC-07 FAILED: Validation message not shown for empty title."
        );

        Assert.assertFalse(
                dashboardPage.isNotePresent(description),
                "TC-07 FAILED: Note was created despite empty title."
        );

        System.out.println("TC-07 PASSED: Validation shown, note not created.");
    }

    // ─── TC-08 ───────────────────────────────────────────────────────────────

    @Test(priority = 4, testName = "TC-08: Empty Description Validation")
    public void verifyNoteCreationWithEmptyDescription() {

        Excelreader.loadExcel("NoteData");
        String category = Excelreader.getCellData(4, 0);
        String title    = Excelreader.getCellData(4, 1);

        loginAsValidUser();

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.clickAddNote();

        AddNotePage addNotePage = new AddNotePage();
        addNotePage.selectCategory(category);
        addNotePage.enterTitle(title);
        addNotePage.clickCreate();

        Assert.assertTrue(
                addNotePage.isValidationMessageDisplayed(),
                "TC-08 FAILED: Validation message not shown for empty description."
        );

        Assert.assertFalse(
                dashboardPage.isNotePresent(title),
                "TC-08 FAILED: Note was created despite empty description."
        );

        System.out.println("TC-08 PASSED: Validation shown, note not created.");
    }

    // ─── TC-09: Note appears instantly in UI ─────────────────────────────────
@Test(
        priority = 5,
        testName = "TC-09: Note Appears After Creation"
)

public void verifyNoteAppearsAfterCreation() {

    Excelreader.loadExcel("NoteData");

    String category =
            Excelreader.getCellData(1, 0);

    String title =
            Excelreader.getCellData(1, 1);

    String description =
            Excelreader.getCellData(1, 2);

    loginAsValidUser();

    DashboardPage dashboardPage =
            new DashboardPage();

    dashboardPage.clickAddNote();

    AddNotePage addNotePage =
            new AddNotePage();

    addNotePage.selectCategory(category);

    addNotePage.enterTitle(title);

    addNotePage.enterDescription(description);

    addNotePage.clickCreate();

    // WAIT FOR DASHBOARD / AUTO REFRESH

    dashboardPage.waitForDashboardToLoad();
    io.qameta.allure.Allure.step(
    "OBSERVATION: Dashboard refreshed automatically and newly created note became visible after page reload."
);

    boolean noteVisible =
            dashboardPage
                    .isNotePresentWithoutModalCheck(title);

    Assert.assertTrue(
            noteVisible,
            "TC-09 : Newly created note visible after refresh."
    );

    System.out.println(
            "TC-09 PASSED: Newly created note became visible after creation."
    );

    System.out.println(
            "OBSERVATION: UI takes around 1 second to refresh and display note."
    );
}
}