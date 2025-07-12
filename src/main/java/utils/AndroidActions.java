package utils;

import javax.sound.midi.VoiceStatus;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class AndroidActions extends AppiumUtils{
	
	AndroidDriver driver;
	
	public AndroidActions(AndroidDriver driver) 
	{
		super();
		this.driver = driver;
	}
	
	public void longPressAction(WebElement element) {
		((JavascriptExecutor)driver).executeScript("mobile: longClickGesture",
				ImmutableMap.of("elementId", ((RemoteWebElement)element).getId(), 
						"duration", 2000));
	}
	
	public void swipeAction(WebElement element, String direction, double percentage) {
		((JavascriptExecutor)driver).executeScript("mobile: swipeGesture",
				ImmutableMap.of("elementId", ((RemoteWebElement)element).getId(), 
						"direction", direction, 
						"percent", percentage));
	}
	
	public void dragDrop(WebElement element, int endX, int endY) {
		((JavascriptExecutor)driver).executeScript("mobile: dragGesture",
				ImmutableMap.of("elementId", ((RemoteWebElement)element).getId(), 
						"endX", endX, 
						"endY", endY));
	}
	
	public void scrollToText(String text) 
	{
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+text+"\"));"));
	}
}
