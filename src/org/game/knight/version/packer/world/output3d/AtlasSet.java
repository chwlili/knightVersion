package org.game.knight.version.packer.world.output3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.game.knight.version.packer.world.model.AtfParam;
import org.game.knight.version.packer.world.model.ImageFrame;

public class AtlasSet
{
	public final boolean anim;
	/**
	 * 索引键
	 */
	public final String key;

	/**
	 * ATF参数信息
	 */
	public final AtfParam atfParam;

	/**
	 * 原始图像帧列表
	 */
	public final ImageFrame[] frameList;

	/**
	 * 结果贴图集列表
	 */
	public final Atlas[] atlasList;

	/**
	 * 构造函数
	 * 
	 * @param atlasList
	 */
	public AtlasSet(boolean anim, AtfParam atfParam, Atlas[] atlasList)
	{
		this.anim = anim;
		this.atfParam = atfParam;
		this.atlasList = atlasList;

		ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>();
		for (Atlas atlas : atlasList)
		{
			for (AtlasRect rect : atlas.rects)
			{
				frames.add(rect.frame);
			}
		}
		this.frameList = frames.toArray(new ImageFrame[frames.size()]);
		this.key = createKey(anim, atfParam, this.frameList);
	}

	/**
	 * 创建引用键
	 * 
	 * @param atfParam
	 * @param frames
	 * @return
	 */
	public static String createKey(boolean anim, AtfParam atfParam, ImageFrame[] frames)
	{
		frames = Arrays.copyOf(frames, frames.length);
		Arrays.sort(frames, new Comparator<ImageFrame>()
		{
			@Override
			public int compare(ImageFrame o1, ImageFrame o2)
			{
				int id1 = Integer.parseInt(o1.file.gid);
				int id2 = Integer.parseInt(o2.file.gid);
				if (id1 != id2)
				{
					return id1 - id2;
				}
				else
				{
					id1 = o1.row;
					id2 = o2.row;
					if (id1 != id2)
					{
						return id1 - id2;
					}
					else
					{
						id1 = o1.col;
						id2 = o2.col;
						if (id1 != id2)
						{
							return id1 - id2;
						}
						else
						{
							return o1.index - o2.index;
						}
					}
				}
			}
		});

		StringBuilder sb = new StringBuilder();
		sb.append((anim ? 1 : 2) + "+" + atfParam.width + "+" + atfParam.height + "+" + atfParam.other + "<-");
		for (int i = 0; i < frames.length; i++)
		{
			if (i > 0)
			{
				sb.append("+");
			}
			ImageFrame frame = frames[i];
			sb.append(frame.file.gid + "_" + frame.row + "_" + frame.col + "_" + frame.index);
		}

		return sb.toString();
	}
}
