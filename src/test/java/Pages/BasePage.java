package Pages;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import com.relevantcodes.extentreports.LogStatus;
import core.generic.utilities.AutomationCore;

@SuppressWarnings("deprecation")
public  class BasePage extends AutomationCore{
	
	HomePage homePage;
	ProviderSearchPage providerSearchPage;
	
	private int firstCharClassIndex ;
	private String currentbBaseScreenName;
	static String tierOneTotalCost = "";
		
	public String getHelClassName(@SuppressWarnings("rawtypes") Class c)
	{
		firstCharClassIndex = c.getName().lastIndexOf(".")+1;
		if(firstCharClassIndex>0)
		{
			currentbBaseScreenName = c.getName().substring(firstCharClassIndex).toString();
			return currentbBaseScreenName;
		}	
		
		return null;
	}
	
	public long getCurrentBrowserSyncTime()
	{
		if(currentBrowser.equals("ff"))
		{
			return Long.parseLong(Environment("ffDefaultTestSyncTimePeriod"));
		}
		else if(currentBrowser.equals("ie"))
		{
			return Long.parseLong(Environment("ieDefaultTestSyncTimePeriod"));
		}
		return 0;
	}
	
	boolean isTotalCostValidated = false;
	public void validateCalculations(int travelerCount, String actual, String expected, String actualTotalCost, String expectedTotalCost)
	{
		
		try {
			if(!isTotalCostValidated)
			{
				if(actualTotalCost.equals(expectedTotalCost))
				{
					insertReportLine(LogStatus.PASS, "The expected total cost of the trip is "+expectedTotalCost, "The actual Total Cost of the trip is is "+actualTotalCost);
					//sfAssert.assertTrue(true);
				}
				else
				{
					insertReportLine(LogStatus.FAIL, "The expected total cost of the trip is "+expectedTotalCost, "The actual Total Cost of the trip is "+actualTotalCost);
					sfAssert.assertTrue(false,"The expected total cost of the trip is "+expectedTotalCost+ "The actual Total Cost of the trip is "+actualTotalCost);
				}
				isTotalCostValidated = true;
			}
			
			if(actual.equals(expected))
			{
				insertReportLine(LogStatus.PASS, "The expected tripcost for Traveler~"+travelerCount+" is "+expected, "The actual tripcost for Traveler~"+travelerCount+" is "+actual);
				//Assert.assertTrue(true);
			}
			else
			{
				insertReportLine(LogStatus.FAIL, "The expected tripcost for Traveler~"+travelerCount+" is "+expected, "The actual tripcost for Traveler~"+travelerCount+" is "+actual);
				sfAssert.assertTrue(false, "The expected tripcost for Traveler~"+travelerCount+" is "+expected+ "The actual tripcost for Traveler~"+travelerCount+" is "+actual);
			}
			
			
		}catch(Exception e)
		{
			insertReportLine(e);
			//insertReportLine(LogStatus.FAIL, e.getClass().getName()+" Exception occured while validating the traveler tripcost.");
			sfAssert.assertTrue(false,  e.getClass().getName()+" Exception occured while validating the traveler tripcost.");
		}
	}
		
	//*-----------------specific to Safetrip Only-------------------------*//
	
