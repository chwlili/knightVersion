package org.game.knight.version.packer.world.model;

import java.util.Hashtable;

public class Attire
{
	public final String gid;
	public final String nativeName;
	public final String name;
	public final int type;
	public final String[] typeParams;
	public final AttireHitRect hitRect;
	public final AttireAction[] actions;
	
	private Hashtable<Integer, AttireAction> actionTable = new Hashtable<Integer, AttireAction>();

	/**
	 * 构造函数
	 * 
	 * @param key
	 * @param rectW
	 * @param rectH
	 * @param anims
	 * @param audios
	 */
	public Attire(String fileID, String key, int type, AttireHitRect hitRect,AttireAction[] actions)
	{
		this.gid=fileID+"."+key;
		this.nativeName = key;
		this.type = type;
		this.hitRect=hitRect;
		this.actionTable = new Hashtable<Integer, AttireAction>();
		this.actions=actions;
		
		//清除名称前的参数
		String lastIDs="";
		String lastName="";
		for(int i=0;i<key.length();i++)
		{
			char c=key.charAt(i);
			if(!Character.isDigit(c) && c!='-' && c!=',' && c!='_')
			{
				lastIDs=key.substring(0,i);
				lastName=key.substring(i);
				break;
			}
		}
		if(!lastName.isEmpty())
		{
			this.name = lastName;
		}
		else
		{
			this.name = key;
		}
		
		lastIDs=lastIDs.trim();
		for(int i=0;i<lastIDs.length();i++)
		{
			char c=lastIDs.charAt(i);
			if(c!='_' && c!='-' && c!=',')
			{
				lastIDs=lastIDs.substring(i);
				break;
			}
		}
		for(int i=lastIDs.length()-1;i>=0;i--)
		{
			char c=lastIDs.charAt(i);
			if(c!='_' && c!='-' && c!=',')
			{
				lastIDs=lastIDs.substring(0,i+1);
				break;
			}
		}
		
		this.typeParams = lastIDs.split("_");
		
		for(AttireAction action:actions)
		{
			actionTable.put(action.id, action);
		}
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
	 * 获取动作
	 * 
	 * @param id
	 * @return
	 */
	public AttireAction getAction(int id)
	{
		return actionTable.get(id);
	}
}
