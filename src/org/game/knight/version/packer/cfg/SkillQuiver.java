package org.game.knight.version.packer.cfg;

public class SkillQuiver extends AbsXmlNode
{
	public int delayTime;
	public int duration;
	public int interval;
	public int distanceX;
	public int distanceY;
	public float scale;

	@Override
	protected String buildString()
	{
		return String.format("<quiver delayTime=\"%s\" duration=\"%s\" distanceX=\"%s\" distanceY=\"%s\" scale=\"%s\" interval=\"%s\" />", delayTime, duration, distanceX, distanceY, scale, interval);
	}
}
