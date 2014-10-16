package org.xml2as.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.xml2as.parser.Xml2AsLexer;
import org.xml2as.parser.Xml2AsParser;
import org.xml2as.parser.Xml2AsParser.EnumFieldContext;
import org.xml2as.parser.Xml2AsParser.EnumTypeContext;
import org.xml2as.parser.Xml2AsParser.MetaContext;
import org.xml2as.parser.Xml2AsParser.MetaParamContext;
import org.xml2as.parser.Xml2AsParser.TypeContext;
import org.xml2as.parser.Xml2AsParser.TypeFieldContext;
import org.xml2as.parser.Xml2AsParser.Xml2Context;

public class ClassTable
{
	private String packName = "";
	private String inputFile = null;

	private Class mainClass = null;
	private HashMap<String, Class> name2Class = new HashMap<String, Class>();
	private HashMap<String, Enum> name2Enum = new HashMap<String, Enum>();

	/**
	 * 打开
	 * 
	 * @param file
	 * @throws IOException
	 */
	public ClassTable(File file) throws IOException
	{
		open(file);
	}

	/**
	 * 获取输入文件
	 * 
	 * @return
	 */
	public String getInputFile()
	{
		return inputFile;
	}

	/**
	 * 获取包名
	 * 
	 * @return
	 */
	public String getPackName()
	{
		return packName;
	}

	/**
	 * 获取所有主类
	 * 
	 * @return
	 */
	public Class getMainClass()
	{
		return mainClass;
	}

	/**
	 * 获取所有类
	 * 
	 * @return
	 */
	public Class[] getAllClass()
	{
		return name2Class.values().toArray(new Class[] {});
	}

	/**
	 * 获取所有枚举
	 * 
	 * @return
	 */
	public Enum[] getAllEnum()
	{
		return name2Enum.values().toArray(new Enum[] {});
	}

	/**
	 * 获取类
	 * 
	 * @param name
	 * @return
	 */
	public Class getClass(String name)
	{
		return name2Class.get(name);
	}

	/**
	 * 获取枚举
	 * 
	 * @param name
	 * @return
	 */
	public Enum getEnum(String name)
	{
		return name2Enum.get(name);
	}

	/**
	 * 获取类型ID
	 * 
	 * @param name
	 * @return
	 */
	public int getClassID(String name)
	{
		if ("int".equals(name))
		{
			return 1;
		}
		if ("uint".equals(name))
		{
			return 2;
		}
		if ("Boolean".equals(name))
		{
			return 3;
		}
		if ("Number".equals(name))
		{
			return 4;
		}
		if ("String".equals(name))
		{
			return 5;
		}

		return getClass(name).order + 10;
	}

	/**
	 * 是否为基础类型
	 * 
	 * @param name
	 * @return
	 */
	private boolean isBaseType(String name)
	{
		return "int".equals(name) || "uint".equals(name) || "Boolean".equals(name) || "Number".equals(name) || "String".equals(name);
	}

	/**
	 * 是否为扩展类型
	 * 
	 * @param name
	 * @return
	 */
	private boolean isExtendType(String name)
	{
		return name2Class.containsKey(name);
	}

