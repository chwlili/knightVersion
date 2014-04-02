package org.game.knight.version;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.DialogSettings;

/**
 * 程序设置存储器
 * @author chw
 *
 */
public class AppSetting extends DialogSettings
{
	private String path;
	
	public AppSetting(String section)
	{
		super(section);
	}
	
	/**
	 * 打开
	 * @param fileName
	 */
	public void open(String fileName)
	{
		path=System.getProperty("user.home")+File.separatorChar+fileName;
		
		try
		{
			load(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存
	 */
	public void save()
	{
		try
		{
			save(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
