package org.game.knight.version.packer.world.attire;

import org.game.knight.version.packer.world.Mp3File;

public class AttireAudio
{
	private int id;
	private Mp3File mp3;
	private int loop;
	private float volume;
	
	public AttireAudio(int id,Mp3File mp3,int loop,float volume)
	{
		this.id=id;
		this.mp3=mp3;
		this.loop=loop;
		this.volume=volume;
	}
	
	public int getID()
	{
		return id;
	}
	
	public Mp3File getMp3()
	{
		return mp3;
	}
	
	public int getLoop()
	{
		return loop;
	}
	
	public float getVolume()
	{
		return volume;
	}
}
