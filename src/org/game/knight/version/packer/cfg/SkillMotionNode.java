package org.game.knight.version.packer.cfg;

public class SkillMotionNode extends AbsXmlNode
{
	public int distanceX;
	public int distanceY;
	public int distanceH;

	public int easingX;
	public int easingY;
	public int easingH;

	public int actionID;

	public int duration;
	public int stiffTime;

	public int moveMode;
	public boolean isAir;
	public int speedX;
	
	@Override
	protected String buildString()
	{
		return String.format("<node distanceX=\"%s\" distanceY=\"%s\" distanceZ=\"%s\" easingX=\"%s\" easingY=\"%s\" easingZ=\"%s\" actionID=\"%s\" duration=\"%s\" stiffTime=\"%s\" moveMode=\"%s\" isAir=\"%s\" speedX=\"%s\"/>", 
				                     distanceX,       distanceY,       distanceH,       easingX,       easingY,       easingH,       actionID,       duration,       stiffTime,       moveMode,       isAir,       speedX);
	}
}
