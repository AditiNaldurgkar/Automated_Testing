package utils;

import drivers.DriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Waits {
    private static WebDriverWait getWait() {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(10)
        );
    }
    private static FluentWait<org.openqa.selenium.WebDriver> getFluentWait(int timeoutSeconds) { //fluent wait
        return new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);
    }

    public static WebElement waitForVisibility(By locator) {
        return getWait().until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }
    public static WebElement waitForClickable(By locator) {
        return getWait().until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }
    public static WebElement waitForPresence(By locator) {
        return getWait().until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );
    }
    public static boolean waitForUrlContains(String text) {
        return getWait().until(
                ExpectedConditions.urlContains(text)
        );
    }
    public static boolean waitForInvisibility(By locator) {
        return getWait().until(
                ExpectedConditions.invisibilityOfElementLocated(locator)
        );
    }
    public static List<WebElement> waitForPresenceOfAll(By locator) {
        return getWait().until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator)
        );
    }
    public static boolean waitForElementCountMoreThan(By locator, int count) {
        return getFluentWait(15).until(driver ->
                driver.findElements(locator).size() > count
        );
    }
    public static WebElement waitForAnyVisible(By... locators) {
        return getFluentWait(10).until(driver -> {
            for (By locator : locators) {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    return elements.get(0);
                }
            }
            return null;
        });
    }
    public static boolean waitForTextOnPage(String text) {
        return getFluentWait(15).until(driver ->
                driver.getPageSource().contains(text)
        );
    }
    public static void jsClick(By locator) {

        WebElement element = waitForClickable(locator);

        org.openqa.selenium.JavascriptExecutor js =
                (org.openqa.selenium.JavascriptExecutor)
                        DriverManager.getDriver();

        js.executeScript("arguments[0].scrollIntoView(true);", element);
        js.executeScript("arguments[0].click();", element);
    }
public static List<WebElement> waitForDescriptionsVisible(By locator) { 
    return getFluentWait(15).until(driver -> {
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
            return elements;
        }
        return null;
    });
}
public static void clickUsingJS(WebElement element) {

    JavascriptExecutor js =
            (JavascriptExecutor)
                    DriverManager.getDriver();

    js.executeScript(
            "arguments[0].click();",
            element
    );
}
}