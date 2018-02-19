package Tests;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Testing extends BaseTest {

	public static void main(String[] args) throws Exception
	{
		ReadDataFromExcel1("HomePageValidation");
	}
	
	public synchronized static Object[][] ReadDataFromExcel1(String tableName) {
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

		try {
			FileInputStream fileInputStream = new FileInputStream(
					System.getProperty("user.dir") + Environment("testDataFilePath"));
			Workbook workBook = new XSSFWorkbook(fileInputStream);
			int namedCellIdx = workBook.getNameIndex(tableName.trim());
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
					cellDataList.add(c.getStringCellValue().toString());
					if (c.getStringCellValue().toString().trim().equalsIgnoreCase("No")
							&& c.getColumnIndex() - r.getFirstCellNum() == statuscol) 
					{
						ignoredRows.add(c.getRowIndex());
					}
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
					try {
						cellDataList.add(c.getStringCellValue().toString());
						if (c.getStringCellValue().toString().trim().equalsIgnoreCase("No") && c.getColumnIndex() == statuscol)
						{
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
						if (cellDataList.get(statuscol).equalsIgnoreCase("Yes")) {
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
		

	}
	


