package org.game.knight.version.packer.cfg;

import java.util.ArrayList;

public class SkillData extends AbsXmlNode
{

	public int skillCooldown;
	public String skillPrevAction;
	public boolean skillIgnorePath;
	
	public ArrayList<Integer> skillAttackPath = new ArrayList<Integer>();
	public ArrayList<Integer> skillBulletList=new ArrayList<Integer>();
	public ArrayList<Integer> skillQuiverList=new ArrayList<Integer>();
	
	public ArrayList<Integer> skillHitList=new ArrayList<Integer>();

	@Override
	protected String buildString()
	{
		return String.format("<skillData cd=\"%s\" ignorePath=\"%s\" preActions=\"%s\" movePath=\"%s\" bullets=\"%s\" quivers=\"%s\" hits=\"%s\"/>", skillCooldown, skillIgnorePath, skillPrevAction,formatList(skillAttackPath), formatList(skillBulletList), formatList(skillQuiverList),formatList(skillHitList));
	}
}
