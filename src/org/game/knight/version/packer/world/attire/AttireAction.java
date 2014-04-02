package org.game.knight.version.packer.world.attire;

import java.util.ArrayList;

import org.game.knight.version.packer.world.TextureSetKey;

public class AttireAction
{
	private int id;
	private HitRect hitRect;
	private int nameX;
	private int nameY;

	private ArrayList<AttireAnim> animList = new ArrayList<AttireAnim>();
	private ArrayList<AttireAudio> audioList = new ArrayList<AttireAudio>();

	private TextureSetKey textureSetKey;

	/**
	 * ���캯��
	 * 
	 * @param id
	 */
	public AttireAction(int id, HitRect hitRect, int nameX, int nameY)
	{
		this.id = id;
		this.hitRect = hitRect;
		this.nameX = nameX;
		this.nameY = nameY;
	}

	/**
	 * ��ȡ����ID
	 * 
	 * @return
	 */
	public int getID()
	{
		return id;
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
	 * ��ȡ����X����
	 * @return
	 */
	public int getNameX()
	{
		return nameX;
	}
	
	/**
	 * ��ȡ����Y����
	 * @return
	 */
	public int getNameY()
	{
		return nameY;
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

	/**
	 * ��Ӷ���
	 * 
	 * @param anim
	 */
	public void addAnim(AttireAnim anim)
	{
		animList.add(anim);
	}

	/**
	 * ��ȡ����
	 * 
	 * @return
	 */
	public ArrayList<AttireAnim> getAnims()
	{
		return animList;
	}

	/**
	 * �����Ч
	 * 
	 * @param audio
	 */
	public void addAudio(AttireAudio audio)
	{
		audioList.add(audio);
	}

	/**
	 * ��ȡ��Ч
	 * 
	 * @return
	 */
	public ArrayList<AttireAudio> getAudios()
	{
		return audioList;
	}
}
