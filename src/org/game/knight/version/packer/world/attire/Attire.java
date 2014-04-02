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
	 * ���캯��
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
	 * �ļ�ID
	 * 
	 * @return
	 */
	public String getFileID()
	{
		return fileID;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * ������
	 * 
	 * @return
	 */
	public Integer[] getParams()
	{
		return params;
	}

	/**
	 * �Ƿ�Ϊ����װ��
	 * 
	 * @return
	 */
	public boolean isAnimAttire()
	{
		return type == 1;
	}

	/**
	 * ��ȡ�������
	 * @return
	 */
	public HitRect getHitRect()
	{
		return hitRect;
	}

	/**
	 * ����X����
	 * 
	 * @return
	 */
	public int getNameX()
	{
		return nameX;
	}

	/**
	 * ����Y����
	 * 
	 * @return
	 */
	public int getNameY()
	{
		return nameY;
	}
	
	/**
	 * ��ȡATF����
	 * @return
	 */
	public Hashtable<String,String> getAtfParams()
	{
		return atfParams;
	}

	/**
	 * ��Ӷ���
	 * 
	 * @param id
	 * @param action
	 */
	public void addAction(int id, AttireAction action)
	{
		actionTable.put(id, action);
	}

	/**
	 * ��ȡ����
	 * 
	 * @param id
	 * @return
	 */
	public AttireAction getAction(int id)
	{
		return actionTable.get(id);
	}

	/**
	 * ��ȡ�����б�
	 * 
	 * @return
	 */
	public Collection<AttireAction> getActions()
	{
		return actionTable.values();
	}

	/**
	 * ��ȡ����KEY
	 * 
	 * @param key
	 * @return
	 */
	public String getRefKey()
	{
		return refKey;
	}

	/**
	 * ��ȡ��ͼ��KEY
	 * 
	 * @return
	 */
	public TextureSetKey getTextureSetKey()
	{
		return textureSetKey;
	}

	/**
	 * ������ͼ��KEY
	 * 
	 * @param textureSetKey
	 */
	public void setTextureSetKey(TextureSetKey textureSetKey)
	{
		this.textureSetKey = textureSetKey;
	}
}
