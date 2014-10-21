package org.game.knight.version.packer.cfg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

			String cfgContents = mergeConfigs(oldZip, newZip);
			if (GamePacker.isCancel())
			{
				return false;
			}

			String skillContents = cherryPickSkillData(oldZip, newZip);
			if (GamePacker.isCancel())
			{
				return false;
			}

			StringBuilder dbContent = new StringBuilder();
			dbContent.append("<project>\n");
			dbContent.append(cfgContents);
			dbContent.append(skillContents);
			dbContent.append("</project>");

			if (GamePacker.isCancel())
			{
				return false;
			}

			newZip.setVersion(dbContent.toString());
			newZip.setVersionProps(oldZip.getVersionProps());
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

	protected static class XmlFile extends File
	{
		public XmlFile(String path)
		{
			super(path);
		}

		public boolean isIconDir()
		{
			return getParentFile().getName().equals("icons");
		}

		public boolean isFileDir()
		{
			return getParentFile().getName().equals("files");
		}

		public boolean isCodeDir()
		{
			return getParentFile().getName().equals("games");
		}

		public boolean isViewDir()
		{
			return getParentFile().getName().equals("views");
		}

		public boolean isWorldDir()
		{
			return getParentFile().getName().equals("world");
		}

		public boolean isConfigsDir()
		{
			return getParentFile().getName().equals("configs");
		}
	}

	protected static class BuildItem
	{
		public final String name;
		public final String path;
		public final String lang;

		public final File xmlFile;
		public final File xml2File;
		public final File xlsFile;

		public final String xmlMD5;
		public final String xml2MD5;
		public final String xlsMD5;

		public byte[] bytes;

		public BuildItem(String name, String xmlPath, String langName, File xmlFile, String xmlMD5, File xml2File, String xml2MD5, File xlsFile, String xlsMD5, byte[] bytes)
		{
			this.name = name;
			this.path = xmlPath;
			this.lang = langName;
			this.xmlFile = xmlFile;
			this.xmlMD5 = xmlMD5;
			this.xml2File = xml2File;
			this.xml2MD5 = xml2MD5;
			this.xlsFile = xlsFile;
			this.xlsMD5 = xlsMD5;
			this.bytes = bytes;
		}
	}

	protected String mergeConfigs(ConfigZip oldZip, ConfigZip newZip) throws Exception
	{
		File[] $xmlFiles = listAll$XmlFiles();
		File[] xmlFiles = listAllXmlFiles();
		File[] xml2Files = listAllXml2Files();
		File[] xlsFiles = listAllXlsFiles();

		// 确定文件MD5码
		HashMap<File, String> file_md5 = new HashMap<File, String>();
		ArrayList<File> allFiles = new ArrayList<File>();
		for (File file : $xmlFiles)
		{
			allFiles.add(file);
		}
		for (File file : xmlFiles)
		{
			allFiles.add(file);
		}
		for (File file : xml2Files)
		{
			allFiles.add(file);
		}
		for (File file : xlsFiles)
		{
			allFiles.add(file);
		}
		for (int i = 0; i < allFiles.size(); i++)
		{
			File file = allFiles.get(i);
			GamePacker.progress(String.format("检测文件变化(%s/%s)：%s。", i + 1, allFiles.size(), allFiles.get(i).getName()));

			if (file.getName().endsWith(".xls"))
			{
				file_md5.put(file, getXlsMD5(file));
			}
			else
			{
				file_md5.put(file, MD5Util.md5(file));
			}

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

			for (File xmlFile : xmlFiles)
			{
				String xmlName = xmlFile.getName().substring(0, xmlFile.getName().length() - 4);
				String xmlPath = xmlFile.getPath().substring(helper.cfgFolder.getPath().length()).replaceAll("\\\\", "/");
				String xmlMD5 = file_md5.get(xmlFile);

				File xml2File = xmlURL_xml2File.get(xmlPath);
				String xml2MD5 = "";

				if (xml2File != null)
				{
					xml2MD5 = file_md5.get(xml2File);
				}

				byte[] lastBytes = oldZip.getConfig(xmlMD5, xml2MD5, xlsMD5);

				BuildItem item = new BuildItem(xmlName, xmlPath, langName, xmlFile, xmlMD5, xml2File, xml2MD5, xlsFile, xlsMD5, lastBytes);
				allBuildItem.add(item);
				if (lastBytes == null)
				{
					addedItems.add(item);
					if (xml2File != null)
					{
						addedBuilders.put(item.path, item);
					}
				}
			}
			for (File $xmlFile : $xmlFiles)
			{
				String xmlName = $xmlFile.getName().substring(0, $xmlFile.getName().length() - 4);
				String xmlPath = $xmlFile.getName();
				String xmlMD5 = file_md5.get($xmlFile);

				File xml2File = xmlURL_xml2File.get($xmlFile.getName());
				String xml2MD5 = "";

				if (xml2File != null)
				{
					xml2MD5 = file_md5.get(xml2File);
				}

				byte[] lastBytes = oldZip.getConfig(xmlMD5, xml2MD5, xlsMD5);

				BuildItem item = new BuildItem(xmlName, xmlPath, langName, $xmlFile, xmlMD5, xml2File, xml2MD5, xlsFile, xlsMD5, lastBytes);
				allBuildItem.add(item);
				if (lastBytes == null)
				{
					addedItems.add(item);
					if (xml2File != null)
					{
						addedBuilders.put(item.path, item);
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

			UnitConfigBuilder builder = new UnitConfigBuilder(xmlURL_classTable.get(item.path));
			builder.read(new FileInputStream(item.xmlFile));
			builderMap.put(item.path, builder);

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
				GamePacker.progress(String.format("生成配置文件(%s/%s)：[%s] %s", buildIndex + 1, buildCount, item.lang, item.name));

				UnitConfigBuilder builder = builderMap.get(item.path);
				if (builder != null)
				{
					langTable.setSheet(item.xmlFile.getName().substring(0, item.xmlFile.getName().length() - 4));

					item.bytes = builder.toBytes(langTable);
				}
				else
				{
					item.bytes = ZlibUtil.compress(FileUtil.getFileBytes(item.xmlFile));
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
		HashMap<String, ArrayList<BuildItem>> writeItems = new HashMap<String, ArrayList<BuildItem>>();
		for (BuildItem item : allBuildItem)
		{
			if (!writeItems.containsKey(item.lang))
			{
				writeItems.put(item.lang, new ArrayList<BuildItem>());
			}
			writeItems.get(item.lang).add(item);
		}
		StringBuilder db_xml = new StringBuilder();
		String[] writeKeys = writeItems.keySet().toArray(new String[] {});
		Arrays.sort(writeKeys);
		int writeIndex = 0;
		int writeCount = allBuildItem.size();
		for (String writeKey : writeKeys)
		{
			ArrayList<BuildItem> items = writeItems.get(writeKey);
			Collections.sort(items, new Comparator<BuildItem>()
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
				GamePacker.progress("构建配置(" + (writeIndex + 1) + "/" + writeCount + "):", item.lang + " : " + item.name);

				ByteArrayOutputStream nameOutput = new ByteArrayOutputStream();
				nameOutput.write((item.name + (item.xml2File != null ? ".xml" : "")).getBytes("utf8"));
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

			db_xml.append("\t<configs langs=\"" + writeKey + "\" file=\"/" + outputFolder.getName() + writeURL + "\" size=\"" + writeBytes.length + "\"/>\n");
		}

		return db_xml.toString();
	}

	/**
	 * 列出所有的特殊XML文件
	 * 
	 * @return
	 */
	private File[] listAll$XmlFiles()
	{
		ArrayList<File> $XmlFiles = new ArrayList<File>();

		File tmp = new File(outputFolder.getPath() + "/tmp");
		if (tmp.exists())
		{
			tmp.delete();
		}

		File rootFolder = helper.outputFolder;
		for (File folder : rootFolder.listFiles())
		{
			if (folder.isHidden())
			{
				continue;
			}

			if (folder.isDirectory())
			{
				File zipFile = new File(folder.getPath() + "/ver.zip");
				if (zipFile.exists())
				{
					ZipConfig cfg = new ZipConfig(zipFile);

					HashMap<String, byte[]> entrys = cfg.getCfgFiles();
					for (String name : entrys.keySet())
					{
						File curr = new File(tmp.getPath() + "/" + folder.getName() + "/" + name);
						FileUtil.writeFile(curr, entrys.get(name));

						$XmlFiles.add(curr);
					}
				}
				else
				{
					for (File file : folder.listFiles())
					{
						if (file.isHidden())
						{
							continue;
						}

						if (file.isFile() && file.getName().startsWith("$") && file.getName().endsWith(".xml"))
						{
							$XmlFiles.add(file);
						}
					}
				}
			}
		}

		return $XmlFiles.toArray(new File[] {});
	}

	/**
	 * 列出所有xml文件
	 * 
	 * @return
	 */
	private File[] listAllXmlFiles()
	{
		ArrayList<XmlFile> xmlFiles = new ArrayList<ConfigExporter.XmlFile>();
		File[] files = helper.listFiles(helper.cfgFolder, "xml");
		for (File file : files)
		{
			xmlFiles.add(new XmlFile(file.getPath()));
		}
		return xmlFiles.toArray(new File[] {});
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
		folders.add(helper.cfgFolder);
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
			sb.append("\t<skills>\n");
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
					sb.append(String.format("\t\t<skill id=\"%s\" actions=\"%s\" effects=\"%s\"/>\n", key, formatArray(actions), formatArray(effects)));
				}
			}
			for (String key : skillHandler.groupToEffects.keySet())
			{
				String[] effects = skillHandler.groupToEffects.get(key).toArray(new String[skillHandler.groupToEffects.get(key).size()]);
				if (effects.length > 0)
				{
					Arrays.sort(effects);
					sb.append(String.format("\t\t<skill id=\"%s\" actions=\"\" effects=\"%s\" />\n", key, formatArray(effects)));
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
					sb.append(String.format("\t\t<label id=\"%s\" effects=\"%s\" />\n", key, formatArray(effects)));
				}
			}
			sb.append("\t</skills>\n");

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
