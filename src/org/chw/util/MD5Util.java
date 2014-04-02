package org.chw.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util
{
	private static char	hexChar[]	= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	/**
	 * 计算文件的MD5
	 * @param file
	 * @return
	 */
	public static String md5File(File file)
	{
		try
		{
			InputStream fis = new FileInputStream(file);
			byte buffer[] = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance("SHA");
			for (int numRead = 0; (numRead = fis.read(buffer)) > 0;)
			{
				md5.update(buffer, 0, numRead);
			}

			fis.close();
			return toHexString(md5.digest());
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{

		}
		return "";
	}
	
	/**
	 * 计算字节数组的MD5
	 * @param bytes
	 * @return
	 */
	public static String md5Bytes(byte[] bytes)
	{
		try
		{
			InputStream fis =new ByteArrayInputStream(bytes);// new FileInputStream(file);
			byte buffer[] = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			for (int numRead = 0; (numRead = fis.read(buffer)) > 0;)
			{
				md5.update(buffer, 0, numRead);
			}

			fis.close();
			return toHexString(md5.digest());
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{

		}
		return "";
	}

	
	/**
	 * 转换为16进制显示
	 * @param b
	 * @return
	 */
	private static String toHexString(byte b[])
	{
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++)
		{
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0xf]);
		}

		return sb.toString();
	}
	
	/**
	 * 写入文件
	 * @param dest
	 * @param bytes
	 */
	public static void writeFile(File dest,byte[] bytes)
	{
		try
		{
			// 新建文件输出流并对它进行缓冲
			FileOutputStream output = new FileOutputStream(dest);
			BufferedOutputStream outBuff = new BufferedOutputStream(output);
			
			// 缓冲数组
			outBuff.write(bytes, 0, bytes.length);
			outBuff.flush();
			
			// 关闭流
			outBuff.close();
			output.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 复制文件
	 */
	public static void copyFile(File from,File dest)
	{
		if(from.isFile() && from.exists())
		{
			try
			{
				// 新建文件输入流并对它进行缓冲
				FileInputStream input = new FileInputStream(from);
				BufferedInputStream inBuff = new BufferedInputStream(input);
				
				// 新建文件输出流并对它进行缓冲
				FileOutputStream output = new FileOutputStream(dest);
				BufferedOutputStream outBuff = new BufferedOutputStream(output);
				
				// 缓冲数组
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = inBuff.read(b)) != -1)
				{
					outBuff.write(b, 0, len);
				}
				outBuff.flush();
				
				// 关闭流
				inBuff.close();
				outBuff.close();
				output.close();
				input.close();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
