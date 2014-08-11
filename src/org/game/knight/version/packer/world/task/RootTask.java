package org.game.knight.version.packer.world.task;

import java.io.File;

import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.AtfParamTable;
import org.game.knight.version.packer.world.model.AttireTable;
import org.game.knight.version.packer.world.model.ImageFrameTable;
import org.game.knight.version.packer.world.model.ProjectFileTable;
import org.game.knight.version.packer.world.model.WorldTable;
import org.game.knight.version.packer.world.model.WriteFileTable;

public class RootTask
{
	private boolean cancel;
	
	private final File inputFolder;
	private final File outputFolder;

	private ProjectFileTable fileTable;
	private AtfParamTable paramTable;
	private AttireTable attireTable;
	private WorldTable worldTable;
	private ImageFrameTable imageFrameTable;
	private WriteFileTable writeFileTable;

	/**
	 * ���캯��
	 * @param inputFolder
	 * @param outputFolder
	 */
	public RootTask(File inputFolder, File outputFolder)
	{
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
	}

	/**
	 * ����Ŀ¼
	 * 
	 * @return
	 */
	public File getInputFolder()
	{
		return inputFolder;
	}

	/**
	 * ���Ŀ¼
	 * 
	 * @return
	 */
	public File getOutputFolder()
	{
		return outputFolder;
	}
	
	/**
	 * �ļ���
	 * @return
	 */
	public ProjectFileTable getFileTable()
	{
		return fileTable;
	}
	
	/**
	 * ATF������
	 * @return
	 */
	public AtfParamTable getAtfParamTable()
	{
		return paramTable;
	}
	
	/**
	 * װ���
	 * @return
	 */
	public AttireTable getAttireTable()
	{
		return attireTable;
	}
	
	/**
	 * �����
	 * @return
	 */
	public WorldTable getWorldTable()
	{
		return worldTable;
	}
	
	/**
	 * ��ȡͼ��֡��
	 * @return
	 */
	public ImageFrameTable getImageFrameTable()
	{
		return imageFrameTable;
	}
	
	/**
	 * ����ļ���
	 * @return
	 */
	public WriteFileTable getWriteFileTable()
	{
		return writeFileTable;
	}

	/**
	 * ȡ��
	 */
	public synchronized void cancel()
	{
		cancel=true;
	}
	
	/**
	 * �Ƿ��Ѿ�ȡ��
	 * 
	 * @return
	 */
	public synchronized boolean isCancel()
	{
		return cancel;
	}
	
	
	
	/**
	 * ��ʼ��ȡװ��
	 */
	public void start()
	{
		GamePacker.beginTask("����");
		
		GamePacker.progress("��ȡ������Ϣ");
		fileTable=new ProjectFileTable(this);
		fileTable.start();
		if(isCancel())
		{
			return;
		}
		
		GamePacker.progress("��ȡATF������Ϣ");
		paramTable = new AtfParamTable(this);
		paramTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("��ȡװ����Ϣ");
		attireTable=new AttireTable(this);
		attireTable.start();
		if (isCancel())
		{
			return;
		}
		
		GamePacker.progress("��ȡ������Ϣ");
		worldTable=new WorldTable(this);
		worldTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("����ͼ��Ĳ�����Ϣ");
		imageFrameTable=new ImageFrameTable(this);
		imageFrameTable.start();
		if(isCancel())
		{
			return;
		}

		GamePacker.progress("��ȡ�ļ�������");
		writeFileTable=new WriteFileTable(this);
		writeFileTable.start();
		if(isCancel())
		{
			return;
		}
	}
}
