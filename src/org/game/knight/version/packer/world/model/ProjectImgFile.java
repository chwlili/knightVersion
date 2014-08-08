package org.game.knight.version.packer.world.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ProjectImgFile extends ProjectFile
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4323116449220773420L;

	/**
	 * ���
	 */
	public final int width;
	
	/**
	 * �߶�
	 */
	public final int height;
	
	/**
	 * ���캯��
	 * @param file
	 * @param url
	 */
	public ProjectImgFile(ProjectFile file)
	{
		super(file);
		
		int w=0;
		int h=0;
		if(file.exists() && file.isFile())
		{
			Image img=new Image(Display.getCurrent(), file.getAbsolutePath());
			if(img!=null)
			{
				w=img.getBounds().width;
				h=img.getBounds().height;
				img.dispose();
			}
		}
		this.width=w;
		this.height=h;
	}
}
