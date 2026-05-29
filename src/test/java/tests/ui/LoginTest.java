package tests.ui;

import base.BaseTest;

import drivers.DriverManager;

import org.testng.Assert;
import org.testng.annotations.Test;

import pages.LoginPage;
import pages.DashboardPage;

import utils.TestDataExtract;

public class LoginTest extends BaseTest {

    @Test(
            dataProvider = "loginData",
            dataProviderClass = TestDataExtract.class
    )

    public void verifyLoginFunctionality(
            String email,
            String password,
            String expected
    ) {
        System.out.println("Executing Test");

        System.out.println("Email : " + email);

        LoginPage loginPage =
                new LoginPage();

        loginPage.login(email, password);

        String currentUrl =
                DriverManager.getDriver()
                        .getCurrentUrl();

        // Positive Test

        if(expected.equalsIgnoreCase("pass")) {

            Assert.assertTrue(
                    currentUrl.contains("/notes/app")
            );

            System.out.println(
                    "Login Successful"
            );

            DashboardPage dashboardPage =
                    new DashboardPage();

            dashboardPage.logout();

            System.out.println(
                    "Logged out successfully"
            );
        }

        // Negative Test

        else {

            Assert.assertTrue(
                    currentUrl.contains("/login")
            );

            System.out.println(
                    "Negative login validated"
            );
        }
    }
}