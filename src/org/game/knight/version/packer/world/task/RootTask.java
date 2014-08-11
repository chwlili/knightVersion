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
	 * 构造函数
	 * @param inputFolder
	 * @param outputFolder
	 */
	public RootTask(File inputFolder, File outputFolder)
	{
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder;
	}

	/**
	 * 输入目录
	 * 
	 * @return
	 */
	public File getInputFolder()
	{
		return inputFolder;
	}

	/**
	 * 输出目录
	 * 
	 * @return
	 */
	public File getOutputFolder()
	{
		return outputFolder;
	}
	
	/**
	 * 文件表
	 * @return
	 */
	public ProjectFileTable getFileTable()
	{
		return fileTable;
	}
	
	/**
	 * ATF参数表
	 * @return
	 */
	public AtfParamTable getAtfParamTable()
	{
		return paramTable;
	}
	
	/**
	 * 装扮表
	 * @return
	 */
	public AttireTable getAttireTable()
	{
		return attireTable;
	}
	
	/**
	 * 世界表
	 * @return
	 */
	public WorldTable getWorldTable()
	{
		return worldTable;
	}
	
	/**
	 * 获取图像帧表
	 * @return
	 */
	public ImageFrameTable getImageFrameTable()
	{
		return imageFrameTable;
	}
	
	/**
	 * 输出文件表
	 * @return
	 */
	public WriteFileTable getWriteFileTable()
	{
		return writeFileTable;
	}

	/**
	 * 取消
	 */
	public synchronized void cancel()
	{
		cancel=true;
	}
	
	/**
	 * 是否已经取消
	 * 
	 * @return
	 */
	public synchronized boolean isCancel()
	{
		return cancel;
	}
	
	
	
	/**
	 * 开始读取装扮
	 */
	public void start()
	{
		GamePacker.beginTask("世界");
		
		GamePacker.progress("读取输入信息");
		fileTable=new ProjectFileTable(this);
		fileTable.start();
		if(isCancel())
		{
			return;
		}
		
		GamePacker.progress("读取ATF分组信息");
		paramTable = new AtfParamTable(this);
		paramTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("读取装扮信息");
		attireTable=new AttireTable(this);
		attireTable.start();
		if (isCancel())
		{
			return;
		}
		
		GamePacker.progress("读取世界信息");
		worldTable=new WorldTable(this);
		worldTable.start();
		if (isCancel())
		{
			return;
		}

		GamePacker.progress("计算图像的裁切信息");
		imageFrameTable=new ImageFrameTable(this);
		imageFrameTable.start();
		if(isCancel())
		{
			return;
		}

		GamePacker.progress("读取文件导出表");
		writeFileTable=new WriteFileTable(this);
		writeFileTable.start();
		if(isCancel())
		{
			return;
		}
	}
}
