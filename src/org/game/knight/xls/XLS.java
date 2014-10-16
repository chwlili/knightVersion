package org.game.knight.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class XLS
{
	private File input;
	private File output;

	public XLS(File input, File output)
	{
		this.input = input;
		this.output = output;
	}

	public void run() throws FileNotFoundException, IOException
	{
		String[] one = new String[] { "data_building", "data_military_rank", "data_monster", "data_partner", "data_quest", "data_quest_active", "data_talk", "data_title" };
		String[] two = new String[] { "data_chapter", "data_aura", "data_item", "data_quest_aim", "data_quest_daily" };
		String[] three = new String[] { "data_equipment", "data_npc" };
		String[] four = new String[] { "data_package" };

		HSSFWorkbook inputBook = new HSSFWorkbook(new FileInputStream(input));
		HSSFWorkbook outputBook = new HSSFWorkbook();

		for (int i = 0; i < inputBook.getNumberOfSheets(); i++)
		{
			HSSFSheet sheet = inputBook.getSheetAt(i);
			String sheetName = inputBook.getSheetName(i).trim();

			AbsSheet outputSheet = null;
			for (String key : one)
			{
				if (key.equals(sheetName))
				{
					outputSheet = new OneColumnSheet();
					break;
				}
			}
			for (String key : two)
			{
				if (key.equals(sheetName))
				{
					outputSheet = new TwoColumnSheet();
					break;
				}
			}
			for (String key : three)
			{
				if (key.equals(sheetName))
				{
					outputSheet = new ThreeColumnSheet();
					break;
				}
			}
			for (String key : four)
			{
				if (key.equals(sheetName))
				{
					outputSheet = new FourColumnSheet();
					break;
				}
			}
			if (outputSheet != null)
			{
				outputSheet.exec(sheet, sheetName.replaceFirst("data_", ""), outputBook);
			}
		}

		outputBook.write(new FileOutputStream(output));
	}
}
