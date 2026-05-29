package utils;
import drivers.DriverManager;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtil {
    public static byte[] captureScreenshotBytes() {
        WebDriver driver =
                DriverManager.getDriver();
        if (driver == null) {

            System.out.println(
                    "Driver is null. Cannot capture screenshot."
            );
            return null;
        }
        return ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
    }
    public static void attachScreenshot(String label) {
    WebDriver driver = DriverManager.getDriver();
    byte[] bytes =
            ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);
    Allure.addAttachment(
            label,
            "image/png",
            new ByteArrayInputStream(bytes),
            ".png"
    );
}
}