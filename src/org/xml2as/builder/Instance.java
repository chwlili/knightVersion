package org.xml2as.builder;

import java.util.ArrayList;

class Instance
{
	/**
	 * ���Ͷ���
	 */
	public final Class type;

	/**
	 * �ֶ��б�
	 */
	public final ArrayList<InstanceField> fields = new ArrayList<InstanceField>();

	/**
	 * ���캯��
	 * 
	 * @param type
	 */
	public Instance(Class type)
	{
		this.type = type;
	}

	/**
	 * �����ֶ�
	 * 
	 * @param name
	 * @return
	 */
	public InstanceField getField(String name)
	{
		for (InstanceField field : fields)
		{
			if (field.meta.name.equals(name))
			{
				return field;
			}
		}
		return null;
	}
}