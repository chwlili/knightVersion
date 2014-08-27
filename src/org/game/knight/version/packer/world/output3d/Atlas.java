package org.game.knight.version.packer.world.output3d;

import org.game.knight.version.packer.world.model.AtfParam;

public class Atlas
{
	public final AtlasRect[] rects;
	public final AtfParam atfParam;
	public final String atfURL;
	public final String previewURL;

	public Atlas(AtlasRect[] rects, AtfParam atfParam, String atfURL,String previewURL)
	{
		this.rects = rects;
		this.atfParam = atfParam;
		this.atfURL = atfURL;
		this.previewURL=previewURL;
	}
}
