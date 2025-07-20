package TestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import utils.AppiumUtils;

public class IOSBaseTest extends AppiumUtils {

	public IOSDriver driver;
	public AppiumDriverLocalService service;

	@BeforeClass(alwaysRun = true)
	public void ConfigAppium() throws URISyntaxException, IOException {
//        // Create a custom environment map with Android SDK paths
//        Map<String, String> env = new HashMap<>(System.getenv());
//        env.put("ANDROID_HOME", "C:\\Users\\olamide.ige\\AppData\\Local\\Android\\Sdk");
//        env.put("ANDROID_SDK_ROOT", "C:\\Users\\olamide.ige\\AppData\\Local\\Android\\Sdk");
// 
//
//        
		// Create and start the Appium service
//        AppiumServiceBuilder builder = new AppiumServiceBuilder()
//            .withAppiumJS(new File("C:\\Users\\olamide.ige\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
//            .withIPAddress("127.0.0.1")
//            .usingPort(4723)
//            .withEnvironment(env); 
//            
//        service = AppiumDriverLocalService.buildService(builder);
//        service.start();

//		service = new AppiumServiceBuilder()
//				.withAppiumJS(new File("/Users/olams99/.npm-global/lib/node_modules/appium/build/lib/main.js"))
//				.withIPAddress("127.0.0.1").usingPort(4723).build();
//		service.start();

		Properties properties = new Properties();
		Path filePath = Paths.get(System.getProperty("user.dir"), 
			    "src", "main", "java", "resources", "data.properties");
		
//		FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") +"\\src\\main\\java\\resources\\data.properties");
//		properties.load(fileInputStream);
		FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
		properties.load(fileInputStream);

//		String ipAddress =  properties.getProperty("ipAddress");
		String ipAddress = System.getProperty("ipAddress")!=null ?  System.getProperty("ipAddress") : properties.getProperty("ipAddress");
		String port =  properties.getProperty("port");

		service = startAppiumServer(ipAddress, Integer.parseInt(port));

		XCUITestOptions options = new XCUITestOptions();
		options.setDeviceName(properties.getProperty("IOSDeviceName"));
		options.setApp(
				"/Users/olams99/Downloads/ios-uicatalog-master/UIKitCatalog/build/Build/Products/Debug-iphonesimulator/UIKitCatalog.app");
//		options.setApp(System.getProperty("user.dir") + "/src/test/java/resources/TestApp 3.app");
		options.setPlatformVersion(properties.getProperty("OSVersion"));
		options.setWdaLaunchTimeout(Duration.ofSeconds(20));

//		UiAutomator2Options options = new UiAutomator2Options();
//		options.setDeviceName("android pixel 3a");
//		// Add this capability for automatic ChromeDriver management
//		options.setChromedriverExecutable("C:\\Users\\olamide.ige\\Downloads\\software\\appium\\chromeDriver133\\chromedriver.exe");
//		
////		options.setApp("C:\\Users\\olamide.ige\\eclipse-workspace\\appiumTutorial\\src\\test\\java\\resources\\ApiDemos-debug.apk");
//		options.setApp("C:\\Users\\olamide.ige\\eclipse-workspace\\appiumTutorial\\src\\test\\java\\resources\\General-Store.apk");

//		driver = new IOSDriver(new URI("http://127.0.0.1:4723").toURL(), options);
		driver = new IOSDriver(service.getUrl(), options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//		AndroidDriver driver = new AndroidDriver(new URI("http://192.168.1.101:4723").toURL(), options);

	}

	@BeforeMethod(alwaysRun = true)
	public void preSetup() {
//		driver.terminateApp("com.androidsample.generalstore");
//		driver.activateApp("com.androidsample.generalstore");
//		Activity activity = new Activity("com.androidsample.generalstore", "com.androidsample.generalstore.SplashActivity");

//		((JavascriptExecutor)driver).executeScript("mobile: startActivity", 
//				ImmutableMap.of("intent", "com.androidsample.generalstore/com.androidsample.generalstore.SplashActivity"));
//		((JavascriptExecutor)driver).executeScript("mobile: startActivity", 
//				ImmutableMap.of("intent", "com.androidsample.generalstore/com.androidsample.generalstore.MainActivity"));
	}
	
	public void touchAndHold(WebElement element) {
		Map <String,Object>params = new HashMap<>();
		params.put("element", ((RemoteWebElement)element).getId());
		params.put("duration", 5);
		driver.executeScript("mobile:touchAndHold", params);
	}
	

//	@AfterClass(alwaysRun = true)
//	public void tearDown() {
//		driver.quit();
//		service.stop();
//
//	}
	@AfterClass(alwaysRun = true)
	public void tearDown() {
		if (driver != null) {
			try {
				System.out.println("🛑 Cleaning up test session...");
				driver.quit();
				service.stop();
			} catch (Exception e) {
				System.err.println("Error quitting driver: " + e.getMessage());
			} finally {
				driver = null;
			}
		}
	}

}
