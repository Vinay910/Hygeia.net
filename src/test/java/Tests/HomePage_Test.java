package Tests;
import org.testng.annotations.Test;

import Pages.HomePage;

public class HomePage_Test extends BaseTest {

	@Test
	public void HomePageValidation()
	{
		homePage=new HomePage();
		homePage.ProviderSearch();
	}
}