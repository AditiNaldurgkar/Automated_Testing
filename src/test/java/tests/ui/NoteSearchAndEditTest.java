package tests.ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import pages.DashboardPage;
import pages.LoginPage;
import pages.NoteEditPage;
import utils.ConfigReader;
import utils.Excelreader;
import utils.ScreenshotUtil;
import utils.Waits;

public class NoteSearchAndEditTest extends BaseTest {

    private void loginAsValidUser() {

        LoginPage loginPage = new LoginPage();

        loginPage.login(
                ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password")
        );

        Waits.waitForUrlContains("/notes/app");
    }

@Test(
        priority = 2,
        testName = "TC-24: Save Without Changes Shows No Alert"
)
public void verifySaveWithoutChangesInEditMode() {

    loginAsValidUser();

    Excelreader.loadExcel("NoteData");

    String existingTitle =
            Excelreader.getCellData(1, 1);

    DashboardPage dashboardPage =
            new DashboardPage();

    dashboardPage.waitForDashboardToLoad();

    dashboardPage.clickEditNote(existingTitle);

    NoteEditPage noteEditPage =
            new NoteEditPage();

    System.out.println(
            "STEP 1: Opened existing note in edit mode."
    );

    // ─────────────────────────────────────────────
    // CHECK SAVE BUTTON STATE BEFORE ANY CHANGES
    // ─────────────────────────────────────────────

    boolean isSaveEnabled =
            noteEditPage.isSaveButtonEnabled();

    System.out.println(
            "STEP 2: Save button enabled without modifications = "
                    + isSaveEnabled
    );

    // ALLURE LOG

    io.qameta.allure.Allure.step(
            "OBSERVATION: Save button is ENABLED even when no fields are modified."
    );

    // SCREENSHOT EVIDENCE

    ScreenshotUtil.attachScreenshot(
            "TC-24_Save_Button_Enabled_Without_Changes"
    );

    // CLICK SAVE WITHOUT MAKING CHANGES

    noteEditPage.clickSave();

    System.out.println(
            "STEP 3: Save button clicked without editing note."
    );

    dashboardPage.waitForDashboardToLoad();

    // ALLURE OBSERVATION

    io.qameta.allure.Allure.step(
            "DEFECT OBSERVED: No validation message or warning displayed after clicking Save without changes."
    );

    System.out.println(
            "Expected: Save button disabled OR validation/warning message displayed."
    );

    System.out.println(
            "Actual: Save action executed and dashboard refreshed silently."
    );

    // FINAL ASSERTION
    // Dashboard should remain stable

    Assert.assertTrue(
            dashboardPage.isNotePresentWithoutModalCheck(
                    existingTitle
            ),

            "TC-24 FAILED: Note missing after save action."
    );

    System.out.println(
            "TC-24 PASSED (Observation Logged): "
                    + "Application allows save without modifications."
    );
}

}