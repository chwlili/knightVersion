package org.game.knight.version.packer.cfg;

public class SkillHitEffect extends AbsXmlNode
{
	public int delay;
	public String attireId;
	public int offsetX;
	public int offsetY;
	public int duration;
	public boolean lockOwner;

	@Override
	protected String buildString()
	{
		return String.format("<effect delay=\"%s\" duration=\"%s\" attireID=\"%s\" x=\"%s\" y=\"%s\" lockOwner=\"%s\"/>", delay,duration,attireId,offsetX,offsetY,lockOwner ? "true":"false");
	}
}
