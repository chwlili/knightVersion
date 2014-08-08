package org.game.knight.version.packer.world.model;


public class AttireAction
{
	/**
	 * ID
	 */
	public final int id;

	/**
	 * 打击矩形
	 */
	public final AttireHitRect hitRect;

	/**
	 * 动画列表
	 */
	public final AttireAnim[] anims;

	/**
	 * 单效列表
	 */
	public final AttireAudio[] audios;

	/**
	 * 构造函数
	 * 
	 * @param id
	 */
	public AttireAction(int id, AttireHitRect hitRect, AttireAnim[] anims, AttireAudio[] audios)
	{
		this.id = id;
		this.hitRect = hitRect;
		this.anims = anims;
		this.audios = audios;
	}
}
