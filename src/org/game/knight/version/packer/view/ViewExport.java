package org.game.knight.version.packer.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.chw.swf.clear.SwfTagClear;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.dom4j.Element;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;

public class ViewExport extends AbsExporter
{
	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public ViewExport(File src, File dst)
	{
		super("导出视图", src, dst);
	}

	// -------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------

	private ClassIDTable classIDTable;

	@Override
	protected void openVers()
	{
		super.openVers();

		classIDTable = new ClassIDTable(this);
		classIDTable.open(new File(getDestDir().getPath() + "/.ver/classID"));
	}

	@Override
	protected void saveVers()
	{
		super.saveVers();

		classIDTable.save();
	}

	/**
	 * 获取类型ID表
	 * 
	 * @return
	 */
	public ClassIDTable getClassIDTable()
	{
		return classIDTable;
	}

	// -------------------------------------------------------------------------------
	//
	// 导出
	//
	// -------------------------------------------------------------------------------

	private Hashtable<String, ViewFile> fileTable;
	private Hashtable<String, ViewFileGroup> fileBags;

	/**
	 * 按路径获取视图文件
	 * 
	 * @param path
	 * @return
	 */
	public ViewFile getViewBy(String path)
	{
		return fileTable.get(path);
	}

	/**
	 * 按交叉引用获取文件组
	 * 
	 * @param crossRef
	 * @return
	 */
	public ViewFileGroup getBagBy(String crossRef)
	{
		if (!fileBags.containsKey(crossRef))
		{
			fileBags.put(crossRef, new ViewFileGroup());
		}
		return fileBags.get(crossRef);
	}

	/**
	 * 添加输出文件
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void addOutputFile(ViewFile file) throws Exception
	{
		String key = getChecksumTable().getGID(file.getInnerPath());

		boolean exists = hasExportedFile(key);

		// exportFile(key, file.getFile());
		exportFile(key, FileUtil.getFileBytes(file.getFile()), getFileExtName(file.getFile()));

		if (!exists)
		{
			String ext = getFileExtName(file.getFile());
			if (ext.toLowerCase().equals("swf"))
			{
				SwfTagClear.clearSwfFile(getExportedFile(key));
			}
		}

		File saveFile = getExportedFile(key);
		FileUtil.writeFile(saveFile, MD5Util.addSuffix(FileUtil.getFileBytes(saveFile)));

		GamePacker.progress("输出文件", file.getInnerPath());
	}

	/**
	 * 获取输出文件的地址
	 * 
	 * @param file
	 * @return
	 */
	public String getOutputFileURL(ViewFile file)
	{
		String key = getChecksumTable().getGID(file.getInnerPath());

		return getExportedFileUrl(key);
	}

	/**
	 * 添加输出包
	 * 
	 * @param bag
	 * @throws Exception
	 */
	public void addOutputBag(ViewFileBag bag) throws Exception
	{
		if (!hasExportedFile(bag.getKey()))
		{
			// exportFile(bag.getKey(), bag.build(swfZip), "swf");
			exportFile(bag.getKey(), MD5Util.addSuffix(bag.build()), "swf");
		}
		GamePacker.progress("生成文件", getExportedFileUrl(bag.getKey()));
	}

