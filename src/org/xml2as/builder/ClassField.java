package org.xml2as.builder;

public class ClassField
{
	/**
	 * ����·��
	 */
	public final String xpath;

	/**
	 * �ֶ�����
	 */
	public final String name;

	/**
	 * �ֶ�ע��
	 */
	public final String comment;

	/**
	 * �ֶ�����
	 */
	public final String type;

	/**
	 * �ֶ����
	 */
	private final int typeKind;

	/**
	 * �Ƿ�Ϊ�б�
	 */
	public final Boolean repeted;

	/**
	 * �����ֶ�
	 */
	public final String[] indexKeys;

	/**
	 * �Ƿ�Ϊ��Ƭ�б�
	 */
	public final Boolean slice;

	/**
	 * ��Ƭʱʹ�õķָ��ַ�
	 */
	public final String sliceChar;

	/**
	 * ��Ҫ������֧��
	 */
	public final boolean NLS;

	/**
	 * ���캯��
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, int typeKind, boolean repeted, String[] indexKeys)
	{
		this(xpath, name, comment, type, typeKind, repeted, indexKeys, false, null, false);
	}

	/**
	 * �ι��캯��
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, int typeKind, boolean slice, String sliceChar)
	{
		this(xpath, name, comment, type, typeKind, false, null, slice, sliceChar, false);
	}

	/**
	 * ���캯��
	 * 
	 * @param xpath
	 * @param name
	 * @param comment
	 * @param type
	 * @param repeted
	 * @param indexKeys
	 */
	public ClassField(String xpath, String name, String comment, String type, int typeKind, boolean repeted, String[] indexKeys, boolean slice, String sliceChar, boolean NLS)
	{
		this.xpath = xpath;
		this.name = name;
		this.comment = comment;
		this.type = type;
		this.typeKind = typeKind;
		this.repeted = repeted;
		this.indexKeys = indexKeys;
		this.slice = slice;
		this.sliceChar = sliceChar;
		this.NLS = NLS;
	}

	/**
	 * �Ƿ�Ϊboolean
	 * 
	 * @return
	 */
	public boolean isBoolean()
	{
		return type.equals("Boolean");
	}

	/**
	 * �Ƿ�Ϊint
	 * 
	 * @return
	 */
	public boolean isInt()
	{
		return type.equals("int");
	}

	/**
	 * �Ƿ�Ϊuint
	 * 
	 * @return
	 */
	public boolean isUint()
	{
		return type.equals("uint");
	}

	/**
	 * �Ƿ�ΪNumber
	 * 
	 * @return
	 */
	public boolean isNumber()
	{
		return type.equals("Number");
	}

	/**
	 * �Ƿ�ΪString
	 * 
	 * @return
	 */
	public boolean isString()
	{
		return type.equals("String");
	}

	/**
	 * �Ƿ�Ϊ��������
	 * 
	 * @return
	 */
	public boolean isBaseType()
	{
		return typeKind == 1;
	}

	/**
	 * �Ƿ�Ϊö������
	 * 
	 * @return
	 */
	public boolean isEnumType()
	{
		return typeKind == 2;
	}

	/**
	 * �Ƿ�Ϊ�Զ�������
	 * 
	 * @return
	 */
	public boolean isExtendType()
	{
		return typeKind == 3;
	}

	/**
	 * �Ƿ�������
	 * 
	 * @return
	 */
	public boolean hasIndex()
	{
		return indexKeys != null && indexKeys.length > 0;
	}
}