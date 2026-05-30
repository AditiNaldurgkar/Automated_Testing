package pages;
import drivers.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.SelfHealingDriver;
import utils.Waits;
import utils.click_util;

import java.time.Duration;
import java.util.List;

public class DashboardPage {

    private final By logoutButton =
            By.cssSelector("[data-testid='logout']");

    private final By addNoteButton =
            By.cssSelector("[data-testid='add-new-note']");

    private final By noteTitles =
            By.cssSelector("[data-testid='note-card-title']");

    private final By noteDescriptions =
            By.cssSelector("[data-testid='note-card-description']");

    private final By submitButton =
            By.cssSelector("[data-testid='note-submit']");
    private WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    //methods for dashboard pg
    public void logout() {
        Waits.waitForClickable(logoutButton);
        click_util.safeClick(logoutButton);
    }

    public void clickAddNote() {
        Waits.waitForClickable(addNoteButton);
        SelfHealingDriver.click(
                By.cssSelector("[data-testid='add-new-note']"),
                By.cssSelector("[data-testid='add-note']"),
                By.cssSelector("button[class*='add']"),
                By.xpath("//button[contains(text(),'Add')]")
        );
        click_util.safeClick(addNoteButton);  //using this for ads/iframes
        Waits.waitForVisibility(
                By.cssSelector("[data-testid='note-title']")
        );
    }
  public void waitForDashboardToLoad() {
    try {
        Waits.waitForVisibility(addNoteButton);
        new WebDriverWait(getDriver(), Duration.ofSeconds(10))
            .until(ExpectedConditions.refreshed(
                ExpectedConditions.presenceOfAllElementsLocatedBy(noteTitles)
            ));

    } catch (Exception e) {
    
    }
}
    public void clickEditNote(String noteTitle) {

        Waits.waitForPresenceOfAll(noteTitles);
        List<WebElement> titleElements =
                getDriver().findElements(noteTitles);
        for (WebElement titleEl : titleElements) {
            if (titleEl.getText().trim()
                    .equalsIgnoreCase(noteTitle.trim())) {
                WebElement card = titleEl.findElement(
                        By.xpath(
                                "./ancestor::div[contains(@class,'card') " +
                                "or contains(@class,'note') " +
                                "or @data-testid][1]"
                        )
                );
                WebElement editBtn = card.findElement(

                        By.cssSelector("[data-testid='note-edit']")
                );

                JavascriptExecutor js =
                        (JavascriptExecutor) getDriver();

                js.executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});",
                        editBtn
                );

                try {

                    editBtn.click();

                } catch (Exception e) {

                    js.executeScript(
                            "arguments[0].click();",
                            editBtn
                    );
                }

                Waits.waitForVisibility(
                        By.cssSelector("[data-testid='note-description']")
                );
                return;
            }
        }

        throw new RuntimeException(
                "Note not found on dashboard: " + noteTitle
        );
    }
    public boolean isNotePresent(String noteTitle) { //check note present or not
        try {
            Waits.waitForInvisibility(submitButton);
            Waits.waitForPresenceOfAll(noteTitles);
            return isNotePresentWithoutModalCheck(noteTitle);

        } catch (Exception e) {

            return false;
        }
    }

    public boolean isNotePresentWithoutModalCheck(String noteTitle) {

        try {

            Waits.waitForPresenceOfAll(noteTitles);

            List<WebElement> titles =
                    getDriver().findElements(noteTitles);

            for (WebElement title : titles) {

                if (title.getText().trim()
                        .equalsIgnoreCase(noteTitle.trim())) {

                    return true;
                }
            }

        } catch (Exception e) {

            // No notes visible
        }

        return false;
    }
 public boolean isDescriptionPresent(String descriptionText) {
    try {
        // ✅ Wait until ANY description element contains the exact updated text
        // This is the missing wait — your old method never waited for new content
        new WebDriverWait(getDriver(), Duration.ofSeconds(10))
            .until(driver -> {
                List<WebElement> descs = driver.findElements(noteDescriptions);
                return descs.stream()
                    .anyMatch(el -> el.getText().trim()
                        .contains(descriptionText.trim()));
            });

        return true;

    } catch (TimeoutException e) {

        return false;

    } catch (Exception e) {
        return false;
    }
}
    public int getNoteCount() {
        try {

            Waits.waitForPresenceOfAll(noteTitles);

            return getDriver()
                    .findElements(noteTitles)
                    .size();

        } catch (Exception e) {

            return 0;
        }
    }
    // Helper Methods 
    public String getNoteTitle(String noteTitle) {

        try {

            Waits.waitForPresenceOfAll(noteTitles);

            List<WebElement> titles =
                    getDriver().findElements(noteTitles);

            for (WebElement title : titles) {

                if (title.getText().trim()
                        .equalsIgnoreCase(noteTitle.trim())) {

                    return title.getText().trim();
                }
            }

        } catch (Exception e) {
        }

        return "";
    }

    public String getNoteDescription(String noteTitle) {

        try {

            Waits.waitForPresenceOfAll(noteTitles);

            List<WebElement> titles =
                    getDriver().findElements(noteTitles);

            for (WebElement title : titles) {

                if (title.getText().trim()
                        .equalsIgnoreCase(noteTitle.trim())) {

                    WebElement card = title.findElement(

                            By.xpath(
                                    "./ancestor::div[contains(@class,'card') " +
                                    "or @data-testid][1]"
                            )
                    );

                    return card.findElement(noteDescriptions)
                            .getText()
                            .trim();
                }
            }

        } catch (Exception e) {
        }

        return "";
    }

    public String getNoteCategory(String noteTitle) {

        try {

            Waits.waitForPresenceOfAll(noteTitles);

            List<WebElement> titles =
                    getDriver().findElements(noteTitles);

            for (WebElement title : titles) {

                if (title.getText().trim()
                        .equalsIgnoreCase(noteTitle.trim())) {

                    WebElement card = title.findElement(

                            By.xpath(
                                    "./ancestor::div[contains(@class,'card') " +
                                    "or @data-testid][1]"
                            )
                    );

                    return card.findElement(
                                    By.cssSelector("[data-testid='note-category']")
                            )
                            .getText()
                            .trim();
                }
            }

        } catch (Exception e) {
        }

        return "";
    }
}