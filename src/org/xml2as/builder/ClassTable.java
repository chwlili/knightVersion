package org.xml2as.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.xml2as.parser.Xml2AsLexer;
import org.xml2as.parser.Xml2AsParser;
import org.xml2as.parser.Xml2AsParser.FieldContext;
import org.xml2as.parser.Xml2AsParser.HashTypeContext;
import org.xml2as.parser.Xml2AsParser.InputContext;
import org.xml2as.parser.Xml2AsParser.ListTypeContext;
import org.xml2as.parser.Xml2AsParser.NativeTypeContext;
import org.xml2as.parser.Xml2AsParser.TypeContext;
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

		packName = "";
		if (root.packName() != null)
		{
			packName = root.packName().getText();
		}

		int order = 1;
		for (TypeContext type : root.type())
		{
			InputContext typeMeta = type.input();

			String typeName = type.typeName().getText();
			String typeInputFile = typeMeta != null && typeMeta.filePath != null ? typeMeta.filePath.getText() : null;
			String typeInputPath = typeMeta != null && typeMeta.nodePath != null ? typeMeta.nodePath.getText() : null;
			ArrayList<ClassField> typeFields = new ArrayList<ClassField>();

			if (typeInputFile != null && typeInputFile.charAt(0) == '"')
			{
				typeInputFile = typeInputFile.substring(1, typeInputFile.length() - 1);
			}
			if (typeInputPath != null && typeInputPath.charAt(0) == '"')
			{
				typeInputPath = typeInputPath.substring(1, typeInputPath.length() - 1);
			}

			for (FieldContext field : type.field())
			{
				String fieldName = field.typeName().getText();
				String fieldType = "";
				boolean fieldRepeted = false;
				String[] indexList = null;
				String fieldInputPath = field.nodePath != null ? field.nodePath.getText() : "";

				if (fieldInputPath != null && fieldInputPath.charAt(0) == '"')
				{
					fieldInputPath = fieldInputPath.substring(1, fieldInputPath.length() - 1);
				}

				NativeTypeContext nativeType = field.nativeType();
				ListTypeContext listType = field.listType();
				HashTypeContext hashType = field.hashType();

				if (nativeType != null)
				{
					fieldType = nativeType.typeName().getText();
				}
				else if (listType != null)
				{
					fieldType = listType.typeName().getText();
					fieldRepeted = true;
				}
				else if (hashType != null)
				{
					fieldType = hashType.typeName().getText();
					fieldRepeted = true;
					List<Token> tokens = hashType.params;
					if (tokens != null)
					{
						ArrayList<String> keys = new ArrayList<String>();
						for (Token token : tokens)
						{
							String key = token.getText();
							if (!key.trim().isEmpty())
							{
								keys.add(key);
							}
						}
						indexList = keys.toArray(new String[] {});
					}
				}

				typeFields.add(new ClassField(fieldInputPath, fieldName, "", fieldType, fieldRepeted, indexList));
			}

			Class clazz = new Class(typeInputFile, typeInputPath, packName, typeName, "", order, typeFields.toArray(new ClassField[] {}));
			order++;

			name2Class.put(typeName, clazz);
			if (typeInputFile != null)
			{
				inputFile = typeInputFile;

				mainClass.add(clazz);
			}
		}
	}
}
