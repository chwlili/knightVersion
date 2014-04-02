package org.chw.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class CmdUtil
{
	public static void openDir(String path)
	{
		if (path == null || path.isEmpty()) { return; }

		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) { return; }

		try
		{
			Process p = Runtime.getRuntime().exec("cmd /c explorer " + file.getPath());
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}

	public static void openDirAndSelect(String path)
	{
		if (path == null || path.isEmpty()) { return; }

		File file = new File(path);
		if (!file.exists()) { return; }

		try
		{
			Process p = Runtime.getRuntime().exec("cmd /c explorer /select," + file.getPath());
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	

	public static void openWeb(String url)
	{
		try
		{
			URI uri = new URI(url);
			Desktop desktop = null;
			if (Desktop.isDesktopSupported())
			{
				desktop = Desktop.getDesktop();
			}
			if (desktop != null)
			{
				desktop.browse(uri);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch (URISyntaxException e)
		{
		}
	}

	public static void call(String cmd)
	{
		try
		{
			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
