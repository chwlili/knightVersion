package org.game.knight.version.packer.view;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

import org.game.knight.version.packer.base.ZipConfig;

public class ViewZip extends ZipConfig
{
	public ViewZip(File file)
	{
		super(file);
	}

	public ViewZip()
	{
		super(null);
	}

	private HashMap<String, String> typeNameMap;

	public String getTypeName(String key)
	{
		openTypeNameMap();

		return typeNameMap.get(key);
	}

	public void setTypeName(String key, String val)
	{
		openTypeNameMap();

		typeNameMap.put(key, val);
	}

	@Override
	protected void prevSave()
	{
		saveTypeNameMap();

		super.prevSave();
	}

	private void openTypeNameMap()
	{
		if (typeNameMap != null)
		{
			return;
		}

		typeNameMap = new HashMap<String, String>();
		String text = getEntryContent("core/typeName.txt");
		if (text != null)
		{
			String[] lines = text.split("\\n");
			for (String line : lines)
			{
				line = line.trim();

				if (line.length() > 0)
				{
					String[] pairs = line.split("=");
					if (pairs.length == 2)
					{
						String key = pairs[0].trim();
						String name = pairs[1].trim();

						typeNameMap.put(key, name);
					}
				}
			}
		}
	}

	private void saveTypeNameMap()
	{
		StringBuilder sb = new StringBuilder();
		if (typeNameMap != null)
		{
			String[] keys = typeNameMap.keySet().toArray(new String[] {});
			Arrays.sort(keys);

			for (String key : keys)
			{
				sb.append(key + " = " + typeNameMap.get(key) + "\n");
			}
		}

		try
		{
			setEntry("core/typeName.txt", sb.toString().getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
