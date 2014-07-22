package org.game.knight.version.packer.files;

import java.io.File;
import java.util.Hashtable;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.chw.util.ZlibUtil;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.base.AbsExporter;


public class FilesExporter extends AbsExporter
{
	private Hashtable<String, File> files;
	private boolean zip;
	
	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public FilesExporter(File src, File dst,boolean zip)
	{
		super("导出文件",src,dst);
		
		this.zip=zip;
	}
	
	/**
	 * 导出内容
	 * @throws Exception 
	 */
	@Override
	protected void exportContent() throws Exception
	{
		files=new Hashtable<String, File>();

		if(isCancel())
		{
			return;
		}
		
		//遍历文件
		GamePacker.beginLogSet("读取文件");
		readDir(getSourceDir());
		GamePacker.endLogSet();
		
		//排序文件
		GamePacker.beginLogSet("排序文件");
		String[] urls=new String[files.keySet().size()];
		urls=files.keySet().toArray(urls);
		GamePacker.endLogSet();

		if(isCancel())
		{
			return;
		}
		
		//导出文件
		GamePacker.beginLogSet("输出文件");
		for(String url : urls)
		{
			GamePacker.progress("输出文件",url);
			//exportFile(getChecksumTable().getChecksumID(url),files.get(url));
			exportFile(getChecksumTable().getChecksumID(url), MD5Util.addSuffix(FileUtil.getFileBytes(files.get(url))), getFileExtName(files.get(url)));
			if(isCancel())
			{
				return;
			}
		}
		GamePacker.endLogSet();

		if(isCancel())
		{
			return;
		}
		
		//生成配置
		GamePacker.beginLogSet("输出配置信息");
		GamePacker.log("生成配置信息");
		StringBuilder txt = new StringBuilder();
		txt.append("<fileSet>\n");
		for(String innerPath : urls)
		{
			String exportKey=getChecksumTable().getChecksumID(innerPath);
			
			txt.append(String.format("\t<file name=\"%s\" url=\"%s\" size=\"%s\"/>\n",innerPath,getExportedFileUrl(exportKey),getExportedFileSize(exportKey)));
		}
		txt.append("</fileSet>");
		GamePacker.log("保存配置信息");
		byte[] bytes=txt.toString().getBytes("UTF-8");
		String checksum=(zip ? "zlib_md5":"md5")+MD5Util.md5Bytes(bytes);
		exportFile(checksum,MD5Util.addSuffix((zip ? ZlibUtil.compress(bytes):bytes)),"cfg");
		GamePacker.endLogSet();

		if(isCancel())
		{
			return;
		}
		
		//导出项目配置
		GamePacker.beginLogSet("输出汇总信息");
		GamePacker.log("生成汇总信息");
		StringBuilder sb = new StringBuilder();
		sb.append("<project>\n");
		sb.append("\t<configs>\n");
		sb.append(String.format("\t\t<config name=\"fileSet\" path=\"%s\" size=\"%s\" />\n", getExportedFileUrl(checksum), getExportedFileSize(checksum)));
		sb.append("\t</configs>\n");
		sb.append("</project>");
		GamePacker.log("保存汇总信息");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), sb.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		//生成文件列表
		GamePacker.beginLogSet("输出文件汇总");
		GamePacker.log("生成文件汇总");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList=getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("保存文件汇总");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
	}
	

	/**
	 * 读取目录
	 * 
	 * @param dir
	 */
	private void readDir(File dir)
	{
		File[] files = dir.listFiles();

		if (files == null) { return; }

		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];

			if (file.isHidden())
			{
				continue;
			}

			if (file.isDirectory())
			{
				readDir(file);
			}
			else
			{
				String innerPath = file.getPath().substring(getSourceDir().getPath().length()).replaceAll("\\\\", "/");

				GamePacker.progress("读取文件",innerPath);
				
				this.files.put(innerPath, file);
			}
		}
	}
	
}
