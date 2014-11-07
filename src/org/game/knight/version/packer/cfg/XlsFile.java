package org.game.knight.version.packer.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.chw.util.MD5Util;

public class XlsFile
{
	private File file;
	private String fileMD5;
	private XlsLangFile langTable;

	public XlsFile(File file)
	{
		this.file = file;
	}

	public String getLangName()
	{
		String name = file.getName();
		int index = name.indexOf(".");
		if (index != -1)
		{
			name = name.substring(0, index);
		}
		return name;
	}

	public XlsLangFile getLangTable() throws FileNotFoundException, IOException
	{
		if (langTable == null)
		{
			langTable = new XlsLangFile(file);
		}
		return langTable;
	}

	public String getFileMD5() throws Exception
	{
		if (fileMD5 == null)
		{
			StringBuilder sb = new StringBuilder();

			if (file != null && file.exists() && file.length() > 0)
			{
				HSSFWorkbook xls = new HSSFWorkbook(new FileInputStream(file));
				for (int i = 0; i < xls.getNumberOfSheets(); i++)
				{
					sb.append(xls.getSheetName(i) + "\n");

					HSSFSheet sheet = xls.getSheetAt(0);
					for (int j = 0; j < sheet.getLastRowNum() + 1; j++)
					{
						HSSFRow row = sheet.getRow(j);
						if (row != null)
						{
							sb.append("row:");
							for (int k = 0; k < row.getLastCellNum() + 1; k++)
							{
								HSSFCell cell = row.getCell(k);
								if (cell != null)
								{
									if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN)
									{
										sb.append("cell:" + String.valueOf(cell.getBooleanCellValue()));
									}
									else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
									{
										sb.append("cell:" + String.valueOf(cell.getNumericCellValue()));
									}
									else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)
									{
										sb.append("cell" + cell.getCellFormula());
									}
									else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK)
									{
										sb.append("cell");
									}
									else
									{
										sb.append("cell:" + String.valueOf(cell.getStringCellValue()));
									}
								}
							}
							sb.append("\n");
						}
					}
				}
			}

			if (sb.length() > 0)
			{
				fileMD5 = MD5Util.md5Bytes(sb.toString().getBytes());
			}
			else
			{
				fileMD5 = "";
			}
		}
		return fileMD5;
	}
}
