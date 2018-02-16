package Pages;
import org.testng.annotations.Test;

public class HomePage_Test extends BasePage {
	@Test
	public void HomePageValidation()
	{
		homePage=new HomePage();
		homePage.ProviderSearch();
	}
}
