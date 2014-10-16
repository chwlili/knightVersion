package org.game.knight.xls;

import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class AbsSheet
{
	private HSSFWorkbook book;
	private HSSFSheet sheet;

	private HashMap<Integer, HSSFCellStyle> formats = new HashMap<Integer, HSSFCellStyle>();

	/**
	 * 执行
	 * 
	 * @param inputSheet
	 * @param inputSheetName
	 * @param outputBook
	 * @return
	 */
	public HSSFSheet exec(HSSFSheet inputSheet, String inputSheetName, HSSFWorkbook outputBook)
	{
		HSSFSheet outputSheet = outputBook.createSheet(inputSheetName);

		book = outputBook;
		sheet = outputSheet;

		// 构建标题
		createHeader(outputBook, outputSheet);

		// 构建内容
		createRows(inputSheet, outputBook, outputSheet);

		return outputSheet;
	}

	/**
	 * 构建标题
	 * 
	 * @param book
	 * @param sheet
	 */
	private void createHeader(HSSFWorkbook book, HSSFSheet sheet)
	{
		HSSFFont font = book.createFont();
		font.setColor(HSSFColor.WHITE.index);

		HSSFCellStyle headerStyle = book.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) getHeadHeight());

		String[] titles = getHeadText();
		int[] widths = getHeadWidth();
		for (int i = 0; i < titles.length; i++)
		{
			int width = i < widths.length ? widths[i] : 20000;
			sheet.setColumnWidth(i, width);

			String title = titles[i];
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(title);
			cell.setCellStyle(headerStyle);
		}
	}

	/**
	 * 构建内容
	 * 
	 * @param inputSheet
	 * @param outputBook
	 * @param outputSheet
	 */
	protected void createRows(HSSFSheet inputSheet, HSSFWorkbook outputBook, HSSFSheet outputSheet)
	{
	}

	/**
	 * 获取标题文本
	 * 
	 * @return
	 */
	protected String[] getHeadText()
	{
		return new String[] { "状态", "ID", "字段", "原文", "译文", "字数" };
	}

	/**
	 * 获取标题宽度
	 * 
	 * @return
	 */
	protected int[] getHeadWidth()
	{
		return new int[] { 2000, 2000, 3000, 20000, 20000, 2000 };
	}

	/**
	 * 获取标题高度
	 * 
	 * @return
	 */
	protected int getHeadHeight()
	{
		return 500;
	}

	/**
	 * 获取格式
	 * 
	 * @return
	 */
	protected HSSFCellStyle getFormat(int index)
	{
		if (!formats.containsKey(index))
		{
			formats.put(index, createFormat(book, index));
		}
		return formats.get(index);
	}

	/**
	 * 创建格式
	 * 
	 * @param index
	 * @return
	 */
	protected HSSFCellStyle createFormat(HSSFWorkbook book, int index)
	{
		HSSFCellStyle style = book.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		style.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		style.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);

		if (index == 5)
		{
			style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		return style;
	}

	/**
	 * 创建行
	 * 
	 * @param sheet
	 * @param index
	 * @return
	 */
	protected HSSFRow createRow(int index)
	{
		return sheet.createRow(index);
	}

	/**
	 * 创建单元格
	 * 
	 * @param row
	 * @param index
	 * @return
	 */
	protected HSSFCell createCell(HSSFRow row, int index, String value)
	{
		HSSFCell cell = row.createCell(index);
		cell.setCellStyle(getFormat(index));
		cell.setCellValue(value);

		return cell;
	}

	/**
	 * 创建单元格
	 * 
	 * @param row
	 * @param index
	 * @return
	 */
	protected HSSFCell createCell(HSSFRow row, int index, double value)
	{
		HSSFCell cell = row.createCell(index);
		cell.setCellStyle(getFormat(index));
		cell.setCellValue(value);

		return cell;
	}

	/**
	 * 创建单元格
	 * 
	 * @param row
	 * @param index
	 * @return
	 */
	protected HSSFCell createCell(HSSFRow row, int index, Boolean value)
	{
		HSSFCell cell = row.createCell(index);
		cell.setCellStyle(getFormat(index));
		cell.setCellValue(value);

		return cell;
	}

	/**
	 * 获取单元格值
	 * 
	 * @param row
	 * @param index
	 * @return
	 */
	protected String getCellValue(HSSFRow row, int index)
	{
		HSSFCell cell = row.getCell(index);
		if (cell != null)
		{
			if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN)
			{
				return String.valueOf(cell.getBooleanCellValue());
			}
			else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			{
				return String.valueOf(cell.getNumericCellValue());
			}
			else
			{
				return String.valueOf(cell.getStringCellValue());
			}
		}
		return "";
	}
}
