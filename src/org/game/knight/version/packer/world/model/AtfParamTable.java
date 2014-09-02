package org.game.knight.version.packer.world.model;

import java.io.File;
import java.util.HashMap;

import org.chw.util.XmlUtil;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.BaseWriter;
import org.game.knight.version.packer.world.WorldWriter;

public class AtfParamTable extends BaseWriter
{
	private HashMap<String, AtfParam> id_atfParam = new HashMap<String, AtfParam>();
	private HashMap<String, AtfParam> value_atfParam = new HashMap<String, AtfParam>();

	/**
	 * ���캯��
	 * 
	 * @param params
	 */
	public AtfParamTable(WorldWriter root)
	{
		super(root, null);
	}

	/**
	 * ��ȡAtf����
	 * 
	 * @param id
	 * @return
	 */
	public AtfParam getAtfParam(String id)
	{
		if (!id_atfParam.containsKey(id))
		{
			id = "default";
		}

		return id_atfParam.get(id);
	}

	/**
	 * ��ֵ����ATF����
	 * 
	 * @param w
	 * @param h
	 * @param param
	 * @return
	 */
	public AtfParam findAtfParam(int w, int h, String param)
	{
		return value_atfParam.get(w + "_" + h + "_" + param);
	}

	// ----------------------------------------------------------------------------------
	//
	// ����ʵ��
	//
	// ----------------------------------------------------------------------------------

	@Override
	protected void startup() throws Exception
	{
		GamePacker.log("��ʼ��ȡAtf���������");
	}

	/**
	 * ����
	 */
	@Override
	protected void exec() throws Exception
	{
		ProjectFile[] files = root.fileTable.getAllParamFiles();
		for (int i = 0; i < files.length; i++)
		{
			if (root.isCancel())
			{
				return;
			}

			File file = files[i];

			GamePacker.progress(String.format("�����������(%s/%s) : %s", i + 1, files.length, file.getPath()));

			SAXReader reader = new SAXReader();
			Document document = reader.read(file);

			String[] lines = document.getRootElement().getText().split("\n");
			for (String line : lines)
			{
				line = line.trim();
				if (line.isEmpty())
				{
					continue;
				}

				String[] fields = line.split("=");
				if (fields.length != 2)
				{
					GamePacker.error("��Ч�����������(" + file.getPath() + ")  " + line);
					continue;
				}

				String id = fields[0].trim();
				String[] params = fields[1].trim().split(",", 3);
				if (id.isEmpty() || params.length != 3)
				{
					GamePacker.error("��Ч�����������(" + file.getPath() + ")  " + line);
					continue;
				}

				if (id_atfParam.containsKey(id))
				{
					GamePacker.error("�������ID��ͻ��(" + file.getPath() + " : " + id + ") -> ( " + id_atfParam.get(id).file.getPath() + " : " + id + ")");
					continue;
				}

				AtfParam param = new AtfParam(file, id, XmlUtil.parseInt(params[0].trim(), 0), XmlUtil.parseInt(params[1].trim(), 0), params[2].trim());
				id_atfParam.put(id, param);
				value_atfParam.put(param.width + "_" + param.height + "_" + param.other, param);
			}
		}

		if (!id_atfParam.containsKey("default"))
		{
			AtfParam param = new AtfParam(null, "default", 2048, 2048, "-n 0:0 -r -q 30 -s");
			id_atfParam.put("default", param);
			value_atfParam.put(param.width + "_" + param.height + "_" + param.other, param);
		}
	}
}
