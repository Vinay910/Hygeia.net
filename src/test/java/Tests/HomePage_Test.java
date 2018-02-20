package Tests;
import org.testng.annotations.Test;
import org.testng.annotations.Test;
import Pages.BasePage;

public class HomePage_Test extends BaseTest {

	@Test(dataProvider="dataProvider")
	public void HomePageValidation(String one,String two,String three)
	{
		contactUs=homePage.contactUs();
		currentDriver.switchTo().frame(0);
		contactUs=contactUs.clickContactUs1();
		currentDriver.switchTo().frame("leftnav");
		contactUs=contactUs.clickContactUs2();
		currentDriver.switchTo().frame(0);
		contactUs.clickHome();
		
	}
}
