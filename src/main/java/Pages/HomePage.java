package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends BasePage{

	private WebDriver webdriver=new FirefoxDriver();
	public HomePage()
	{
		PageFactory.initElements(webdriver, this);
	}
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[9]/td/table/tbody/tr/td[1]/a/img")
	private WebElement providerSearch;
	
	public void ProviderSearch()
	{
		webdriver.get("https://hygeia.net/");
		providerSearch.click();
	}
}
