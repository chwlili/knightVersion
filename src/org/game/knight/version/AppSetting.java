package org.game.knight.version;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.DialogSettings;

/**
 * �������ô洢��
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
	 * ��
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
	 * ����
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
