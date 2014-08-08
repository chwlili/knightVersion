package org.game.knight.version.packer.world.model;


public class AttireAudio
{
	/**
	 * ID
	 */
	public final int id;
	
	/**
	 * MP3文件
	 */
	public final ProjectMp3File mp3;
	
	/**
	 * 循环次数
	 */
	public final int loop;
	
	/**
	 * 音量
	 */
	public final float volume;
	
	/**
	 * 构造函数
	 * @param id
	 * @param mp3
	 * @param loop
	 * @param volume
	 */
	public AttireAudio(int id,ProjectMp3File mp3,int loop,float volume)
	{
		this.id=id;
		this.mp3=mp3;
		this.loop=loop;
		this.volume=volume;
	}
}
