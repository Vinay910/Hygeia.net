package Pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class ProviderSearchPage extends BasePage{

	public ProviderSearchPage()
	{
		PageFactory.initElements(currentDriver, this);
	}
	
	@FindBy (how=How.XPATH,using="html/body/table[1]/tbody/tr/td/table[1]/tbody/tr/td[3]/a")
	private WebElement providerSearchHomePage;
	@FindBy (how=How.XPATH,using="html/body/table[1]/tbody/tr/td/table[2]/tbody/tr/td/font/blockquote[1]/form/input[2]")
	private WebElement searchFacilityButton;
	@FindBy (how=How.XPATH,using="html/body/table[1]/tbody/tr/td/table[2]/tbody/tr/td/font/blockquote[2]/form/input[2]")
	private WebElement searchPractitioner;
	@FindBy (how=How.XPATH,using="html/body/table[1]/tbody/tr/td/table[3]/tbody/tr[3]/td/span/a")
	private WebElement hygeiaLink;

	public HomePage LinkVerification()
	{
		providerSearchPage=homePage.ProviderSearch();
		seleniumHelper.clickElement(hygeiaLink);
		return new HomePage();
	}
	
	
	
}
