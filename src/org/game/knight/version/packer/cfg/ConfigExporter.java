package org.game.knight.version.packer.cfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerHelper;
import org.game.knight.version.packer.base.ZipConfig;
import org.xml2as.builder.ClassTable;
import org.xml2as.builder.UnitConfigBuilder;

public class ConfigExporter
{
	private GamePackerHelper helper;
	private File outputFolder;

	private ConfigZip oldZip;
	private ConfigZip newZip;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public ConfigExporter(GamePackerHelper helper, File dst)
	{
		this.helper = helper;
		this.outputFolder = dst;

		this.oldZip = new ConfigZip(new File(dst + File.separator + "ver.zip"));
		this.newZip = new ConfigZip();

	}

	/**
	 * 导出内容
	 * 
	 * @throws Exception
	 */
	public Boolean publish()
	{
		try
		{
			GamePacker.beginTask("处理语言包");

			String skillContents = cherryPickSkillData(oldZip, newZip);
			if (GamePacker.isCancel())
			{
				return false;
			}

			String cfgContents = mergeConfigs(oldZip, newZip, skillContents);
			if (GamePacker.isCancel())
			{
				return false;
			}

			StringBuilder dbContent = new StringBuilder();
			dbContent.append("<projects>\n");
			dbContent.append(cfgContents);
			dbContent.append("</projects>");

			if (GamePacker.isCancel())
			{
				return false;
			}

			newZip.setVersion(dbContent.toString());
			newZip.setVersionProps(oldZip.getVersionProps());
			newZip.getVersionFiles().add("/" + outputFolder.getName() + oldZip.getFile().getName());
			newZip.saveTo(oldZip.getFile());

			File tmp = new File(outputFolder.getPath() + "/tmp");
			if (tmp.exists())
			{
				for (File file : tmp.listFiles())
				{
					for (File child : file.listFiles())
					{
						child.delete();
					}
					file.delete();
				}
				tmp.delete();
			}

			return true;
		}
		catch (Exception err)
		{
			GamePacker.error(err);
		}
		finally
		{
			GamePacker.endTask();
		}

		return false;
	}

	// ===============================================================================================================================================

	protected class XmlFile
	{
		public final File root;
		public final String name;
		public final String path;
		public final byte[] bytes;

		public XmlFile(File root, String name, String path, byte[] bytes)
		{
			this.root = root;
			this.name = name;
			this.path = path;
			this.bytes = bytes;
		}
	}

	protected static class BuildItem
	{
		public final String lang;
		public final String mode;
		public final String name;
		public final String xmlPath;
		public final String buildPath;

		public final byte[] xmlBytes;
		public final String xmlMD5;

		public final File xml2File;
		public final String xml2MD5;

		public final File xlsFile;
		public final String xlsMD5;

		public byte[] bytes;

		public BuildItem(String langName, String mode, String name, String xmlPath, String buildPath, byte[] xmlBytes, String xmlMD5, File xml2File, String xml2MD5, File xlsFile, String xlsMD5, byte[] bytes)
		{
			this.lang = langName;
			this.mode = mode;

			this.name = name;
			this.xmlPath = xmlPath;
			this.buildPath = buildPath;

			this.xmlBytes = xmlBytes;
			this.xmlMD5 = xmlMD5;
			this.xml2File = xml2File;
			this.xml2MD5 = xml2MD5;
			this.xlsFile = xlsFile;
			this.xlsMD5 = xlsMD5;
			this.bytes = bytes;
		}
	}

