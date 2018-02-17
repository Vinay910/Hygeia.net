package Tests;

import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Testing extends BaseTest {

	private static Workbook workBook;

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("user.dir")+Environment("testDataFilePath"));
		FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+Environment("testDataFilePath"));
		workBook = new XSSFWorkbook(fileInputStream);
		System.out.println(workBook);
	}

}
