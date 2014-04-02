package org.game.knight.version.packer.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.chw.svn.SvnFile;
import org.chw.svn.SvnFileDelta;
import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerConst;


public abstract class AbsExporter
{
	private String name;
	private File src;
	private File dst;

	private OptionTable optionTable;
	private ChecksumTable checksumTable;
	private FileTable exportedFileTable;
	
	/**
	 * ���캯��
	 * 
	 * @param src
	 * @param dst
	 */
	public AbsExporter(String name, File src, File dst)
	{
		this.name = name;
		this.src = src;
		this.dst = dst;
	}

	// ------------------------------------------------------------------------------------
	//
	// ��־��Ϣ�ռ�
	//
	// ------------------------------------------------------------------------------------

	/**
	 * �Ƿ���ȡ��
	 * 
	 * @return
	 */
	protected boolean isCancel()
	{
		return GamePacker.isCancel();
	}

	// ------------------------------------------------------------------------------------
	//
	// ������Ϣ
	//
	// ------------------------------------------------------------------------------------

	/**
	 * ��ȡԴĿ¼
	 * 
	 * @return
	 */
	public File getSourceDir()
	{
		return src;
	}

	/**
	 * ��ȡĿ��Ŀ¼
	 * 
	 * @return
	 */
	public File getDestDir()
	{
		return dst;
	}

	/**
	 * ��ȡѡ���
	 * 
	 * @return
	 */
	public OptionTable getOptionTable()
	{
		return optionTable;
	}

	/**
	 * ��ȡУ�����
	 * 
	 * @return
	 */
	public ChecksumTable getChecksumTable()
	{
		return checksumTable;
	}

	// ------------------------------------------------------------------------------------
	//
	// ����
	//
	// ------------------------------------------------------------------------------------

	/**
	 * ����
	 */
	public void publish()
	{
		GamePacker.beginTask(name != null ? name : "");

		if (isCancel()) { return; }

		openVers();

		if (isCancel()) { return; }

		try
		{
			syncChangedFiles();
		}
		catch (Exception e)
		{
			GamePacker.error(e);
			return;
		}

		if (isCancel()) { return; }

		try
		{
			exportContent();
		}
		catch (Exception e)
		{
			GamePacker.error(e);
			return;
		}

		if (isCancel()) { return; }

		saveVers();

		GamePacker.endTask();
	}

	// -------------------------------------------------------------------------------
	//
	// �汾��Ϣ
	//
	// -------------------------------------------------------------------------------

	/**
	 * �򿪰汾��Ϣ
	 */
	protected void openVers()
	{
		GamePacker.log("��ȡ�汾��Ϣ");

		optionTable = new OptionTable();
		optionTable.open(new File(dst.getPath() + "/.ver/option"));

		checksumTable = new ChecksumTable(this);
		checksumTable.open(new File(dst.getPath() + "/.ver/checksum"));

		exportedFileTable = new FileTable(this);
		exportedFileTable.open(new File(dst.getPath() + "/.ver/exportedFiles"));
	}

	/**
	 * ����汾��Ϣ
	 */
	protected void saveVers()
	{
		GamePacker.log("����汾��Ϣ");

		optionTable.save();

		checksumTable.save();

		exportedFileTable.save();
	}

	// -------------------------------------------------------------------------------
	//
	// ͬ���Ķ����ļ�
	//
	// -------------------------------------------------------------------------------