	public  void selectDateFromCalender(String date,  WebDriver driver) 
	{
		//String pageName = getPageClassName();
		//counter = getStepCounter();
		//String testName = getMetodName();		
		
		String[] dateArr = date.split("/");
		String day = dateArr[1];
		String month = dateArr[0];
		String year = dateArr[2];
		
		String mnthNames = "";
		switch(Integer.parseInt(month)) {
		case 1:mnthNames = "Jan";break;	
		case 2:mnthNames = "Feb";break;	
		case 3:mnthNames = "Mar";break;
		case 4:mnthNames = "Apr";break;
		case 5:mnthNames = "May";break;
		case 6:mnthNames = "Jun";break;
		case 7:mnthNames = "Jul";break;
		case 8:mnthNames = "Aug";break;
		case 9:mnthNames = "Sep";break;
		case 10:mnthNames = "Oct";break;
		case 11:mnthNames = "Nov";break;
		case 12:mnthNames = "Dec";break;
		}
		
		try {
			WebElement yearEle = driver.findElement(By.className("ui-datepicker-year"));
			Select sel1 = new Select(yearEle);
			sel1.selectByVisibleText(year);
//			Assert.assertTrue(true);
//			
//			Assert.assertTrue(true);
//			
			insertReportLine(LogStatus.PASS, "Verify that <b>"+year+"</b> should be selected from <b style = 'color:#00008B;'>Year</b> dropdownlist","<b>"+year+"</b> is selected from <b style = 'color:#00008B;'>Year</b> dropdownlist");
			
		WebElement mnthEle = driver.findElement(By.className("ui-datepicker-month"));
		Select sel = new Select(mnthEle);
		sel.selectByVisibleText(mnthNames);
		
		//Assert.assertTrue(true);
		
		insertReportLine(LogStatus.PASS, "Verify that <b>"+mnthNames+"</b> should be selected from <b style = 'color:#00008B;'>Month</b> dropdownlist",""+mnthNames+" is selected from <b style = 'color:#00008B;'>Month</b> dropdownlist");
	
		day = Integer.toString((Integer.parseInt(day)));
	
		WebElement dayEle = driver.findElement(By.xpath("(//a[text()='"+day+"'])[1]"));
		dayEle.click();
		
		}catch(Exception e) {
			
			//e.printStackTrace();
			insertReportLine(e);
			insertReportLine(LogStatus.FAIL, "Unable to pick the date from calender", e.getClass().getName());
			Assert.assertTrue(false, e.getClass().getName()+" Exception Occured while selecting date from  calendar");
		}
	}
	
	
	public Object[][] getTravelerDetailsFromExcel(String tableName, int noOfTravelers)
	{
		 ArrayList<String> cellDataList = new ArrayList<String>();
		int rowCount=0;
		 int columnCount=0;
		 Object[][] data=null;
		 Cell c=null;
		 int nItr=0;
		try
		{
			FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+Environment("testDataFilePath"));
			Workbook workBook = new XSSFWorkbook(fileInputStream);
			int namedCellIdx = workBook.getNameIndex(tableName);			
		    org.apache.poi.ss.usermodel.Name aNamedCell = workBook.getNameAt(namedCellIdx);
			AreaReference area = new AreaReference(aNamedCell.getRefersToFormula());
			CellReference[] cellrefs = area.getAllReferencedCells();
			Sheet s = workBook.getSheet(aNamedCell.getSheetName());
			
			int startRowindex = cellrefs[0].getRow();
			
			int endRowindex = (startRowindex+noOfTravelers)-1;
			
			
			Row r = null;
			
			rowCount = (endRowindex-startRowindex)+1;
			columnCount = (s.getRow(startRowindex).getLastCellNum())-1;
			
			for(int i = startRowindex; i<=endRowindex; i++)
			{		
				 r = s.getRow(i);
				 try {
				for(int j=0; j<=r.getLastCellNum()-1;j++)
				{
				c= r.getCell(j);
				
				switch (c.getCellType()) 
				{
					case XSSFCell.CELL_TYPE_STRING:
						cellDataList.add(c.getStringCellValue().toString());
						
					break;
					case XSSFCell.CELL_TYPE_NUMERIC:
						c.setCellType(HSSFCell.CELL_TYPE_STRING);
						cellDataList.add(c.getStringCellValue().toString());
						break;
					case XSSFCell.CELL_TYPE_BLANK:
						c.setCellType(HSSFCell.CELL_TYPE_STRING);
						cellDataList.add(c.getStringCellValue().toString());
						break;
					case XSSFCell.CELL_TYPE_FORMULA:
						try
						{							
							cellDataList.add(c.getStringCellValue().toString());
						}
						catch(Exception e)
						{						
							if(DateUtil.isCellDateFormatted(c))
							{
								cellDataList.add(c.getDateCellValue().toString());
							}
							else if(e.getMessage().contains("Cannot get a text value from a numeric formula cell"))
							{
								c.setCellType(HSSFCell.CELL_TYPE_STRING);
								cellDataList.add(c.getStringCellValue().toString());
							}
						}	
				}												
			}
				 }catch(Exception e)
				 {
					
					 insertReportLine(LogStatus.FATAL, "Either any of the field value on the TravelerDetails section in excel sheet is blank OR the number of traveler count doesnot match the traveler records count.");
					 insertReportLine(e);
					Assert.assertTrue(false, "Either any of the field value on the TravelerDetails section in excel sheet is blank OR the number of traveler count doesnot match the traveler records count.");
				 }
		  }	
		workBook.close();
			
			if(cellDataList.size()!=0)
			{
				while(nItr<cellDataList.size())
				{
					data= new Object[rowCount][columnCount+1];
					for(int i=0;i<=rowCount-1;i++)
					{	
						for (int j=0;j<columnCount+1;j++)
						{
							data[i][j]= cellDataList.get(nItr);
							nItr++;
						}
					}
				}
			}
			return data;
		}
		catch (Exception exp)
		{
			exp.printStackTrace();
			insertReportLine(LogStatus.FATAL, "Exception Occured during retriving travler verify the data in excel sheeet.");
			 insertReportLine(exp);
		}
		return data;
	}
	
	 public  boolean isBetween(int lowerRange, int valueToTest, int upperRange) {
	        return lowerRange <= valueToTest && valueToTest <= upperRange;
	    }
	 
}



