package Tests;
import java.io.FileInputStream;
import java.util.LinkedList;

import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;


public class Testing extends BaseTest {

	public static void main(String[] args) throws Exception
	{
		Login();
	}
	
	public static void Login() throws Exception {
		
		TestCaseRunner("Login");
		
	}
	public static void TestCaseRunner(String TestCase) throws Exception
	{
		LinkedList<String> linky=new LinkedList<String>();
		FileInputStream file=new FileInputStream(System.getProperty("user.dir")+Environment("testDataFilePath_Test"));
		Workbook workbook=new XSSFWorkbook(file);
		Name name=workbook.getName(TestCase);
		@SuppressWarnings("deprecation")
		AreaReference area = new AreaReference(name.getRefersToFormula());  
		CellReference[] cellrefs = area.getAllReferencedCells();
		Sheet s = workbook.getSheet(name.getSheetName());
		int start=cellrefs[0].getCol();
		int end=cellrefs[cellrefs.length-1].getCol();
		for(int i=start;i<=end;i++)
		{
			int j=0;
			linky.add(s.getRow(cellrefs[j].getRow()).getCell(i).toString());
			j++;
		}
		

	}
	
}

