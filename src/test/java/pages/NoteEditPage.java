package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import utils.Waits;

public class NoteEditPage {

    private final By descriptionField =
            By.cssSelector("[data-testid='note-description']");
    private final By saveButton =
            By.cssSelector("[data-testid='note-submit']");
    public void clearAndEnterDescription(String description) {
        WebElement field = Waits.waitForVisibility(descriptionField);
        // Clear existing content fully before typing new value
        field.sendKeys(Keys.CONTROL + "a");
        field.sendKeys(Keys.DELETE);
        field.sendKeys(description);
    }

    public void clickSave() {
        Waits.waitForClickable(saveButton).click();
    }
public boolean isNoChangeAlertDisplayed() {     // for tc 24 known defect no validation msg shown / save btn enabled always
    try {
        WebElement alert = Waits.waitForAnyVisible(
                By.cssSelector("[class*='alert']"),
                By.cssSelector("[class*='toast']"),
                By.cssSelector("[class*='error']"),
                By.cssSelector("[class*='warning']")
        );
        return alert != null && alert.isDisplayed();
    } catch (Exception e) {
        return false;}}
public boolean isSaveButtonEnabled() {
    return Waits.waitForVisibility(saveButton)
            .isEnabled();}}