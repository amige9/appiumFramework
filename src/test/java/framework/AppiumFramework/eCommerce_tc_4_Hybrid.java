package framework.AppiumFramework;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import TestUtils.AndroidBaseTest;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import pageObjects.android.CartPage;
import pageObjects.android.FormPage;
import pageObjects.android.ProductPage;

public class eCommerce_tc_4_Hybrid extends AndroidBaseTest {

	@DataProvider
	public Object[][] getData() throws IOException {
		// Using Json format for test data
		List<HashMap<String, String>> data = getJsonData(
				System.getProperty("user.dir") + "/src/test/java/testData/eCommerce.json");
		return new Object[][] { { data.get(0) }};
	}


	@Test(dataProvider = "getData", groups = {"Smoke"})
	public void FillForm(HashMap<String, String> input) throws InterruptedException {

		FormPage formPage = new FormPage(driver);

		formPage.setCountry(input.get("country"));
		formPage.setNameField(input.get("name"));
		formPage.setGender(input.get("gender"));
		formPage.submitForm();

		ProductPage productPage = new ProductPage(driver);

		productPage.addItemToCart(0);
		productPage.addItemToCart(0);
		productPage.goToCartPage();

		CartPage cartPage = new CartPage(driver);

		double totalSum = cartPage.getProductsSum();
		double displayFormattedSum = cartPage.getTotalAmountDisplayed();
		Assert.assertEquals(totalSum, displayFormattedSum);
		cartPage.acceptTermsConditions();
		cartPage.submitOrder();

		Thread.sleep(6000);
		
		// To retrieve the context names
		Set<String> contexts = driver.getContextHandles();
		for(String contextName:contexts) {
			System.out.println(contextName);
		}
		// Switching to the web view
		driver.context("WEBVIEW_com.androidsample.generalstore");
		System.out.println("Current context: " + driver.getContext());
//		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//		wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
		driver.findElement(By.name("q")).sendKeys("rahul shetty academy");
		driver.findElement(By.name("q")).sendKeys(Keys.ENTER);
		driver.pressKey(new KeyEvent(AndroidKey.BACK));
		// Switching back to android
		driver.context("NATIVE_APP");
        System.out.println("🎉 TEST PASSED - All steps completed successfully!");

		
//
//	
	}

}
