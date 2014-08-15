package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.AtfParam;

public class Atlas
{
	public final String atfID;
	public final int width;
	public final int height;
	public final AtfParam atfParam;
	public final String atfURL;
	public final AtlasRect[] rects;

	public Atlas(String atfID,int width,int height,AtfParam atfParam, String atfURL, AtlasRect[] rects)
	{
		this.atfID=atfID;
		this.width=width;
		this.height=height;
		this.atfParam = atfParam;
		this.atfURL = atfURL;
		this.rects = rects;
	}
}
