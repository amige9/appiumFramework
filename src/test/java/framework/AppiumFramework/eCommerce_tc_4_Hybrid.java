package framework.AppiumFramework;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import TestUtils.BaseTest;
import pageObjects.android.CartPage;
import pageObjects.android.FormPage;
import pageObjects.android.ProductPage;

public class eCommerce_tc_4_Hybrid extends BaseTest {

//	@DataProvider
//	public Object[] [] getData() 
//	{
//		
//		return new Object[][]
//			{{"Olamide Ige", "female" , "Azerbaijan"},
//			{"Micheal Jordan", "male" , "Belgium"}};
//	} 

//	@Test(dataProvider = "getData")
//	public void FillForm(String name, String gender, String country) throws InterruptedException 
//	{

//		FormPage formPage = new FormPage(driver);
//		
//		formPage.setNameField(name);		
//		formPage.setGender(gender);
//		formPage.setCountry(country);
//		formPage.submitForm();
//		
//		ProductPage productPage =  new ProductPage(driver);
//		
//		productPage.addItemToCart(0);
//		productPage.addItemToCart(0);
//		productPage.goToCartPage();
//		
//		CartPage cartPage = new CartPage(driver);
//		
//		double totalSum = cartPage.getProductsSum();
//		double displayFormattedSum = cartPage.getTotalAmountDisplayed();
//		Assert.assertEquals(totalSum, displayFormattedSum);
//		cartPage.acceptTermsConditions();
//		cartPage.submitOrder();
//
//		Thread.sleep(6000);
//		
//		// To retrieve the context names 
//		Set<String> contexts = driver.getContextHandles();
//		for(String contextName:contexts) {
//			System.out.println(contextName);
//		}
//		// Switching to the web view
//		driver.context("WEBVIEW_com.androidsample.generalstore");
//		driver.findElement(By.name("q")).sendKeys("rahul shetty academy");
//		driver.findElement(By.name("q")).sendKeys(Keys.ENTER);
//		driver.pressKey(new KeyEvent(AndroidKey.BACK));
//		// Switching back to android
//		driver.context("NATIVE_APP");

//	}

	@DataProvider
	public Object[][] getData() throws IOException {
		// Using Json format for test data
		List<HashMap<String, String>> data = getJsonData(
				System.getProperty("user.dir") + "\\src\\test\\java\\testData\\eCommerce.json");
		return new Object[][] { { data.get(0) }, { data.get(1) } };
	}

//	C:\Users\olamide.ige\eclipse-workspace\AppiumFramework\src\test\java\testData\eCommerce.json
//	System.getProperty("user.dir")+ "\\src\\test\\java\\testData\\eCommerce.json"

	@Test(dataProvider = "getData", groups = { "Smoke" })
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
////		
////		// To retrieve the context names
////		Set<String> contexts = driver.getContextHandles();
////		for(String contextName:contexts) {
////			System.out.println(contextName);
////		}
////		// Switching to the web view
////		driver.context("WEBVIEW_com.androidsample.generalstore");
////		driver.findElement(By.name("q")).sendKeys("rahul shetty academy");
////		driver.findElement(By.name("q")).sendKeys(Keys.ENTER);
////		driver.pressKey(new KeyEvent(AndroidKey.BACK));
////		// Switching back to android
////		driver.context("NATIVE_APP");
//
//	
	}

}
