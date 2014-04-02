package org.game.knight.version.packer.cfg;

import java.util.HashSet;


public class Skill
{
	public int skillID;
	public int skillLv;
	
	public HashSet<Integer> consumeBuffs=new HashSet<Integer>();
	public HashSet<Integer> gainBuffs=new HashSet<Integer>();
	public HashSet<Integer> gainLabels=new HashSet<Integer>();
	
	public int skillDataID;
}
