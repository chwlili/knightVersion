package org.game.knight.version.packer.world.model;

import java.io.File;
import java.util.HashMap;

import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.task.RootTask;

public class AtfParamTable
{
	private RootTask root;
	private HashMap<String, AtfParam> paramMap;
	
	/**
	 * 构造函数
	 * @param params
	 */
	public AtfParamTable(RootTask root)
	{
		this.root=root;
	}
	
	/**
	 * 获取Atf参数
	 * @param id
	 * @return
	 */
	public AtfParam getAtfParam(String id)
	{
		if(!paramMap.containsKey(id))
		{
			id="default";
		}
		
		return paramMap.get(id); 
	}
	
	/**
	 * 构建
	 */
	public void start()
	{
		paramMap=new HashMap<String, AtfParam>();
		
		ProjectFile[] files=root.getFileTable().getParamFiles();
		for(int i=0;i<files.length;i++)
		{
			if(root.isCancel())
			{
				return;
			}
			
			File file=files[i];

			GamePacker.progress(String.format("解析输出参数(%s/%s) : %s", i+1, files.length, file.getPath()));

			Document document = null;
			try
			{
				SAXReader reader = new SAXReader();
				document = reader.read(file);
			}
			catch (DocumentException e)
			{
				GamePacker.error("输出参数文件解析失败！(" + file.getPath() + ")   " + e);
				continue;
			}
			
			String[] lines = document.getRootElement().getText().split("\n");
			for (String line : lines)
			{
				line = line.trim();
				if (line.isEmpty())
				{
					continue;
				}

				String[] fields = line.split("=");
				if (fields.length != 2)
				{
					GamePacker.error("无效的输出参数！(" + file.getPath() + ")  " + line);
					continue;
				}
				
				String id = fields[0].trim();
				String[] params = fields[1].trim().split(",", 3);
				if(id.isEmpty() || params.length!=3)
				{
					GamePacker.error("无效的输出参数！(" + file.getPath() + ")  " + line);
					continue;
				}
				
				if (paramMap.containsKey(id))
				{
					GamePacker.error("输出参数ID冲突！(" + file.getPath() + " : " + id + ") -> ( " + paramMap.get(id).file.getPath() + " : " + id + ")");
					continue;
				}
				
				paramMap.put(id, new AtfParam(file, id, XmlUtil.parseInt(params[0].trim(), 0), XmlUtil.parseInt(params[1].trim(), 0), params[2].trim()));
			}
		}
		
		if (!paramMap.containsKey("default"))
		{
			paramMap.put("default", new AtfParam(null, "default", 2048, 2048, "-n 0:0 -r -q 30 -s"));
		}
	}
}
