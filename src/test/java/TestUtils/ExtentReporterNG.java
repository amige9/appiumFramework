package TestUtils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//public class ExtentReporterNG {
//	public static ExtentReports extent;
//
//	
//	public static ExtentReports getReporterObject() {
//		String path= System.getProperty("user.dir")+ "/reports/index.html";
//		ExtentSparkReporter reporter = new ExtentSparkReporter(path);
//		reporter.config().setReportName("Automation Report");
//		reporter.config().setDocumentTitle("Test Result");
//
//		extent = new ExtentReports();
//		extent.attachReporter(reporter);
//		extent.setSystemInfo("Tester", "Olamide");
//        extent.setSystemInfo("Environment", "Test");
//        extent.setSystemInfo("Platform", System.getProperty("os.name"));
//        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
//		return extent;
//	}
//
//}



public class ExtentReporterNG {
    private static ExtentReports extent;
    private static String reportPath;

    public static synchronized ExtentReports getReporterObject() {
        if (extent == null) {
            createExtentReports();
        }
        return extent;
    }

    private static void createExtentReports() {
        // Create reports directory
        String reportsDir = System.getProperty("user.dir") + "/reports";
        File reportsDirFile = new File(reportsDir);
        if (!reportsDirFile.exists()) {
            reportsDirFile.mkdirs();
        }

        // Dynamic report name with timestamp
        String timestamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        reportPath = reportsDir + "/TestReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        
        // Configure report
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setReportName("Mobile Automation Test Report");
        sparkReporter.config().setDocumentTitle("Appium Test Execution Results");
        sparkReporter.config().setTimelineEnabled(true);
        sparkReporter.config().setEncoding("UTF-8");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        
        // ✅ Set ALL system information here - only once
        setAllSystemInformation();
        
        System.out.println("📊 ExtentReports initialized: " + reportPath);
    }

    private static void setAllSystemInformation() {
        // Basic Information
        extent.setSystemInfo("👤 Tester", "Olamide");
        extent.setSystemInfo("🌍 Environment", getEnvironment());
        extent.setSystemInfo("🖥️ Platform", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        extent.setSystemInfo("☕ Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("🏗️ OS Architecture", System.getProperty("os.arch"));
        extent.setSystemInfo("👤 User", System.getProperty("user.name"));
        extent.setSystemInfo("📁 Working Directory", System.getProperty("user.dir"));
        extent.setSystemInfo("🕐 Execution Time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        // Mobile Testing Information
        extent.setSystemInfo("📱 Android SDK", getAndroidSDKPath());
        extent.setSystemInfo("🚀 Appium Server", "http://127.0.0.1:4723");
        extent.setSystemInfo("🔧 Test Framework", "TestNG + Appium");
        
        // Runtime Information
        Runtime runtime = Runtime.getRuntime();
        extent.setSystemInfo("💾 Total Memory", (runtime.totalMemory() / 1024 / 1024) + " MB");
        extent.setSystemInfo("🆓 Free Memory", (runtime.freeMemory() / 1024 / 1024) + " MB");
        extent.setSystemInfo("🖱️ Available Processors", String.valueOf(runtime.availableProcessors()));
    }

    // ✅ Method to update final execution summary (called only once at the end)
    public static synchronized void updateExecutionSummary(int passed, int failed, int skipped, long totalTimeMs) {
        if (extent != null) {
            extent.setSystemInfo("✅ Passed Tests", String.valueOf(passed));
            extent.setSystemInfo("❌ Failed Tests", String.valueOf(failed));
            extent.setSystemInfo("⏭️ Skipped Tests", String.valueOf(skipped));
            extent.setSystemInfo("⏱️ Total Execution Time", (totalTimeMs / 1000) + " seconds");
        }
    }

    private static String getEnvironment() {
        String env = System.getProperty("test.env");
        if (env != null) {
            return env.toUpperCase();
        }
        return "LOCAL";
    }

    private static String getAndroidSDKPath() {
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null) {
            return androidHome;
        }
        androidHome = System.getenv("ANDROID_SDK_ROOT");
        if (androidHome != null) {
            return androidHome;
        }
        return "Not Found";
    }

    public static String getReportPath() {
        return reportPath;
    }

    public static synchronized void finalizeReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("📊 Final report generated: " + reportPath);
        }
    }
}
