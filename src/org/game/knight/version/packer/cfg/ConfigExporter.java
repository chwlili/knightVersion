package org.game.knight.version.packer.cfg;

import java.io.ByteArrayInputStream;
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
	 * ���캯��
	 * 
	 * @param src
	 * @param dst
	 */
	public ConfigExporter(File src, File xml2, File dst, boolean zip)
	{
		super("��������", src, dst);

		xml2Folder = xml2;
	}

	/**
	 * ��������
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

		// �����ļ�
		GamePacker.beginLogSet("��ȡ�ļ�");
		readDir(getSourceDir());
		readDir(xml2Folder);
		GamePacker.endLogSet();

		// �����ļ�
		GamePacker.beginLogSet("�����ļ�");
		String[] urls = new String[files.keySet().size()];
		urls = files.keySet().toArray(urls);
		Arrays.sort(urls);
		GamePacker.endLogSet();

		if (isCancel())
		{
			return;
		}

		// ��ȡ����������ת���ļ�
		HashMap<String, ClassTable> url_classTable = new HashMap<String, ClassTable>();
		String[] xml2URLs = xml2Files.keySet().toArray(new String[] {});
		for (int i = 0; i < xml2URLs.length; i++)
		{
			File xml2File = xml2Files.get(xml2URLs[i]);

			GamePacker.progress("��ȡ����ת���ļ���:(" + (i + 1) + "/" + xml2URLs.length + ") : " + xml2URLs[i]);

			ClassTable table = new ClassTable(xml2File);

			String inputURL = table.getInputFile();
			if (inputURL != null && table.getMainClass() != null)
			{
				url_classTable.put(inputURL, table);
			}
		}

		// ѹ�����ϲ������ļ�
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ArrayList<String> file_names = new ArrayList<String>();
		ArrayList<Integer> file_sizes = new ArrayList<Integer>();
		for (int i = 0; i < urls.length; i++)
		{
			String url = urls[i];

			File file = files.get(url);
			byte[] fileByte = null;

			if (url_classTable.containsKey(url) && url_classTable.get(url).getMainClass() != null)
			{
				GamePacker.progress("ת�������ļ�(" + (i + 1) + "/" + urls.length + "):", url);
				UnitConfigBuilder builder = new UnitConfigBuilder(url_classTable.get(url));
				fileByte = builder.build(new FileInputStream(file));

				// FileUtil.writeFile(new File(getDestDir().getPath() + "/" +
				// file.getName() + ".cfg"), fileByte);
			}
			else
			{
				GamePacker.progress("ѹ�������ļ�(" + (i + 1) + "/" + urls.length + "):", url);
				fileByte = ZlibUtil.compress(getFileContent(file));
			}

			file_names.add(url_classTable.containsKey(url) ? file.getName() : getFileName(file));
			file_sizes.add(fileByte.length);

			output.write(fileByte);

			if (isCancel())
			{
				return;
			}
		}

		// ��ֲ�����ļ�
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		int partSize = 500 * 1024;
		ArrayList<String> partMD5 = new ArrayList<String>();
		while (input.available() > 0)
		{
			byte[] part = new byte[Math.min(input.available(), partSize)];

			input.read(part);

			part = MD5Util.addSuffix(part);

			String md5 = MD5Util.md5Bytes(part);

			partMD5.add(md5);

			exportFile(md5, part, "cfg");
		}

		// ��֯�ļ��ֲ���
		StringBuilder cfgData = new StringBuilder();
		cfgData.append("\t<configs>\n");
		for (int i = 0; i < file_names.size(); i++)
		{
			cfgData.append(String.format("\t\t<part name=\"%s\" size=\"%s\" />\n", file_names.get(i), file_sizes.get(i)));
		}
		for (int i = 0; i < partMD5.size(); i++)
		{
			String md5 = partMD5.get(i);
			cfgData.append(String.format("\t\t<partFile path=\"%s\" size=\"%s\" />\n", getExportedFileUrl(md5), getExportedFileSize(md5)));
		}
		cfgData.append("\t</configs>\n");

		if (isCancel())
		{
			return;
		}

		SkillConfigHandler skillHandler = new SkillConfigHandler();
		for (String url : urls)
		{
			if (url.endsWith("skills.xml"))
			{
				File file = new File(getSourceDir().getPath() + File.separator + url);
				if (file.exists())
				{
					// �����������
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser parser = factory.newSAXParser();

					parser.parse(new FileInputStream(file), skillHandler);
					break;
				}
			}
		}

		// ������Ŀ����
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("���ɻ�����Ϣ");
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
		GamePacker.log("���������Ϣ");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), sb.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		// �����ļ��б�
		GamePacker.beginLogSet("����ļ�����");
		GamePacker.log("�����ļ�����");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList = getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("�����ļ�����");
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
	 * ��ȡĿ¼
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
						GamePacker.progress("��ȡ�ļ�", innerPath);
						this.files.put(innerPath, file);

						if (innerPath.endsWith("skills.xml"))
						{
							skillFile = file;
						}
					}
					else if (innerPath.toLowerCase().endsWith(".xml2"))
					{
						GamePacker.progress("��ȡ�ļ�", innerPath);
						this.xml2Files.put(innerPath, file);
					}
				}
			}
		}
	}

	/**
	 * ת�������ļ�����
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
		// ת������
		if (skillFile != null && skillFile.getPath().equals(file.getPath()))
		{
			SkillConvert convert = new SkillConvert();
			convert.build(skillFile);

			// ת������
			FileUtil.writeFile(new File(getDestDir().getPath() + "/skills.xml"), convert.getContent().getBytes("UTF-8"));

			return convert.getContent().getBytes("UTF-8");
		}

		return FileUtil.getFileBytes(file);
	}
}
