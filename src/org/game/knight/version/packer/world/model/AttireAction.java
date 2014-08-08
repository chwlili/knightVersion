package org.game.knight.version.packer.world.model;


public class AttireAction
{
	/**
	 * ID
	 */
	public final int id;

	/**
	 * �������
	 */
	public final AttireHitRect hitRect;

	/**
	 * �����б�
	 */
	public final AttireAnim[] anims;

	/**
	 * ��Ч�б�
	 */
	public final AttireAudio[] audios;

	/**
	 * ���캯��
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
