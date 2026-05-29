package utils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY = 2;
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {

            String failureMessage = result.getThrowable() != null
                    ? result.getThrowable().getMessage()
                    : "";
            boolean isFlaky = isFlakyFailure(failureMessage);
            if (isFlaky) {
                retryCount++;
                System.out.println(
                    "[RETRY] Flaky failure detected — retrying test: "
                    + result.getMethod().getMethodName()
                    + " (Attempt " + retryCount + " of " + MAX_RETRY + ")"
                );
                if (isAdOrIframeFailure(failureMessage)) {  //checks if ads or iframe was reason of failure 
                    System.out.println(
                        "[RETRY] Ad/iframe interference detected — " +
                        "restarting browser for clean retry..."
                    );
                    restartBrowser(); //restarts the browser
                }
                return true;
            }
            System.out.println(
                "[RETRY] Genuine failure — not retrying: "
                + result.getMethod().getMethodName()
                + " | Reason: " + failureMessage
            );
        }
        return false;
    }
    private boolean isFlakyFailure(String message) {

        if (message == null) return false;

        String m = message.toLowerCase();

        return m.contains("timeout")
            || m.contains("stale element")
            || m.contains("element click intercepted")
            || m.contains("no such element")
            || m.contains("connection")
            || m.contains("socket")
            || isAdOrIframeFailure(message); // check if there was an ad
    }
    private boolean isAdOrIframeFailure(String message) {
        if (message == null) return false;
        String m = message.toLowerCase();
        return m.contains("element click intercepted")
            || m.contains("other element would receive the click")
            || m.contains("unable to switch to frame")
            || m.contains("no such frame")
            || m.contains("frame")
            || m.contains("ins.adsbygoogle")   // Google AdSense iframe
            || m.contains("ad-")               // common ad class names
            || m.contains("advertisement");
    }
    private void restartBrowser() {
        try {
            if (drivers.DriverManager.getDriver() != null) {
                drivers.DriverManager.getDriver().quit();
                drivers.DriverManager.unload();
            }
            Thread.sleep(2000);
            io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
            org.openqa.selenium.chrome.ChromeOptions options =
                    new org.openqa.selenium.chrome.ChromeOptions();
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-save-password-bubble");
            options.addArguments("--disable-features=PasswordLeakDetection");
            options.addArguments("--password-store=basic");
            options.addArguments("--disable-extensions");
            options.setExperimentalOption(
                    "prefs",
                    java.util.Map.of(
                            "credentials_enable_service", false,
                            "profile.password_manager_enabled", false,
                            "profile.password_manager_leak_detection", false,
                            "autofill.profile_enabled", false
                    )
            );

            options.setExperimentalOption(
                    "excludeSwitches",
                    java.util.List.of("enable-automation")
            );

            org.openqa.selenium.WebDriver driver =
                    new org.openqa.selenium.chrome.ChromeDriver(options);

            drivers.DriverManager.setDriver(driver);
            driver.manage().window().maximize();
            driver.manage().timeouts()
                    .implicitlyWait(java.time.Duration.ofSeconds(10));

            driver.get(utils.ConfigReader.get("ui.base.url"));

            System.out.println("[RETRY] Browser restarted successfully.");

        } catch (Exception e) {
            System.out.println("[RETRY] Browser restart failed: " + e.getMessage());
        }
    }
}