package Tests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.annotations.Test;

import Pages.HomePage;

public class HomePage_Test extends BaseTest {

	@Test(dataProvider="dataProvider")
	public void HomePageValidation()
	{
		homePage=new HomePage();
		homePage.ProviderSearch();
	}
}
