package org.game.knight.version.packer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;
import org.xml2as.builder.ClassTable;
import org.xml2as.builder.UnitConfigBuilder;

public class GamePackerHelper
{
	public final boolean cfgChecked;
	public final File cfgInputFolder;
	public final File cfgOutputFolder;
	public final boolean iconChecked;
	public final File iconInputFolder;
	public final File iconOutputFolder;
	public final boolean fileChecked;
	public final File fileInputFolder;
	public final File fileOutputFolder;
	public final boolean codeChecked;
	public final File codeInputFolder;
	public final File codeOutputFolder;
	public final boolean viewChecked;
	public final File viewInputFolder;
	public final File viewOutputFolder;
	public final boolean worldChecked;
	public final File worldInputFolder;
	public final File worldOutputFolder;

	public final File outputFolder;

	public final File xml2Folder;
	public final File nlsFolder;

	public GamePackerHelper(boolean cfg, String cfgFolder, String cfgOutput, boolean icon, String iconFolder, String iconOutput, boolean file, String fileFolder, String fileOutput, boolean code, String codeFolder, String codeOutput, boolean view, String viewFolder, String viewOutput, boolean world, String worldFolder, String worldOutput, String outputFolder, String xml2Folder, String nlsFolder)
	{
		this.cfgChecked = cfg;
		this.cfgInputFolder = new File(cfgFolder);
		this.cfgOutputFolder = new File(cfgOutput);
		this.iconChecked = icon;
		this.iconInputFolder = new File(iconFolder);
		this.iconOutputFolder = new File(iconOutput);
		this.fileChecked = file;
		this.fileInputFolder = new File(fileFolder);
		this.fileOutputFolder = new File(fileOutput);
		this.codeChecked = code;
		this.codeInputFolder = new File(codeFolder);
		this.codeOutputFolder = new File(codeOutput);
		this.viewChecked = view;
		this.viewInputFolder = new File(viewFolder);
		this.viewOutputFolder = new File(viewOutput);
		this.worldChecked = world;
		this.worldInputFolder = new File(worldFolder);
		this.worldOutputFolder = new File(worldOutput);

		this.outputFolder = new File(outputFolder);

		this.xml2Folder = new File(xml2Folder);
		this.nlsFolder = new File(nlsFolder);
	}

	/**
	 * 按扩展名列出指定目录下所有文件
	 * 
	 * @param folder
	 * @param ext
	 * @return
	 */
	public File[] listFiles(File folder, String ext)
	{
		ArrayList<File> xmlFiles = new ArrayList<File>();

		ArrayList<File> folders = new ArrayList<File>();
		if (folder.exists() && folder.isDirectory())
		{
			folders.add(folder);
		}

		while (folders.size() > 0)
		{
			File[] files = folders.remove(0).listFiles();
			for (File file : files)
			{
				if (file.isHidden())
				{
					continue;
				}

				if (file.isDirectory())
				{
					folders.add(file);
					continue;
				}

				if (file.isFile())
				{
					if (ext.equals("*") || file.getName().toLowerCase().endsWith("." + ext))
					{
						xmlFiles.add(file);
					}
				}
			}
		}

		return xmlFiles.toArray(new File[] {});
	}

	/**
	 * 获取文件名
	 * 
	 * @param path
	 * @return
	 */
	public String getFileName(String path)
	{
		String name = "";

		int index = path.lastIndexOf("/");
		if (index == -1)
		{
			index = path.lastIndexOf("\\");
		}

		if (index != -1)
		{
			name = path.substring(index + 1);

			index = name.lastIndexOf(".");
			if (index != -1)
			{
				name = name.substring(0, index);
			}
		}

		return name;
	}

	/**
	 * 获取文件名
	 * 
	 * @param file
	 * @return
	 */
	public String getFileName(File file)
	{
		return getFileName(file.getPath());
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param file
	 * @return
	 */
	public String getFileExtName(File file)
	{
		return getFileExtName(file.getPath());
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param path
	 * @return
	 */
	public String getFileExtName(String path)
	{
		String ext = "";

		int index = path.lastIndexOf("/");
		if (index == -1)
		{
			index = path.lastIndexOf("\\");
		}

		if (index != -1)
		{
			String name = path.substring(index + 1);

			index = name.lastIndexOf(".");
			if (index != -1)
			{
				ext = name.substring(index + 1);
			}
		}

		return ext;
	}

	/**
	 * 获取XML2文件
	 * 
	 * @param name
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws CoreException
	 * @throws IOException
	 */
	public byte[] convertXmlToAs(InputStream input, String name) throws IOException, CoreException, SAXException, ParserConfigurationException
	{
		File file = null;
		if (xml2Folder != null)
		{
			ArrayList<File> folders = new ArrayList<File>();
			folders.add(xml2Folder);

			while (folders.size() > 0)
			{
				File folder = folders.remove(0);
				for (File child : folder.listFiles())
				{
					if (child.isHidden())
					{
						continue;
					}

					if (child.isDirectory())
					{
						folders.add(child);
					}
					else if (child.getName().endsWith(name))
					{
						file = child;
						break;
					}
				}
			}
		}
		if (file != null)
		{
			UnitConfigBuilder builder = new UnitConfigBuilder(new ClassTable(file));
			builder.read(input);
			return builder.toBytes(null);
		}
		return null;
	}
}
