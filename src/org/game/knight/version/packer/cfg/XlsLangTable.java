package org.game.knight.version.packer.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.xml2as.builder.LangTable;

public class XlsLangTable extends LangTable
{
	private File xls;
	private String sheetName;

	private HashMap<String, HashMap<String, String>> oldRows = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> newRows = new HashMap<String, HashMap<String, String>>();

	/**
	 * 构造函数
	 * 
	 * @param xls
	 */
	public XlsLangTable(File xls)
	{
		this.xls = xls;
	}

	/**
	 * 获取文本
	 */
	@Override
	public String getText(String key)
	{
		HashMap<String, String> oldMap = oldRows.get(sheetName);
		HashMap<String, String> newMap = newRows.get(sheetName);

		if (!newMap.containsKey(key))
		{
			if (oldMap.containsKey(key))
			{
				newMap.put(key, oldMap.get(key));
			}
			else
			{
				newMap.put(key, key);
			}
		}

		return newMap.get(key);
	}

	/**
	 * 打开指定的表
	 * 
	 * @param name
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void setSheet(String name) throws FileNotFoundException, IOException
	{
		if (oldRows.containsKey(name))
		{
			return;
		}

		sheetName = name;
		oldRows.put(name, new HashMap<String, String>());
		newRows.put(name, new HashMap<String, String>());

		if (!xls.exists())
		{
			return;
		}

		HashMap<String, String> map = oldRows.get(name);

		HSSFWorkbook inputBook = new HSSFWorkbook(new FileInputStream(xls));

		for (int i = 0; i < inputBook.getNumberOfSheets(); i++)
		{
			HSSFSheet sheet = inputBook.getSheetAt(i);
			if (inputBook.getSheetName(i).trim().equals(sheetName))
			{
				for (int j = 1; j < sheet.getLastRowNum() + 1; j++)
				{
					HSSFRow row = sheet.getRow(j);

					HSSFCell keyCell = row.getCell(3);
					HSSFCell valCell = row.getCell(4);

					if (keyCell != null)
					{
						String key = keyCell.getStringCellValue();
						String val = valCell != null ? valCell.getStringCellValue() : "";

						if (key.equals(val))
						{
							continue;
						}

						key = key.trim();
						val = val.trim();

						if (!map.containsKey(key))
						{
							map.put(key, val);
						}
						else if (map.get(key).isEmpty())
						{
							map.put(key, val);
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * 保存
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void save() throws FileNotFoundException, IOException
	{
		HSSFWorkbook book = new HSSFWorkbook();

		HSSFFont font = book.createFont();
		font.setColor(HSSFColor.WHITE.index);

		HSSFCellStyle headerStyle = book.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFCellStyle style1 = book.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		style1.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style1.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style1.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style1.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);

		HSSFCellStyle style2 = book.createCellStyle();
		style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		style2.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style2.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style2.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style2.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);

		HSSFCellStyle style3 = book.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		style3.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style3.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style3.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style3.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);

		String[] sheetNames = newRows.keySet().toArray(new String[] {});
		Arrays.sort(sheetNames);
		for (String sheetName : sheetNames)
		{
			final HashMap<String, String> oldMap = oldRows.get(sheetName);
			final HashMap<String, String> newMap = newRows.get(sheetName);

			if (newMap.size() == 0 && oldMap.size() == 0)
			{
				continue;
			}

			int rowIndex = 1;

			int charCount = 0;
			int charCountAdded = 0;
			int charCountDeled = 0;
			int wordCount = 0;
			int wordCountAdded = 0;
			int wordCountDeled = 0;

			HSSFSheet sheet = book.createSheet(sheetName);
			HSSFRow row;
			HSSFCell cell;

			// 表头
			row = sheet.createRow(0);
			row.setHeight((short) 500);
			String[] titles = new String[] { "状态", "ID", "字段", "原文", "译文", "字数", "", "", "" };
			int[] widths = new int[] { 2000, 2000, 3000, 20000, 20000, 2000, 2000, 5000, 5000 };
			for (int i = 0; i < titles.length; i++)
			{
				int width = i < widths.length ? widths[i] : 20000;
				sheet.setColumnWidth(i, width);

				String title = titles[i];
				if (title.isEmpty() == false)
				{
					cell = row.createCell(i);
					cell.setCellValue(title);
					cell.setCellStyle(headerStyle);
				}
			}

			// 表体
			HashSet<String> keySet = new HashSet<String>();
			for (String key : newMap.keySet())
			{
				keySet.add(key);
			}
			for (String key : oldMap.keySet())
			{
				keySet.add(key);
			}
			String[] keys = keySet.toArray(new String[] {});
			Arrays.sort(keys, new Comparator<String>()
			{
				@Override
				public int compare(String o1, String o2)
				{
					int added1 = (newMap.containsKey(o1) && !oldMap.containsKey(o1)) ? 0 : newMap.containsKey(o1) ? 1 : 2;
					int added2 = (newMap.containsKey(o2) && !oldMap.containsKey(o2)) ? 0 : newMap.containsKey(o2) ? 1 : 2;
					if (added1 == added2)
					{
						return o1.compareTo(o2);
					}
					else
					{
						return added1 - added2;
					}
				}
			});

			for (String key : keys)
			{
				boolean isAdd = !oldMap.containsKey(key);
				boolean isDel = !newMap.containsKey(key);

				row = sheet.createRow(rowIndex);

				cell = row.createCell(0);
				cell.setCellValue(!oldMap.containsKey(key) ? "+" : !newMap.containsKey(key) ? "-" : "");
				cell.setCellStyle(style1);

				cell = row.createCell(1);
				cell.setCellValue("");
				cell.setCellStyle(style2);

				cell = row.createCell(2);
				cell.setCellValue("");
				cell.setCellStyle(style2);

				cell = row.createCell(3);
				cell.setCellValue(key);
				cell.setCellStyle(style2);

				cell = row.createCell(4);
				cell.setCellValue(newMap.containsKey(key) ? newMap.get(key) : oldMap.get(key));
				cell.setCellStyle(style2);

				cell = row.createCell(5);
				cell.setCellValue(key.length());
				cell.setCellStyle(style3);

				rowIndex++;
				charCount += key.length();
				wordCount++;
				if (isAdd)
				{
					charCountAdded += key.length();
					wordCountAdded++;
				}
				else if (isDel)
				{
					charCountDeled += key.length();
					wordCountDeled++;
				}
			}

			row = sheet.getRow(1) != null ? sheet.getRow(1) : sheet.createRow(1);
			cell = row.createCell(7);
			cell.setCellValue("统计信息");
			cell.setCellStyle(headerStyle);

			row = sheet.getRow(2) != null ? sheet.getRow(2) : sheet.createRow(2);
			cell = row.createCell(7);
			cell.setCellValue("总计词条数：" + wordCount);
			cell.setCellStyle(style2);

			row = sheet.getRow(3) != null ? sheet.getRow(3) : sheet.createRow(3);
			cell = row.createCell(7);
			cell.setCellValue("总计字符数：" + charCount);
			cell.setCellStyle(style2);

			row = sheet.getRow(4) != null ? sheet.getRow(4) : sheet.createRow(4);
			cell = row.createCell(7);
			cell.setCellValue("新增词条数：" + wordCountAdded);
			cell.setCellStyle(style2);

			row = sheet.getRow(5) != null ? sheet.getRow(5) : sheet.createRow(5);
			cell = row.createCell(7);
			cell.setCellValue("新增字符数：" + charCountAdded);
			cell.setCellStyle(style2);

			row = sheet.getRow(6) != null ? sheet.getRow(6) : sheet.createRow(6);
			cell = row.createCell(7);
			cell.setCellValue("删除词条数：" + wordCountDeled);
			cell.setCellStyle(style2);

			row = sheet.getRow(7) != null ? sheet.getRow(7) : sheet.createRow(7);
			cell = row.createCell(7);
			cell.setCellValue("删除字符数：" + charCountDeled);
			cell.setCellStyle(style2);
		}

		FileOutputStream output = new FileOutputStream(xls);
		book.write(output);
		output.close();
	}
}
