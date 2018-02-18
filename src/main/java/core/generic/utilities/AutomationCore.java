package core.generic.utilities;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

@SuppressWarnings("deprecation")
public class AutomationCore {
	List<String> objectArray = new ArrayList<String>();
	public static String appURL = "";
	public static String pageTitle = "";
	public static WebDriver currentDriver;
	public static Object bp;
	public static String currentBrowser = "";
	static String newFile = null;
	public static SeleniumHelper seleniumHelper = new SeleniumHelper();
	protected static String testExecutionPlatform = "Local Machine";
	public static Object[][] reportTestData = null;
	public static ExtentTest dataProviderTest = null;
	public static String testParaMeters = "";
	public static ExtentReports reportGenerator;
	public static ExtentTest testStatus;
	public boolean reportCounterFlag = true;
	public Connection conn = null;
	static AutomationCore ac = new AutomationCore();
	static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
	public static String currentTestName = "Test";
	public String testName = null;
	public static String fieldName = "fName";
	public static String pageName = "tPage";
	public static SoftAssert sfAssert;
	private String fileName = "";

	public static synchronized void insertReportLine(LogStatus logStatus, String stepName) {
		insertReportLine(logStatus, "<p style = 'word-wrap:break-word;'><i style='color:RED;'>" + stepName + "</i></p>",
				"");
	}

	public static synchronized void insertReportLine(Throwable t) {
		insertReportLine(LogStatus.FATAL, "<b style = 'COLOR:RED;'>" + t.getClass().getName() + "EXCEPTION OCCURED</b>",
				"Click <a href = 'EXCEPTION_LOG.txt'>here</a> to get the Exception details");
		setExceptionLog(t);
	}

	public static synchronized void insertReportLine(LogStatus logStatus, String stepName, String stepDetails) {
		if (stepName.contains(fieldName)) {
			stepName = stepName.replace(fieldName, "<b style='color:#00008B;'>" + fieldName + "</b>");
		}
		if (stepDetails.contains(fieldName)) {
			stepDetails = stepDetails.replace(fieldName, "<b style='color:#00008B;'>" + fieldName + "</b>");
		}

		if (stepName.contains(pageName)) {
			stepName = stepName.replace(pageName, "<b style='color:#00008B;'>" + pageName + "</b>");
		}
		if (stepDetails.contains(pageName)) {
			stepDetails = stepDetails.replace(pageName, "<b style='color:#00008B;'>" + pageName + "</b>");
		}

		if (logStatus.toString().equalsIgnoreCase("pass") || logStatus.toString().equalsIgnoreCase("info")) {
			if (testExecutionPlatform.equalsIgnoreCase("Local Machine")) {
				testStatus.log(logStatus, "<p style = 'word-wrap:break-word;'>" + stepName + "</p>",
						"<p style = 'word-wrap:break-word;'>" + stepDetails + "</p>");
			} else {
				SeleniumHelper.getTest().log(logStatus, "<p style = 'word-wrap:break-word;'>" + stepName + "</p>",
						"<p style = 'word-wrap:break-word;'>" + stepDetails + "</p>");
			}
		} else {
			if (testExecutionPlatform.equalsIgnoreCase("Local Machine")) {
				testStatus.log(logStatus, "<p style = 'word-wrap:break-word;'>" + stepName + "</p>",
						"<p style = 'word-wrap:break-word;'><p style='color:RED;'>" + stepDetails + "</p></p>");
			} else {
				SeleniumHelper.getTest().log(logStatus, "<p style = 'word-wrap:break-word;'>" + stepName + "</p>",
						"<p style = 'word-wrap:break-word;'><i style='color:RED;'>" + stepDetails + "</i></p>");
			}
		}

	}

