package org.game.knight.version.packer.cfg;

import java.util.ArrayList;

public class SkillHit extends AbsXmlNode
{
	public int hitID;
	public int delayTime;
	public int offsetX;
	public int offsetY;
	public int offsetZ;
	public int rangeX;
	public int rangeY;
	public int rangeZ;
	public int deathSkillID;
	public int hitEffect;
	public int quiver;
	
	public ArrayList<Integer> skillAttackPath = new ArrayList<Integer>();
	
	@Override
	protected String buildString()
	{
		return String.format("<hit hitID=\"%s\" delay=\"%s\" x=\"%s\" y=\"%s\" z=\"%s\" rangeX=\"%s\" rangeY=\"%s\" rangeZ=\"%s\" deathSkillID=\"%s\" movePath=\"%s\" hitEffect=\"%s\" quiver=\"%s\"/>", hitID, delayTime, offsetX, offsetY, offsetZ, rangeX, rangeY, rangeZ, deathSkillID, formatList(skillAttackPath),hitEffect,quiver);
	}
}
