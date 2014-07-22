package org.game.knight.version.packer.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFConditionalFormattingRule;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatternFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSheetConditionalFormatting;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dom4j.Element;
import org.game.knight.version.packer.GamePacker;

public class MergerViewText
{
	private static class LangItem
	{
		public int state;
		public String url;
		public String key;
		public String txt;
		public String value;

		public static final int ADD = 1;
		public static final int DEL = 2;
		public static final int SET = 3;

		public LangItem(String url, String key, String txt, String value)
		{
			this.url = url;
			this.key = key;
			this.txt = txt;
			this.value = value;
		}
	}

	public static void mergerLangFiles(ViewFile[] list, File langDIR) throws Exception
	{
		// 统计语言项
		ArrayList<LangItem> itemList = new ArrayList<LangItem>();
		Hashtable<String, LangItem> itemHash = new Hashtable<String, LangItem>();
		for (ViewFile file : list)
		{
			if (file.isCfg())
			{
				Element dom = file.getTextsNode();

				if (dom != null)
				{
					@SuppressWarnings("rawtypes")
					List nodes = dom.selectNodes("text");
					for (int i = 0; i < nodes.size(); i++)
					{
						Element node = (Element) nodes.get(i);

						String url = file.getInnerPath();
						String key = node.attributeValue("id");
						String txt = node.getText();
						LangItem item = new LangItem(url, key, txt, txt);

						if (itemHash.containsKey(url + "@" + key) == false)
						{
							itemList.add(item);
							itemHash.put(url + "@" + key, item);
						}
						else
						{
							GamePacker.error("重复的文本ID:" + key, file.getInnerPath());
						}
					}
				}
			}
		}

		File cnFile = new File(langDIR.getPath() + File.separatorChar + "cn.xls");
		saveToExcel(itemList, cnFile, true);

		// 旧语言文件
		for (File file : langDIR.listFiles())
		{
			if (file.isFile() && !file.isHidden() && file.getName().endsWith(".xls") && file.getPath().equals(cnFile.getPath()) == false)
			{
				ArrayList<LangItem> items = new ArrayList<LangItem>();
				Hashtable<String, LangItem> path2Item = new Hashtable<String, LangItem>();

				HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
				HSSFSheet sheet = book.getSheetAt(0);
				for (int i = 1; i < sheet.getLastRowNum() + 1; i++)
				{
					HSSFRow row = sheet.getRow(i);

					HSSFCell cell0 = row.getCell(0);
					HSSFCell cell1 = row.getCell(1);
					HSSFCell cell2 = row.getCell(2);
					HSSFCell cell3 = row.getCell(3);
					HSSFCell cell4 = row.getCell(4);

					if (cell0 == null || cell1 == null || cell2 == null || cell3 == null || cell4 == null)
					{
						throw new Exception("文件格式无效！ " + file.getPath());
					}

					String url = cell1.getStringCellValue();
					String key = cell2.getStringCellValue();
					String txt = cell3.getStringCellValue();
					String val = cell4.getStringCellValue();

					String path = url + "@" + key;

					LangItem item = new LangItem(url, key, txt, val);

					items.add(item);
					path2Item.put(path, item);

					if (!itemHash.containsKey(path))
					{
						item.state = LangItem.DEL;
					}
					else
					{
						String newTxt = itemHash.get(path).txt;
						if (!newTxt.equals(txt))
						{
							item.txt = newTxt;
							item.state = LangItem.SET;
						}
					}
				}

				for (LangItem item : itemList)
				{
					String path = item.url + "@" + item.key;

					if (!path2Item.containsKey(path))
					{
						LangItem newItem = new LangItem(item.url, item.key, item.txt, item.txt);
						newItem.state = LangItem.ADD;

						items.add(newItem);
					}
				}

				saveToExcel(items, file, false);
			}
		}
	}

