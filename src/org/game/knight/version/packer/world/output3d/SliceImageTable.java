package org.game.knight.version.packer.world.output3d;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.chw.util.FileUtil;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.ProjectImgFile;
import org.game.knight.version.packer.world.model.Scene;
import org.game.knight.version.packer.world.model.SceneAnim;
import org.game.knight.version.packer.world.model.SceneBackLayer;
import org.game.knight.version.packer.world.task.RootTask;

public class SliceImageTable
{
	private RootTask root;
	private List<ProjectImgFile> imgs;
	
	/**
	 * 构造函数
	 * @param root
	 */
	public SliceImageTable(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		imgs=Collections.synchronizedList(Arrays.asList(filterSliceImage()));
	}
	
	
	
	//--------------------------------------------------------------------------
	//
	// 过滤所有需要切片的图像
	//
	//--------------------------------------------------------------------------
	
	/**
	 * 过滤需要切片的图像文件
	 * @return
	 */
	private ProjectImgFile[] filterSliceImage()
	{
		HashSet<ProjectImgFile> imgFiles = new HashSet<ProjectImgFile>();

		Scene[] scenes = root.getWorldTable().getAllScene();
		for (Scene scene : scenes)
		{
			for (SceneBackLayer layer : scene.backLayers)
			{
				imgFiles.add(layer.img.imgFile);
			}
			for (SceneAnim role : scene.backAnims)
			{
				if (role.attire == null)
				{
					continue;
				}
				
				for (AttireAction action : role.attire.actions)
				{
					for (AttireAnim anim : action.anims)
					{
						imgFiles.add(anim.img);
					}
				}
			}
		}
		
		return imgFiles.toArray(new ProjectImgFile[imgFiles.size()]);
	}

	// ------------------------------------------------------------------------------------------------------------
	//
	// 查找需要切割的图像
	//
	// ------------------------------------------------------------------------------------------------------------
	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + ".slice");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		this.nextID = 1;
		this.oldTable = new HashMap<String, String>();
		this.newTable = new HashMap<String, String>();

		if (!getVerFile().exists())
		{
			return;
		}

		try
		{
			String text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
			String[] lines = text.split("\\n");
			for (String line : lines)
			{
				line = line.trim();

				if (line.startsWith("\\$"))
				{
					String[] values = line.substring(1).split("=");
					if (values.length == 2)
					{
						String name = values[0].trim();
						String value = values[1].trim();

						if ("nextID".equals(name))
						{
							nextID = Integer.parseInt(value);
						}
					}
				}
				else
				{
					String[] values = line.split("=");
					if (values.length == 2)
					{
						String key = values[0].trim();
						String url = values[1].trim();
						
						oldTable.put(key, url);
					}
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 保存版本信息
	 */
	private void saveVer()
	{
		StringBuilder output = new StringBuilder();

		output.append("$nextID = " + nextID + "\n");

		if (newTable != null)
		{
			String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
			Arrays.sort(keys);

			for (String key : keys)
			{
				output.append(key + " = " + newTable.get(key) + "\n");
			}
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
