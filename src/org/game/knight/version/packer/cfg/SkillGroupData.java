package org.game.knight.version.packer.cfg;


public class SkillGroupData extends AbsXmlNode
{
	//public int id;
	//public int level;
	public int type;
	public int faction;
	public int factionLv;
	//public int cooldown;
	//public int consumeBuff;
	public String name;
	public String desc;
	//public String descVar1;
	public String hotKey;
	public String skills;
	public String books;
	public int quality;

	@Override
	protected String buildString()
	{
		return String.format("<skillGroupData type=\"%s\" faction=\"%s\" factionLv=\"%s\" name=\"%s\" desc=\"%s\" hotKey=\"%s\" skills=\"%s\" books=\"%s\" quality=\"%s\"/>", type,faction,factionLv,name,desc,hotKey,skills,books,quality);
	}

}
