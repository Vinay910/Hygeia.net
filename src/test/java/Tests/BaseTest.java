package Tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.UnexpectedException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import Pages.HomePage;
import core.generic.utilities.AutomationCore;
import core.generic.utilities.SeleniumHelper;


@SuppressWarnings("deprecation")
public class BaseTest extends AutomationCore {
	
	HomePage homePage;
	public WebDriver testDriver;
	public boolean status=true;
	public int totalIterations =0;
	public int currentIteration=1;
	public String initialTIme=null;
	ExtentReports extent;
	ExtentTest test;
	ExtentTest childTest = null;
	ThreadLocal<ExtentTest> testStatusThread = new ThreadLocal<ExtentTest>();
	public String seleniumURI = "@ondemand.saucelabs.com:443";
	public String buildTag = System.getenv("BUILD_TAG");
	public String username = "nsahu102";//System.getenv("SAUCE_USERNAME");
	public String accesskey = "";//System.getenv("SAUCE_ACCESS_KEY");
	//public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(username, accesskey);
	private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
	private ThreadLocal<String> sessionId = new ThreadLocal<String>();
	boolean reportAdded = false;
	int st = 0;
	public void getExecutionDetails()
	{
		try {
			FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+Environment("testDataFilePath"));
			Workbook workBook = new XSSFWorkbook(fileInputStream);
			int namedCellIdx = workBook.getNameIndex("TestExecution_Details");			
		    org.apache.poi.ss.usermodel.Name aNamedCell = workBook.getNameAt(namedCellIdx);
			AreaReference area = new AreaReference(aNamedCell.getRefersToFormula());
			CellReference[] cellrefs = area.getAllReferencedCells();
			Sheet s = workBook.getSheet(aNamedCell.getSheetName());
			int startRowindex = cellrefs[0].getRow();
			appURL = s.getRow(startRowindex).getCell(2).getStringCellValue();
			workBook.close();
			
		}catch(Exception e)
		{
			insertReportLine(e);
		}
	}

	@BeforeTest()
	public void beforeTest()
	{
		getExecutionDetails();
	}

	@BeforeSuite
	public void beforeSuite() throws IOException
	{
		sfAssert = new SoftAssert();
		currentBrowser = Environment("browser");
		reportGenerator();
	}
	@BeforeClass()
	public void beforClass() 
	{
		if(testExecutionPlatform.equalsIgnoreCase("Local Machine")) {
		StartDriver();
		getHomePage();
		testDriver.manage().deleteAllCookies();
		testDriver.manage().window().maximize();
		}
	}
	boolean isDependant;
	@Parameters({"browser","browserversion","os"})
	@BeforeMethod()
	public void beforMethod(@Optional("internet explorer") String browser, @Optional("11.0") String browserversion, @Optional("Windows 7") String os, Method method) 
	{
		isDependant = false;
		try {		
			
			 Test testClass = method.getAnnotation(Test.class);
			 if(testClass.dependsOnMethods().length>0) {
				 isDependant = true;
			 }

			currentTestName = method.getName();
			System.out.println("<*****>Started Execution of script "+currentTestName+"<*****>");
			startTestForReport(browser, browserversion, os, method.getName() );
			if(!testExecutionPlatform.equalsIgnoreCase("Local Machine")) {
				this.createDriver(browser,browserversion, os, method.getName());
				testDriver = this.getWebDriver();
				currentDriver = testDriver;
				}else
				{
//					if(!isDependant)
//						clearBrowserCache(browser);
				}
			if(!isDependant) {
			
			testDriver.manage().deleteAllCookies();
			testDriver.get(appURL);
			}
			testDriver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
		
		}catch(Exception e)
		{
			insertReportLine(e);
		}
	}

	@AfterMethod
	public void afterMethod(ITestResult result)
	{		
		
		System.out.println("Test Case "+result.getStatus());
		System.out.println("<*****>Ended Execution of script "+result.getMethod().getMethodName()+"<*****>");
		System.out.println("************************************************************************************************");
		try {
			//testDriver.quit();
		attachScreenShotInHTMLreport(testDriver, result);
		
		finalizeReport();
		
	
		if(conn!=null)
		conn.close();
		} catch (SQLException e) {
			insertReportLine(e);
		}
		
	}
			
	@AfterSuite
	public void aftersuite() 
	{
		try {
			exceptionLogContent.flush();
			exceptionLogContent.close();
			reportGenerator.close();
			System.out.print("Open the following file to get the Current execution report>>: ");
			System.out.println(getReportFileName());
			testDriver.get(getReportFileName());
			
			
			WebElement summaryView = testDriver.findElement(By.xpath("//a[@id='enableDashboard']/i"));
			summaryView.click();
		
		} catch (Exception e) {
			insertReportLine(e);
		}
	}
	
	@DataProvider
	public Object[][] dataProvider(Method method)
	{
		 testName = method.getName();
		  reportTestData = ReadDataFromExcel(testName);
		  totalIterations=reportTestData.length;	 
		  initialTIme=testStartTime();
		  return reportTestData;
	}

	public void startTestForReport(String browser, String browserVersion, String os, String testName)
	{
		if(testExecutionPlatform.equalsIgnoreCase("Local Machine"))
		{
			if(reportTestData == null)
			{
				testStatus = reportGenerator.startTest(testName);
			}
			else if(reportTestData.length>1)
			{

				if(!reportAdded) {
					testStatus = reportGenerator.startTest(testName);
					dataProviderTest = testStatus;
					reportAdded = true;
				}
					if(st!=reportTestData.length) {
						for(int j = 0; j<=reportTestData[st].length-1; j++)
						{	
							testParaMeters  +="'"+reportTestData[st][j]+"'"+", ";
						}
						st++;
						testParaMeters = testParaMeters.substring(0, testParaMeters.length()-2);
						testStatus = reportGenerator.startTest(testName+"_"+st+"("+testParaMeters+")");
						testParaMeters = "";
						dataProviderTest.appendChild(testStatus);
					}
				}else
				{
				testStatus = reportGenerator.startTest(testName);
				}
		}
		else
		{
			 SeleniumHelper.startTest(testName+"_"+os+"_"+browser+"_"+browserVersion, "THIS TEST IS PART OF CROSS_BROWSER TESTING OF "+testName+" test case");
			
		}	
	}
	public void finalizeReport()
	{
		if(testExecutionPlatform.equalsIgnoreCase("Local Machine"))
		{
			if(reportTestData == null)
			{
				reportGenerator.flush();
				reportGenerator.endTest(testStatus);
				
			}
			else if(reportTestData.length>1)
			{
				reportGenerator.flush();
				if(st==reportTestData.length)
				{
					
					reportGenerator.endTest(dataProviderTest);
					
				}
			}
			else if(reportTestData.length ==1 ){
			reportGenerator.endTest(testStatus);
			reportGenerator.flush();
			}
		}
		else
			{
				reportGenerator.flush();
				SeleniumHelper.endTest();
			}
	
		}
	public WebDriver getWebDriver() {
	        return webDriver.get();
	    }

	    public String getSessionId() {
	        return sessionId.get();
	    }
	    
	    
	    protected void createDriver(String browser, String version, String os, String methodName) throws MalformedURLException, UnexpectedException {
	        DesiredCapabilities capabilities = new DesiredCapabilities();
	        capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
	        capabilities.setCapability(CapabilityType.VERSION, version);
	        capabilities.setCapability(CapabilityType.PLATFORM, os);
	        capabilities.setCapability("name", methodName+"_"+browser+"_"+version);

	        if (buildTag != null) {
	            capabilities.setCapability("build", buildTag);
	        }

	        //webDriver.set(new RemoteWebDriver(new URL("https://" + authentication.getUsername() + ":" + authentication.getAccessKey() + seleniumURI +"/wd/hub"), capabilities));
	        
	        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
	        sessionId.set(id);
	        
	        System.out.println("===================== Execution Started on Sauce Lab using "+browser.toUpperCase()+" browser on version "+version+" having session ID "+this.getSessionId()+" =====================");
	    }
	
		public synchronized void StartDriver()
		{	
			
			
				currentBrowser = "InternetExplorer";
				
				closeAllOpenedBrowsers();
				
				killExcel();
				
				if(currentBrowser.equalsIgnoreCase("Firefox"))
				{
					testDriver=SeleniumHelper.launchDriver(currentBrowser,System.getProperty("user.dir")+Environment("ffDriverPath")
							,Long.parseLong(Environment("ffDefaultTestSyncTimePeriod")));	
				}
				else if(currentBrowser.equalsIgnoreCase("InternetExplorer"))
				{
					testDriver=SeleniumHelper.launchDriver(currentBrowser,System.getProperty("user.dir")+Environment("ieDriverPath")
							,Long.parseLong(Environment("ieDefaultTestSyncTimePeriod")));	
				}
				else if(currentBrowser.equals("chrome"))
				{
					testDriver=SeleniumHelper.launchDriver(currentBrowser,Environment("chromeDriverPath")
							,Long.parseLong(Environment("chromeDefaultTestSyncTimePeriod")));
				}
			
			}
	
	public synchronized void StartDriver(String browser,String browserversion, String os)
	{	
		
			currentBrowser = "InternetExplorer";
			
			closeDriverServers();
			
			closeAllOpenedBrowsers();
			
			killExcel();
			
			if(currentBrowser.equalsIgnoreCase("Firefox"))
			{
				testDriver=SeleniumHelper.launchDriver(currentBrowser,""
						,Long.parseLong(Environment("ffDefaultTestSyncTimePeriod")));	
			}
			else if(currentBrowser.equalsIgnoreCase("InternetExplorer"))
			{
				//System.out.println(Environment("ieDriverPath"));
				testDriver=SeleniumHelper.launchDriver(currentBrowser,System.getProperty("user.dir")+Environment("ieDriverPath")
						,Long.parseLong(Environment("ieDefaultTestSyncTimePeriod")));	
			}
			else if(currentBrowser.equals("chrome"))
			{
				testDriver=SeleniumHelper.launchDriver(currentBrowser,Environment("chromeDriverPath")
						,Long.parseLong(Environment("chromeDefaultTestSyncTimePeriod")));
			}
		
		}
		
	public void startReport()
	{
		  extent = new ExtentReports(System.getProperty("user.dir")+"/ExtentReport-OutPut/myreport.html", true);
		  extent.addSystemInfo("Host Name", "Sample Host")
		  .addSystemInfo("EnvironMent","Test")
		  .addSystemInfo("User", "Sample User");
		  extent.loadConfig(new File(System.getProperty("user.dir")+"/extent-config.xml"));
	}

	public void getHomePage()
	{
		
		homePage = new HomePage();
	}
	
	public String testStartTime()
	{
		String startTIme=formatDateAndTime(getCurrentDateAndTime(),Environment("testTimeFormat"));
		return startTIme;
	}
	
	public String testEndTime()
	{
		String endTime=formatDateAndTime(getCurrentDateAndTime(),Environment("testTimeFormat"));
		return endTime;
	}
	
	public void updateStatus(boolean statusVal)
	{
		if(statusVal==false)
		{
			status = statusVal;
		}		 
		 currentIteration=currentIteration+1;
	}
		
	
}
