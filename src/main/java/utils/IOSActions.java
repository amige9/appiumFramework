package utils;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;


import io.appium.java_client.ios.IOSDriver;

public class IOSActions extends AppiumUtils {

	IOSDriver driver;

	public IOSActions(IOSDriver driver) {
//		super(driver);
		this.driver = driver;
	}

	public void longPressAction(WebElement element) {
		Map<String, Object> params = new HashMap<>();
		params.put("element", ((RemoteWebElement) element).getId());
		params.put("duration", 5);
		driver.executeScript("mobile:touchAndHold", params);
	}

	public void swipeAction(String direction) {
		Map<String, Object> params = new HashMap<>();
		params.put("direction", direction);
		driver.executeScript("mobile:swipe", params);
	}

	public void scrollToText(WebElement element) {
		Map<String, Object> params = new HashMap<>();
		params.put("direction", "down");
		params.put("element", ((RemoteWebElement) element).getId());
		driver.executeScript("mobile:scroll", params);
	}

}
