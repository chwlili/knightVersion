package org.game.knight.version.packer.world.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class OptionTable extends BaseWriter
{
	/**
	 * ÿ��Ŀ¼�ļ���
	 */
	private static int FILE_COUNT_EACH_DIR = 1000;

	/**
	 * NextID ��
	 */
	private static final String NEXT_ID = "nextID";

	private int nextID = 1;

	/**
	 * ���캯��
	 * 
	 * @param root
	 */
	public OptionTable(WorldWriter root)
	{
		super(root, "option");
	}

	/**
	 * ��ȡ��һ�������ļ�
	 * 
	 * @return
	 */
	public synchronized String getNextExportFile()
	{
		long fileID = nextID;
		long folderID = (fileID - 1) / FILE_COUNT_EACH_DIR + 1;

		nextID++;

		return "/" + folderID + "/" + fileID;
	}

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("��ʼ��ȡȫ����Ϣ");
	}

	@Override
	protected void readHistory(BufferedReader reader) throws Exception
	{
		while (true)
		{
			String line = reader.readLine();
			if (line == null)
			{
				break;
			}

			line = line.trim();
			if (line.isEmpty())
			{
				continue;
			}

			String[] items = line.split("=");
			if (items.length == 2)
			{
				String key = items[0].trim();
				String val = items[1].trim();

				if (key.equals(NEXT_ID))
				{
					nextID = Integer.parseInt(val);
				}
			}
		}
	}

	@Override
	protected void saveHistory(BufferedWriter writer) throws Exception
	{
		writer.write(NEXT_ID + " = " + nextID + "\n");
	}
}
