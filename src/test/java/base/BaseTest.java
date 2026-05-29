package base;
import drivers.DriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import utils.PerformanceUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BaseTest {

    @BeforeMethod
    public void setup() {
        if (DriverManager.getDriver() != null) {
            try {
                DriverManager.getDriver().quit();
            } catch (Exception ignored) {
            }
            DriverManager.unload();
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
 
        // Browser settings to block or handle ads popups pasword alerts etc
        options.addArguments("--incognito");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordLeakDetection");
        options.addArguments("--password-store=basic");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--guest");
        options.setExperimentalOption(
                "prefs",
                Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false,
                        "profile.password_manager_leak_detection", false,
                        "autofill.profile_enabled", false,
                        "profile.default_content_setting_values.notifications", 2,
                        "profile.default_content_setting_values.popups", 0
                )
        );
        options.setExperimentalOption(
                "excludeSwitches",
                List.of("enable-automation")
        );

        WebDriver driver = new ChromeDriver(options);

        DriverManager.setDriver(driver);

        driver.manage().window().maximize();

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://practice.expandtesting.com/notes/app/login");
        PerformanceUtil.measureAndLog("Login Page");
    }

    @AfterMethod
    public void tearDown() {

        try {
            if (DriverManager.getDriver() != null) {
                DriverManager.getDriver().quit();
            }
        } catch (Exception ignored) {
        } finally {
            DriverManager.unload();
        }
    }
}