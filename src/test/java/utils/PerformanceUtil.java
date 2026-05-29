package utils;

import drivers.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.JavascriptExecutor;

public class PerformanceUtil {
    public static void measureAndLog(String pageName) {
        JavascriptExecutor js =
                (JavascriptExecutor) DriverManager.getDriver();
        Long navigationTime = (Long) js.executeScript(
                "var t = window.performance.timing;" +
                "return t.loadEventEnd > 0 " +
                "? t.loadEventEnd - t.navigationStart " +
                ": t.domContentLoadedEventEnd - t.navigationStart;"
        );
        Long domReadyTime = (Long) js.executeScript(  //dom time means dom parsed and ready
                "return window.performance.timing.domContentLoadedEventEnd - " +
                "window.performance.timing.navigationStart;"
        );
        String status = navigationTime > 5000 ? " ⚠ SLOW" : " ✓ OK"; //check 5000 as practice site
        String log = pageName +
                " | Navigation: " + navigationTime + "ms" +
                " | DOM Ready: " + domReadyTime + "ms" +
                status;
        System.out.println("[PERF] " + log);
        Allure.addAttachment("UI Timing — " + pageName, "text/plain", log);
    }
}