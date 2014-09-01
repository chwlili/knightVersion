package org.game.knight.version.packer.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
	 * ��ȡ�汾�ļ�
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		if (verFileName != null && !verFileName.isEmpty())
		{
			return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + verFileName);
		}
		return null;
	}

	/**
	 * ��ʼִ��
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception
	{
		startup();

		File file = getVerFile();
		if (file != null)
		{
			readHistory(new FileInputStream(file));
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
		File file = getVerFile();
		if (file != null)
		{
			saveHistory(new FileOutputStream(file));
		}
	}

	/**
	 * ��ȡ��ʷ
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void readHistory(InputStream stream) throws Exception
	{

	}

	/**
	 * ������ʷ
	 * 
	 * @param stream
	 * @throws Exception
	 */
	protected void saveHistory(OutputStream stream) throws Exception
	{

	}
}
