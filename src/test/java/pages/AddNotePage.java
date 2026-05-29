package pages;
import drivers.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utils.Waits;
import utils.click_util;

public class AddNotePage {
    private final By categoryDropdown =
            By.cssSelector("[data-testid='note-category']");
    private final By titleField =
            By.cssSelector("[data-testid='note-title']");
    private final By descriptionField =
            By.cssSelector("[data-testid='note-description']");
    private final By createButton =
            By.cssSelector("[data-testid='note-submit']");

    private final By validationMsg1 =
            By.xpath("//div[text()='Title is required']");

    private final By validationMsg2 =
            By.xpath("//div[text()='Description is required']");

    private final By validationMsg3 =
            By.cssSelector("[class*='alert']");

    private final By validationMsg4 =
            By.cssSelector("[class*='toast']");

    private WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    public void selectCategory(String category) {
        WebElement dropdown = Waits.waitForVisibility(categoryDropdown);
        new Select(dropdown).selectByVisibleText(category);
    }

    public void enterTitle(String title) {
        Waits.waitForVisibility(titleField).sendKeys(title);
    }

    public void enterDescription(String description) {
        Waits.waitForVisibility(descriptionField).sendKeys(description);
    }

    public void clickCreate() {
        Waits.waitForClickable(createButton);
        click_util.safeClick(createButton);
    }
    public boolean isValidationMessageDisplayed() {
        try {
            WebElement msg = Waits.waitForAnyVisible(
                    validationMsg1,
                    validationMsg2,
                    validationMsg3,
                    validationMsg4
            );
            return msg != null && msg.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}