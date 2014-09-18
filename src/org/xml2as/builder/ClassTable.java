package org.xml2as.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.xml2as.parser.Xml2AsLexer;
import org.xml2as.parser.Xml2AsParser;
import org.xml2as.parser.Xml2AsParser.FieldContext;
import org.xml2as.parser.Xml2AsParser.FieldMetaContext;
import org.xml2as.parser.Xml2AsParser.ListMetaContext;
import org.xml2as.parser.Xml2AsParser.SliceMetaContext;
import org.xml2as.parser.Xml2AsParser.TypeContext;
import org.xml2as.parser.Xml2AsParser.TypeMetaContext;
import org.xml2as.parser.Xml2AsParser.TypeNameContext;
import org.xml2as.parser.Xml2AsParser.Xml2Context;

public class ClassTable
{
	private String packName;
	private String inputFile;
	private ArrayList<Class> mainClass = new ArrayList<Class>();
	private HashMap<String, Class> name2Class = new HashMap<String, Class>();

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
	 * 获取包名
	 * 
	 * @return
	 */
	public String getPackName()
	{
		return packName;
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
	 * 获取所有类
	 * 
	 * @return
	 */
	public Class[] getAllClass()
	{
		return name2Class.values().toArray(new Class[] {});
	}

	/**
	 * 获取所有主类
	 * 
	 * @return
	 */
	public Class[] getAllMainClass()
	{
		return mainClass.toArray(new Class[] {});
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
	 * 读取文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void open(File file) throws IOException
	{
		mainClass = new ArrayList<Class>();
		name2Class = new HashMap<String, Class>();

		Xml2AsLexer lexer = new Xml2AsLexer(new ANTLRFileStream(file.getPath()));
		Xml2AsParser parser = new Xml2AsParser(new CommonTokenStream(lexer));

		Xml2Context root = parser.xml2();

		inputFile = null;
		if (root.input != null && root.input.url != null)
		{
			inputFile = root.input.url.getText();
			if (inputFile.charAt(0) == '\"')
			{
				inputFile = inputFile.substring(1, inputFile.length() - 1);
			}
		}

		packName = "";
		if (root.pack != null && root.pack.pack != null)
		{
			packName = root.pack.pack.getText();
		}

		int order = 1;
		for (TypeContext type : root.type())
		{
			TypeMetaContext typeMeta = type.typeMeta();

			String typeName = type.typeName().getText();
			String typeXPath = typeMeta != null && typeMeta.xpath != null ? typeMeta.xpath.getText() : null;
			if (typeXPath != null && typeXPath.charAt(0) == '"')
			{
				typeXPath = typeXPath.substring(1, typeXPath.length() - 1);
			}

			ArrayList<ClassField> typeFields = new ArrayList<ClassField>();

			for (FieldContext field : type.field())
			{
				String fieldType = field.fieldType.getText();
				String fieldName = field.fieldName.getText();
				String fieldXPath = field.fieldXPath.getText();
				boolean fieldRepeted = false;
				String[] indexList = null;
				boolean sliceList = false;
				String sliceChar = null;

				boolean isNative = isBaseType(fieldType);

				if (fieldXPath != null && fieldXPath.charAt(0) == '"')
				{
					fieldXPath = fieldXPath.substring(1, fieldXPath.length() - 1);
				}

				ListMetaContext listMeta = null;
				SliceMetaContext sliceMeta = null;

				FieldMetaContext metaList = field.fieldMeta();
				if (metaList != null && metaList.getChildCount() > 0)
				{
					for (int i = metaList.getChildCount() - 1; i >= 0; i--)
					{
						if (metaList.getChild(i) instanceof ListMetaContext)
						{
							listMeta = (ListMetaContext) metaList.getChild(i);
							break;
						}
						else if (metaList.getChild(i) instanceof SliceMetaContext)
						{
							sliceMeta = (SliceMetaContext) metaList.getChild(i);
							break;
						}
					}
				}

				if (listMeta != null)
				{
					fieldRepeted = true;

					if (!isNative)
					{
						ArrayList<String> indexs = new ArrayList<String>();
						for (TypeNameContext key : listMeta.key)
						{
							String name = key.getText().trim();
							if (name != null && name.isEmpty() == false)
							{
								indexs.add(name);
							}
						}

						if (indexs.size() > 0)
						{
							indexList = indexs.toArray(new String[] {});
						}
					}
				}
				else if (sliceMeta != null)
				{
					if (isNative)
					{
						sliceList = true;
						sliceChar = sliceMeta.sliceChar.getText();
						if(sliceChar.charAt(0)=='"')
						{
							sliceChar=sliceChar.substring(1, sliceChar.length()-1);
						}
					}
				}

				typeFields.add(new ClassField(fieldXPath, fieldName, "", fieldType, fieldRepeted, indexList, sliceList, sliceChar));
			}

			Class clazz = new Class(typeXPath, typeName, "", order, typeFields.toArray(new ClassField[] {}));
			order++;

			name2Class.put(typeName, clazz);
			if (typeXPath != null)
			{
				mainClass.add(clazz);
			}
		}
	}
}
