package tests.ui;

import base.BaseTest;
import drivers.DriverManager;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddNotePage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.NoteEditPage;
import utils.ConfigReader;
import utils.Excelreader;
import utils.Waits;

public class NoteEditTest extends BaseTest {

    // ─── Login helper ────────────────────────────────────────────────────────

    private void loginAsValidUser() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(
                ConfigReader.get("valid.email"),
                ConfigReader.get("valid.password")
        );
        Waits.waitForUrlContains("/notes/app");
    }

    // ─── TC-09: Verify newly created note appears instantly in UI ────────────
    //MANUALLY I KEPT UNDER OBSERVATION BCOZ IT FIRST REFRESHES AND THEN IT IS SHOWN INSTANLTY

    @Test(priority = 1, testName = "TC-09: Note Appears Instantly After Creation")
    public void verifyNoteAppearsInstantlyInUI() {

        loginAsValidUser();

        Excelreader.loadExcel("NoteData");
        String category    = Excelreader.getCellData(1, 0);
        String title       = Excelreader.getCellData(1, 1);
        String description = Excelreader.getCellData(1, 2);

        DashboardPage dashboardPage = new DashboardPage();
        dashboardPage.clickAddNote();

        AddNotePage addNotePage = new AddNotePage();
        addNotePage.selectCategory(category);
        addNotePage.enterTitle(title);
        addNotePage.enterDescription(description);
        addNotePage.clickCreate();
        //FR-03 INSTANT APPEAR 
        Assert.assertTrue(
                dashboardPage.isNotePresent(title),
                "TC-09 FAILED: Note did not appear instantly in UI after creation."
        );

        System.out.println("TC-09 PASSED: Note appeared instantly — " + title);
    }

    // ─── TC-10: Verify note edit functionality ───────────────────────────────

    @Test(priority = 2, testName = "TC-10: Note Edit Functionality")
    public void verifyNoteEditFunctionality() {

        loginAsValidUser();

        // Load the title to search for + updated description from Excel
        Excelreader.loadExcel("NoteData");
        String existingTitle      = Excelreader.getCellData(1, 1); // same note created in TC-09
        String updatedDescription = Excelreader.getCellData(5, 2); // row 5 = edit data

        DashboardPage dashboardPage = new DashboardPage();

        // Wait for dashboard to fully load before searching for the note
        dashboardPage.waitForDashboardToLoad();

        dashboardPage.clickEditNote(existingTitle);

        NoteEditPage noteEditPage = new NoteEditPage();
        noteEditPage.clearAndEnterDescription(updatedDescription);
        noteEditPage.clickSave();

        // Wait for save to complete and dashboard to reload
        dashboardPage.waitForDashboardToLoad();

        Assert.assertTrue(
                dashboardPage.isNotePresentWithoutModalCheck(existingTitle),
                "TC-10 FAILED: Note not found after editing."
        );

        Assert.assertTrue(
                dashboardPage.isDescriptionPresent(updatedDescription),
                "TC-10 FAILED: Updated description not reflected in UI."
        );

        System.out.println("TC-10 PASSED: Note edited successfully — " + updatedDescription);
    }

    // ─── TC-11: Verify edited note persists after page refresh ───────────────

    @Test(priority = 3, testName = "TC-11: Edited Note Persists After Refresh")
    public void verifyEditedNotePersistsAfterRefresh() {

        loginAsValidUser();

        Excelreader.loadExcel("NoteData");
        String existingTitle      = Excelreader.getCellData(1, 1);
        String updatedDescription = Excelreader.getCellData(5, 2);

        DashboardPage dashboardPage = new DashboardPage();

        // Wait for dashboard to fully load before searching for the note
        dashboardPage.waitForDashboardToLoad();

        dashboardPage.clickEditNote(existingTitle);

        NoteEditPage noteEditPage = new NoteEditPage();
        noteEditPage.clearAndEnterDescription(updatedDescription);
        noteEditPage.clickSave();

        // Wait for save, then refresh
        dashboardPage.waitForDashboardToLoad();
       // Refresh the page
DriverManager.getDriver().navigate().refresh();

// Wait for dashboard to FULLY reload — descriptions need time to render
dashboardPage.waitForDashboardToLoad();

// Add an extra wait specifically for descriptions to be visible
Waits.waitForVisibility(By.cssSelector("[data-testid='note-card-description']"));

Assert.assertTrue(
        dashboardPage.isNotePresentWithoutModalCheck(existingTitle),
        "TC-11 FAILED: Note not found after page refresh."
);

Assert.assertTrue(
        dashboardPage.isDescriptionPresent(updatedDescription),
        "TC-11 FAILED: Updated description did not persist after refresh."
);

        System.out.println("TC-11 PASSED: Edited note persisted after refresh — " + updatedDescription);
    }
}