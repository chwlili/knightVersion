package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.AtfParam;
import org.game.knight.version.packer.world.model.ImageFrame;

public class AtlasRect
{
	public final AtfParam param;
	public final ImageFrame frame;
	public final int x;
	public final int y;
	
	public AtlasRect(AtfParam param,ImageFrame frame, int x, int y)
	{
		this.param=param;
		this.frame = frame;
		this.x = x;
		this.y = y;
	}
}
