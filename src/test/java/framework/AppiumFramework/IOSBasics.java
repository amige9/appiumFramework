package framework.AppiumFramework;

import org.testng.annotations.Test;

import TestUtils.IOSBaseTest;
import io.appium.java_client.AppiumBy;
import pageObjects.IOS.Alertpage;
import pageObjects.IOS.HomePage;

public class IOSBasics extends IOSBaseTest{
	
	@Test(groups = {"Smoke"})
	public void IOSBasicsTest() {
		
		// Initialize Factory Pages
		HomePage homepage = new HomePage(driver);
		Alertpage alertPage = new Alertpage(driver);
		
		// Locators in IOS
		// accessibility id, id, Xpath, classname, IOS, iosClassChain, IOSPredicateString
		homepage.selectAlertViews();
		alertPage.selectTextEntry();
		alertPage.enterText("Hello World");
		alertPage.clickOkButtton();

		alertPage.clickConfirmMenu();
		alertPage.assertainConfirmMessage();
		alertPage.clickSubmitButton();
		
//		driver.findElement(AppiumBy.iOSNsPredicateString("name == 'Confirm / Cancel' AND type == 'XCUIElementTypeStaticText'")).click();
////		driver.findElement(AppiumBy.iOSNsPredicateString("name == 'Confirm / Cancel' AND value BEGINSWITH[c] 'Confirm'")); // Adding c makes it not case sensitive
////		driver.findElement(AppiumBy.iOSNsPredicateString("name == 'Confirm / Cancel' AND value ENDSWITH 'Cancel'"));
//		String text = driver.findElement(AppiumBy.iOSNsPredicateString("name BEGINSWITH[c] 'A message'")).getText();
//		System.out.println(text);
//
//
//		driver.findElement(AppiumBy.iOSNsPredicateString("name == 'Confirm'")).click();

		
	}
	

}
