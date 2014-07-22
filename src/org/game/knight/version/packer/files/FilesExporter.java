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
	 * ���캯��
	 * 
	 * @param src
	 * @param dst
	 */
	public FilesExporter(File src, File dst,boolean zip)
	{
		super("�����ļ�",src,dst);
		
		this.zip=zip;
	}
	
	/**
	 * ��������
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
		
		//�����ļ�
		GamePacker.beginLogSet("��ȡ�ļ�");
		readDir(getSourceDir());
		GamePacker.endLogSet();
		
		//�����ļ�
		GamePacker.beginLogSet("�����ļ�");
		String[] urls=new String[files.keySet().size()];
		urls=files.keySet().toArray(urls);
		GamePacker.endLogSet();

		if(isCancel())
		{
			return;
		}
		
		//�����ļ�
		GamePacker.beginLogSet("����ļ�");
		for(String url : urls)
		{
			GamePacker.progress("����ļ�",url);
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
		
		//��������
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("����������Ϣ");
		StringBuilder txt = new StringBuilder();
		txt.append("<fileSet>\n");
		for(String innerPath : urls)
		{
			String exportKey=getChecksumTable().getChecksumID(innerPath);
			
			txt.append(String.format("\t<file name=\"%s\" url=\"%s\" size=\"%s\"/>\n",innerPath,getExportedFileUrl(exportKey),getExportedFileSize(exportKey)));
		}
		txt.append("</fileSet>");
		GamePacker.log("����������Ϣ");
		byte[] bytes=txt.toString().getBytes("UTF-8");
		String checksum=(zip ? "zlib_md5":"md5")+MD5Util.md5Bytes(bytes);
		exportFile(checksum,MD5Util.addSuffix((zip ? ZlibUtil.compress(bytes):bytes)),"cfg");
		GamePacker.endLogSet();

		if(isCancel())
		{
			return;
		}
		
		//������Ŀ����
		GamePacker.beginLogSet("���������Ϣ");
		GamePacker.log("���ɻ�����Ϣ");
		StringBuilder sb = new StringBuilder();
		sb.append("<project>\n");
		sb.append("\t<configs>\n");
		sb.append(String.format("\t\t<config name=\"fileSet\" path=\"%s\" size=\"%s\" />\n", getExportedFileUrl(checksum), getExportedFileSize(checksum)));
		sb.append("\t</configs>\n");
		sb.append("</project>");
		GamePacker.log("���������Ϣ");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.xml"), sb.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();

		//�����ļ��б�
		GamePacker.beginLogSet("����ļ�����");
		GamePacker.log("�����ļ�����");
		StringBuilder filesSB = new StringBuilder();
		String[] urlList=getExportedFileUrls();
		for (String url : urlList)
		{
			filesSB.append(url + "\n");
		}
		GamePacker.log("�����ļ�����");
		FileUtil.writeFile(new File(getDestDir().getPath() + "/db.ver"), filesSB.toString().getBytes("UTF-8"));
		GamePacker.endLogSet();
	}
	

	/**
	 * ��ȡĿ¼
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

				GamePacker.progress("��ȡ�ļ�",innerPath);
				
				this.files.put(innerPath, file);
			}
		}
	}
	
}
