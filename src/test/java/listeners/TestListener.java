package listeners;

import io.qameta.allure.Allure;
import org.testng.IAnnotationTransformer;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import utils.RetryAnalyzer;
import utils.ScreenshotUtil;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestListener implements ITestListener, IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation,          //retry
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {

        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
    @Override
    public void onTestFailure(ITestResult result) {

        String testName =
                result.getMethod().getMethodName();

        System.out.println(
                "TEST FAILED: "
                        + testName
        );

        byte[] screenshot =
                ScreenshotUtil.captureScreenshotBytes();

        if (screenshot != null) {

            Allure.addAttachment(
                    "Failure Screenshot",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );

            System.out.println(
                    "Screenshot attached to Allure."
            );
        }
    }
    @Override
    public void onTestSuccess(ITestResult result) {

        System.out.println(
                "TEST PASSED: "
                        + result.getMethod().getMethodName()
        );
    }
    @Override
    public void onTestSkipped(ITestResult result) {

        System.out.println(
                "TEST SKIPPED: "
                        + result.getMethod().getMethodName()
        );
    }
}