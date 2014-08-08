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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FileUtil
{
	/**
	 * 获取文件扩展名
	 * 
	 * @param path
	 * @return
	 */
	public static String getExt(String path)
	{
		int index = path.lastIndexOf(".");
		return path.substring(index + 1);
	}

	/**
	 * 复制到
	 * 
	 * @throws IOException
	 */
	public static void copyTo(File dest, File from) throws IOException
	{
		if (dest.exists() && from != null && from.isFile())
		{
			dest.delete();
		}

		if (!dest.getParentFile().exists())
		{
			dest.getParentFile().mkdirs();
		}

		// 新建文件输入流并对它进行缓冲
		FileInputStream input = null;
		BufferedInputStream inBuff = null;

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = null;
		BufferedOutputStream outBuff = null;

		try
		{
			// 新建文件输入流并对它进行缓冲
			input = new FileInputStream(from);
			inBuff = new BufferedInputStream(input);

			// 新建文件输出流并对它进行缓冲
			output = new FileOutputStream(dest);
			outBuff = new BufferedOutputStream(output);

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1)
			{
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		}
		finally
		{
			if (inBuff != null)
			{
				inBuff.close();
			}
			if (outBuff != null)
			{
				outBuff.close();
			}
			if (output != null)
			{
				output.close();
			}
			if (input != null)
			{
				input.close();
			}
		}
	}

	public static void writeFile(File dest, String txt)
	{
		try
		{
			writeFile(dest, txt.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 写入文件
	 * 
	 * @param dest
	 * @param bytes
	 */
	public static void writeFile(File dest, byte[] bytes)
	{
		writeFile(dest, new ByteArrayInputStream(bytes));
	}

	/**
	 * 写入文件
	 * 
	 * @param input
	 * @param dest
	 */
	public static void writeFile(File dest, InputStream input)
	{
		if (dest.exists())
		{
			dest.delete();
		}
		else
		{
			if (dest.getParent() == null)
			{
				return;
			}
			else if (!dest.getParentFile().exists())
			{
				dest.getParentFile().mkdirs();
			}
		}

		try
		{
			// 新建文件输入流并对它进行缓冲
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

			inBuff.close();
			outBuff.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 复制文件
	 */
	public static void copyFile(File from, File dest)
	{
		if (from.isFile() && from.exists())
		{
			if (dest.exists())
			{
				dest.delete();
			}
			else
			{
				if (dest.getParent() == null)
				{
					return;
				}
				else if (!dest.getParentFile().exists())
				{
					dest.getParentFile().mkdirs();
				}
			}

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

	public static byte[] getFileBytes(File file)
	{
		try
		{
			InputStream is = new FileInputStream(file);

			// 获取文件大小
			long length = file.length();

			if (length > Integer.MAX_VALUE)
			{
				// 文件太大，无法读取
				// throw new IOException("File is to large " + file.getName());
			}

			// 创建一个数据来保存文件数据
			byte[] bytes = new byte[(int) length];

			// 读取数据到byte数组中
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			{
				offset += numRead;
			}

			// 确保所有数据均被读取
			if (offset < bytes.length)
			{
				throw new IOException("Could not completely read file " + file.getName());
			}

			is.close();

			return bytes;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 列出所有文件、子文件
	 * @param folder
	 * @return
	 */
	public static File[] listFiles(File folder)
	{
		ArrayList<File> dirs = new ArrayList<File>();
		ArrayList<File> files = new ArrayList<File>();

		if(folder!=null && folder.exists())
		{
			if(folder.isDirectory())
			{
				dirs.add(folder);
			}
			else if(folder.isFile())
			{
				files.add(folder);
			}
		}
		
		while (dirs.size() > 0)
		{
			File curr = dirs.remove(0);
			if (curr.isDirectory())
			{
				File[] childs = curr.listFiles();
				for (int i = 0; i < childs.length; i++)
				{
					File child = childs[i];

					if (child.isHidden())
					{
						continue;
					}

					if (child.isDirectory())
					{
						dirs.add(child);
					}
					else
					{
						files.add(child);
					}
				}
			}
		}
		
		return files.toArray(new File[files.size()]);
	}
}
