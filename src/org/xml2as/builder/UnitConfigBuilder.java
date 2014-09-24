package org.xml2as.builder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.Deflater;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;

public class UnitConfigBuilder
{
	private ClassTable classTable;

	private int nextID = 1;

	private HashMap<Object, Integer> def_id = new HashMap<Object, Integer>();
	private HashMap<Integer, Integer> int_id = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> uint_id = new HashMap<Integer, Integer>();
	private HashMap<Boolean, Integer> boolean_id = new HashMap<Boolean, Integer>();
	private HashMap<Float, Integer> float_id = new HashMap<Float, Integer>();
	private HashMap<String, Integer> string_id = new HashMap<String, Integer>();
	private HashMap<String, Integer> instance_id = new HashMap<String, Integer>();
	private HashMap<Instance, String> instance_txt = new HashMap<Instance, String>();

	private HashMap<Integer, Integer> id_refCount = new HashMap<Integer, Integer>();
	private HashMap<Integer, Object> id_value = new HashMap<Integer, Object>();
	private HashMap<Integer, ClassField> id_field = new HashMap<Integer, ClassField>();
	private HashMap<Integer, Integer> id_order = new HashMap<Integer, Integer>();

	private HashMap<String, HashSet<Integer>> typeName_ids = new HashMap<String, HashSet<Integer>>();
	private HashMap<String, Integer[]> typeName_idArray = new HashMap<String, Integer[]>();

	/**
	 * ���캯��
	 * 
	 * @param instance
	 * @throws IOException
	 */
	public UnitConfigBuilder(ClassTable types)
	{
		this.classTable = types;
	}