	@Override
	protected void exportContent() throws Exception
	{
		fileTable = new Hashtable<String, ViewFile>();
		fileBags = new Hashtable<String, ViewFileGroup>();

		if (isCancel())
		{
			return;
		}

		// 读取文件
		GamePacker.log("读取文件");
		readDir(getSourceDir());

		if (isCancel())
		{
			return;
		}

		// 打开所有文件，并初始化引用
		GamePacker.beginLogSet("解析文件");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("解析文件", file.getInnerPath());
			file.open(this);

			if (isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 合并文件的继承引用
		GamePacker.beginLogSet("重构文件");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("重构文件", file.getInnerPath());
			file.mergerInheritRef(this);

			if (isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 计算交叉引用
		GamePacker.beginLogSet("计算引用");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("计算引用", file.getInnerPath());
			file.measureCrossRef();

			if (isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 确定文件分包，初始化类型ID
		GamePacker.beginLogSet("分配类型");
		for (ViewFile file : fileTable.values())
		{
			GamePacker.progress("分配类型", file.getInnerPath());
			file.initClassID(this);

			if (isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 生成文件包
		GamePacker.log("生成文件");
		for (ViewFileGroup group : fileBags.values())
		{
			group.buildBags(this);

			if (isCancel())
			{
				return;
			}
		}

		if (isCancel())
		{
			return;
		}

		// writeDB
		writeDB();
	}

	/**
	 * 读取目录
	 * 
	 * @param dir
	 */
	private void readDir(File dir)
	{
		File[] files = dir.listFiles();

		if (files == null)
		{
			return;
		}

		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];

			if (file.isHidden())
			{
				continue;
			}

			if (file.isDirectory())
			{
				readDir(file);
			}
			else
			{
				String innerPath = file.getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");
				String innerDirPath = file.getParentFile().getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");

				GamePacker.progress("读取文件", innerPath);

				fileTable.put(innerPath, new ViewFile(innerDirPath, innerPath, file));
			}
		}
	}

	private void writeDB() throws Exception
	{
		GamePacker.beginLogSet("输出汇总信息");
		GamePacker.log("生成汇总信息");

		// 排序视图文件
		ViewFile[] list = new ViewFile[fileTable.size()];
		list = fileTable.values().toArray(list);
		Arrays.sort(list, new Comparator<ViewFile>()
		{
			@Override
			public int compare(ViewFile o1, ViewFile o2)
			{
				return o1.getInnerPath().compareTo(o2.getInnerPath());
			}
		});

		// 合并语言文件
		writeLangXML(list);
		mergerLangs(list);
		mergerLanguages(list);

		// 生成语言包
		StringBuilder langFileListXML = new StringBuilder();
		langFileListXML.append("\t<viewLangs>\n");
		StringBuilder viewFileListXML = new StringBuilder();
		File langDir = new File(getSourceDir().getPath() + File.separatorChar + "langs");
		if (langDir == null || !langDir.exists() || langDir.listFiles().length == 0)
		{
			FileUtil.writeFile(new File(langDir.getPath() + File.separatorChar + "cn.txt"), "");
		}

		File[] langFiles = langDir.listFiles();
		for (int i = 0; i < langFiles.length; i++)
		{
			File langFile = langFiles[i];
			String langName = langFile.getName().replaceAll("\\.txt", "");

			StringBuilder langXML = new StringBuilder();
			langXML.append("<xml>\n");
			langXML.append("\t<texts>\n");

			String langFileContent = "";
			byte[] bytes = FileUtil.getFileBytes(langFile);
			if (bytes != null)
			{
				langFileContent = new String(FileUtil.getFileBytes(langFile), "utf8");
				;
			}

			String[] lines = langFileContent.split("\\n\\$");
			for (String line : lines)
			{
				int index = line.indexOf("=");
				if (index != -1)
				{
					String key = line.substring(0, index).trim();
					String val = line.substring(index + 1).trim();

					langXML.append("\t\t<text id=\"" + key + "\"><![CDATA[" + val + "]]></text>\n");
				}
			}
			langXML.append("\t</texts>\n");
			langXML.append("</xml>");

			byte[] bagContent = langXML.toString().getBytes("UTF-8");
			String bagSHA = MD5Util.md5Bytes(bagContent);
			long classID = getClassIDTable().getClassID(bagSHA + ".lang");

			addOutputBag(new ViewLangBag(bagSHA, classID, bagContent));

			String bagURL = getExportedFileUrl(bagSHA);
			long bagSize = getExportedFileSize(bagSHA);

			langFileListXML.append("\t\t<viewLang lang=\"" + langName + "\" url=\"/langs/" + langName + ".xml\" size=\"" + bagSize + "\" file=\"" + bagURL + "\"/>\n");

			viewFileListXML.append("\t\t<view name=\"\" url=\"/langs/" + langName + ".xml\" id=\"FILE_" + classID + "\" size=\"" + bagSize + "\" files=\"" + bagURL + "\"/>\n");
		}
		langFileListXML.append("\t</viewLangs>\n");

		Hashtable<Integer, Integer> method_size = new Hashtable<Integer, Integer>();
		Hashtable<Integer, HashSet<String>> method_files = new Hashtable<Integer, HashSet<String>>();

		// 准备输出缓存
		StringBuilder descXML = new StringBuilder();
		descXML.append("<project>\n");
		descXML.append(langFileListXML.toString());
		descXML.append("\t<views>\n");
		descXML.append(viewFileListXML.toString());
		for (ViewFile file : list)
		{
			if (file.isCfg())
			{
				// 获取内部路径
				String url = file.getInnerPath();

				// 总计大小
				long bagSize = 0;

				// 获取所有引用文件包的ID
				HashSet<String> swfKeys = new HashSet<String>();
				ArrayList<String> swfUrls = new ArrayList<String>();
				for (ViewFileGroup group : file.getDependViewFileGroups())
				{
					for (ViewFileBag bag : group.getBags())
					{
						if (!swfKeys.contains(bag.getKey()))
						{
							String bagKey = bag.getKey();
							String bagURL = getExportedFileUrl(bagKey);
							long bagLen = getExportedFileSize(bagKey);

							swfKeys.add(bagKey);
							swfUrls.add(bagURL);
							bagSize += bagLen;

							if (file.getPreloadMethod() != null && file.getPreloadMethod().length > 0)
							{
								int size = 0;
								int[] methods = file.getPreloadMethod();
								for (int method : methods)
								{
									if (method == -1)
									{
										continue;
									}

									if (method_size.containsKey(method))
									{
										size = method_size.get(method);
									}
									if (!method_files.containsKey(method))
									{
										method_files.put(method, new HashSet<String>());
									}

									if (!method_files.get(method).contains(bagURL))
									{
										method_files.get(method).add(bagURL);
										method_size.put(method, (int) (size + bagLen));
									}
								}
							}
						}
					}
				}
				for (ViewFileSwf swf : file.getDependViewSwfs())
				{
					if (!swfKeys.contains(swf.getKey()))
					{
						String bagKey = swf.getKey();
						String bagURL = getExportedFileUrl(getChecksumTable().getGID(swf.getFile().getInnerPath()));
						long bagLen = getExportedFileSize(getChecksumTable().getGID(swf.getFile().getInnerPath()));

						swfKeys.add(bagKey);
						swfUrls.add(bagURL);
						bagSize += bagLen;

						if (file.getPreloadMethod() != null && file.getPreloadMethod().length > 0)
						{
							int size = 0;
							int[] methods = file.getPreloadMethod();
							for (int method : methods)
							{
								if (method == -1)
								{
									continue;
								}

								if (method_size.containsKey(method))
								{
									size = method_size.get(method);
								}
								if (!method_files.containsKey(method))
								{
									method_files.put(method, new HashSet<String>());
								}

								if (!method_files.get(method).contains(bagURL))
								{
									method_files.get(method).add(bagURL);
									method_size.put(method, (int) (size + bagLen));
								}
							}
						}
					}
				}

				// 排序文件包
				Collections.sort(swfUrls);

				// 组合文件路径名
				StringBuilder urls = new StringBuilder();
				for (String path : swfUrls)
				{
					urls.append(urls.length() == 0 ? path : "," + path);
				}

				// 记录信息
				StringBuilder sb = new StringBuilder();
				for (int typeID : file.getPreloadMethod())
				{
					if (sb.length() > 0)
					{
						sb.append(",");
					}
					sb.append(typeID);
				}
				descXML.append("\t\t<view preload=\"" + sb.toString() + "\" name=\"" + file.getBagName() + "\" url=\"" + url + "\" id=\"FILE_" + file.getClassID() + "\" size=\"" + bagSize + "\" files=\"" + urls.toString() + "\"/>\n");
			}
		}
		descXML.append("\t</views>\n");
		descXML.append("\t<viewBags>\n");
		Integer[] methodIDs = method_files.keySet().toArray(new Integer[method_files.keySet().size()]);
		Arrays.sort(methodIDs);
		for (Integer method : methodIDs)
		{
			int size = method_size.get(method);
			HashSet<String> urls = method_files.get(method);
			String[] paths = urls.toArray(new String[urls.size()]);
			Arrays.sort(paths);

			StringBuilder sb = new StringBuilder();
			for (String path : paths)
			{
				if (sb.length() > 0)
				{
					sb.append(",");
				}
				sb.append(path);

				// File viewFrom = new
				// File(getDestDir().getParentFile().getPath() + path);
				// File viewDest = new File(getDestDir().getPath() +
				// "/.ver/preloads/" + method + "/" + viewFrom.getName());
				// FileUtil.copyTo(viewDest, viewFrom);
			}
			descXML.append(String.format("\t\t<viewBagFiles type=\"%s\" size=\"%s\" files=\"%s\" />\n", method, size, sb.toString()));
		}
		descXML.append("\t</viewBags>\n");
		descXML.append("</project>");

		GamePacker.log("保存汇总信息");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), descXML.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		// 生成文件列表
		GamePacker.beginLogSet("输出文件汇总");
		GamePacker.log("生成文件汇总");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList = getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("保存文件汇总");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
	}

	/**
	 * 输出语言包XML文件
	 * 
	 * @param list
	 * @throws UnsupportedEncodingException
	 */
	private void writeLangXML(ViewFile[] list) throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<uitext>\n");

		// 统计语言项
		for (ViewFile file : list)
		{
			if (file.isCfg())
			{
				Element dom = file.getTextsNode();
				if (dom != null)
				{
					String url = file.getInnerPath();

					sb.append("\t<bag url=\"" + url + "\">\n");

					@SuppressWarnings("rawtypes")
					List nodes = dom.selectNodes("text");
					for (int i = 0; i < nodes.size(); i++)
					{
						Element node = (Element) nodes.get(i);

						String key = node.attributeValue("id");
						String val = node.getText();

						sb.append("\t\t<item id=\"" + key + "\">");
						sb.append("<![CDATA[");
						sb.append(val.replaceAll("\\]]>", "&&&;"));
						sb.append("]]>");
						sb.append("</item>\n");
					}

					sb.append("\t</bag>\n");
				}
			}
		}
		sb.append("</uitext>");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/$UIText.xml"), sb.toString().getBytes("UTF-8"));
	}

	private static class LangItem
	{
		public String url;
		public String key;
		public String value;

		public LangItem(String url, String key, String value)
		{
			this.url = url;
			this.key = key;
			this.value = value;
		}
	}

	private void mergerLangs(ViewFile[] list) throws Exception
	{
		MergerViewText.mergerLangFiles(list, new File(getSourceDir().getPath() + File.separatorChar + "langs"));
		if ("tt".length() > 0)
		{
			return;
		}
		ArrayList<LangItem> items = new ArrayList<ViewExport.LangItem>();

		// 统计语言项
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
						String val = node.getText();

						items.add(new LangItem(url, key, val));
					}
				}
			}
		}

		// 排序语言项
		// 生成excel
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("cn");
		sheet.setColumnWidth(0, 1000);
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
		lineStyle.setBorderLeft((short) 1);
		lineStyle.setTopBorderColor((short) 1);
		lineStyle.setRightBorderColor((short) 1);
		lineStyle.setBorderBottom((short) 2);
		lineStyle.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle.setBottomBorderColor(HSSFColor.RED.index);

		HSSFCellStyle lineStyle2 = book.createCellStyle();
		lineStyle2.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);
		lineStyle2.setBorderLeft((short) 1);
		lineStyle2.setTopBorderColor((short) 1);
		lineStyle2.setRightBorderColor((short) 1);
		lineStyle2.setBorderBottom((short) 1);

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
					prevCell0.setCellStyle(lineStyle);
					prevCell1.setCellStyle(lineStyle);
					prevCell2.setCellStyle(lineStyle);
					prevCell3.setCellStyle(lineStyle);
					prevCell4.setCellStyle(lineStyle);
				}
			}

			HSSFRow row = sheet.createRow(i + 1);

			HSSFCell cell0 = row.createCell(0);
			cell0.setCellValue("x");
			cell0.setCellStyle(lineStyle2);

			HSSFCell cell1 = row.createCell(1);
			cell1.setCellValue(item.url);
			cell1.setCellStyle(lineStyle2);

			HSSFCell cell2 = row.createCell(2);
			cell2.setCellValue(item.key);
			cell2.setCellStyle(lineStyle2);

			HSSFCell cell3 = row.createCell(3);
			cell3.setCellValue(item.value);
			cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell3.setCellStyle(lineStyle2);

			HSSFCell cell4 = row.createCell(4);
			cell4.setCellValue(item.value);
			cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell4.setCellStyle(lineStyle2);

			prevCell0 = cell0;
			prevCell1 = cell1;
			prevCell2 = cell2;
			prevCell3 = cell3;
			prevCell4 = cell4;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		book.write(output);
		output.close();

		System.out.print(output.toByteArray().toString());

		FileUtil.writeFile(new File(getSourceDir().getPath() + File.separatorChar + "langs" + File.separatorChar + "test.xls"), output.toByteArray());

		// int index=5;
		// while(true)
		// {
		// File file=new File("E:/students"+index+".xls");
		// if(!file.exists())
		// {
		// FileOutputStream output=new
		// FileOutputStream(file.getPath());//getSourceDir().getPath() +
		// File.separatorChar + "langs" + File.separatorChar+"lang45.xls");
		// book.write(output);
		// output.close();
		// break;
		// }
		// index++;
		// }
	}

	/**
	 * 合并语言文件
	 * 
	 * @param list
	 * @throws Exception
	 */
	private void mergerLanguages(ViewFile[] list) throws Exception
	{
		// 统计新的语言项
		Hashtable<String, String> newLangContentTable = new Hashtable<String, String>();
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
						String key = node.attributeValue("id");
						String val = node.getText();
						newLangContentTable.put(key, val);
					}
				}
			}
		}

		String[] newLangKeys = newLangContentTable.keySet().toArray(new String[newLangContentTable.size()]);
		Arrays.sort(newLangKeys);

		// 合并旧语言文件
		File langDIR = new File(getSourceDir().getPath() + File.separatorChar + "langs" + File.separatorChar);
		if (!langDIR.exists())
		{
			FileUtil.writeFile(new File(langDIR.getPath() + File.separatorChar + "cn.txt"), "");
		}
		File[] langFiles = langDIR.listFiles();
		for (File langFile : langFiles)
		{
			if (langFile.isFile() && langFile.getName().endsWith(".txt") && !langFile.isHidden())
			{
				String langContent = new String(FileUtil.getFileBytes(langFile), "UTF-8");

				ArrayList<String> mergeredContentKeys = new ArrayList<String>();
				Hashtable<String, String> mergeredContentTable = new Hashtable<String, String>();

				if (langContent != null && !langContent.isEmpty())
				{
					String[] segments = langContent.split("\\r\\n$");
					for (String segment : segments)
					{
						int index = segment.indexOf("=");
						String txtKey = segment.substring(0, index);
						String txtVal = segment.substring(index + 1);

						if (newLangContentTable.containsKey(txtKey))
						{
							txtKey = txtKey.trim();
							txtVal = txtVal.trim();

							mergeredContentKeys.add(txtKey);
							mergeredContentTable.put(txtKey, txtVal);
						}
					}
				}

				for (String newLangKey : newLangKeys)
				{
					if (!mergeredContentTable.containsKey(newLangKey))
					{
						mergeredContentKeys.add(newLangKey);
						mergeredContentTable.put(newLangKey, newLangContentTable.get(newLangKey));
					}
				}

				StringBuilder mergeredLangContent = new StringBuilder();
				for (String mergeredLangKey : mergeredContentKeys)
				{
					if (mergeredLangContent.length() > 0)
					{
						mergeredLangContent.append("\r\n$");
					}
					mergeredLangContent.append(mergeredLangKey + " = " + mergeredContentTable.get(mergeredLangKey));
				}

				String newLangContent = mergeredLangContent.toString();
				if (!newLangContent.equals(langContent))
				{
					FileUtil.writeFile(langFile, newLangContent.getBytes("UTF-8"));
				}
			}
		}
	}
}
