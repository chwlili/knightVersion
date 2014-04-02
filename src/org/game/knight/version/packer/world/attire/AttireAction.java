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
	 * 构造函数
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
	 * 获取动作ID
	 * 
	 * @return
	 */
	public int getID()
	{
		return id;
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
	 * 获取名称X坐标
	 * @return
	 */
	public int getNameX()
	{
		return nameX;
	}
	
	/**
	 * 获取名称Y名称
	 * @return
	 */
	public int getNameY()
	{
		return nameY;
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

	/**
	 * 添加动画
	 * 
	 * @param anim
	 */
	public void addAnim(AttireAnim anim)
	{
		animList.add(anim);
	}

	/**
	 * 获取动画
	 * 
	 * @return
	 */
	public ArrayList<AttireAnim> getAnims()
	{
		return animList;
	}

	/**
	 * 添加音效
	 * 
	 * @param audio
	 */
	public void addAudio(AttireAudio audio)
	{
		audioList.add(audio);
	}

	/**
	 * 获取音效
	 * 
	 * @return
	 */
	public ArrayList<AttireAudio> getAudios()
	{
		return audioList;
	}
}
