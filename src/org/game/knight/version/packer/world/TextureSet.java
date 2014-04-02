package org.game.knight.version.packer.world;

import java.util.Hashtable;

public class TextureSet
{
	private TextureSetKey key;
	private Texture[] textures;
	
	private Hashtable<String, Region> regionTable;
	
	public TextureSet(TextureSetKey key,Texture[] textures)
	{
		this.key=key;
		this.textures=textures;
	}
	
	/**
	 * ��ȡkey
	 * @return
	 */
	public TextureSetKey getKey()
	{
		return key;
	}
	
	/**
	 * ��ȡ��ͼ����
	 * @return
	 */
	public Texture[] getTextures()
	{
		return textures;
	}
	
	/**
	 * ��ȡ����
	 * @param ownerChecksum
	 * @param index
	 * @return
	 */
	public Region getRegion(String ownerChecksum,int index)
	{
		if(regionTable==null)
		{
			regionTable=new Hashtable<String, Region>();
			
			for(Texture texture : textures)
			{
				for(Region region : texture.getRegions())
				{
					region.setTexturePath(texture.getAtfFilePath());
					regionTable.put(region.getOwnerChecksum()+"_"+region.getIndex(), region);
				}
			}
		}
		
		return regionTable.get(ownerChecksum+"_"+index);
	}
}