	public static synchronized Properties loadProperties() {
		InputStream instream = ac.getClass().getClassLoader().getResourceAsStream("config.properties");
		if (instream != null) {
			Properties prop = new Properties();
			try {
				prop.load(instream);
				return prop;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void closeDriverServers() {
		String line;
		String pidInfo = "";
		try {

			for (int i = 1; i <= 120; i++) {
				Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					pidInfo += line;
				}
				input.close();

				if (pidInfo.contains("IEDriverServer.exe")) {
					// System.out.println("Cleaning Driver Servers");

					Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");

					Thread.sleep(1000);
					pidInfo = "";
				} else {
					// System.out.println("Driver Servers Cleaned");
					break;
				}
			}
		} catch (Exception e) {

		}
	}

	public void clearBrowserCache(String browser) {
		if (browser.equalsIgnoreCase("Internet Explorer")) {
			closeIEBrowserCache();
		}
	}

	public void closeIEBrowserCache() {
		String line;
		String pidInfo = "";
		try {
			Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 255");

			for (int i = 1; i <= 120; i++) {
				Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) {
					pidInfo += line;
				}
				input.close();

				if (pidInfo.contains("rundll32.exe")) {
					Thread.sleep(1000);
					pidInfo = "";

				} else {
					// System.out.println("Driver Servers Cleaned");
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + "-----Line #" + e.getStackTrace()[0].getLineNumber());
		}

	}

	public void closeAllOpenedBrowsers() {
		try {
			String os = System.getProperty("os.name");
			if (os.contains("Windows")) {
				if (currentBrowser.toString().toLowerCase().equals("firefox")) {
					Runtime.getRuntime().exec("taskkill /F /IM firefox.exe");
				} else if (currentBrowser.toString().toLowerCase().equals("chrome")) {
					Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
				} else if (currentBrowser.toString().toLowerCase().equals("internetexplorer")) {
					Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
				}
			}
		} catch (IOException e) {
			e.getMessage();
		}
	}

	public void killExcel() {
		try {
			String os = System.getProperty("os.name");
			if (os.contains("Windows")) {
				Runtime.getRuntime().exec("taskkill /IM EXCEL.EXE");
			}
		} catch (IOException e) {
			e.getMessage();
		}
	}

	public static synchronized String Environment(String propertyName) {
		Properties propValue = loadProperties();
		return propValue.getProperty(propertyName);
	}

	public synchronized String readORProperties(String screenName, String objPropName) {
		try {
			File fXmlFile = new File(System.getProperty("user.dir") + Environment("objectRepoPath"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(screenName);

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					return eElement.getElementsByTagName(objPropName).item(0).getTextContent();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized Object[][] ReadDataFromExcel(String tableName) {
		ArrayList<String> cellDataList = new ArrayList<String>();
		ArrayList<String> newCellDataList = new ArrayList<String>();
		ArrayList<Integer> ignoredRows = new ArrayList<Integer>();
		int RowCount = 0;
		int columnCount = 0;
		int itr = 0;
		Object[][] data = null;
		int statuscol = 0;
		Cell c = null;
		int lastCellIndex = 0;
		int nItr = 0;
		boolean recordsFOund = false;
		System.out.println("k");

		try {
			FileInputStream fileInputStream = new FileInputStream(
					System.getProperty("user.dir") + Environment("testDataFilePath"));
			Workbook workBook = new XSSFWorkbook(fileInputStream);
			int namedCellIdx = workBook.getNameIndex(tableName.trim());
			// workBook.getname
			org.apache.poi.ss.usermodel.Name aNamedCell = workBook.getNameAt(namedCellIdx);
			AreaReference area = new AreaReference(aNamedCell.getRefersToFormula());
			CellReference[] cellrefs = area.getAllReferencedCells();
			Sheet s = workBook.getSheet(aNamedCell.getSheetName());
			for (int i = 0; i < cellrefs.length; i++) {
				Row r = s.getRow(cellrefs[i].getRow());
				if (statuscol == 0) {
					statuscol = (r.getLastCellNum() - r.getFirstCellNum()) - 1;
					lastCellIndex = statuscol;
				}
				c = r.getCell(cellrefs[i].getCol());

				switch (c.getCellType()) {
				case XSSFCell.CELL_TYPE_STRING:
					// str =true;
					cellDataList.add(c.getStringCellValue().toString());
					if (c.getStringCellValue().toString().trim().equals("No")
							&& c.getColumnIndex() - r.getFirstCellNum() == statuscol) {

						ignoredRows.add(c.getRowIndex());
					}
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					c.setCellType(HSSFCell.CELL_TYPE_STRING);
					cellDataList.add(c.getStringCellValue().toString());
					// num=true;
					break;
				case XSSFCell.CELL_TYPE_BLANK:
					c.setCellType(HSSFCell.CELL_TYPE_STRING);
					cellDataList.add(c.getStringCellValue().toString());
					break;

				case XSSFCell.CELL_TYPE_FORMULA:
					try {
						// str =true;
						cellDataList.add(c.getStringCellValue().toString());
						if (c.getStringCellValue().toString().trim().equals("No") && c.getColumnIndex() == statuscol) {

							ignoredRows.add(c.getRowIndex());
						}
					} catch (Exception e) {
						if (DateUtil.isCellDateFormatted(c)) {
							cellDataList.add(c.getDateCellValue().toString());
						} else if (e.getMessage().contains("Cannot get a text value from a numeric formula cell ")) {
							c.setCellType(HSSFCell.CELL_TYPE_STRING);
							cellDataList.add(c.getStringCellValue().toString());
						}
					}
				}

				if (i == cellrefs.length - 1) {
					columnCount = r.getPhysicalNumberOfCells();
					RowCount = (((i + 1) / columnCount) - (ignoredRows.size()));
					// RowCount=RowCount-1;
				}
			}
			workBook.close();
			recordsFOund = true;

		} catch (Exception exp) {
			exp.printStackTrace();
			System.out.println("Your test name and table name are not maching: " + tableName);
		}

		if (recordsFOund) {
			try {
				if (cellDataList.size() != 0) {

					while (itr < cellDataList.size()) {
						if (cellDataList.get(statuscol).equals("Yes")) {
							while (itr <= statuscol) {
								if (itr != statuscol) {
									newCellDataList.add(cellDataList.get(itr));
								}

								itr++;
							}

						} else {
							itr = (itr + 1) + lastCellIndex;
						}

						statuscol = (statuscol + lastCellIndex) + 1;
					}
				}

				if (newCellDataList.size() != 0) {
					while (nItr < newCellDataList.size()) {
						data = new Object[RowCount][columnCount - 1];
						for (int i = 0; i < RowCount; i++) {
							for (int j = 0; j < columnCount - 1; j++) {
								data[i][j] = newCellDataList.get(nItr);
								nItr++;
							}
						}
					}
				} else {
					System.out.println("No records are marked as 'Yes' in given table:" + tableName);
				}
			}

			catch (Exception e) {
				e.getMessage();
				data = null;
			}
		} else {
			System.out.println("No records avialble in given table:" + tableName);
		}

		return data;
	}
	//Called by getCurrentDateAndTime in BaseTest
	public static String formatDateAndTime(Date dateValue, String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		String formatedDateValue = ft.format(dateValue);
		return formatedDateValue;
	}

	public String formatDateAndTime(String dateValue, String inputformat, String outputFormat) {
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputformat);
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
		String formatedDateValue = null;
		Date inputDate = null;
		try {
			inputDate = inputDateFormat.parse(dateValue);
			formatedDateValue = outputDateFormat.format(inputDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formatedDateValue;
	}

	public String convertDateFromString(String dateValue, String currentFormat, String expFormat) {
		try {
			// Locale.setDefault(Locale.US);
			DateFormat ft = new SimpleDateFormat(currentFormat);
			DateFormat dft = new SimpleDateFormat(expFormat);
			Date formatedDateValue = (Date) ft.parse(dateValue);
			Date finalDate = (Date) dft.parse(formatedDateValue.toString());

			return finalDate.toString();

		} catch (Exception e) {
			e.getMessage();
		}
		return null;

	}
	//Called by getCurrentDateAndTime in BaseTest
	public static Date getCurrentDateAndTime() {
		Date dNow = new Date();
		return dNow;
	}

	public long timeDiffInSeconds(String time1, String time2, String formatValue) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatValue);
			Date date1 = format.parse(time1);
			Date date2 = format.parse(time2);
			long difference = date2.getTime() - date1.getTime();
			difference = (difference) / (1000);
			return difference;
		} catch (Exception e) {
			e.getMessage();
			return 0;
		}
	}

	public synchronized void attachScreenShotInHTMLreport(WebDriver driver, ITestResult result) {
		try {

			Date currentDate = getCurrentDateAndTime();
			String formatedCurrentDate = formatDateAndTime(currentDate, Environment("resultDateFormat"));
			String screenshotPath = SeleniumHelper.takeSnapShot(driver,
					System.getProperty("user.dir") + Environment("screenShotPathForHTMLreport") + result.getName() + "_"
							+ formatedCurrentDate + ".png");
			String image = testStatus.addScreenCapture(screenshotPath);

			if (Environment("ScreenShotToBeAttachedOnReport(ALWAYS/FAIL)").equals("FAIL")) {
				if ((result.getStatus() == ITestResult.FAILURE) || (result.getStatus() == ITestResult.SKIP))
					insertReportLine(LogStatus.FAIL,
							"<b style = 'color:RED;'>Test Case Failed, ScreenShot Attached>>>></b>", image);
				else if (result.getStatus() == ITestResult.SKIP)
					insertReportLine(LogStatus.SKIP,
							"<b style = 'color:YELLOW;'>Test Case Skipped, ScreenShot Attached>>>></b>", image);
			}

			else if (Environment("ScreenShotToBeAttachedOnReport(ALWAYS/FAIL)").equals("ALWAYS")) {
				if (result.getStatus() == ITestResult.FAILURE)
					insertReportLine(LogStatus.FAIL,
							"<b style = 'color:RED;'>Test Case Failed, ScreenShot Attached>>>></b>", image);
				else if (result.getStatus() == ITestResult.SUCCESS)
					insertReportLine(LogStatus.PASS,
							"<b style = 'color:GREEN;'>Test Case Passed, ScreenShot Attached>>>></b>", image);
				else if (result.getStatus() == ITestResult.SKIP)
					insertReportLine(LogStatus.SKIP,
							"<b style = 'color:YELLOW;'>Test Case Skipped, ScreenShot Attached>>>></b>", image);
			} else {
				System.out.println(
						"Enter Either ALWAYS or FAIL in the Properties File for <-ScreenShotToBeAttachedOnReport(ALWAYS/FAIL)-> property ");
			}

		} catch (Exception E) {
			System.out.println(E.getMessage());
		}
	}

	public synchronized void reportGenerator() {
		try {
			exceptionLogContent = new BufferedWriter(new FileWriter(new File(Environment("exceptionLog_FilePath"))));

			Date currentDate = getCurrentDateAndTime();
			String formatedCurrentDate = formatDateAndTime(currentDate, Environment("resultDateFormat"));
			setReportFileName(System.getProperty("user.dir") + Environment("extentReportPath")
					+ Environment("projectName") + "_NewHTMLreport_" + formatedCurrentDate + ".html");
			if (reportGenerator == null) {
				reportGenerator = new ExtentReports(getReportFileName(), true);
				reportGenerator.loadConfig(new File(System.getProperty("user.dir") + "/extent-config.xml"));
			}
		} catch (Exception e) {
			System.out.println("Exception Occured while generating Extent Report Files");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public synchronized ResultSet dbConnect(String qwery) throws SQLException {
		ResultSet rs = null;
		try {
			String dbEnvironMent = "", dbServerName = "", dbName = "", authenticationType = "", dbUserName = "",
					dbPassword = "";
			Object[][] as = ReadDataFromExcel("Db_Connect");
			for (int i = 0; i <= as.length - 1; i++) {
				for (int j = 0; j <= as[i].length - 1; j++) {
					switch (j) {
					// case 0: dbEnvironMent = (String) as[i][j];break;
					case 0:
						dbServerName = (String) as[i][j];
						break;
					case 1:
						dbName = (String) as[i][j];
						break;
					case 2:
						authenticationType = (String) as[i][j];
						break;
					case 3:
						dbUserName = (String) as[i][j];
						break;
					case 4:
						dbPassword = (String) as[i][j];
						break;
					default:
						System.out.println("invalid input");
					}
				}

			}
			String db_connect_string = "";

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			if (authenticationType.equals("Windows Authentication")) {
				db_connect_string = "jdbc:sqlserver://" + dbServerName + ";integratedSecurity=true";
				conn = DriverManager.getConnection(db_connect_string);
			} else {
				db_connect_string = "jdbc:sqlserver://" + dbServerName;
				conn = DriverManager.getConnection(db_connect_string, dbUserName, dbPassword);
			}
			Statement statement = conn.createStatement();
			rs = statement.executeQuery(qwery);

		} catch (Exception e) {

			testStatus.log(LogStatus.FAIL,
					e.getClass().getSimpleName() + " Exception Occured While fetching Data From DB");
		}

		return rs;

	}

	public String runQuerytoGetSingleColumnValue(String tableName, String qwery, String columnName) {
		ResultSet rs = null;
		String value = "";
		Statement statement = null;
		try {
			@SuppressWarnings("unused")
			String dbEnvironMent = "", dbServerName = "", dbName = "", authenticationType = "", dbUserName = "",
					dbPassword = "";
			Object[][] as = ReadDataFromExcel(tableName);
			for (int i = 0; i <= as.length - 1; i++) {
				for (int j = 0; j <= as[i].length - 1; j++) {
					switch (j) {
					// case 0: dbEnvironMent = (String) as[i][j];break;
					case 0:
						dbServerName = (String) as[i][j];
						break;
					case 1:
						dbName = (String) as[i][j];
						break;
					case 2:
						authenticationType = (String) as[i][j];
						break;
					case 3:
						dbUserName = (String) as[i][j];
						break;
					case 4:
						dbPassword = (String) as[i][j];
						break;
					default:
						System.out.println("invalid input");
					}
				}

			}
			String db_connect_string = "";

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			if (authenticationType.equals("Windows Authentication")) {
				db_connect_string = "jdbc:sqlserver://" + dbServerName + ";integratedSecurity=true";
				conn = DriverManager.getConnection(db_connect_string);
			} else {
				db_connect_string = "jdbc:sqlserver://" + dbServerName;
				conn = DriverManager.getConnection(db_connect_string, dbUserName, dbPassword);
			}
			statement = conn.createStatement();
			rs = statement.executeQuery(qwery);

			while (rs.next()) {

				for (int k = 1; k <= rs.getMetaData().getColumnCount(); k++) {

					String columnNames = rs.getMetaData().getColumnName(k);

					if (columnNames.equalsIgnoreCase(columnName)) {
						value = rs.getString(columnNames);
						break;
					}

				}

			}

			statement.getConnection().close();
			statement.close();
		} catch (Exception e) {

			testStatus.log(LogStatus.FAIL,
					e.getClass().getSimpleName() + " Exception Occured While fetching Data From DB");
			try {
				statement.getConnection().close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				statement.getConnection().close();
				statement.close();
			} catch (SQLException e) {
				System.out.println("The connection is closed");
			}
		}
		return value;
	}

	@SuppressWarnings("unused")
	public synchronized Statement dbConnectForSalesCompletedVerification() throws SQLException {
		// ResultSet rs = null;
		try {
			String dbEnvironMent = "", dbServerName = "", dbName = "", authenticationType = "", dbUserName = "",
					dbPassword = "";
			Object[][] as = ReadDataFromExcel("Db_Connect");
			for (int i = 0; i <= as.length - 1; i++) {
				for (int j = 0; j <= as[i].length - 1; j++) {
					switch (j) {
					// case 0: dbEnvironMent = (String) as[i][j];break;
					case 0:
						dbServerName = (String) as[i][j];
						break;
					case 1:
						dbName = (String) as[i][j];
						break;
					case 2:
						authenticationType = (String) as[i][j];
						break;
					case 3:
						dbUserName = (String) as[i][j];
						break;
					case 4:
						dbPassword = (String) as[i][j];
						break;
					default:
						System.out.println("invalid input");
					}
				}

			}
			String db_connect_string = "";

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			if (authenticationType.equals("Windows Authentication")) {
				db_connect_string = "jdbc:sqlserver://" + dbServerName + ";integratedSecurity=true";
				conn = DriverManager.getConnection(db_connect_string);
			} else {
				db_connect_string = "jdbc:sqlserver://" + dbServerName;
				conn = DriverManager.getConnection(db_connect_string, dbUserName, dbPassword);
			}
			Statement statement = conn.createStatement();

			return statement;
			// rs = statement.executeQuery(qwery);

		} catch (Exception e) {

			testStatus.log(LogStatus.FAIL,
					e.getClass().getSimpleName() + " Exception Occured While connecting with DB");
		} finally {

		}
		return null;

	}

	public String getBirthDate(int ageInYears) {
		int dobYear = ageInYears;
		int dobMonth = 1;
		int dobDay = -5;

		LocalDate now = LocalDate.now();
		LocalDate dob = now.minusYears(dobYear).minusMonths(dobMonth).minusDays(dobDay);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String output = dob.format(formatter);
		return output;
	}

	public int getDateDifff(String tripStartDate, String tripEndDate) {
		try {
			Date dateOne = new Date(tripStartDate);
			Date dateTwo = new Date(tripEndDate);// new Date(currentDate) ;
			String diff = "";
			long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
			diff = String.format("%d", (TimeUnit.MILLISECONDS.toHours(timeDiff)) / 24);
			return Integer.parseInt(diff) + 1;
		} catch (Exception e) {
			testStatus.log(LogStatus.FAIL, e.getClass().getName()
					+ " Exception Occured inside getDaysDiff Method while getting the days difference");
			Assert.assertTrue(false);
		}
		return 0;
	}

	public String addDaysToADate(String fromDate, String format, int days) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Calendar c = Calendar.getInstance();
			c.setTime(new Date(fromDate)); // Now use today date.
			c.add(Calendar.DATE, days); // Adding 5 days
			String output = sdf.format(c.getTime());
			// System.out.println(output);

			return output;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public int getDaysDifffromCurrentdate(String tripStartDate) {

		try {
			Date dateOne = new Date(tripStartDate);
			Date dateTwo = new Date();// new Date(currentDate) ;
			String diff = "";
			// System.out.println("comp "+dateTwo.compareTo(new Date()));

			if (dateOne.compareTo(new Date()) >= 1) {
				long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());

				diff = String.format("%d", (TimeUnit.MILLISECONDS.toHours(timeDiff)) / 24);

				return Integer.parseInt(diff);
			} else {
				testStatus.log(LogStatus.FAIL,
						"Start Date Provided in the sheet  is either a current date or a past date, which is not acceptable");
				Assert.assertTrue(false,
						"Start Date Provided in the sheet  is either a current date or a past date, which is not acceptable ");
			}
		} catch (Exception e) {
			testStatus.log(LogStatus.FAIL, e.getClass().getName()
					+ " Exception Occured inside getDaysDiff Method while getting the days difference");
			Assert.assertTrue(false);
		}
		return 0;
	}

	public static synchronized String getDataFromExcelSheet(String filePath, String sheetName, int row, int column) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(System.getProperty("user.dir") + Environment("testDataFilePath"));
			Workbook workBook = new XSSFWorkbook(fileInputStream);

			Sheet sheet = workBook.getSheet(sheetName);
			String s = sheet.getRow(row).getCell(column).getStringCellValue();
			workBook.close();
			return s;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getReportFileName() {
		return fileName;
	}

	public void setReportFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getObjectName() {
		String lineText;
		String lineArray[];
		int lineNumber = 0;
		String fileName;
		String outputFileName;
		String str;
		File file;
		try {
			// int lineNumber = 20;
			lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
			fileName = Thread.currentThread().getStackTrace()[3].getFileName();
			outputFileName = getPackageName(fileName) + "\\" + fileName;
			file = new File(outputFileName);
			str = FileUtils.readFileToString(file);

			lineArray = str.split("\n");
			lineText = lineArray[lineNumber - 1].trim();

			if (lineText.contains(",")) {
				int indexOfBracket = lineText.indexOf('(');
				lineText = lineText.substring(indexOfBracket + 1, lineText.indexOf(','));
			} else {
				lineText = lineText.substring(lineText.indexOf('(') + 1, lineText.indexOf(')'));
			}

			if (lineText.contains("seleniumHelper.isElementExist(")) {
				lineText = lineText.replace("seleniumHelper.isElementExist(", " ");
			}

			if (lineText.contains(".getText()")) {
				lineText = lineText.replace(".getText()", " ");
			}

			return lineText;
			// return "<b style='color:#00008B;'>"+lineText+"</b>";
		} catch (Throwable t) {
			return "ObjectOnLine#" + Integer.toString(lineNumber);
		}
	}

	public String getPackageName(String fileName) {
		String pageFilePath = ".\\src\\test\\java\\" + Environment("projectName").toLowerCase() + "\\pages";
		String testFilePath = ".\\src\\test\\java\\" + Environment("projectName").toLowerCase() + "\\tests";
		String genUtilitiesPath = ".\\src\\main\\java\\core\\generic\\utilities";
		String busUtilitiesPath = ".\\src\\main\\java\\core\\business\\utilities";
		File[] pagefiles = new File(pageFilePath).listFiles();

		File[] testfiles = new File(testFilePath).listFiles();
		File[] genUtilitiesFile = new File(genUtilitiesPath).listFiles();
		File[] busUtilitiesFile = new File(busUtilitiesPath).listFiles();

		for (File file : pagefiles) {
			if (file.isFile() && file.getName().contains(fileName)) {
				return pageFilePath;
			}
		}
		for (File file : testfiles) {
			if (file.isFile() && file.getName().contains(fileName)) {
				return testFilePath;
			}
		}
		for (File file : genUtilitiesFile) {
			if (file.isFile() && file.getName().contains(fileName)) {
				return genUtilitiesPath;
			}
		}
		for (File file : busUtilitiesFile) {
			if (file.isFile() && file.getName().contains(fileName)) {
				return busUtilitiesPath;
			}
		}
		return null;
	}

	static String elementXpath;

	String getObjectName(WebElement element, String pageName) {
		String objectName = "";
		String outputFileName = System.getProperty("user.dir") + "\\src\\main\\java\\safetrip\\pages\\" + pageName
				+ ".java";

		try {
			int indexOfColon = element.toString().indexOf(":", element.toString().indexOf(":") + 1);

			elementXpath = element.toString().substring(indexOfColon + 2);
			elementXpath = elementXpath.substring(0, elementXpath.length() - 1);

			File file = new File(outputFileName);
			String str = FileUtils.readFileToString(file);
			String x = str.substring(str.indexOf(elementXpath));
			String y = x.substring(x.indexOf("WebElement"), 200);

			String[] arr = y.split(" ");
			List<String> lst = new ArrayList<String>();
			for (String s : arr) {

				if (!s.equals("")) {
					lst.add(s);
				}
			}

			for (int i = 0; i <= lst.size() - 1; i++) {
				if (lst.get(i).contains(";")) {

					x = lst.get(i);
					objectName = x.substring(0, x.indexOf(';'));
					return objectName;

				}
			}

			return getDistinctObject(elementXpath, pageName);

		} catch (Exception e) {
			// e.printStackTrace();
			return getDistinctObject(elementXpath, pageName);

		}

	}

	public synchronized String getObjectName(WebElement element) {
		try {

			Map<String, String> objectNames = new HashMap<String, String>();

			int indexOfColon = element.toString().indexOf(":", element.toString().indexOf(":") + 1);

			elementXpath = element.toString().substring(indexOfColon + 2);

			if (objectNames.containsKey(elementXpath)) {
				// return objectNames.get(elementXpath);
			}

			Class<?> aClass = Class.forName("safetrip.pages." + pageName);
			Field[] fields = aClass.getFields();
			Field field1 = null;
			WebElement value = null;

			for (Field field : fields) {
				fieldName = field.getName();
				field1 = aClass.getField(fieldName);

				if (field1.get(bp) instanceof WebElement) {
					value = (WebElement) field1.get(bp);
				}
				String s1 = Integer.toHexString(System.identityHashCode(value));
				String s2 = Integer.toHexString(System.identityHashCode(element));

				if (s1.equals(s2)) {

					return fieldName;
				}
			}

		} catch (Exception e) {

			System.out.println("Exceprion On getObjectName Method " + e.getMessage() + " the exception is "
					+ e.getClass().getName());
			return elementXpath;
		}
		return elementXpath;
	}

	public String getDistinctObject(String elementXpath, String pageName) {

		try {

			String outputFileName = System.getProperty("user.dir") + "\\src\\main\\java\\safetrip\\pages\\" + pageName
					+ ".java";
			File file = new File(outputFileName);
			String str = FileUtils.readFileToString(file);
			str = str.substring(str.indexOf("DYNAMIC XPATH"), str.indexOf("DYNAMIC XPATH END"));
			List<String> xpathList = new ArrayList<String>();
			String[] xpathArray = str.split("\n");

			for (String x : xpathArray) {
				if (x.contains("XXX") && (x.contains("//") || x.contains("/*"))) {
					xpathList.add(x.trim());
				}

			}
			String xpathsOfPages = "";
			String xpathx = "";
			List<String> xpathsArray = new ArrayList<String>();
			for (int i = 0; i <= xpathList.size() - 1; i++) {
				if (i != xpathList.size() - 1) {
					xpathsOfPages = xpathList.get(i).trim();
				} else {
					xpathsOfPages = xpathList.get(i)
							.substring(xpathList.get(i).indexOf("String"), xpathList.get(i).indexOf(';') + 1).trim();
				}
				xpathx = xpathsOfPages.substring(xpathsOfPages.indexOf('=') + 1, xpathsOfPages.length()).trim();
				xpathx = xpathx.substring(1, xpathx.length() - 2);
				xpathsArray.add(xpathx);
			}

			String correctXpath = getObjectXpath(elementXpath.trim(), xpathsArray);
			String objectXpathName = "";
			for (String x : xpathList) {
				if (x.contains(correctXpath)) {
					objectXpathName = x.substring(0, x.indexOf('=')).trim();
					break;
				}
			}
			String object = elementXpath;
			str = FileUtils.readFileToString(file);
			xpathArray = str.split("\n");

			for (String f : xpathArray) {
				if (f.contains(objectXpathName + ".replace(")) {
					object = f.substring(0, f.indexOf('=')).trim();
					break;
				}

			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>inside distinct" + object);
			return object;
		} catch (Exception e) {
			return elementXpath;
		}
	}

	public static String getObjectXpath(String elementXpath, List<String> dynamicXpathList) {
		String xpathFromPageFile = "";
		char chr = ';';
		// int lengthDiff = 0;
		int indexOfX = 0;
		int indexOfele = 0;
		try {
			for (int i = 0; i <= dynamicXpathList.size() - 1; i++) {
				xpathFromPageFile = dynamicXpathList.get(i);

				indexOfX = xpathFromPageFile.indexOf('X');
				chr = xpathFromPageFile.charAt(indexOfX + 3);
				indexOfele = elementXpath.indexOf(chr, indexOfX);

				if (xpathFromPageFile.substring(0, indexOfX - 1).equals(elementXpath.substring(0, indexOfX - 1))) {
					if (xpathFromPageFile.trim().substring(indexOfX + 3)
							.equals(elementXpath.trim().substring(indexOfele))) {
						return xpathFromPageFile;
					}
				}

			}
		} catch (Exception e)

		{
			System.out.println("Exception inside getObjectXpath method inside SeleniumHelper Class");
			e.printStackTrace();
		}
		return xpathFromPageFile;
	}

	public void createTestNGxmlFile(String xlsFileName, String xmlFileName) {

		DocumentBuilderFactory domFactory = null;
		DocumentBuilder domBuilder = null;
		String suiteName = "";
		String testName = "";
		int dataStartRow = 0;
		int dataStartCol = 0;
		ByteArrayOutputStream baos = null;
		OutputStreamWriter osw = null;
		Document newDoc = null;

		try {
			InputStream InputStream = new FileInputStream(new File(xlsFileName));
			@SuppressWarnings("resource")
			HSSFWorkbook workBook = new HSSFWorkbook(InputStream);
			HSSFSheet sheet = workBook.getSheet("Test Cases");
			Iterator<?> rows = sheet.rowIterator();

			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				System.out.println("Last Row Number --" + sheet.getLastRowNum());

				int rowNumber = row.getRowNum();
				Iterator<?> cells = row.cellIterator();

				while (cells.hasNext()) {

					HSSFCell cell = (HSSFCell) cells.next();

					int cellNumber = cell.getCellType();

					if (cell.getStringCellValue().trim().equals("Suite Name")) {
						suiteName = row.getCell(cellNumber + 1).getStringCellValue();
						System.out.println("suite Name " + suiteName);
						System.out.println("cell Number " + cellNumber + 1);
						System.out.println("Row Number " + rowNumber);
					} else if (cell.getStringCellValue().trim().equals("Test Name")) {
						testName = row.getCell(cellNumber + 1).getStringCellValue();
						System.out.println("Test Name " + testName);
					} else if (cell.getStringCellValue().trim()
							.equals("Fully Qualified TestNG class nameTest ClassName")) {
						System.out.println("row number " + rowNumber);
						System.out.println("Cell Number" + cellNumber);
						dataStartRow = rowNumber + 1;
						dataStartCol = cellNumber;
					}
				}

			}

			domFactory = DocumentBuilderFactory.newInstance();
			domBuilder = domFactory.newDocumentBuilder();
			newDoc = domBuilder.newDocument();
			Element suiteElement = newDoc.createElement("suite");
			suiteElement.setAttribute("verbose", "1");
			suiteElement.setAttribute("name", suiteName);
			suiteElement.setAttribute("parallel", "tests");
			suiteElement.setAttribute("thread-count", "4");
			newDoc.appendChild(suiteElement);
			Element parameterELement = newDoc.createElement("parameter");
			parameterELement.setAttribute("name", "browser");
			parameterELement.setAttribute("value", "firefox");
			
			suiteElement.appendChild(parameterELement);

			Element testElement = newDoc.createElement("test");
			testElement.setAttribute("name", testName);
			suiteElement.appendChild(testElement);
			// rowElement.setAttribute("name", "Test Method Name");

			Element classesElement = newDoc.createElement("classes");
			// classesElement.appendChild(newDoc.createTextNode("value"));
			testElement.appendChild(classesElement);

			Element classElement = null;
			Element methodElement = null;
			String testNgClassName = "";

			for (int i = dataStartRow; i != sheet.getLastRowNum(); i++) {
				// testNgClassName = sheet.getRow(i).getCell(dataStartCol).getStringCellValue();
				@SuppressWarnings("unused")
				String testns2 = sheet.getRow(dataStartRow).getCell(dataStartCol).getStringCellValue();

				testNgClassName = sheet.getRow(i).getCell(dataStartCol).getStringCellValue();
				if (!testNgClassName.equals("")) {

					classElement = newDoc.createElement("class");
					classElement.setAttribute("name", sheet.getRow(i).getCell(dataStartCol).getStringCellValue());// ****Here
																													// the
																													// Test
																													// class
																													// Name
																													// should
																													// be
																													// declared

					classesElement.appendChild(classElement);
					methodElement = newDoc.createElement("methods");
					classElement.appendChild(methodElement);

				}

				if (sheet.getRow(i).getCell(dataStartCol + 2).getStringCellValue().trim().equals("YES")) {
					Element includeElement = newDoc.createElement("include");
					includeElement.setAttribute("name", sheet.getRow(i).getCell(dataStartCol + 1).getStringCellValue());// ***include
																														// the
																														// test
																														// case
																														// Names
																														// from
																														// the
																														// test
																														// classes****//
					methodElement.appendChild(includeElement);
				} else if (sheet.getRow(i).getCell(dataStartCol + 2).getStringCellValue().trim().equals("NO")) {
					Element includeElement = newDoc.createElement("exclude");
					includeElement.setAttribute("name", sheet.getRow(i).getCell(dataStartCol + 1).getStringCellValue());// ***include
																														// the
																														// test
																														// case
																														// Names
																														// from
																														// the
																														// test
																														// classes****//
					methodElement.appendChild(includeElement);
				} else if (!sheet.getRow(i).getCell(dataStartCol + 1).getStringCellValue().trim().equals("")
						&& sheet.getRow(i).getCell(dataStartCol + 2).getStringCellValue().trim().equals("")) {
					// Should add the Extend Reporting message with fatal error.
					System.out.println(
							"testng.xml file cannot be created properly, flag the test case names as either YES or NO");
					Assert.assertTrue(false);
				}
			}
		} catch (Exception e) {

		}

		try {

			baos = new ByteArrayOutputStream();
			osw = new OutputStreamWriter(baos);

			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
			aTransformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://testng.org/testng-1.0.dtd");

			Source src = new DOMSource(newDoc);
			Result result = new StreamResult(new File(xmlFileName));
			aTransformer.transform(src, result);

			osw.flush();
			System.out.println(new String(baos.toByteArray()));

		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			try {
				osw.close();
			} catch (Exception e) {
			}
			try {
				baos.close();
			} catch (Exception e) {
			}
		}

	}

	public static PrintStream exceptionLog;
	static String currentTestExceptionDetails;
	protected static BufferedWriter exceptionLogContent;
	public static ByteArrayOutputStream baos;

	public static synchronized void setExceptionLog(Throwable t) {
		try {
			String exceptionMessage = t.getMessage();
			String exceptionLocalizedMessage = t.getLocalizedMessage();
			String writeContent = "TEST CASE NAME: " + currentTestName + System.getProperty("line.separator")
					+ "BROWSER:" + currentBrowser + System.lineSeparator();

			String underLine = "";
			for (int i = 0; i <= writeContent.length() - 1; i++) {
				underLine += "-";
			}
			exceptionLogContent.append(writeContent + underLine + System.getProperty("line.separator"));
			System.out.println(writeContent + underLine + System.getProperty("line.separator"));

			baos = new ByteArrayOutputStream();
			exceptionLog = new PrintStream(baos);

			t.printStackTrace(exceptionLog);

			String exceptionContent = new String(baos.toByteArray(), StandardCharsets.UTF_8);
			exceptionLogContent.append(exceptionContent);
			System.out.println(exceptionContent);

			underLine = "";
			for (int i = 0; i <= writeContent.length() - 1; i++) {
				underLine += "*";
			}
			currentTestExceptionDetails = System.getProperty("line.separator") + "EXCEPTION MESSAGE: "
					+ exceptionMessage + System.getProperty("line.separator") + "EXCEPTION LOCALISED MESSAGE:"
					+ exceptionLocalizedMessage + System.lineSeparator() + underLine + System.lineSeparator()
					+ System.lineSeparator();
			exceptionLogContent.append(currentTestExceptionDetails);

			exceptionLog.flush();
			exceptionLog.close();

		} catch (Throwable th) {

		}
	}

	public synchronized void setBP(Object obj) {
		bp = obj;
	}

	public void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	public synchronized Object getBP() {
		return bp;

	}

}
