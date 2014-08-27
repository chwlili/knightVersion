package org.game.knight.version.packer.world.output3d;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.Attire;
import org.game.knight.version.packer.world.model.AttireAction;
import org.game.knight.version.packer.world.model.AttireAnim;
import org.game.knight.version.packer.world.model.AttireAudio;
import org.game.knight.version.packer.world.model.ImageFrame;
import org.game.knight.version.packer.world.task.RootTask;

public class Config3dAttireWriter
{
	private RootTask root;
	private String outputURL;

	private HashMap<String, String> newTable = new HashMap<String, String>();
	private HashMap<String, String> oldTable = new HashMap<String, String>();

	/**
	 * 构造函数
	 * 
	 * @param root
	 */
	public Config3dAttireWriter(RootTask root)
	{
		this.root = root;
	}

	/**
	 * 开始
	 */
	public void start()
	{
		GamePacker.progress("输出装扮配置");

		openVer();

		writeAttireConfig();
	}

	/**
	 * 获取输出URL
	 * 
	 * @return
	 */
	public String getOutputURL()
	{
		return outputURL;
	}

	/**
	 * 输出装扮配置
	 */
	private void writeAttireConfig()
	{
		Attire[] attires = root.getAttireTable().getAllAttire();
		Arrays.sort(attires, new Comparator<Attire>()
		{
			@Override
			public int compare(Attire o1, Attire o2)
			{
				return o1.gid.compareTo(o2.gid);
			}
		});

		// 统计所有的贴图文件地址
		HashSet<String> atfURLs = new HashSet<String>();
		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			for (AttireAction action : attire.actions)
			{
				for (AttireAnim anim : action.anims)
				{
					for (int i = 0; i < anim.row * anim.col; i++)
					{
						if (anim.times[i] > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								Atlas atlas = root.getAtlasTable().findAtlasByImageFrame(frame);
								if (atlas != null)
								{
									atfURLs.add(atlas.atfURL);
								}
							}
						}
					}
				}
			}
		}

		// 排序所有输出路径
		String[] atfURLArray = atfURLs.toArray(new String[atfURLs.size()]);
		Arrays.sort(atfURLArray);

		// 为所有输出路径分配ID
		HashMap<String, Integer> atfURL_refID = new HashMap<String, Integer>();
		for (int i = 0; i < atfURLArray.length; i++)
		{
			atfURL_refID.put(atfURLArray[i], i + 1);
		}

		// 生成配置文件
		StringBuilder attireText = new StringBuilder();
		attireText.append("<attires>\n");
		attireText.append("\t<textures>\n");
		for (int i = 0; i < atfURLArray.length; i++)
		{
			String atfURL = atfURLArray[i];
			int atfID = atfURL_refID.get(atfURL);
			File file = new File(root.getOutputFolder().getPath() + atfURL);

			attireText.append(String.format("\t\t<texture id=\"%s\" path=\"%s\" size=\"%s\" />\n", atfID, root.localToCdnURL(atfURL), file.length()));
		}
		attireText.append("\t</textures>\n");
		for (Attire attire : root.getAttireTable().getAllAttire())
		{
			attireText.append(String.format("\t<attire id=\"%s\" name=\"%s\" x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\">\n", attire.gid, attire.name, attire.hitRect.x, attire.hitRect.y, attire.hitRect.width, attire.hitRect.height));
			for (AttireAction action : attire.actions)
			{
				attireText.append(String.format("\t\t<action id=\"%s\" nameX=\"%s\" nameY=\"%s\">\n", action.id, action.hitRect.nameX, action.hitRect.nameY));
				for (AttireAnim anim : action.anims)
				{
					attireText.append("\t\t\t<anim x=\"" + anim.x + "\" y=\"" + anim.y + "\" scaleX=\"" + anim.scaleX + "\" scaleY=\"" + anim.scaleY + "\" flip=\"" + anim.flip + "\" actionID=\"" + action.id + "\" groupID=\"" + anim.groupID + "\" layerID=\"" + anim.layerID + "\">\n");

					int regionCount = anim.row * anim.col;
					for (int i = 0; i < regionCount; i++)
					{
						int delay = anim.times[i];
						if (delay > 0)
						{
							ImageFrame frame = root.getImageFrameTable().get(anim.img.gid, anim.row, anim.col, i);
							if (frame != null)
							{
								SliceImage slice = root.getSliceImageWriter().getSliceImage(frame);
								Atlas atlas = root.getAtlasTable().findAtlasByImageFrame(frame);
								if (slice != null)
								{
									attireText.append(String.format("\t\t\t\t<frame frameW=\"%s\" frameH=\"%s\" clipX=\"%s\" clipY=\"%s\" clipW=\"%s\" clipH=\"%s\" sliceRow=\"%s\" sliceCol=\"%s\" previewURL=\"%s\" delay=\"%s\"/>\n", slice.frame.frameW, slice.frame.frameH, slice.frame.clipX, slice.frame.clipY, slice.frame.clipW, slice.frame.clipH, slice.sliceRow, slice.sliceCol, root.localToCdnURL(slice.previewURL), delay));
								}
								else if (atlas != null)
								{
									attireText.append("\t\t\t\t<frame texture=\"" + root.localToCdnURL(atlas.atfURL) + "\" frameID=\"" + frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + i + "\" frameW=\"" + frame.frameW + "\" frameH=\"" + frame.frameH + "\" delay=\"" + delay + "\"/>\n");
								}
							}
						}
					}
					attireText.append("\t\t\t</anim>\n");
				}
				for (AttireAudio audio : action.audios)
				{
					attireText.append(String.format("\t\t\t<audio path=\"%s\" loop=\"%s\" volume=\"%s\"/>\n", root.localToCdnURL(root.getMp3Writer().getMp3URL(audio.mp3)), audio.loop, audio.volume));
				}
				attireText.append("\t\t</action>\n");
			}
			attireText.append("\t</attire>\n");
		}
		attireText.append("</attires>");

		// 存储文件
		byte[] bytes = null;
		try
		{
			bytes = attireText.toString().getBytes("UTF-8");
			if (root.hasZIP())
			{
				bytes = ZlibUtil.compress(bytes);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}

		String md5 = MD5Util.md5Bytes(bytes);
		String url = oldTable.get(md5);

		if (url == null)
		{
			url = root.getGlobalOptionTable().getNextExportFile() + ".cfg";

			File outputFile = new File(root.getOutputFolder().getPath() + url);
			FileUtil.writeFile(outputFile, bytes);
			root.addFileSuffix(outputFile);
		}

		newTable.put(md5, url);
		outputURL = url;
	}

	// -------------------------------------------------------------------------------------------------------------------
	//
	// 版本信息
	//
	// -------------------------------------------------------------------------------------------------------------------

	/**
	 * 获取版本文件
	 * 
	 * @return
	 */
	private File getVerFile()
	{
		return new File(root.getOutputFolder().getPath() + File.separatorChar + ".ver" + File.separatorChar + "3dAttire");
	}

	/**
	 * 打开版本信息
	 */
	private void openVer()
	{
		newTable = new HashMap<String, String>();
		oldTable = new HashMap<String, String>();

		if (!getVerFile().exists())
		{
			return;
		}

		String text = null;
		try
		{
			text = new String(FileUtil.getFileBytes(getVerFile()), "utf8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return;
		}

		String[] lines = text.split("\n");
		for (String line : lines)
		{
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

				oldTable.put(key, val);
			}
		}
	}

	/**
	 * 保存版本信息
	 */
	public void saveVer()
	{
		if (newTable == null)
		{
			return;
		}

		String[] keys = newTable.keySet().toArray(new String[newTable.size()]);
		Arrays.sort(keys);

		StringBuilder output = new StringBuilder();
		for (String key : keys)
		{
			output.append(key + " = " + newTable.get(key) + "\n");
		}

		try
		{
			FileUtil.writeFile(getVerFile(), output.toString().getBytes("utf8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			GamePacker.error(e);
			return;
		}

		// 记录输出文件
		root.addOutputFile(outputURL);
	}
}
