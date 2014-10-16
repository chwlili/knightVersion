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
	private File cfgFolder;
	private File xml2Folder;
	private File fileFolder;
	private File iconFolder;

	public GamePackerHelper(File cfgFolder, File xml2Folder, File fileFolder, File iconFolder)
	{
		this.cfgFolder = cfgFolder;
		this.xml2Folder = xml2Folder;
		this.fileFolder = fileFolder;
		this.iconFolder = iconFolder;
	}

	public File getCfgFolder()
	{
		return cfgFolder;
	}

	public File getXml2Folder()
	{
		return xml2Folder;
	}

	public File getFileFolder()
	{
		return fileFolder;
	}

	public File getIconFolder()
	{
		return iconFolder;
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
