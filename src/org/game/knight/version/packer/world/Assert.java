package org.game.knight.version.packer.world;


public class Assert
{
	public static void isNotNull(Object object)
	{
		isNotNull(object, "");//$NON-NLS-1$
	}

	public static void isNotNull(Object object, String message)
	{
		if (object == null)
		{
			throw new RuntimeException("null argument;" + message);//$NON-NLS-1$
		}
	}
}