	/**
	 * ͬ���Ķ����ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void syncChangedFiles() throws Exception
	{
		//GamePacker.beginLogSet("SHAУ��");
		
		//����Ŀ¼
		ArrayList<File> dirs=new ArrayList<File>();
		ArrayList<File> files=new ArrayList<File>();
		
		dirs.add(new File(src.getPath()));
		while (dirs.size() > 0)
		{
			File curr = dirs.remove(0);
			if (curr.isDirectory())
			{
				File[] childs = curr.listFiles();
				for (int i = 0; i < childs.length; i++)
				{
					File child = childs[i];
					
					if(child.isHidden())
					{
						continue;
					}
					
					if (child.isDirectory())
					{
						dirs.add(child);
					}
					else
					{
						files.add(child);
					}
				}
			}
		}

		//SHAУ��
		String[] keys = checksumTable.getKeys();
		
		HashSet<String> newKeys = new HashSet<String>();
		for(int i=0;i<files.size();i++)
		{
			File file=files.get(i);

			String innerPath = file.getPath().substring(src.getPath().length()).replaceAll("\\\\", "/");
			
			GamePacker.progress(String.format("SHA(%s/%s) : %s",i+1,files.size(),innerPath));
			
			String childSHA=MD5Util.md5File(file);

			if (isCancel()) { return; }
			
			String checksum = checksumTable.getChecksum(innerPath);
			if (checksum == null)
			{
				GamePacker.progress("����", innerPath);
				checksumTable.add(innerPath, childSHA);
			}
			else
			{
				if (!checksum.equals(childSHA))
				{
					GamePacker.progress("�ı�", innerPath);
					checksumTable.set(innerPath, childSHA);
				}
			}

			newKeys.add(innerPath);
		}

		for (int i = 0; i < keys.length; i++)
		{
			if (!newKeys.contains(keys[i]))
			{
				GamePacker.progress("ɾ��", keys[i]);
				checksumTable.del(keys[i]);
			}
		}

		optionTable.setRevision("SHA:0");

		//GamePacker.endLogSet();
	}
	

	protected void syncChangedFiles_old1() throws Exception
	{
		GamePacker.beginLogSet("SHAУ��");

		ArrayList<File> files = new ArrayList<File>();
		files.add(new File(src.getPath()));

		String[] keys = checksumTable.getKeys();

		HashSet<String> newKeys = new HashSet<String>();
		while (files.size() > 0)
		{
			File curr = files.remove(0);
			if (curr.isDirectory())
			{
				File[] childs = curr.listFiles();
				for (int i = 0; i < childs.length; i++)
				{
					File child = childs[i];
					if (child.isDirectory())
					{
						files.add(child);
					}
					else
					{
						String innerPath = child.getPath().substring(src.getPath().length()).replaceAll("\\\\", "/");

						GamePacker.progress("SHA:", innerPath);
						String childSHA=MD5Util.md5File(child);
						
						String checksum = checksumTable.getChecksum(innerPath);
						if (checksum == null)
						{
							GamePacker.progress("����", innerPath);
							checksumTable.add(innerPath, childSHA);
						}
						else
						{
							if (!checksum.equals(childSHA))
							{
								GamePacker.progress("�ı�", innerPath);
								checksumTable.set(innerPath, childSHA);
							}
						}

						newKeys.add(innerPath);
					}
				}
			}
		}

		for (int i = 0; i < keys.length; i++)
		{
			if (!newKeys.contains(keys[i]))
			{
				GamePacker.progress("ɾ��", keys[i]);
				checksumTable.del(keys[i]);
			}
		}

		optionTable.setRevision("SHA:0");

		GamePacker.endLogSet();
	}

	/**
	 * ͬ���Ķ����ļ�
	 * 
	 * @param file
	 * @throws Exception
	 */
	protected void syncChangedFiles_old() throws Exception
	{
		String lastRev = getOptionTable().getRevision();

		int lastSvnRev = 0;
		if (lastRev.startsWith("SVN:"))
		{
			lastSvnRev = Integer.parseInt(lastRev.substring(4));
		}

		SvnFile svn = new SvnFile(src);
		if (svn.svnInfo() != null && lastSvnRev > 0)
		{
			String status = svn.svnStatus();
			if (status != null) { throw new Exception("SVNĿ¼����δ�ύ���޸ģ�"); }

			GamePacker.log("SVN����");
			svn.svnUpdate();

			GamePacker.beginLogSet("SVNУ��");

			int oldRevision = lastSvnRev;
			int newRevision = Integer.parseInt(svn.svnInfo().revision);

			SvnFileDelta delta = svn.svnDiff(oldRevision, newRevision);
			for (String addedFile : delta.addedFiles)
			{
				File curr = new File(addedFile);
				if (curr.isFile())
				{
					String path = addedFile.substring(src.getPath().length()).replaceAll("\\\\", "/");

					GamePacker.progress("����", path);
					checksumTable.add(path, (new SvnFile(addedFile)).svnInfo().checksum);
				}
			}

			for (String changedFile : delta.modefiedFiles)
			{
				File curr = new File(changedFile);
				if (curr.isFile())
				{
					String path = changedFile.substring(src.getPath().length()).replaceAll("\\\\", "/");

					GamePacker.progress("�ı�", path);
					checksumTable.set(path, (new SvnFile(changedFile)).svnInfo().checksum);
				}
			}

			for (String removedFile : delta.deletedFiles)
			{
				String path = removedFile.substring(src.getPath().length()).replaceAll("\\\\", "/");

				GamePacker.progress("ɾ��", path);
				checksumTable.del(path);
			}

			optionTable.setRevision("SVN:" + newRevision);

			GamePacker.endLogSet();
		}
		else
		{
			if (svn.svnInfo() != null)
			{
				GamePacker.log("SVN����");
				svn.svnUpdate();
			}

			GamePacker.beginLogSet("SHAУ��");

			ArrayList<File> files = new ArrayList<File>();
			files.add(new File(src.getPath()));

			String[] keys = checksumTable.getKeys();

			HashSet<String> newKeys = new HashSet<String>();
			while (files.size() > 0)
			{
				File curr = files.remove(0);
				if (curr.isDirectory())
				{
					File[] childs = curr.listFiles();
					for (int i = 0; i < childs.length; i++)
					{
						File child = childs[i];
						if (child.isDirectory())
						{
							files.add(child);
						}
						else
						{
							String innerPath = child.getPath().substring(src.getPath().length()).replaceAll("\\\\", "/");

							String checksum = checksumTable.getChecksum(innerPath);
							if (checksum == null)
							{
								GamePacker.progress("����", innerPath);
								checksumTable.add(innerPath, MD5Util.md5File(child));
							}
							else
							{
								String sha = MD5Util.md5File(child);
								if (!checksum.equals(sha))
								{
									GamePacker.progress("�ı�", innerPath);
									checksumTable.set(innerPath, sha);
								}
							}

							newKeys.add(innerPath);
						}
					}
				}
			}

			for (int i = 0; i < keys.length; i++)
			{
				if (!newKeys.contains(keys[i]))
				{
					GamePacker.progress("ɾ��", keys[i]);
					checksumTable.del(keys[i]);
				}
			}

			if (svn.svnInfo() != null)
			{
				optionTable.setRevision("SVN:" + svn.svnInfo().revision);
			}
			else
			{
				optionTable.setRevision("SHA:" + (new Date().getTime()));
			}

			GamePacker.endLogSet();
		}
	}

