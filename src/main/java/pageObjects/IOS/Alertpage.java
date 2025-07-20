package pageObjects.IOS;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import utils.IOSActions;

public class Alertpage extends IOSActions {

	IOSDriver driver;

	public Alertpage(IOSDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);

	}

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == \"Text Entry\"`]")
	private WebElement textEntryMenu;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == 'Horizontal scroll bar, 1 page'`][4]")
	private WebElement textEntryField;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeButton[`name == \"OK\"`]")
	private WebElement okButton;
	
	@iOSXCUITFindBy(iOSNsPredicate  = "name == 'Confirm / Cancel' AND type == 'XCUIElementTypeStaticText'")
	private WebElement confirmPopup;
	
	@iOSXCUITFindBy(iOSNsPredicate  = "name BEGINSWITH[c] 'A message'")
	private WebElement confirmMessage;
	
	@iOSXCUITFindBy(iOSNsPredicate  = "name == 'Confirm'")
	private WebElement submit;
	

	// Actions
	public void selectTextEntry() {
		textEntryMenu.click();
	}

	public void enterText(String text) {
		textEntryField.sendKeys(text);
	}

	public void clickOkButtton() {
		okButton.click();
	}
	
	public void clickConfirmMenu() {
		confirmPopup.click();
	}
	
	public void assertainConfirmMessage() {
		String text = confirmMessage.getText();
		Assert.assertEquals(text, "A message should be a short, complete sentence.");
	}
	
	public void clickSubmitButton() {
		submit.click();
	}

}
