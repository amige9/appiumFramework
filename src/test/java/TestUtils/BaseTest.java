package TestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.common.collect.ImmutableMap;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import utils.AppiumUtils;

public class BaseTest extends AppiumUtils{
	
	public AndroidDriver driver;
	public AppiumDriverLocalService service;
	public ExtentReports extent;
	
	
//	public BaseTest(AndroidDriver driver) 
//	{
//		super(driver);
//		this.driver = driver;
//	}
	
//	@BeforeTest
//	public void reportConfig() {
//		String path= System.getProperty("user.dir")+ "\\reports\\index.html";
//		ExtentSparkReporter reporter = new ExtentSparkReporter(path);
//		reporter.config().setReportName("Automation Report");
//		reporter.config().setDocumentTitle("Test Result");
//
//		extent = new ExtentReports();
//		extent.attachReporter(reporter);
//		extent.setSystemInfo("Tester", "Olamide");
//	}
	

	@BeforeClass(alwaysRun = true)
	public void ConfigAppium() throws URISyntaxException, IOException {
//        // Create a custom environment map with Android SDK paths
//        Map<String, String> env = new HashMap<>(System.getenv());
//        env.put("ANDROID_HOME", "C:\\Users\\olamide.ige\\AppData\\Local\\Android\\Sdk");
//        env.put("ANDROID_SDK_ROOT", "C:\\Users\\olamide.ige\\AppData\\Local\\Android\\Sdk");
// 
//
//        
//        // Create and start the Appium service
//        AppiumServiceBuilder builder = new AppiumServiceBuilder()
//            .withAppiumJS(new File("C:\\Users\\olamide.ige\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
//            .withIPAddress("127.0.0.1")
//            .usingPort(4723)
//            .withEnvironment(env); 
//            
//        service = AppiumDriverLocalService.buildService(builder);
//        service.start();
		Properties properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") +"\\src\\main\\java\\resources\\data.properties");
		properties.load(fileInputStream);
//		String ipAddress =  properties.getProperty("ipAddress");
		String ipAddress = System.getProperty("ipAddress")!=null ?  System.getProperty("ipAddress") : properties.getProperty("ipAddress");
		String port =  properties.getProperty("port");

		service = startAppiumServer(ipAddress, Integer.parseInt(port));
//        
		UiAutomator2Options options = new UiAutomator2Options();
		options.setDeviceName(properties.getProperty("AndriodDeviceName"));
		// Add this capability for automatic ChromeDriver management
		options.setChromedriverExecutable("C:\\Users\\olamide.ige\\Downloads\\software\\appium\\chromeDriver133\\chromedriver.exe");
		
//		options.setApp("C:\\Users\\olamide.ige\\eclipse-workspace\\appiumFramework\\src\\test\\java\\resources\\ApiDemos-debug.apk");
		options.setApp("C:\\Users\\olamide.ige\\eclipse-workspace\\appiumFramework\\src\\test\\java\\resources\\General-Store.apk");

		
//		driver = new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), options);
		driver = new AndroidDriver(service.getUrl(), options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//		AndroidDriver driver = new AndroidDriver(new URI("http://192.168.1.101:4723").toURL(), options);
		
	}
	
	@BeforeMethod(alwaysRun = true)
	public void preSetup() 
	{
		driver.terminateApp("com.androidsample.generalstore");
		driver.activateApp("com.androidsample.generalstore");
	}
	

	
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		driver.quit();
		service.stop();
	
	}

}
