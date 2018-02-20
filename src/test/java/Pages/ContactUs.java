package Pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ContactUs extends BasePage {


	public ContactUs()
	{
		PageFactory.initElements(currentDriver, this);
	}
	
	@FindBy (how=How.NAME,using="img1c")
	private WebElement Home;
	
	@FindBy (how=How.NAME,using="img1")
	private WebElement ContactUs1;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr[2]/td/p[3]/a/img")
	private WebElement ContactUs2;
	
	public HomePage clickHome()
	{
		seleniumHelper.clickElement(Home);
		return new HomePage();
	}
	public ContactUs clickContactUs1()
	{
		seleniumHelper.clickElement(ContactUs1);
		return new ContactUs();
	}
	public ContactUs clickContactUs2()
	{
		seleniumHelper.clickElement(ContactUs2);
		return new ContactUs();
	}

	
}