	protected String mergeConfigs(ConfigZip oldZip, ConfigZip newZip, String skills) throws Exception
	{
		XmlFile[] xmlFiles = listAllXmlFiles();
		File[] xml2Files = listAllXml2Files();
		File[] xlsFiles = listAllXlsFiles();

		// 确定文件MD5码
		HashMap<Object, String> file_md5 = new HashMap<Object, String>();
		int fileStep = 0;
		int fileCount = xmlFiles.length + xml2Files.length + xlsFiles.length;
		for (XmlFile file : xmlFiles)
		{
			GamePacker.progress(String.format("检测文件变化(%s/%s)：%s。", fileStep + 1, fileCount, file.name));
			file_md5.put(file, MD5Util.md5Bytes(file.bytes));
			fileStep++;
			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		for (File file : xml2Files)
		{
			GamePacker.progress(String.format("检测文件变化(%s/%s)：%s。", fileStep + 1, fileCount, file.getName()));
			file_md5.put(file, MD5Util.md5(file));
			fileStep++;
			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		for (File file : xlsFiles)
		{
			GamePacker.progress(String.format("检测文件变化(%s/%s)：%s。", fileStep + 1, fileCount, file.getName()));
			file_md5.put(file, getXlsMD5(file));
			fileStep++;
			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		GamePacker.log("检测文件变化：完成。");

		// 确定语言包文件
		HashSet<String> langNames = new HashSet<String>();
		HashMap<String, File> langName_file = new HashMap<String, File>();
		for (int i = 0; i < xlsFiles.length; i++)
		{
			File xlsFile = xlsFiles[i];
			String langName = xlsFile.getName().substring(0, xlsFile.getName().length() - 4);

			GamePacker.progress(String.format("确定语言列表(%s/%s)：%s。", i + 1, xlsFiles.length, langName));

			langNames.add(langName);
			langName_file.put(langName, xlsFile);

			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		langNames.add("zh");
		GamePacker.log("确定语言列表：完成。");

		// 解析XML2文件
		HashMap<String, ClassTable> xmlURL_classTable = new HashMap<String, ClassTable>();
		HashMap<String, File> xmlURL_xml2File = new HashMap<String, File>();
		for (int i = 0; i < xml2Files.length; i++)
		{
			File xml2File = xml2Files[i];

			GamePacker.progress("解析xml2文件(" + (i + 1) + "/" + xml2Files.length + "):" + xml2File.getName());

			ClassTable table = new ClassTable(xml2File);

			String inputURL = table.getInputFile();
			if (inputURL != null && table.getMainClass() != null)
			{
				xmlURL_classTable.put(inputURL, table);
				xmlURL_xml2File.put(inputURL, xml2File);
			}

			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		GamePacker.log("解析xml2文件：完成。");

		// 确定新增的构建项，构建表
		ArrayList<BuildItem> allBuildItem = new ArrayList<BuildItem>();
		ArrayList<BuildItem> addedItems = new ArrayList<BuildItem>();
		HashMap<String, BuildItem> addedBuilders = new HashMap<String, BuildItem>();
		for (String langName : langNames)
		{
			File xlsFile = langName_file.get(langName);
			String xlsMD5 = xlsFile != null ? file_md5.get(xlsFile) : "";

			for (XmlFile xmlFile : xmlFiles)
			{
				String xmlMode = xmlFile.path.endsWith(".2d.xml") ? "2d" : "3d";
				String xmlName = xmlFile.name.replaceAll("\\.2d\\.xml", "").replaceAll("\\.xml", "");
				String xmlPath = xmlFile.path;
				String buildPath = xmlPath.replaceAll("\\.2d\\.xml", ".xml");
				String xmlMD5 = file_md5.get(xmlFile);

				File xml2File = xmlURL_xml2File.get(buildPath);
				String xml2MD5 = xml2File != null ? file_md5.get(xml2File) : "";

				if (allowOutput(xmlFile))
				{
					byte[] bytes = oldZip.getConfig(xmlMD5, xml2MD5, xlsMD5);

					BuildItem item = new BuildItem(langName, xmlMode, xmlName, xmlPath, buildPath, xmlFile.bytes, xmlMD5, xml2File, xml2MD5, xlsFile, xlsMD5, bytes);
					allBuildItem.add(item);
					if (bytes == null)
					{
						addedItems.add(item);
						if (xml2File != null)
						{
							addedBuilders.put(xmlPath, item);
						}
					}
				}
				else
				{
					String[] infos = oldZip.getXmlInfo(langName, xmlMode, xmlName);
					if (infos != null)
					{
						allBuildItem.add(new BuildItem(langName, xmlMode, xmlName, xmlPath, buildPath, xmlFile.bytes, xmlMD5, xml2File, xml2MD5, xlsFile, xlsMD5, oldZip.getConfig(infos[0], infos[1], infos[2])));
					}
				}
			}
		}

		// 初始化Builder
		HashMap<String, UnitConfigBuilder> builderMap = new HashMap<String, UnitConfigBuilder>();
		BuildItem[] builders = addedBuilders.values().toArray(new BuildItem[] {});
		for (int i = 0; i < builders.length; i++)
		{
			BuildItem item = builders[i];

			GamePacker.progress(String.format("解析xml文件(%s/%s)：%s", i + 1, builders.length, item.name));

			UnitConfigBuilder builder = new UnitConfigBuilder(xmlURL_classTable.get(item.buildPath));
			builder.read(new ByteArrayInputStream(item.xmlBytes));
			builderMap.put(item.xmlPath, builder);

			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		GamePacker.log("解析xml文件：完成。");

		// 构建所有新增的配置
		int buildIndex = 0;
		int buildCount = addedItems.size();
		HashMap<String, ArrayList<BuildItem>> buildItems = new HashMap<String, ArrayList<BuildItem>>();
		for (BuildItem item : addedItems)
		{
			if (!buildItems.containsKey(item.lang))
			{
				buildItems.put(item.lang, new ArrayList<BuildItem>());
			}
			buildItems.get(item.lang).add(item);
		}
		for (ArrayList<BuildItem> list : buildItems.values())
		{
			XlsLangTable langTable = new XlsLangTable(list.get(0).xlsFile);
			for (BuildItem item : list)
			{
				GamePacker.progress(String.format("生成配置文件(%s/%s)：[ %s %s ] %s", buildIndex + 1, buildCount, item.mode, item.lang, item.name));

				UnitConfigBuilder builder = builderMap.get(item.xmlPath);
				if (builder != null)
				{
					langTable.setSheet(item.name);

					item.bytes = builder.toBytes(langTable);
				}
				else
				{
					item.bytes = ZlibUtil.compress(item.xmlBytes);
				}

				buildIndex++;

				if (GamePacker.isCancel())
				{
					return null;
				}
			}
			langTable.save();
		}
		GamePacker.log("生成配置文件：完成。");

		// 输出所有配置
		HashMap<String, HashMap<String, HashMap<String, BuildItem>>> writeItems = new HashMap<String, HashMap<String, HashMap<String, BuildItem>>>();
		for (BuildItem item : allBuildItem)
		{
			if (!writeItems.containsKey(item.lang))
			{
				writeItems.put(item.lang, new HashMap<String, HashMap<String, BuildItem>>());
				writeItems.get(item.lang).put("3d", new HashMap<String, BuildItem>());
				writeItems.get(item.lang).put("2d", new HashMap<String, BuildItem>());
			}
			writeItems.get(item.lang).get(item.mode).put(item.name, item);
		}
		for (HashMap<String, HashMap<String, BuildItem>> modes : writeItems.values())
		{
			for (String key : modes.get("3d").keySet())
			{
				if (!modes.get("2d").containsKey(key))
				{
					modes.get("2d").put(key, modes.get("3d").get(key));
				}
			}
		}
		StringBuilder db_xml = new StringBuilder();
		String[] writeKeys = writeItems.keySet().toArray(new String[] {});
		Arrays.sort(writeKeys);
		int writeIndex = 0;
		int writeCount = allBuildItem.size();
		for (String writeKey : writeKeys)
		{
			String[] modes = new String[] { "2d", "3d" };
			for (String mode : modes)
			{
				BuildItem[] items = writeItems.get(writeKey).get(mode).values().toArray(new BuildItem[] {});
				Arrays.sort(items, new Comparator<BuildItem>()
				{
					@Override
					public int compare(BuildItem o1, BuildItem o2)
					{
						return o1.name.compareTo(o2.name);
					}
				});

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				for (BuildItem item : items)
				{
					GamePacker.progress("构建配置(" + (writeIndex + 1) + "/" + writeCount + "):", "[ " + mode + " " + writeKey + " ] " + item.name);

					String name = item.name;
					if (item.xml2File != null)
					{
						name = name + ".xml";
					}

					ByteArrayOutputStream nameOutput = new ByteArrayOutputStream();
					nameOutput.write(name.getBytes("utf8"));
					byte[] nameBytes = nameOutput.toByteArray();

					byte[] fileBytes = item.bytes;

					output.write(nameBytes.length & 0xFF);
					output.write(nameBytes.length >>> 8 & 0xFF);
					output.write(nameBytes.length >>> 16 & 0xFF);
					output.write(nameBytes.length >>> 24 & 0xFF);
					output.write(nameBytes);
					output.write(fileBytes.length & 0xFF);
					output.write(fileBytes.length >>> 8 & 0xFF);
					output.write(fileBytes.length >>> 16 & 0xFF);
					output.write(fileBytes.length >>> 24 & 0xFF);
					output.write(fileBytes);
					output.flush();

					newZip.setConfig(item.xmlMD5, item.xml2MD5, item.xlsMD5, item.bytes);
					newZip.setXmlInfo(item.lang, item.mode, item.name, item.xmlMD5, item.xml2MD5, item.xlsMD5);

					writeIndex++;
				}

				byte[] writeBytes = MD5Util.addSuffix(output.toByteArray());
				String writeMD5 = MD5Util.md5Bytes(writeBytes);
				String writeURL = oldZip.getGameFiles().get(writeMD5);

				if (writeURL == null)
				{
					writeURL = oldZip.getVersionNextGameFileURL("cfg");
					FileUtil.writeFile(new File(outputFolder.getPath() + writeURL), writeBytes);
				}

				newZip.getGameFiles().put(writeMD5, writeURL);
				newZip.getVersionFiles().add("/" + outputFolder.getName() + writeURL);

				db_xml.append(String.format("\t<project lang=\"%s\" mode=\"%s\">\n", writeKey, mode));
				db_xml.append(String.format("\t\t<configs file=\"/%s\" size=\"%s\"/>\n", outputFolder.getName() + writeURL, writeBytes.length));
				db_xml.append(String.format("%s", skills));
				db_xml.append(String.format("\t</project>\n"));
			}
		}

		return db_xml.toString();
	}

	/**
	 * 列出所有xml文件
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	private XmlFile[] listAllXmlFiles() throws FileNotFoundException
	{
		ArrayList<XmlFile> xmlFiles = new ArrayList<ConfigExporter.XmlFile>();

		// 常规XML配置
		File[] files = helper.listFiles(helper.cfgInputFolder, "xml");
		for (File file : files)
		{
			xmlFiles.add(new XmlFile(helper.cfgOutputFolder, file.getName(), file.getPath().substring(helper.cfgInputFolder.getPath().length()).replaceAll("\\\\", "/"), FileUtil.getFileBytes(file)));
		}

		// 特殊XML配置
		File[] outputs = new File[] { helper.cfgOutputFolder, helper.iconOutputFolder, helper.fileOutputFolder, helper.codeOutputFolder, helper.viewOutputFolder, helper.worldOutputFolder };
		for (File folder : outputs)
		{
			if (!folder.isHidden())
			{
				if (folder.isDirectory())
				{
					File zipFile = new File(folder.getPath() + "/ver.zip");
					if (zipFile.exists())
					{
						ZipConfig cfg = new ZipConfig(zipFile);
						HashMap<String, byte[]> entrys = cfg.getCfgFiles();
						for (String name : entrys.keySet())
						{
							xmlFiles.add(new XmlFile(folder, name, name, entrys.get(name)));
						}
					}
					else
					{
						for (File file : folder.listFiles())
						{
							if (!file.isHidden() && file.isFile() && file.getName().startsWith("$") && file.getName().endsWith(".xml"))
							{
								xmlFiles.add(new XmlFile(folder, file.getName(), file.getName(), FileUtil.getFileBytes(file)));
							}
						}
					}
				}
			}
		}

		return xmlFiles.toArray(new XmlFile[] {});
	}

	/**
	 * 列出所有xml2文件
	 * 
	 * @return
	 */
	private File[] listAllXml2Files()
	{
		return helper.listFiles(helper.xml2Folder, "xml2");
	}

	/**
	 * 列出所有XLS文件
	 * 
	 * @return
	 */
	private File[] listAllXlsFiles()
	{
		return helper.listFiles(helper.nlsFolder, "xls");
	}

	/**
	 * 是否允许输出些文件
	 * 
	 * @param file
	 * @return
	 */
	private boolean allowOutput(XmlFile file)
	{
		File[] folders = new File[] { helper.iconOutputFolder, helper.fileOutputFolder, helper.codeOutputFolder, helper.viewOutputFolder, helper.worldOutputFolder, helper.cfgOutputFolder };
		boolean[] checks = new boolean[] { helper.iconChecked, helper.fileChecked, helper.codeChecked, helper.viewChecked, helper.worldChecked, helper.cfgChecked };

		for (int i = 0; i < folders.length; i++)
		{
			if (file.root.getPath().equals(folders[i].getPath()))
			{
				return checks[i];
			}
		}
		return false;
	}

	/**
	 * 获取XLS的MD5码
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private String getXlsMD5(File file) throws FileNotFoundException, IOException
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
			return MD5Util.md5Bytes(sb.toString().getBytes());
		}
		return "";
	}

	// ===============================================================================================================================================

	/**
	 * 读取技能信息
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String cherryPickSkillData(ConfigZip oldZip, ConfigZip newZip) throws Exception
	{
		GamePacker.progress("摘取技能汇总");

		File skillFile = null;
		String skillFileMD5 = null;
		String skillContent = null;

		// 查找技能文件
		ArrayList<File> folders = new ArrayList<File>();
		folders.add(helper.cfgInputFolder);
		while (folders.size() > 0)
		{
			File folder = folders.remove(0);
			for (File file : folder.listFiles())
			{
				if (file.isHidden())
				{
					continue;
				}

				if (file.isFile() && file.getName().equals("skills.xml"))
				{
					skillFile = file;
					skillFileMD5 = MD5Util.md5(file);
					break;
				}
			}
		}

		// 未找到时留空
		if (skillFile == null)
		{
			skillContent = "";
		}

		// 从历史信息中找
		skillContent = oldZip.getSkillContent(skillFileMD5);

		// 处理技能配置
		if (skillContent == null)
		{
			SkillConfigHandler skillHandler = new SkillConfigHandler();
			SAXParserFactory.newInstance().newSAXParser().parse(new FileInputStream(skillFile), skillHandler);

			StringBuilder sb = new StringBuilder();
			sb.append("\t\t<skills>\n");
			for (String key : skillHandler.groupToActions.keySet())
			{
				String[] actions = skillHandler.groupToActions.get(key).toArray(new String[skillHandler.groupToActions.get(key).size()]);
				String[] effects = new String[] {};

				if (skillHandler.groupToEffects.containsKey(key))
				{
					effects = skillHandler.groupToEffects.get(key).toArray(new String[skillHandler.groupToEffects.get(key).size()]);
					skillHandler.groupToEffects.remove(key);
				}

				if (actions.length > 0 || effects.length > 0)
				{
					Arrays.sort(actions);
					Arrays.sort(effects);
					sb.append(String.format("\t\t\t<skill id=\"%s\" actions=\"%s\" effects=\"%s\"/>\n", key, formatArray(actions), formatArray(effects)));
				}
			}
			for (String key : skillHandler.groupToEffects.keySet())
			{
				String[] effects = skillHandler.groupToEffects.get(key).toArray(new String[skillHandler.groupToEffects.get(key).size()]);
				if (effects.length > 0)
				{
					Arrays.sort(effects);
					sb.append(String.format("\t\t\t<skill id=\"%s\" actions=\"\" effects=\"%s\" />\n", key, formatArray(effects)));
				}
			}
			for (String key : skillHandler.labelToEffects.keySet())
			{
				String[] effects = skillHandler.labelToEffects.get(key).toArray(new String[skillHandler.labelToEffects.get(key).size()]);
				if (effects.length > 0)
				{
					for (int i = 0; i < effects.length; i++)
					{
						effects[i] = effects[i].replaceAll("^[\\d+_]+", "");
					}
					Arrays.sort(effects);
					sb.append(String.format("\t\t\t<label id=\"%s\" effects=\"%s\" />\n", key, formatArray(effects)));
				}
			}
			sb.append("\t\t</skills>\n");

			skillContent = sb.toString();
		}

		// 保存到新的历史信息中
		newZip.setSkillContent(skillFileMD5, skillContent);

		return skillContent;
	}

	/**
	 * 格式化数组
	 * 
	 * @param txts
	 * @return
	 */
	private String formatArray(String[] txts)
	{
		StringBuilder sb = new StringBuilder();

		if (txts != null)
		{
			for (String txt : txts)
			{
				if (sb.length() > 0)
				{
					sb.append(",");
				}
				sb.append(txt);
			}
		}

		return sb.toString();
	}
}
