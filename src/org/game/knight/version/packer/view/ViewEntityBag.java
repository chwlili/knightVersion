package org.game.knight.version.packer.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.chw.swf.clear.SwfTagClear;
import org.chw.swf.writer.SwfBitmap;
import org.chw.swf.writer.SwfMp3;
import org.chw.swf.writer.SwfWriter;
import org.chw.swf.writer.SwfXML;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;

public class ViewEntityBag
{
	private ViewEntityWriter writer;
	private ArrayList<ViewEntity> entitys = new ArrayList<ViewEntity>();
	private String outputPath = null;

	public ViewEntityBag(ViewEntityWriter writer)
	{
		this.writer = writer;
	}

	public void add(ViewEntity entity)
	{
		entitys.add(entity);
		entity.setOutputBag(this);
	}

	public String getOutputPath()
	{
		if (outputPath == null)
		{
			Collections.sort(entitys, new Comparator<ViewEntity>()
			{
				@Override
				public int compare(ViewEntity o1, ViewEntity o2)
				{
					return o1.getContentMD5().compareTo(o2.getContentMD5());
				}
			});

			StringBuilder sb = new StringBuilder();
			for (ViewEntity entity : entitys)
			{
				sb.append(entity.getContentMD5());
			}
			outputPath = writer.getFileOutputPath(sb.toString(), "swf");
		}
		return outputPath;
	}

	public void writeTo(File file) throws IOException
	{
		if (entitys.size() == 1 && entitys.get(0).isSwf)
		{
			// 复制文件
			FileUtil.copyFile(entitys.get(0).file, file);
			// 清除swf中的运行时库引用。
			SwfTagClear.clearSwfFile(file);
			// 清加MD5后缀
			FileUtil.writeFile(file, MD5Util.addSuffix(FileUtil.getFileBytes(file)));
		}
		else
		{
			SwfWriter writer = new SwfWriter();

			Collections.sort(entitys, new Comparator<ViewEntity>()
			{
				@Override
				public int compare(ViewEntity o1, ViewEntity o2)
				{
					return o1.getContentMD5().compareTo(o2.getContentMD5());
				}
			});

			for (ViewEntity entity : entitys)
			{
				String path = entity.file.getPath().toLowerCase();
				if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png"))
				{
					writer.addBitmap(new SwfBitmap(entity.file, "app.files", entity.getOutputTypeName()));
				}
				else if (path.endsWith(".mp3"))
				{
					writer.addMp3(new SwfMp3(FileUtil.getFileBytes(entity.file), "app.files", entity.getOutputTypeName()));
				}
				else if (path.endsWith(".xml"))
				{
					writer.addXml(new SwfXML(entity.getOutputBytes(), "app.files", entity.getOutputTypeName()));
				}
			}

			FileUtil.writeFile(file, MD5Util.addSuffix(writer.toBytes(true)));
		}
	}
}
