package org.game.knight.version.packer.cfg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.SAXParserFactory;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerHelper;
import org.game.knight.version.packer.base.ZipConfig;
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
			newZip.getVersionFiles().add("/" + outputFolder.getName() + "/" + oldZip.getFile().getName());
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

	private static class BuildItem
	{
		public final String lang;
		public final String mode;

		public final XmlFile xmlFile;
		public final Xml2File xml2File;
		public final XlsFile xlsFile;

		public byte[] bytes;

		public BuildItem(String langName, String mode, XmlFile xmlFile, Xml2File xml2File, XlsFile xlsFile, byte[] bytes)
		{
			this.lang = langName;
			this.mode = mode;

			this.xmlFile = xmlFile;
			this.xml2File = xml2File;
			this.xlsFile = xlsFile;

			this.bytes = bytes;
		}
	}

	protected String mergeConfigs(ConfigZip oldZip, ConfigZip newZip, String skills) throws Exception
	{
		// 列举相关文件
		XmlFile[] xmlFiles = listAllXmlFiles();
		Xml2File[] xml2Files = listAllXml2Files();
		XlsFile[] xlsFiles = listAllXlsFiles();

		if (GamePacker.isCancel())
		{
			return null;
		}

		// 确定语言列表
		HashSet<String> langNames = new HashSet<String>();
		HashMap<String, XlsFile> langName_file = new HashMap<String, XlsFile>();
		for (int i = 0; i < xlsFiles.length; i++)
		{
			XlsFile xlsFile = xlsFiles[i];
			langNames.add(xlsFile.getLangName());
			langName_file.put(xlsFile.getLangName(), xlsFile);
		}
		langNames.add("zh");
		GamePacker.log("确定语言列表：完成。");

		if (GamePacker.isCancel())
		{
			return null;
		}

		// 解析XML2文件
		HashMap<String, Xml2File> xmlURL_xml2File = new HashMap<String, Xml2File>();
		for (int i = 0; i < xml2Files.length; i++)
		{
			Xml2File xml2File = xml2Files[i];

			GamePacker.progress("解析xml2文件(" + (i + 1) + "/" + xml2Files.length + "):" + xml2File.getFileName());

			xml2File.buildClassTable();

			String inputURL = xml2File.getInputURL();
			if (xmlURL_xml2File.containsKey(inputURL))
			{
				String oldURL = xmlURL_xml2File.get(inputURL).getFileURL();
				String newURL = xml2File.getFileURL();
				GamePacker.error(String.format("输入路径\"%s\"对应到了多个xml2文件。(%s / %s)", inputURL, oldURL, newURL));
				return null;
			}
			else
			{
				xmlURL_xml2File.put(inputURL, xml2File);
			}

			if (GamePacker.isCancel())
			{
				return null;
			}
		}
		GamePacker.log("解析xml2文件：完成。");

		//
		ArrayList<BuildItem> allBuildItem = new ArrayList<BuildItem>();
		HashMap<String, ArrayList<BuildItem>> url_addedItem = new HashMap<String, ArrayList<BuildItem>>();
		for (String langName : langNames)
		{
			for (XmlFile file : xmlFiles)
			{
				String buildMode = file.is2D() ? "2d" : "3d";
				String buildName = file.getBuildName();
				String buildPath = file.getBuildPath();

				XmlFile xmlFile = file;
				Xml2File xml2File = xmlURL_xml2File.get(buildPath);
				XlsFile xlsFile = langName_file.get(langName);

				String xmlMD5 = file.getFileMD5();
				String xmlURL = file.getFileURL();
				String xml2MD5 = xml2File != null ? xml2File.getFileMD5() : "";
				String xlsMD5 = xlsFile != null ? xlsFile.getFileMD5() : "";

				if (allowOutput(file))
				{
					byte[] bytes = oldZip.getConfig(xmlMD5, xml2MD5, xlsMD5);

					BuildItem item = new BuildItem(langName, buildMode, xmlFile, xml2File, xlsFile, bytes);
					allBuildItem.add(item);

					if (bytes == null)
					{
						if (!url_addedItem.containsKey(xmlURL))
						{
							url_addedItem.put(xmlURL, new ArrayList<ConfigExporter.BuildItem>());
						}
						url_addedItem.get(xmlURL).add(item);
					}
				}
				else
				{
					String[] infos = oldZip.getXmlInfo(langName, buildMode, buildName);
					if (infos != null)
					{
						xmlMD5 = infos[0];
						xml2MD5 = infos[1];
						xlsMD5 = infos[2];

						byte[] bytes = oldZip.getConfig(xmlMD5, xml2MD5, xlsMD5);

						BuildItem item = new BuildItem(langName, buildMode, xmlFile, xml2File, xlsFile, bytes);
						allBuildItem.add(item);
					}
				}
			}
		}

		//
		HashMap<String, XlsLangFile> lang_langTable = new HashMap<String, XlsLangFile>();
		String[] xmlURLs = url_addedItem.keySet().toArray(new String[] {});
		for (int i = 0; i < xmlURLs.length; i++)
		{
			String xmlURL = xmlURLs[i];
			ArrayList<BuildItem> items = url_addedItem.get(xmlURL);

			GamePacker.progress(String.format("生成配置文件(%s/%s)：%s", i + 1, xmlURLs.length, xmlURL));

			XmlFile xml = items.get(0).xmlFile;
			Xml2File xml2 = items.get(0).xml2File;
			UnitConfigBuilder builder = null;
			if (xml2 != null)
			{
				builder = new UnitConfigBuilder(xml2.getClassTable());
				builder.read(new ByteArrayInputStream(xml.getFileBytes()));
			}

			for (BuildItem item : items)
			{
				GamePacker.progress(String.format("生成配置文件(%s/%s)：[ %s %s ] %s", i + 1, xmlURLs.length, item.lang, item.mode, item.xmlFile.getBuildPath()));

				if (builder != null)
				{
					XlsLangFile langTable = lang_langTable.get(item.lang);
					if (langTable == null)
					{
						if (helper.nlsFolder != null && helper.nlsFolder.exists())
						{
							langTable = new XlsLangFile(new File(helper.nlsFolder.getPath() + File.separator + item.lang + ".xls"));
							lang_langTable.put(item.lang, langTable);
						}
						else
						{
							langTable = new XlsLangFile(null);
							lang_langTable.put(item.lang, langTable);
						}
					}

					langTable.setSheet(item.xmlFile.getBuildName());

					item.bytes = builder.toBytes(langTable);
				}
				else
				{
					item.bytes = ZlibUtil.compress(item.xmlFile.getFileBytes());
				}
			}
		}
		GamePacker.log("生成配置文件：完成。");

		//
		for (XlsLangFile langTable : lang_langTable.values())
		{
			langTable.save();
		}

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
			writeItems.get(item.lang).get(item.mode).put(item.xmlFile.getBuildName(), item);
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
						return o1.xmlFile.getFileName().compareTo(o2.xmlFile.getFileName());
					}
				});

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				for (BuildItem item : items)
				{
					GamePacker.progress("构建配置(" + (writeIndex + 1) + "/" + writeCount + "):", "[ " + mode + " " + writeKey + " ] " + item.xmlFile.getFileName());

					String name = item.xmlFile.getBuildName();
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

					String xmlMD5 = item.xmlFile.getFileMD5();
					String xml2MD5 = item.xml2File != null ? item.xml2File.getFileMD5() : "";
					String xlsMD5 = item.xlsFile != null ? item.xlsFile.getFileMD5() : "";

					newZip.setConfig(xmlMD5, xml2MD5, xlsMD5, item.bytes);
					newZip.setXmlInfo(item.lang, item.mode, item.xmlFile.getBuildName(), xmlMD5, xml2MD5, xlsMD5);

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
		ArrayList<XmlFile> xmlFiles = new ArrayList<XmlFile>();

		// 常规XML配置
		File[] files = helper.listFiles(helper.cfgInputFolder, "xml");
		for (File file : files)
		{
			xmlFiles.add(new XmlFile(helper.cfgOutputFolder, file.getName(), file.getPath().substring(helper.cfgInputFolder.getPath().length()).replaceAll("\\\\", "/"), file));
		}

		// 特殊XML配置
		File[] outputs = new File[] { helper.cfgOutputFolder, helper.iconOutputFolder, helper.fileOutputFolder, helper.codeOutputFolder, helper.viewOutputFolder, helper.worldOutputFolder };
		for (File folder : outputs)
		{
			if (!folder.isHidden() && folder.isDirectory())
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
							xmlFiles.add(new XmlFile(folder, file.getName(), file.getName(), file));
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
	private Xml2File[] listAllXml2Files()
	{
		ArrayList<Xml2File> result = new ArrayList<Xml2File>();
		for (File file : helper.listFiles(helper.xml2Folder, "xml2"))
		{
			result.add(new Xml2File(file, file.getPath().substring(helper.xml2Folder.getPath().length()).replaceAll("\\\\", "/")));
		}
		return result.toArray(new Xml2File[] {});
	}

	/**
	 * 列出所有XLS文件
	 * 
	 * @return
	 */
	private XlsFile[] listAllXlsFiles()
	{
		ArrayList<XlsFile> result = new ArrayList<XlsFile>();
		for (File file : helper.listFiles(helper.nlsFolder, "xls"))
		{
			result.add(new XlsFile(file));
		}
		return result.toArray(new XlsFile[] {});
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
			if (file.getOutputFolder().getPath().equals(folders[i].getPath()))
			{
				return checks[i];
			}
		}
		return false;
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
