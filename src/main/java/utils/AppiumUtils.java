package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
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
	
    /**
     * Starts Appium server with cross-platform support
     * @param ipAddress Server IP address (usually "127.0.0.1")
     * @param port Server port (usually 4723)
     * @return AppiumDriverLocalService instance
     */
    public AppiumDriverLocalService startAppiumServer(String ipAddress, int port) {
        // Check if service is already running
        if (service != null && service.isRunning()) {
            System.out.println("✅ Appium server is already running");
            return service;
        }

        try {
            // Detect operating system
            String osName = System.getProperty("os.name").toLowerCase();
            boolean isWindows = osName.contains("win");
            
            // Get all required paths
            String androidHome = getAndroidHome();
            String appiumJsPath = getAppiumJsPath();
            String nodeJsPath = getNodeJsPath();
            
            System.out.println("🔧 Setting up Appium server...");
            System.out.println("📱 Android SDK: " + androidHome);
            System.out.println("🚀 Appium JS: " + appiumJsPath);
            System.out.println("🟢 Node.js: " + nodeJsPath);

            // Create environment map with cross-platform PATH handling
            Map<String, String> env = new HashMap<>(System.getenv());
            env.put("ANDROID_HOME", androidHome);
            env.put("ANDROID_SDK_ROOT", androidHome);
            
            // Add Node.js directory to PATH (cross-platform)
            String currentPath = env.get("PATH");
            String nodeDir = new File(nodeJsPath).getParent();
            String pathSeparator = isWindows ? ";" : ":";
            
            if (currentPath != null) {
                env.put("PATH", nodeDir + pathSeparator + currentPath);
            } else {
                env.put("PATH", nodeDir);
            }
            
            // Additional environment variables for Node.js detection
            env.put("NODE_BINARY", nodeJsPath);
            env.put("NODE_EXECUTABLE", nodeJsPath);

            // Create and start the Appium service
            AppiumServiceBuilder builder = new AppiumServiceBuilder()
                    .withAppiumJS(new File(appiumJsPath))
                    .withIPAddress(ipAddress)
                    .usingPort(port)
                    .withEnvironment(env);

            service = AppiumDriverLocalService.buildService(builder);
            service.start();
            
            System.out.println("✅ Appium server started successfully on " + ipAddress + ":" + port);
            return service;
            
        } catch (Exception e) {
            System.err.println("❌ Failed to start Appium server: " + e.getMessage());
            throw new RuntimeException("Could not start Appium server", e);
        }
    }
    
    /**
     * Cross-platform Android SDK detection
     * @return Path to Android SDK
     */
    private String getAndroidHome() {
        // Check environment variables first (cross-platform)
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null && new File(androidHome).exists()) {
            System.out.println("📱 Found Android SDK via ANDROID_HOME: " + androidHome);
            return androidHome;
        }

        androidHome = System.getenv("ANDROID_SDK_ROOT");
        if (androidHome != null && new File(androidHome).exists()) {
            System.out.println("📱 Found Android SDK via ANDROID_SDK_ROOT: " + androidHome);
            return androidHome;
        }

        // Platform-specific default paths
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String[] possiblePaths;
        
        if (osName.contains("win")) {
            // Windows paths
            possiblePaths = new String[]{
                userHome + "\\AppData\\Local\\Android\\Sdk",
                userHome + "\\AppData\\Local\\Android\\sdk",
                "C:\\Android\\Sdk",
                "C:\\Android\\sdk"
            };
        } else {
            // Mac/Linux paths
            possiblePaths = new String[]{
                userHome + "/Library/Android/sdk",    // Mac default
                userHome + "/Android/Sdk",            // Alternative Mac/Linux
                userHome + "/android-sdk",            // Linux alternative
                "/opt/android-sdk"                    // System-wide Linux
            };
        }

        // Check all possible paths
        for (String path : possiblePaths) {
            if (new File(path).exists()) {
                System.out.println("📱 Found Android SDK at: " + path);
                return path;
            }
        }

        throw new RuntimeException("❌ Could not find Android SDK. Please install Android SDK or set ANDROID_HOME environment variable. Searched: " + 
            String.join(", ", possiblePaths));
    }
    
    /**
    * Cross-platform Appium JS file detection
    * @return Path to Appium main.js file
    */
   private String getAppiumJsPath() {
       String userHome = System.getProperty("user.home");
       String osName = System.getProperty("os.name").toLowerCase();
       boolean isWindows = osName.contains("win");

       // Try npm config to get prefix dynamically (most reliable cross-platform method)
       try {
           Process process = Runtime.getRuntime().exec("npm config get prefix");
           BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
           String npmPrefix = reader.readLine();
           process.waitFor();
           
           if (npmPrefix != null && !npmPrefix.trim().isEmpty()) {
               String separator = File.separator;
               String appiumPath = npmPrefix + separator + "lib" + separator + 
                                 "node_modules" + separator + "appium" + separator + 
                                 "build" + separator + "lib" + separator + "main.js";
               if (new File(appiumPath).exists()) {
                   System.out.println("🚀 Found Appium via npm prefix: " + appiumPath);
                   return appiumPath;
               }
           }
       } catch (Exception e) {
           System.err.println("⚠️ Could not get npm prefix: " + e.getMessage());
       }

       // Platform-specific fallback paths
       String[] possiblePaths;
       
       if (isWindows) {
           possiblePaths = new String[]{
               // Windows npm global paths
               userHome + "\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js",
               
               // Windows npm-global custom prefix (Option 2 setup)
               userHome + "\\.npm-global\\lib\\node_modules\\appium\\build\\lib\\main.js",
               
               // Program Files installations
               "C:\\Program Files\\nodejs\\node_modules\\appium\\build\\lib\\main.js",
               "C:\\Program Files (x86)\\nodejs\\node_modules\\appium\\build\\lib\\main.js"
           };
       } else {
           // Mac/Linux paths
           possiblePaths = new String[]{
               // npm-global custom prefix (Option 2 setup)
               userHome + "/.npm-global/lib/node_modules/appium/build/lib/main.js",
               
               // Homebrew paths
               "/opt/homebrew/lib/node_modules/appium/build/lib/main.js",  // Apple Silicon
               "/usr/local/lib/node_modules/appium/build/lib/main.js",     // Intel Mac or Linux
               
               // Standard Linux paths
               "/usr/lib/node_modules/appium/build/lib/main.js"
           };
       }

       // Check all possible paths
       for (String path : possiblePaths) {
           if (new File(path).exists()) {
               System.out.println("🚀 Found Appium at: " + path);
               return path;
           }
       }

       throw new RuntimeException("❌ Could not find Appium JS file. Please ensure Appium is installed: npm install -g appium. " +
           "Searched npm prefix and platform-specific paths.");
   }
   
   /**
    * Cross-platform Node.js executable detection
    * @return Path to Node.js executable
    */
   private String getNodeJsPath() {
       String osName = System.getProperty("os.name").toLowerCase();
       boolean isWindows = osName.contains("win");
       String userHome = System.getProperty("user.home");

       // Try which/where command (most reliable cross-platform method)
       try {
           String command = isWindows ? "where node" : "which node";
           Process process = Runtime.getRuntime().exec(command);
           BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
           String nodePath = reader.readLine();
           process.waitFor();
           
           if (nodePath != null && new File(nodePath).exists()) {
               System.out.println("🟢 Found Node.js via system command: " + nodePath);
               return nodePath;
           }
       } catch (Exception e) {
           System.err.println("⚠️ Could not find node via system command: " + e.getMessage());
       }

       // Platform-specific fallback paths
       String[] possiblePaths;
       
       if (isWindows) {
           possiblePaths = new String[]{
               // Standard Windows Node.js installations
               "C:\\Program Files\\nodejs\\node.exe",
               "C:\\Program Files (x86)\\nodejs\\node.exe",
               
               // npm-global custom prefix (Option 2)
               userHome + "\\.npm-global\\bin\\node.exe",
               
               // AppData installation
               userHome + "\\AppData\\Roaming\\npm\\node.exe",
               
               // Chocolatey installation
               "C:\\ProgramData\\chocolatey\\bin\\node.exe"
           };
       } else {
           // Mac/Linux paths
           possiblePaths = new String[]{
               // npm-global custom prefix (Option 2)
               userHome + "/.npm-global/bin/node",
               
               // Homebrew
               "/opt/homebrew/bin/node",        // Apple Silicon
               "/usr/local/bin/node",           // Intel Mac
               
               // System paths
               "/usr/bin/node",
               "/usr/local/nodejs/bin/node"
           };
       }

       // Check all possible paths
       for (String path : possiblePaths) {
           if (new File(path).exists()) {
               System.out.println("🟢 Found Node.js at: " + path);
               return path;
           }
       }

       throw new RuntimeException("❌ Could not find Node.js executable. Please ensure Node.js is installed. " +
           "Searched system PATH and common installation directories.");
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