	// -------------------------------------------------------------------------------
	//
	// ����������
	//
	// -------------------------------------------------------------------------------

	/**
	 * ��ȡ�ļ���
	 * 
	 * @param path
	 * @return
	 */
	protected String getFileName(String path)
	{
		String name = "";

		int index = path.lastIndexOf("/");
		if (index == -1)
		{
			index = path.lastIndexOf("\\");
		}

		if (index != -1)
		{
			name = path.substring(index + 1);

			index = name.lastIndexOf(".");
			if (index != -1)
			{
				name = name.substring(0, index);
			}
		}

		return name;
	}

	/**
	 * ��ȡ�ļ���
	 * 
	 * @param file
	 * @return
	 */
	protected String getFileName(File file)
	{
		return getFileName(file.getPath());
	}

	/**
	 * ��ȡ�ļ���չ��
	 * 
	 * @param path
	 * @return
	 */
	protected String getFileExtName(String path)
	{
		String ext = "";

		int index = path.lastIndexOf("/");
		if (index == -1)
		{
			index = path.lastIndexOf("\\");
		}

		if (index != -1)
		{
			String name = path.substring(index + 1);

			index = name.lastIndexOf(".");
			if (index != -1)
			{
				ext = name.substring(index + 1);
			}
		}

		return ext;
	}

	/**
	 * ��ȡ�ļ���չ��
	 * 
	 * @param file
	 * @return
	 */
	protected String getFileExtName(File file)
	{
		return getFileExtName(file.getPath());
	}

	/**
	 * ����ļ�
	 * 
	 * @param key
	 * @param source
	 * @throws IOException
	 */
	public String exportFile(String key, File source) throws IOException
	{
		String filePath = exportedFileTable.getUrl(key);
		if (filePath == null)
		{
			long fileID = getOptionTable().getNextFileID();
			long folderID = (fileID - 1) / GamePackerConst.FILE_COUNT_EACH_DIR + 1;

			filePath = "/" + folderID + "/" + fileID + "." + getFileExtName(source).toLowerCase();

			FileUtil.copyTo(new File(getDestDir().getPath() + filePath), source);

			exportedFileTable.add(key, filePath);
		}
		return getExportedFileUrl(key);
	}

	/**
	 * ����ļ�
	 * 
	 * @param key
	 * @param content
	 * @throws IOException
	 */
	public String exportFile(String key, byte[] content, String extName) throws IOException
	{
		String filePath = exportedFileTable.getUrl(key);
		if (filePath == null)
		{
			long fileID = getOptionTable().getNextFileID();
			long folderID = (fileID - 1) / GamePackerConst.FILE_COUNT_EACH_DIR + 1;

			filePath = "/" + folderID + "/" + fileID + "." + extName;

			FileUtil.writeFile(new File(getDestDir().getPath() + filePath), content);

			exportedFileTable.add(key, filePath);
		}
		return getExportedFileUrl(key);
	}

	/**
	 * �Ƿ�����ѵ������ļ�
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasExportedFile(String key)
	{
		return exportedFileTable.getUrl(key) != null;
	}

	/**
	 * ��ȡ�ѵ������ļ�
	 * 
	 * @param key
	 * @return
	 */
	public File getExportedFile(String key)
	{
		return new File(dst.getPath() + exportedFileTable.getUrl(key));
	}

	/**
	 * ��ȡ�ѵ����ļ���URL
	 * 
	 * @param key
	 * @return
	 */
	public String getExportedFileUrl(String key)
	{
		return "/" + dst.getName() + exportedFileTable.getUrl(key);
	}

	/**
	 * ��ȡ�����ѵ����ļ���URL
	 * 
	 * @param key
	 * @return
	 */
	public String[] getExportedFileUrls()
	{
		String[] urls=exportedFileTable.getURLs();
		for(int i=0;i<urls.length;i++)
		{
			urls[i]="/"+dst.getName()+urls[i];
		}
		Arrays.sort(urls);
		return urls;
	}

	/**
	 * ��ȡ�ѵ����ļ��Ĵ�С
	 * 
	 * @param key
	 * @return
	 */
	public long getExportedFileSize(String key)
	{
		return getExportedFile(key).length();
	}

	/**
	 * �������
	 */
	protected abstract void exportContent() throws Exception;

}
