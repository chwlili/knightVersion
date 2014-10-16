package org.game.knight.version.packer.cfg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;
import org.xml.sax.SAXException;
import org.xml2as.builder.ClassTable;
import org.xml2as.builder.UnitConfigBuilder;

public class ConfigExporter extends AbsExporter
{
	private File xml2Folder;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public ConfigExporter(File src, File xml2, File dst, boolean zip)
	{
		super("导出配置", src, dst);

		xml2Folder = xml2;
	}

	/**
	 * 导出内容
	 * 
	 * @throws Exception
	 */
	@Override
	protected void exportContent() throws Exception
	{
		files = new Hashtable<String, File>();
		xml2Files = new Hashtable<String, File>();

		if (isCancel())
		{
			return;
		}

		// 遍历文件
		GamePacker.beginLogSet("读取文件");
		readDir(getSourceDir());
		readDir(xml2Folder);

		File[] otherFolders = getDestDir().getParentFile().listFiles();
		for (File otherFolder : otherFolders)
		{
			if (otherFolder.isDirectory() && otherFolder.isHidden() == false)
			{
				for (File file : otherFolder.listFiles())
				{
					if (file.getName().startsWith("$"))
					{
						this.files.put(file.getName(), file);
					}
				}
			}
		}
		GamePacker.endLogSet();

		// 排序文件
		GamePacker.beginLogSet("排序文件");
		String[] urls = new String[files.keySet().size()];
		urls = files.keySet().toArray(urls);
		Arrays.sort(urls);
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// 读取并解析配置转换文件
		HashMap<String, ClassTable> url_classTable = new HashMap<String, ClassTable>();
		String[] xml2URLs = xml2Files.keySet().toArray(new String[] {});
		for (int i = 0; i < xml2URLs.length; i++)
		{
			File xml2File = xml2Files.get(xml2URLs[i]);

			GamePacker.progress("读取配置转换文件中:(" + (i + 1) + "/" + xml2URLs.length + ") : " + xml2URLs[i]);

			ClassTable table = new ClassTable(xml2File);

			String inputURL = table.getInputFile();
			if (inputURL != null && table.getMainClass() != null)
			{
				url_classTable.put(inputURL, table);
			}
		}

		// 初始化Builder
		HashMap<String, UnitConfigBuilder> builderMap = new HashMap<String, UnitConfigBuilder>();
		for (int i = 0; i < urls.length; i++)
		{
			String url = urls[i];
			File file = files.get(url);
			if (url_classTable.containsKey(url))
			{
				GamePacker.progress("初始化构建器(" + (i + 1) + "/" + urls.length + "):", url);
				UnitConfigBuilder builder = new UnitConfigBuilder(url_classTable.get(url));
				builder.read(new FileInputStream(file));
				builderMap.put(url, builder);
			}
		}

		//
		StringBuilder cfgData = new StringBuilder();

		String[] langs = new String[] { "zh_CN", "tr_TR" };
		for (String lang : langs)
		{
			XlsLangTable xlsFile = new XlsLangTable(new File("C:\\Users\\ds\\Desktop\\" + lang + ".xls"));

			// 压缩并合并配置文件
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (int i = 0; i < urls.length; i++)
			{
				String url = urls[i];

				File file = files.get(url);
				byte[] fileByte = null;

				if (builderMap.containsKey(url))
				{
					GamePacker.progress("转换配置文件(" + (i + 1) + "/" + urls.length + "):", url);
					xlsFile.setSheet(file.getName().replaceAll("\\.xml", ""));
					fileByte = builderMap.get(url).toBytes(xlsFile);
				}
				else
				{
					GamePacker.progress("压缩配置文件(" + (i + 1) + "/" + urls.length + "):", url);
					fileByte = ZlibUtil.compress(getFileContent(file));
				}

				String fileName = url_classTable.containsKey(url) ? file.getName() : getFileName(file);

				ByteArrayOutputStream nameOutput = new ByteArrayOutputStream();
				nameOutput.write(fileName.getBytes("utf8"));
				byte[] nameBytes = nameOutput.toByteArray();

				output.write(nameBytes.length);
				output.write(nameBytes);
				output.write(fileByte.length);
				output.write(fileByte);

				if (isCancel())
				{
					return;
				}
			}
			xlsFile.save();

			// 拆分并输出文件
			byte[] outputBytes = MD5Util.addSuffix(output.toByteArray());
			String outputBytesMd5 = MD5Util.md5Bytes(outputBytes);

			exportFile(outputBytesMd5, outputBytes, "cfg");

			// 组织文件分布表
			cfgData.append("\t<configs langs=\"" + lang + "\" file=\"" + getExportedFileUrl(outputBytesMd5) + "\" size=\"" + getExportedFileSize(outputBytesMd5) + "\"/>\n");

			if (isCancel())
			{
				return;
			}
		}

		GamePacker.progress("摘取技能汇总");
		Thread.sleep(1000);

		SkillConfigHandler skillHandler = new SkillConfigHandler();
		for (String url : urls)
		{
			if (url.endsWith("skills.xml"))
			{
				File file = new File(getSourceDir().getPath() + File.separator + url);
				if (file.exists())
				{
					// 输出技能配置
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();

					parser.parse(new FileInputStream(file), skillHandler);
					break;
				}
			}
		}

		// 生成项目配置
		GamePacker.beginLogSet("输出汇总信息");
		GamePacker.log("生成汇总信息");
		StringBuilder sb = new StringBuilder();
		sb.append("<project>\n");
		sb.append(cfgData.toString());
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
		sb.append("</project>");
		GamePacker.log("保存汇总信息");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), sb.toString().getBytes("UTF-8"));
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

	private Hashtable<String, File> files;
	private File skillFile;

	private Hashtable<String, File> xml2Files;

	/**
	 * 读取目录
	 * 
	 * @param dir
	 */
	private void readDir(File root)
	{
		if (root == null)
		{
			return;
		}

		ArrayList<File> folders = new ArrayList<File>();
		if (root.isDirectory())
		{
			folders.add(root);
		}

		while (folders.size() > 0)
		{
			File folder = folders.remove(0);

			File[] files = folder.listFiles();
			for (File file : files)
			{
				if (file.isHidden())
				{
					continue;
				}

				if (file.isDirectory())
				{
					folders.add(file);
				}
				else
				{
					String innerPath = file.getPath().substring(root.getPath().length()).replaceAll("\\\\", "/");
					if (innerPath.toLowerCase().endsWith(".xml"))
					{
						GamePacker.progress("读取文件", innerPath);
						this.files.put(innerPath, file);

						if (innerPath.endsWith("skills.xml"))
						{
							skillFile = file;
						}
					}
					else if (innerPath.toLowerCase().endsWith(".xml2"))
					{
						GamePacker.progress("读取文件", innerPath);
						this.xml2Files.put(innerPath, file);
					}
				}
			}
		}
	}

	/**
	 * 转换配置文件内容
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private byte[] getFileContent(File file) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException
	{
		// 转换技能
		if (skillFile != null && skillFile.getPath().equals(file.getPath()))
		{
			SkillConvert convert = new SkillConvert();
			convert.build(skillFile);

			// 转换技能
			FileUtil.writeFile(new File(getDestDir().getPath() + "/skills.xml"), convert.getContent().getBytes("UTF-8"));

			return convert.getContent().getBytes("UTF-8");
		}

		return FileUtil.getFileBytes(file);
	}
}
