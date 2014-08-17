package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.AtfParam;

public class Atlas
{
	public final AtfParam atfParam;
	public final String atfURL;
	public final AtlasRect[] rects;

	public Atlas(AtfParam atfParam, String atfURL, AtlasRect[] rects)
	{
		this.atfParam = atfParam;
		this.atfURL = atfURL;
		this.rects = rects;
	}
}
