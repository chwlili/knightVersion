package org.game.knight.version.packer.world.attire;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import org.game.knight.version.packer.world.TextureSetKey;

public class Attire
{
	private String fileID;
	private String key;
	private String refKey;
	private int type;
	private Integer[] params;

	private HitRect hitRect;
	private int nameX;
	private int nameY;

	private Hashtable<String,String> atfParams;

	private Hashtable<Integer, AttireAction> actionTable = new Hashtable<Integer, AttireAction>();

	private TextureSetKey textureSetKey;

	/**
	 * 构造函数
	 * 
	 * @param key
	 * @param rectW
	 * @param rectH
	 * @param anims
	 * @param audios
	 */
	public Attire(String fileID, String key, int type, HitRect hitRect, int nameX, int nameY, Hashtable<String,String> atfParams)
	{
		this.fileID = fileID;
		this.key = key;
		this.refKey = key.replaceAll("^[\\d+_]+", "");
		this.type = type;
		
		this.hitRect=hitRect;
		this.nameX = nameX;
		this.nameY = nameY;

		this.atfParams = atfParams;

		if (refKey == null || refKey.isEmpty())
		{
			this.refKey = key;
		}

		String[] parts = key.split("_");
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for (String part : parts)
		{
			try
			{
				int id = Integer.parseInt(part);
				ints.add(id);
			}
			catch (Exception err)
			{
				break;
			}
		}
		params = ints.toArray(new Integer[ints.size()]);

		actionTable = new Hashtable<Integer, AttireAction>();
	}

	/**
	 * 文件ID
	 * 
	 * @return
	 */
	public String getFileID()
	{
		return fileID;
	}

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * 参数表
	 * 
	 * @return
	 */
	public Integer[] getParams()
	{
		return params;
	}

	/**
	 * 是否为动画装扮
	 * 
	 * @return
	 */
	public boolean isAnimAttire()
	{
		return type == 1;
	}

	/**
	 * 获取打击矩形
	 * @return
	 */
	public HitRect getHitRect()
	{
		return hitRect;
	}

	/**
	 * 名称X坐标
	 * 
	 * @return
	 */
	public int getNameX()
	{
		return nameX;
	}

	/**
	 * 名称Y坐标
	 * 
	 * @return
	 */
	public int getNameY()
	{
		return nameY;
	}
	
	/**
	 * 获取ATF参数
	 * @return
	 */
	public Hashtable<String,String> getAtfParams()
	{
		return atfParams;
	}

	/**
	 * 添加动作
	 * 
	 * @param id
	 * @param action
	 */
	public void addAction(int id, AttireAction action)
	{
		actionTable.put(id, action);
	}

	/**
	 * 获取动作
	 * 
	 * @param id
	 * @return
	 */
	public AttireAction getAction(int id)
	{
		return actionTable.get(id);
	}

	/**
	 * 获取动作列表
	 * 
	 * @return
	 */
	public Collection<AttireAction> getActions()
	{
		return actionTable.values();
	}

	/**
	 * 获取引用KEY
	 * 
	 * @param key
	 * @return
	 */
	public String getRefKey()
	{
		return refKey;
	}

	/**
	 * 获取贴图集KEY
	 * 
	 * @return
	 */
	public TextureSetKey getTextureSetKey()
	{
		return textureSetKey;
	}

	/**
	 * 设置贴图集KEY
	 * 
	 * @param textureSetKey
	 */
	public void setTextureSetKey(TextureSetKey textureSetKey)
	{
		this.textureSetKey = textureSetKey;
	}
}