	static private void saveToExcel(ArrayList<LangItem> items, File file, boolean defLang) throws Exception
	{
		Collections.sort(items, new Comparator<LangItem>()
		{
			@Override
			public int compare(LangItem o1, LangItem o2)
			{
				String a = o1.url + "@" + o1.key;
				String b = o2.url + "@" + o2.key;
				return a.compareTo(b);
			}
		});

		// 生成excel
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("cn");
		sheet.setColumnWidth(0, 2000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 20000);
		sheet.setColumnWidth(4, 20000);
		HSSFRow header = sheet.createRow(0);
		header.setHeight((short) 500);
		HSSFCellStyle headerStyle = book.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont font = book.createFont();
		font.setColor(HSSFColor.WHITE.index);
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(HSSFColor.DARK_TEAL.index);
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFCell headerCell = null;
		headerCell = header.createCell(0);
		headerCell.setCellValue("状态");
		headerCell.setCellStyle(headerStyle);
		headerCell = header.createCell(1);
		headerCell.setCellValue("包/包ID");
		headerCell.setCellStyle(headerStyle);
		headerCell = header.createCell(2);
		headerCell.setCellValue("引用ID");
		headerCell.setCellStyle(headerStyle);
		headerCell = header.createCell(3);
		headerCell.setCellValue("原文");
		headerCell.setCellStyle(headerStyle);
		headerCell = header.createCell(4);
		headerCell.setCellValue("译文");
		headerCell.setCellStyle(headerStyle);

		HSSFCellStyle lineStyle = book.createCellStyle();
		lineStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		lineStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		lineStyle.setBorderLeft((short) 1);
		lineStyle.setTopBorderColor((short) 1);
		lineStyle.setRightBorderColor((short) 1);
		lineStyle.setBorderBottom((short) 2);
		lineStyle.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle.setBottomBorderColor(HSSFColor.RED.index);

		HSSFCellStyle lineStyle2 = book.createCellStyle();
		lineStyle2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		lineStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		lineStyle2.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setBorderLeft((short) 1);
		lineStyle2.setTopBorderColor((short) 1);
		lineStyle2.setRightBorderColor((short) 1);
		lineStyle2.setBorderBottom((short) 1);

		HSSFCellStyle stateStyle = book.createCellStyle();
		stateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		stateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		stateStyle.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle.setBottomBorderColor(HSSFColor.RED.index);
		stateStyle.setBorderLeft((short) 1);
		stateStyle.setTopBorderColor((short) 1);
		stateStyle.setRightBorderColor((short) 1);
		stateStyle.setBorderBottom((short) 2);

		HSSFCellStyle stateStyle2 = book.createCellStyle();
		stateStyle2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		stateStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		stateStyle2.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle2.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle2.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle2.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);
		stateStyle2.setBorderLeft((short) 1);
		stateStyle2.setTopBorderColor((short) 1);
		stateStyle2.setRightBorderColor((short) 1);
		stateStyle2.setBorderBottom((short) 1);

		String url = "";
		HSSFCell prevCell0 = null;
		HSSFCell prevCell1 = null;
		HSSFCell prevCell2 = null;
		HSSFCell prevCell3 = null;
		HSSFCell prevCell4 = null;
		for (int i = 0; i < items.size(); i++)
		{
			LangItem item = items.get(i);

			if (!item.url.equals(url))
			{
				url = item.url;
				if (prevCell1 != null)
				{
					prevCell0.setCellStyle(stateStyle);
					prevCell1.setCellStyle(lineStyle);
					prevCell2.setCellStyle(lineStyle);
					prevCell3.setCellStyle(lineStyle);
					prevCell4.setCellStyle(lineStyle);
				}
			}

			HSSFRow row = sheet.createRow(i + 1);

			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue(getStateText(item.state));
			cell0.setCellStyle(stateStyle2);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(item.url);
			cell1.setCellStyle(lineStyle2);

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(item.key);
			cell2.setCellStyle(lineStyle2);

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(item.txt);
			cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell3.setCellStyle(lineStyle2);

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(item.value);
			cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell4.setCellStyle(lineStyle2);

			HSSFSheetConditionalFormatting scf = sheet.getSheetConditionalFormatting();
			HSSFConditionalFormattingRule rule = scf.createConditionalFormattingRule("TRIM(D" + (i + 2) + ")=TRIM(E" + (i + 2) + ")");
			HSSFPatternFormatting cf = rule.createPatternFormatting();
			cf.setFillBackgroundColor(HSSFColor.LIGHT_ORANGE.index);
			scf.addConditionalFormatting(new CellRangeAddress[] { CellRangeAddress.valueOf("A" + (i + 2) + ":A" + (i + 2) + "") }, new HSSFConditionalFormattingRule[] { rule });
			
			prevCell0 = cell0;
			prevCell1 = cell1;
			prevCell2 = cell2;
			prevCell3 = cell3;
			prevCell4 = cell4;
		}

		FileOutputStream output = new FileOutputStream(file);
		book.write(output);
		output.close();
	}

	private static String getStateText(int state)
	{
		if (state == LangItem.ADD)
		{
			return "新增";
		}
		else if (state == LangItem.DEL)
		{
			return "已删";
		}
		else if (state == LangItem.SET)
		{
			return "改变";
		}

		return "";
	}
}
