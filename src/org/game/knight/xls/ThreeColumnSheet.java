package org.game.knight.xls;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ThreeColumnSheet extends AbsSheet
{
	/**
	 * ¹¹½¨ÄÚÈÝ
	 * 
	 * @param inputSheet
	 * @param outputBook
	 * @param outputSheet
	 */
	@Override
	protected void createRows(HSSFSheet inputSheet, HSSFWorkbook outputBook, HSSFSheet outputSheet)
	{
		HSSFRow row;

		int rowIndex = 0;
		int charCount = 0;

		for (int i = 1; i < inputSheet.getLastRowNum() + 1; i++)
		{
			HSSFRow inputRow = inputSheet.getRow(i);

			String str0;
			String str1;
			String str2;

			str0 = getCellValue(inputRow, 0).trim();

			str1 = getCellValue(inputRow, 1).trim();
			str2 = getCellValue(inputRow, 4).trim();
			if (str1.length() > 0 || str2.length() > 0)
			{
				rowIndex++;
				row = createRow(rowIndex);
				createCell(row, 0, "");
				createCell(row, 1, Double.parseDouble(str0));
				createCell(row, 2, "name");
				createCell(row, 3, str1);
				createCell(row, 4, str2);
				createCell(row, 5, str1.length());
				charCount += str1.length();
			}

			str1 = getCellValue(inputRow, 2).trim();
			str2 = getCellValue(inputRow, 5).trim();
			if (str1.length() > 0 || str2.length() > 0)
			{
				rowIndex++;
				row = createRow(rowIndex);
				createCell(row, 0, "");
				createCell(row, 1, Double.parseDouble(str0));
				createCell(row, 2, "desc");
				createCell(row, 3, str1);
				createCell(row, 4, str2);
				createCell(row, 5, str1.length());
				charCount += str1.length();
			}

			str1 = getCellValue(inputRow, 3).trim();
			str2 = getCellValue(inputRow, 6).trim();
			if (str1.length() > 0 || str2.length() > 0)
			{
				rowIndex++;
				row = createRow(rowIndex);
				createCell(row, 0, "");
				createCell(row, 1, Double.parseDouble(str0));
				createCell(row, 2, "desc_from");
				createCell(row, 3, str1);
				createCell(row, 4, str2);
				createCell(row, 5, str1.length());
				charCount += str1.length();
			}
		}

		rowIndex++;
		row = createRow(rowIndex);
		createCell(row, 5, charCount);
	}
}
