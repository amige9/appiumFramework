package pageObjects.android;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import utils.AndroidActions;

public class ProductPage extends AndroidActions{
	
	AndroidDriver driver;
	
	public ProductPage(AndroidDriver driver) 
	{	
		super(driver);
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
	
	@AndroidFindBy(xpath ="//android.widget.TextView[@text='ADD TO CART']")
	private List<WebElement> addToCart;
	
	@AndroidFindBy(id = "com.androidsample.generalstore:id/appbar_btn_cart")
	private WebElement cartButton;
	

	
	// Actions	
	public void addItemToCart(int index)
	{
		addToCart.get(index).click();

	}
	
	public void goToCartPage() throws InterruptedException 
	{
		cartButton.click();
		Thread.sleep(3000);
	}
	


	

}
