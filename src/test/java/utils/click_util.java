package utils;
import drivers.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class click_util { 
    public static void safeClick(By locator) {

        WebDriver driver = DriverManager.getDriver();

        try {
            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement element =
                    wait.until(
                            ExpectedConditions.elementToBeClickable(locator)
                    );
            ((JavascriptExecutor) driver) //js scroll
                    .executeScript(
                            "arguments[0].scrollIntoView(true);",
                            element
                    );
            try {
                element.click(); //normal click
            } catch (Exception e) {
                System.out.println(
                        "Normal click failed. Using JS click."
                );
                ((JavascriptExecutor) driver)
                        .executeScript(
                                "arguments[0].click();",  //js click
                                element
                        );
            }
        } catch (Exception ex) {
            System.out.println(
                    "safeClick failed for locator: " + locator
            );
            throw ex;
        }
    }
}