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
	 * ��ȡ�ļ���չ��
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
	 * ���Ƶ�
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

		// �½��ļ����������������л���
		FileInputStream input = null;
		BufferedInputStream inBuff = null;

		// �½��ļ���������������л���
		FileOutputStream output = null;
		BufferedOutputStream outBuff = null;

		try
		{
			// �½��ļ����������������л���
			input = new FileInputStream(from);
			inBuff = new BufferedInputStream(input);

			// �½��ļ���������������л���
			output = new FileOutputStream(dest);
			outBuff = new BufferedOutputStream(output);

			// ��������
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
	 * д���ļ�
	 * 
	 * @param dest
	 * @param bytes
	 */
	public static void writeFile(File dest, byte[] bytes)
	{
		writeFile(dest, new ByteArrayInputStream(bytes));
	}

	/**
	 * д���ļ�
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
			// �½��ļ����������������л���
			BufferedInputStream inBuff = new BufferedInputStream(input);

			// �½��ļ���������������л���
			FileOutputStream output = new FileOutputStream(dest);
			BufferedOutputStream outBuff = new BufferedOutputStream(output);

			// ��������
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
	 * �����ļ�
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
				// �½��ļ����������������л���
				FileInputStream input = new FileInputStream(from);
				BufferedInputStream inBuff = new BufferedInputStream(input);

				// �½��ļ���������������л���
				FileOutputStream output = new FileOutputStream(dest);
				BufferedOutputStream outBuff = new BufferedOutputStream(output);

				// ��������
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = inBuff.read(b)) != -1)
				{
					outBuff.write(b, 0, len);
				}
				outBuff.flush();

				// �ر���
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

			// ��ȡ�ļ���С
			long length = file.length();

			if (length > Integer.MAX_VALUE)
			{
				// �ļ�̫���޷���ȡ
				// throw new IOException("File is to large " + file.getName());
			}

			// ����һ�������������ļ�����
			byte[] bytes = new byte[(int) length];

			// ��ȡ���ݵ�byte������
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			{
				offset += numRead;
			}

			// ȷ���������ݾ�����ȡ
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
	 * �г������ļ������ļ�
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