	/**
	 * 是否为枚举类型
	 * 
	 * @param name
	 * @return
	 */
	private boolean isEnumType(String name)
	{
		return name2Enum.containsKey(name);
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void open(File file) throws IOException
	{
		mainClass = null;
		name2Class = new HashMap<String, Class>();

		Xml2AsLexer lexer = new Xml2AsLexer(new ANTLRFileStream(file.getPath()));
		Xml2AsParser parser = new Xml2AsParser(new CommonTokenStream(lexer));

		Xml2Context root = parser.xml2();

		packName = "";
		if (root.pack != null && root.pack.pack != null)
		{
			packName = root.pack.pack.getText();
		}

		inputFile = null;
		if (root.input != null && root.input.url != null)
		{
			inputFile = root.input.url.getText();
			if (inputFile.charAt(0) == '\"')
			{
				inputFile = inputFile.substring(1, inputFile.length() - 1);
			}
		}

		HashSet<String> typeNames = new HashSet<String>();
		for (EnumTypeContext type : root.enumType())
		{
			String enumName = type.typeName().getText();
			EnumField[] enumFields = null;

			if (typeNames.contains(enumName))
			{
				continue;
			}

			ArrayList<EnumField> fields = new ArrayList<EnumField>();
			for (EnumFieldContext field : type.enumField())
			{
				String enumFieldName = field.fieldName.getText();
				String enumFieldValue = field.fieldValue.getText();
				if (enumFieldValue.charAt(0) == '"')
				{
					enumFieldValue = enumFieldValue.substring(1, enumFieldValue.length() - 1);
				}

				fields.add(new EnumField("", enumFieldName, enumFieldValue, fields.size() + 1));
			}
			enumFields = fields.toArray(new EnumField[] {});

			Enum clazz = new Enum("", enumName, enumFields);

			name2Enum.put(enumName, clazz);

			typeNames.add(enumName);
		}

		int order = 1;
		for (TypeContext type : root.type())
		{
			String typeName = type.typeName().getText();

			String typeXPath = "/";
			if (type.meta().size() > 0)
			{
				for (MetaContext meta : type.meta())
				{
					if (meta.prefix.getText().equals("Main") && meta.params.size() > 0)
					{
						String value = meta.params.get(0).value.getText().trim();
						if (value != null && !value.isEmpty())
						{
							if (value.charAt(0) == '"')
							{
								value = value.substring(1);
							}
							if (value.charAt(value.length() - 1) == '"')
							{
								value = value.substring(0, value.length() - 1);
							}
							value = value.trim();
							if (!value.isEmpty())
							{
								typeXPath = value;
							}
						}
					}
				}
			}

			if (typeNames.contains(typeName))
			{
				continue;
			}

			HashSet<String> fieldNames = new HashSet<String>();

			ArrayList<ClassField> typeFields = new ArrayList<ClassField>();
			for (TypeFieldContext field : type.typeField())
			{
				String fieldName = field.fieldName.getText();
				String fieldXPath = field.fieldXPath.getText();
				String fieldType = field.fieldType.getText();
				int fieldTypeKind = 3;
				boolean fieldList = false;
				String[] indexList = null;
				boolean sliceList = false;
				String sliceChar = null;

				if (fieldXPath != null && fieldXPath.charAt(0) == '"')
				{
					fieldXPath = fieldXPath.substring(1, fieldXPath.length() - 1);
				}

				if (fieldNames.contains(fieldName))
				{
					continue;
				}

				boolean isBase = isBaseType(fieldType);
				boolean isEnum = isEnumType(fieldType);

				if (isBase)
				{
					fieldTypeKind = 1;
				}
				else if (isEnum)
				{
					fieldTypeKind = 2;
				}

				MetaContext listMeta = null;
				MetaContext langMeta = null;
				if (field.meta().size() > 0)
				{
					for (MetaContext meta : field.meta())
					{
						String name = meta.prefix.getText().trim();
						if (name.equals("List") || name.equals("Slice"))
						{
							listMeta = meta;
						}
						else if (name.equals("NLS"))
						{
							langMeta = meta;
						}
					}
				}

				if (listMeta != null && listMeta.prefix.getText().trim().equals("List"))
				{
					fieldList = true;

					if (!isBase && !isEnum)
					{
						ArrayList<String> indexs = new ArrayList<String>();
						for (MetaParamContext key : listMeta.params)
						{
							String name = key.value.getText().trim();
							if (name != null && name.isEmpty() == false)
							{
								if (name.charAt(0) == '"')
								{
									name = name.substring(1);
								}
								if (name.charAt(name.length() - 1) == '"')
								{
									name = name.substring(0, name.length() - 1);
								}
								name = name.trim();
								if (name.isEmpty() == false)
								{
									indexs.add(name);
								}
							}
						}

						if (indexs.size() > 0)
						{
							indexList = indexs.toArray(new String[] {});
						}
					}
				}
				else if (listMeta != null && listMeta.prefix.getText().trim().equals("Slice"))
				{
					if (isBase || isEnum)
					{
						sliceList = true;
						sliceChar = listMeta.params.size() > 0 ? listMeta.params.get(0).value.getText().trim() : ",";
						if (sliceChar != null && sliceChar.isEmpty() == false)
						{
							if (sliceChar.charAt(0) == '"')
							{
								sliceChar = sliceChar.substring(1);
							}
							if (sliceChar.charAt(sliceChar.length() - 1) == '"')
							{
								sliceChar = sliceChar.substring(0, sliceChar.length() - 1);
							}
						}
					}
				}

				typeFields.add(new ClassField(fieldXPath, fieldName, "", fieldType, fieldTypeKind, fieldList, indexList, sliceList, sliceChar, langMeta != null));

				fieldNames.add(fieldType);
			}

			Class clazz = new Class(typeXPath, typeName, "", order, typeFields.toArray(new ClassField[] {}));
			order++;

			name2Class.put(typeName, clazz);
			if (typeXPath != null && mainClass == null)
			{
				mainClass = clazz;
			}

			typeNames.add(typeName);
		}
	}
}
