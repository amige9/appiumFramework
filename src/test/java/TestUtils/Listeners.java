package TestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.appium.java_client.AppiumDriver;
import utils.AppiumUtils;

public class Listeners extends AppiumUtils implements ITestListener {
	private static ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
	ExtentTest test;
	ExtentReports extent = ExtentReporterNG.getReporterObject();
    
	AppiumDriver driver;


	/**
	 * Get current thread's ExtentTest
	 */
	public static ExtentTest getTest() {
		return testThreadLocal.get();
	}


	@Override
	public void onTestStart(ITestResult result) {
		test = extent.createTest(result.getMethod().getMethodName());
        testThreadLocal.set(test);

	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.log(Status.PASS, "Test Passed");
        test = getTest();
        if (test != null) {
            test.log(Status.PASS, "Test Passed");
        }
			System.out.println("✅ Test passed: " + result.getMethod().getMethodName());
		
	}

//	@Override
//	public void onTestFailure(ITestResult result) {
//		test.fail(result.getThrowable());
//		
//		try {
//			driver = (AppiumDriver) result.getTestClass().getRealClass().getField("driver")
//					.get(result.getInstance());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			test.addScreenCaptureFromPath(getScreenshotPath(result.getMethod().getMethodName(), driver));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	@Override
	public void onTestFailure(ITestResult result) {
		test = getTest();
		if (test != null) {
			test.fail(result.getThrowable());

			// Screenshot capture
			try {
				driver = (AppiumDriver) result.getTestClass().getRealClass().getField("driver")
						.get(result.getInstance());

				if (driver != null) {
					String screenshotPath = getScreenshotPath(result.getMethod().getMethodName(), driver);
					if (screenshotPath != null) {
						test.addScreenCaptureFromPath(screenshotPath);
						System.out.println("📸 Screenshot captured for: " + result.getMethod().getMethodName());
					}
				}
			} catch (Exception e) {
				System.out.println("⚠️ Screenshot failed: " + e.getMessage());
				test.log(Status.WARNING, "Screenshot capture failed: " + e.getMessage());
			}

			System.out.println("❌ Test failed: " + result.getMethod().getMethodName());
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		test = getTest();
		if (test != null) {
			test.log(Status.SKIP, "Test Skipped: " + result.getThrowable().getMessage());
			System.out.println("⏭️ Test skipped: " + result.getMethod().getMethodName());
		}
	}

	@Override
	public void onFinish(ITestContext context) {
        // ✅ Update execution summary only once at the end
        int totalPassed = context.getPassedTests().size();
        int totalFailed = context.getFailedTests().size();
        int totalSkipped = context.getSkippedTests().size();
        long totalTime = context.getEndDate().getTime() - context.getStartDate().getTime();
        
        ExtentReporterNG.updateExecutionSummary(totalPassed, totalFailed, totalSkipped, totalTime);
        
        // Flush the report
        extent.flush();
        
        // Clean up ThreadLocal
        testThreadLocal.remove();

		System.out.println("📊 Test execution completed. Report generated at: " + System.getProperty("user.dir")
				+ "/reports/index.html");
	}

}
