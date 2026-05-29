package utils;

import drivers.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SelfHealingDriver {
    public static WebElement findElement(By primary, By... fallbacks) { //try to use primary locator 
        try {
            WebElement element = getWait().until(
                    ExpectedConditions.visibilityOfElementLocated(primary)
            );
            System.out.println("[SELF-HEAL] Primary locator worked: " + primary);
            return element;

        } catch (Exception primaryException) {

            System.out.println("[SELF-HEAL] Primary locator failed: " + primary
                    + " — trying fallbacks...");
            for (int i = 0; i < fallbacks.length; i++) {
                try {
                    WebElement element = getWait().until(
                            ExpectedConditions.visibilityOfElementLocated(fallbacks[i])
                    );
                    System.out.println("[SELF-HEAL] Fallback " + (i + 1)
                            + " worked: " + fallbacks[i]
                            + " — update primary locator to this.");
                    return element;

                } catch (Exception fallbackException) {
                    System.out.println("[SELF-HEAL] Fallback " + (i + 1)
                            + " also failed: " + fallbacks[i]);
                }
            }
        }

        throw new RuntimeException(
                "[SELF-HEAL] All locators failed for element. " +
                "Primary: " + primary
        );
    }
    public static void click(By primary, By... fallbacks) {
        WebElement element = findElement(primary, fallbacks);
        element.click();
    }
    private static WebDriverWait getWait() {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(10)
        );
    }
}