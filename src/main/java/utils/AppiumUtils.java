package utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class AppiumUtils {
	
	public AppiumDriverLocalService service;

	
//	AppiumDriver driver;
//	
//	public AppiumUtils(AppiumDriver driver) 
//	{
//		this.driver = driver;
//	}
//	
	public double getFormattedAmount(String amount) 
	{
		Double price = Double.parseDouble(amount.substring(1));
		return price;
	}
	
	public void waitForElementToAppear(WebElement element, AppiumDriver driver) 
	{
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5000));
		wait.until(ExpectedConditions.attributeContains((element), "text", "Cart"));
	}
	
	public List<HashMap<String, String>> getJsonData(String jsonFilePath) throws IOException {
		
		// Convert json file content  to json string
//		String jsonContent = FileUtils.readFileToStr(System.getProperty("user.dir")+ "src\\test\\java\\testData\\eCommerce.json");
		String jsonContent = FileUtils.readFileToString(new File(jsonFilePath), StandardCharsets.UTF_8);
		
		ObjectMapper mapper = new ObjectMapper();
		List<HashMap<String, String>> data = mapper.readValue(jsonContent,
				new TypeReference<List<HashMap<String, String>>> (){
				});
		
		return data;

	}
	
	public AppiumDriverLocalService startAppiumServer(String ipAddress, int port) {
	    // Check if service is already running
	    if (service != null && service.isRunning()) {
	        System.out.println("Appium server is already running");
	        return service;
	    }

	    // Create a custom environment map with Android SDK paths
	    Map<String, String> env = new HashMap<>(System.getenv());
	    env.put("ANDROID_HOME", "C:\\Users\\olamide.ige\\AppData\\Local\\Android\\Sdk");
	    env.put("ANDROID_SDK_ROOT", "C:\\Users\\olamide.ige\\AppData\\Local\\Android\\Sdk");

	    try {
	        // Create and start the Appium service
	        AppiumServiceBuilder builder = new AppiumServiceBuilder()
	            .withAppiumJS(new File("C:\\Users\\olamide.ige\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
	            .withIPAddress(ipAddress)
	            .usingPort(port)
	            .withEnvironment(env);

	        service = AppiumDriverLocalService.buildService(builder);
	        service.start();
	        return service;
	    } catch (Exception e) {
	        System.err.println("Failed to start Appium server: " + e.getMessage());
	        throw new RuntimeException("Could not start Appium server", e);
	    }
	}
	
//	public String getScreenshot(String testCaseName, AppiumDriver driver) throws IOException {
//		File source =  driver.getScreenshotAs(OutputType.FILE);
//		String destinationFile = System.getProperty("user.dir")+"\\reports"+testCaseName+".png";
//		FileUtils.copyFile(source, new File(destinationFile));
//		return destinationFile;
//		
//	}
	   public String getScreenshotPath(String testCaseName, AppiumDriver driver) throws IOException{
	        try {
	            File source = driver.getScreenshotAs(OutputType.FILE);
	    		String destinationFile = System.getProperty("user.dir") + "/reports/" + testCaseName + ".png";
	    		FileUtils.copyFile(source, new File(destinationFile));
	            return destinationFile;
	        } catch (IOException e) {
	            System.err.println("Failed to take screenshot: " + e.getMessage());
	            return null;
	        }
	    }

}