	/**
	 * ת����String
	 * 
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public byte[] build(InputStream input) throws IOException, CoreException, SAXException, ParserConfigurationException
	{
		return build(UnitInstanceBuilder.build(classTable, input));
	}

	/**
	 * ѹ��
	 * 
	 * @param data
	 *            ��ѹ������
	 * @return byte[] ѹ���������
	 */
	private byte[] compress(byte[] data)
	{
		byte[] output = new byte[0];

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

		try
		{
			byte[] buf = new byte[1024];
			while (!compresser.finished())
			{
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		}
		catch (Exception e)
		{
			output = data;
			e.printStackTrace();
		}
		finally
		{
			try
			{
				bos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		compresser.end();
		return output;
	}

	/**
	 * ����ʵ��
	 * 
	 * @param instance
	 */
	private void parseInstance(Instance instance)
	{
		if(instance==null)
		{
			System.out.println("..");
		}
		for (InstanceField field : instance.fields)
		{
			// ���Կ�ֵ
			if (field.value == null)
			{
				continue;
			}

			// ����ö�����͵�ֵ
			if (field.meta.isEnumType())
			{
				continue;
			}

			if (field.meta.repeted || field.meta.slice)
			{
				@SuppressWarnings("rawtypes")
				ArrayList list = (ArrayList) field.value;
				for (Object value : list)
				{
					// �б������������
					incrementRefCount(field.meta, value);

					// ������������������
					if ((value instanceof Instance) && field.meta.hasIndex())
					{
						Instance valueInstance = (Instance) value;
						for (String key : field.meta.indexKeys)
						{
							InstanceField keyField = valueInstance.getField(key);
							if (keyField != null)
							{
								incrementRefCount(keyField.meta, keyField.value);
							}
						}
					}

					// �ݹ�
					if (field.meta.isExtendType())
					{
						parseInstance((Instance) value);
					}
				}
			}
			else
			{
				// ֵ����������
				incrementRefCount(field.meta, field.value);

				// �ݹ�
				if (field.meta.isExtendType())
				{
					parseInstance((Instance) field.value);
				}
			}
		}
	}

	/**
	 * ����ֵ��������ü���
	 * 
	 * @param def
	 * @param value
	 */
	private void incrementRefCount(ClassField def, Object value)
	{
		int id = getID(def, value);
		if (id == 0)
		{
			return;
		}

		int count = 0;
		if (id_refCount.containsKey(id))
		{
			count = id_refCount.get(id);
		}
		count++;

		id_refCount.put(id, count);
		id_value.put(id, value);
		id_field.put(id, def);

		if (!typeName_ids.containsKey(def.type))
		{
			typeName_ids.put(def.type, new HashSet<Integer>());
		}
		typeName_ids.get(def.type).add(id);
	}

	// ------------------------------------------------------------------------------------------------------
	//
	// ��ȡID
	//
	// ------------------------------------------------------------------------------------------------------

	/**
	 * ��ȡ���͵�ID
	 * 
	 * @param type
	 * @return
	 */
	private int getID(Class type)
	{
		if (!def_id.containsKey(type))
		{
			def_id.put(type, nextID);
			nextID++;
		}
		return def_id.get(type);
	}

	/**
	 * ��ȡ�����ֶε�ID
	 * 
	 * @param field
	 * @return
	 */
	private int getID(ClassField field)
	{
		if (!def_id.containsKey(field))
		{
			def_id.put(field, nextID);
			nextID++;
		}
		return def_id.get(field);
	}

	/**
	 * ����ID
	 * 
	 * @param def
	 * @param value
	 * @return
	 */
	private int getID(ClassField def, Object value)
	{
		int id = 0;
		if (def != null && value != null)
		{
			if (def.isInt())
			{
				id = getID((Integer) value, false);
			}
			else if (def.isUint())
			{
				id = getID((Integer) value, true);
			}
			else if (def.isNumber())
			{
				id = getID((Float) value);
			}
			else if (def.isString())
			{
				id = getID((String) value);
			}
			else if (def.isBoolean())
			{
				id = getID((Boolean) value);
			}
			else if (def.isExtendType())
			{
				id = getID((Instance) value);
			}
		}
		return id;
	}

	/**
	 * ��ȡIntֵ��ID
	 * 
	 * @param value
	 * @return
	 */
	private int getID(Integer value, boolean sign)
	{
		if (value == 0)
		{
			return 0;
		}

		if (sign)
		{
			if (!uint_id.containsKey(value))
			{
				uint_id.put(value, nextID);
				nextID++;
			}
			return uint_id.get(value);
		}
		else
		{
			if (!int_id.containsKey(value))
			{
				int_id.put(value, nextID);
				nextID++;
			}
			return int_id.get(value);
		}
	}

	/**
	 * ��ȡBooleanֵ��ID
	 * 
	 * @param value
	 * @return
	 */
	private int getID(Boolean value)
	{
		if (value == false)
		{
			return 0;
		}
		if (!boolean_id.containsKey(value))
		{
			boolean_id.put(value, nextID);
			nextID++;
		}
		return boolean_id.get(value);
	}

	/**
	 * ��ȡFloatֵ��ID
	 * 
	 * @param value
	 * @return
	 */
	private int getID(Float value)
	{
		if (Float.isNaN(value))
		{
			return 0;
		}
		if (!float_id.containsKey(value))
		{
			float_id.put(value, nextID);
			nextID++;
		}
		return float_id.get(value);
	}

	/**
	 * ��ȡStringֵ��ID
	 * 
	 * @param value
	 * @return
	 */
	private int getID(String value)
	{
		if (value.isEmpty())
		{
			return 0;
		}
		if (!string_id.containsKey(value))
		{
			string_id.put(value, nextID);
			nextID++;
		}
		return string_id.get(value);
	}

	/**
	 * ��ȡInstanceֵ��ID
	 * 
	 * @param instance
	 * @return
	 */
	private int getID(Instance instance)
	{
		String txt = hash(instance);
		if (!instance_id.containsKey(txt))
		{
			instance_id.put(txt, nextID);
			nextID++;
		}
		return instance_id.get(txt);
	}

	// ------------------------------------------------------------------------------------------------------
	//
	// ��Ų���
	//
	// ------------------------------------------------------------------------------------------------------

	/**
	 * �������
	 * 
	 * @param def
	 * @param value
	 * @return
	 */
	private int getOrder(ClassField def, Object value)
	{
		int id = getID(def, value);
		if (id != 0)
		{
			return id_order.get(id);
		}
		return 0;
	}

	// ------------------------------------------------------------------------------------------------------
	//
	// ����ʵHASH��
	//
	// ------------------------------------------------------------------------------------------------------

	/**
	 * ��ȡʵ����HASH��ʾ
	 * 
	 * @param instance
	 * @return
	 */
	private String hash(Instance instance)
	{
		InstanceField[] fields = instance.fields.toArray(new InstanceField[instance.fields.size()]);
		Arrays.sort(fields, new Comparator<InstanceField>()
		{
			public int compare(InstanceField o1, InstanceField o2)
			{
				return getID(o1.meta) - getID(o2.meta);
			}
		});

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("$");
		sb.append(getID(instance.type));
		sb.append(":");
		for (InstanceField field : fields)
		{
			sb.append(hash(field));
		}
		sb.append("}");

		instance_txt.put(instance, sb.toString());

		return instance_txt.get(instance);
	}

	/**
	 * ��ȡʵ���ֶε�HASH��ʾ
	 * 
	 * @param field
	 * @return
	 */
	private String hash(InstanceField field)
	{
		ClassField def = field.meta;

		StringBuilder sb = new StringBuilder();
		sb.append("@");
		sb.append(getID(def));
		sb.append("=");

		if (!def.isExtendType())
		{
			if (def.repeted || def.slice)
			{
				sb.append("[");
				@SuppressWarnings("rawtypes")
				ArrayList list = (ArrayList) field.value;
				if (list != null)
				{
					for (Object item : list)
					{
						sb.append(item);
						sb.append(",");
					}
				}
				sb.append("]");
			}
			else
			{
				sb.append(field.value);
			}
		}
		else
		{
			if (def.repeted || def.slice)
			{
				sb.append("[");
				@SuppressWarnings("rawtypes")
				ArrayList list = (ArrayList) field.value;
				if (list != null)
				{
					for (Object item : list)
					{
						Instance instance = (Instance) item;
						sb.append(hash(instance));
						sb.append(",");
					}
				}
				sb.append("]");
			}
			else
			{
				Instance instance = (Instance) field.value;
				if (instance != null)
				{
					sb.append(hash(instance));
				}
			}
		}

		sb.append(";");

		return sb.toString();
	}

	// ------------------------------------------------------------------------------------------------------
	//
	// ����
	//
	// ------------------------------------------------------------------------------------------------------

	/**
	 * ����
	 * 
	 * @throws IOException
	 * @throws CoreException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private byte[] build(Instance instance) throws IOException, SAXException, ParserConfigurationException, CoreException
	{
		// �������и��ڵ�
		parseInstance(instance);

		// �����ô�������ID
		for (String typeName : typeName_ids.keySet())
		{
			HashSet<Integer> ids = typeName_ids.get(typeName);
			Integer[] idArray = ids.toArray(new Integer[ids.size()]);
			Arrays.sort(idArray, new Comparator<Integer>()
			{
				public int compare(Integer o1, Integer o2)
				{
					o1 = id_refCount.get(o1);
					o2 = id_refCount.get(o2);
					if (o1 > o2)
					{
						return -1;
					}
					else if (o1 < o2)
					{
						return 1;
					}
					return 0;
				}
			});

			for (int i = 0; i < idArray.length; i++)
			{
				int id = idArray[i];
				id_order.put(id, i + 1);
			}

			typeName_idArray.put(typeName, idArray);
		}

		// debugs
		// System.out.println(toDebugString(allInstance));

		// ���������б�
		String[] allTypeName = typeName_idArray.keySet().toArray(new String[typeName_idArray.size()]);
		Arrays.sort(allTypeName);

		// ת�����ֽ���
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		CfgOutputStream dataOutput = new CfgOutputStream(byteArrayOutput);

		dataOutput.writeVarInt(allTypeName.length);
		for (String typeName : allTypeName)
		{
			dataOutput.writeVarInt(classTable.getClassID(typeName));
			dataOutput.write(getBytes(typeName_idArray.get(typeName)));
		}

		dataOutput.writeVarInt(classTable.getClassID(instance.type.name));
		dataOutput.write(getBytes(instance));

		return compress(byteArrayOutput.toByteArray());
	}

	/**
	 * ��ȡ�ַ������ֽ�����
	 * 
	 * @param txt
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytes(String txt) throws IOException
	{
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		CfgOutputStream output = new CfgOutputStream(byteArray);

		byte[] bytes = txt.getBytes("utf8");
		output.writeVarInt(bytes.length);
		output.write(bytes);

		return byteArray.toByteArray();
	}

	/**
	 * ��ȡ�����б���ֽ�����
	 * 
	 * @param list
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytes(Integer[] list) throws IOException
	{
		int totalCount = list.length;
		int partLength = 100;

		ArrayList<byte[]> partBytes = new ArrayList<byte[]>();

		// �����Ӳ���
		int partCount = (int) Math.ceil((double) totalCount / partLength);
		for (int i = 0; i < partCount; i++)
		{
			ByteArrayOutputStream partByteArray = new ByteArrayOutputStream();
			CfgOutputStream partOutputStream = new CfgOutputStream(partByteArray);

			int left = i * partLength;
			int right = left + partLength;
			if (right > totalCount)
			{
				right = totalCount;
			}

			for (; left < right; left++)
			{
				ClassField field = id_field.get(list[left]);
				Object value = id_value.get(list[left]);

				if (field.isInt() || field.isUint())
				{
					partOutputStream.writeVarInt((Integer) value);
				}
				else if (field.isBoolean())
				{
					partOutputStream.writeVarInt((Boolean) value ? 1 : 0);
				}
				else if (field.isNumber())
				{
					partOutputStream.writeFloat((Float) value);
				}
				else if (field.isString())
				{
					partOutputStream.write(getBytes((String) value));
				}
				else if (field.isExtendType())
				{
					partOutputStream.write(getBytes((Instance) value));
				}
			}
			partOutputStream.flush();
			partBytes.add(partByteArray.toByteArray());
		}

		// �ϲ������Ӳ���
		ByteArrayOutputStream contentByteArray = new ByteArrayOutputStream();
		CfgOutputStream contentOutputStream = new CfgOutputStream(contentByteArray);
		contentOutputStream.writeVarInt(totalCount);
		contentOutputStream.writeVarInt(partLength);
		for (byte[] bytes : partBytes)
		{
			contentOutputStream.writeVarInt(bytes.length);
			contentOutputStream.write(bytes);
		}
		contentOutputStream.flush();

		// ����
		return contentByteArray.toByteArray();
	}

	/**
	 * ��ȡʵ��������ֽ�����
	 * 
	 * @param instance
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytes(Instance instance) throws IOException
	{
		ByteArrayOutputStream contentByteArray = new ByteArrayOutputStream();
		CfgOutputStream contentOutputStream = new CfgOutputStream(contentByteArray);

		for (ClassField fieldDef : instance.type.fields)
		{
			InstanceField field = instance.getField(fieldDef.name);

			// �ֶ�Ϊ��ʱ����ֵ��������ã��б�ֵ������ȱ��0
			if (field == null || field.value == null)
			{
				contentOutputStream.writeVarInt(0);
				continue;
			}

			// ����ֶ�����
			if (fieldDef.repeted && fieldDef.hasIndex())
			{
				@SuppressWarnings("rawtypes")
				ArrayList vals = (ArrayList) field.value;
				contentOutputStream.writeVarInt(vals.size());
				for (Object val : vals)
				{
					if (fieldDef.isExtendType())
					{
						Instance instanceVal = (Instance) val;
						for (String key : fieldDef.indexKeys)
						{
							InstanceField instanceField = instanceVal.getField(key);
							if (instanceField != null && instanceField.value != null)
							{
								contentOutputStream.writeVarInt(getOrder(instanceField.meta, instanceField.value));
							}
							else
							{
								contentOutputStream.writeVarInt(0);
							}
						}
					}
					contentOutputStream.writeVarInt(getOrder(fieldDef, val));
				}
			}
			else if (fieldDef.repeted || fieldDef.slice)
			{
				@SuppressWarnings("rawtypes")
				ArrayList vals = (ArrayList) field.value;
				contentOutputStream.writeVarInt(vals.size());
				for (Object val : vals)
				{
					if (fieldDef.isEnumType())
					{
						contentOutputStream.writeVarInt((Integer) field.value);
					}
					else
					{
						contentOutputStream.writeVarInt(getOrder(fieldDef, val));
					}
				}
			}
			else
			{
				if (fieldDef.isEnumType())
				{
					contentOutputStream.writeVarInt((Integer) field.value);
				}
				else
				{
					contentOutputStream.writeVarInt(getOrder(fieldDef, field.value));
				}
			}
		}

		return contentByteArray.toByteArray();
	}

	// -------------------------------------------------------------------------------------------------------
	//
	// debug����
	//
	// -------------------------------------------------------------------------------------------------------

	/**
	 * ת����debug�ַ���
	 */
	private String toDebugString(Instance mainInstance)
	{
		StringBuilder sb = new StringBuilder();

		for (String typeName : typeName_idArray.keySet())
		{
			sb.append(typeName + ":");
			for (int id : typeName_idArray.get(typeName))
			{
				Object value = id_value.get(id);
				if (value instanceof ArrayList)
				{
					sb.append("[");
					@SuppressWarnings("rawtypes")
					ArrayList list = (ArrayList) value;
					for (Object item : list)
					{
						sb.append(getOrder(id_field.get(id), item));
						sb.append(",");
					}
					sb.append("]");
				}
				else if (value instanceof Instance)
				{
					sb.append("{");
					Instance instance = (Instance) value;
					for (InstanceField field : instance.fields)
					{
						sb.append(field.meta.name);
						sb.append(":");
						Object val = field.value;
						if (val instanceof ArrayList)
						{

						}
						else if (val instanceof Instance)
						{
							sb.append(getOrder(field.meta, val));
						}
						else if (val != null)
						{
							sb.append(getOrder(field.meta, val));
						}
						sb.append(",");
					}
					sb.append("}");
				}
				else if (value != null)
				{
					sb.append(value);
				}
				sb.append(",");
			}
			sb.append("\n");
		}

		sb.append("{");
		for (InstanceField field : mainInstance.fields)
		{
			sb.append(field.meta.name);
			sb.append(":");
			ClassField def = field.meta;
			if (def.repeted || def.slice)
			{
				sb.append("[");
				if (field.value != null)
				{
					@SuppressWarnings("rawtypes")
					ArrayList list = (ArrayList) field.value;
					for (Object item : list)
					{
						sb.append(getOrder(def, item));
						sb.append(",");
					}
				}
				sb.append("]");
			}
			else
			{
				sb.append(getOrder(def, field.value));
			}
			sb.append(",");
		}
		sb.append("}");
		sb.append("\n");

		return sb.toString();
	}

	/**
	 * ���������
	 * 
	 * @author ds
	 * 
	 */
	private static class CfgOutputStream extends DataOutputStream
	{
		public CfgOutputStream(OutputStream arg0)
		{
			super(arg0);
		}

		public void writeVarInt(int value) throws IOException
		{
			while (true)
			{
				if ((value & ~0x7F) == 0)
				{
					writeByte(value);
					return;
				}
				else
				{
					writeByte((value & 0x7F) | 0x80);
					value >>>= 7;
				}
			}
		}
	}
}
