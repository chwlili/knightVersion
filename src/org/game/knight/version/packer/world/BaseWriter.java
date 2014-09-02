package org.game.knight.version.packer.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public abstract class BaseWriter
{
	protected final WorldWriter root;
	private final String verFileName;

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public BaseWriter(WorldWriter root, String verFileName)
	{
		this.root = root;
		this.verFileName = verFileName;
	}

	/**
	 * ��ʼִ��
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception
	{
		startup();

		if (verFileName != null)
		{
			byte[] bytes = root.getHistory(verFileName);
			if (bytes != null)
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), "utf8"));
				try
				{
					readHistory(reader);
				}
				finally
				{
					reader.close();
				}
			}
		}

		exec();
	}

	/**
	 * ����
	 * 
	 * @throws Exception
	 */
	protected void startup() throws Exception
	{

	}

	/**
	 * ��ȡ��ʷ
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void readHistory(BufferedReader reader) throws Exception
	{

	}

	/**
	 * ִ��
	 */
	protected void exec() throws Exception
	{

	}

	/**
	 * ����汾
	 */
	public void saveVer() throws Exception
	{
		if (verFileName != null)
		{
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteOutput, "utf8"));
			try
			{
				saveHistory(writer);
				writer.flush();
				root.setHistoryOutputStream(verFileName, byteOutput.toByteArray());
			}
			finally
			{
				writer.close();
			}
		}
	}

	/**
	 * ������ʷ
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void saveHistory(BufferedWriter writer) throws Exception
	{

	}
}
