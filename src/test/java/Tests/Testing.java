package Tests;


import java.io.FileInputStream;

import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Testing extends BaseTest {

	public static void main(String[] args) throws Exception {
		FileInputStream file=new FileInputStream(System.getProperty("user.dir")+Environment("testDataFilePath_Test"));
		Workbook workbook=new XSSFWorkbook(file);
		Name name=workbook.getName("TestExecution_Details");
		@SuppressWarnings("deprecation")
		AreaReference area = new AreaReference(name.getRefersToFormula());  
		CellReference[] cellrefs = area.getAllReferencedCells();
		Sheet s = workbook.getSheet(name.getSheetName());
		int startRowindex = cellrefs[0].getRow();
		String appURL = s.getRow(startRowindex).getCell(1).getStringCellValue();
		workbook.close();
		System.out.println(appURL);
		
	}}
