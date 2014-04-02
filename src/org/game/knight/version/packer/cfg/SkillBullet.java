package org.game.knight.version.packer.cfg;

import java.util.ArrayList;

public class SkillBullet extends AbsXmlNode
{
	public int delay;
	public int duration;
	public String attireId;
	public int offsetX;
	public int offsetY;
	public int offsetZ;
	public ArrayList<Integer> nodes=new ArrayList<Integer>();

	@Override
	protected String buildString()
	{
		return String.format("<bullet delay=\"%s\" duration=\"%s\" attireID=\"%s\" x=\"%s\" y=\"%s\" z=\"%s\" movePath=\"%s\"/>", delay,duration,attireId,offsetX,offsetY,offsetZ,formatList(nodes));
	}
}
