package Pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends BasePage{

	public HomePage()
	{
		PageFactory.initElements(currentDriver, this);
	}
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[9]/td/table/tbody/tr/td[1]/a/img")
	private WebElement providerSearch;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr/td[1]/a/img")
	private WebElement contactUs;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[1]/td/table/tbody/tr/td[2]/a/img")
	private WebElement siteMap;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[5]/td/table/tbody/tr/td[1]/a/img")
	private WebElement payerButton;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[5]/td/table/tbody/tr/td[3]/a/img")
	private WebElement contactUsButton;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[5]/td/table/tbody/tr/td[5]/a/img")
	private WebElement providerButton;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[9]/td/table/tbody/tr/td[3]/a/img")
	private WebElement partnerButton;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[9]/td/table/tbody/tr/td[3]/a/img")
	private WebElement partnerLogin;

	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[11]/td/table/tbody/tr/td[1]/a/img")
	private WebElement policyButton;
	
	@FindBy (how=How.XPATH,using="html/body/table/tbody/tr/td/table/tbody/tr[11]/td/table/tbody/tr/td[2]/a/img")
	private WebElement policySecurityButton;
	
	@FindBy (how=How.XPATH, using="html/body/table/tbody/tr/td/table/tbody/tr[11]/td/table/tbody/tr/td[3]/a/img")
	private WebElement termsButton;

	public ProviderSearchPage ProviderSearch()
	{
		currentDriver.get(appURL);
		seleniumHelper.clickElement(providerSearch);
		return new ProviderSearchPage();
	}
	
	public void test()
	{

		seleniumHelper.clickElement(contactUs);
		seleniumHelper.clickElement(siteMap);
		seleniumHelper.clickElement(payerButton);
		seleniumHelper.clickElement(contactUsButton);
		seleniumHelper.clickElement(providerButton);
		seleniumHelper.clickElement(partnerButton);
		seleniumHelper.clickElement(partnerLogin);
		seleniumHelper.clickElement(policyButton);
		seleniumHelper.clickElement(policySecurityButton);
		seleniumHelper.clickElement(termsButton);
	}
}
