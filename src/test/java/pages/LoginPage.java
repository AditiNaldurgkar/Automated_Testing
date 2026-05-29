package pages;

import drivers.DriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utils.Waits;

public class LoginPage {
    private By emailField =
            By.cssSelector("[data-testid='login-email']");

    private By passwordField =
            By.cssSelector("[data-testid='login-password']");

    private By loginButton =
            By.cssSelector("[data-testid='login-submit']");

    private WebDriver getDriver() {

        return DriverManager.getDriver();
    }

    public void enterEmail(String email) {

        Waits.waitForVisibility(emailField);

        getDriver()
                .findElement(emailField)
                .sendKeys(email);
    }

    public void enterPassword(String password) {

        Waits.waitForVisibility(passwordField);

        getDriver()
                .findElement(passwordField)
                .sendKeys(password);

        System.out.println("entered password");
    }

    public void clickLoginButton() {

        Waits.jsClick(loginButton);
    }

    public void login(String email, String password) {

        enterEmail(email);

        enterPassword(password);

        clickLoginButton();
    }
}