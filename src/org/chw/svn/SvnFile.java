package org.chw.svn;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SvnFile
{
	private File file;
	private SvnFileInfo info;
	private SvnFile[] children;
	private String ext;

	/**
	 * 构造函数
	 * 
	 * @param file
	 */
	public SvnFile(File file)
	{
		this.file = file;
	}

	/**
	 * 构造函数
	 * 
	 * @param path
	 */
	public SvnFile(String path)
	{
		this(new File(path));
	}

	/**
	 * 获取绝对路径
	 * 
	 * @return
	 */
	public String getPath()
	{
		return this.file.getPath();
	}

	/**
	 * 获取扩展名
	 * 
	 * @return
	 */
	public String getExt()
	{
		if (ext == null)
		{
			String path = getPath();
			int index = path.lastIndexOf(".");
			ext = path.substring(index + 1);
		}
		return ext;
	}

	/**
	 * 获取原生文件对象
	 * 
	 * @return
	 */
	public File getNativeFile()
	{
		return file;
	}

	/**
	 * 是否为文件
	 * 
	 * @return
	 */
	public boolean isFile()
	{
		return file.isFile();
	}

	/**
	 * 是否为目录
	 * 
	 * @return
	 */
	public boolean isDirectory()
	{
		return file.isDirectory();
	}

	/**
	 * 是否存在
	 * 
	 * @return
	 */
	public boolean exists()
	{
		return file.exists();
	}

	/**
	 * 获取工作考拷贝主路径
	 * 
	 * @return
	 */
	public SvnFile getRoot()
	{
		return new SvnFile(svnInfo().workingCopyRootPath);
	}

	/**
	 * 列出子文件
	 * 
	 * @return
	 */
	public SvnFile[] listFiles()
	{
		if (children == null)
		{
			if (file.exists() && file.isDirectory() && file.isHidden() == false)
			{
				File[] files = file.listFiles();
				SvnFile[] svnFiles = new SvnFile[files.length];
				for (int i = 0; i < files.length; i++)
				{
					svnFiles[i] = new SvnFile(files[i]);
				}
				children = svnFiles;
			}

			children = new SvnFile[] {};
		}
		return children;
	}

	/**
	 * 调用命令 svn info
	 * 
	 * @return
	 */
	public SvnFileInfo svnInfo()
	{
		if (info != null) { return info; }

		try
		{
			SvnFileInfo result = new SvnFileInfo();

			String cmd = "svn info \"" + getPath() + "\"";

			System.out.println("$ " + cmd);

			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}

				System.out.println(msg);

				int index = msg.indexOf(":");
				String key = msg.substring(0, index).trim();
				String val = msg.substring(index + 1, msg.length()).trim();

				if (key.equals("Path"))
				{
					result.path = val;
				}
				else if (key.equals(("Working Copy Root Path")))
				{
					result.workingCopyRootPath = val;
				}
				else if (key.equals("URL"))
				{
					result.url = val;
				}
				else if (key.equals("Repository Root"))
				{
					result.repositoryRoot = val;
				}
				else if (key.equals("Repository UUID"))
				{
					result.repositoryUUID = val;
				}
				else if (key.equals("Revision"))
				{
					result.revision = val;
				}
				else if (key.equals("Node Kind"))
				{
					result.nodeKind = val;
				}
				else if (key.equals("Schedule"))
				{
					result.schedule = val;
				}
				else if (key.equals("Last Changed Author"))
				{
					result.lastChangedAuthor = val;
				}
				else if (key.equals("Last Changed Rev"))
				{
					result.lastChangedRevision = val;
				}
				else if (key.equals("Last Changed Date"))
				{
					result.lastChangeDate = val;
				}
				else if (key.equals("Checksum"))
				{
					result.checksum = val;
				}
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");

			if (result.path != null)
			{
				info = result;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return info;
	}

	/**
	 * 调用命令 svn status
	 * 
	 * @return
	 */
	public String svnStatus()
	{
		StringBuilder status = new StringBuilder();

		try
		{
			Process p = Runtime.getRuntime().exec("cmd /c svn status \"" + getPath() + "\"");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				status.append(msg + "\n");
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				status.append(error + "\n");
			}

			br.close();
			p.destroy();

			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (status.length() > 0)
		{
			return status.toString();
		}
		else
		{
			return null;
		}
	}

	/**
	 * 调用命令 svn diff
	 * 
	 * @return
	 */
	public SvnFileDelta svnDiff(int ver1, int ver2)
	{
		try
		{
			ArrayList<String> addedFiles = new ArrayList<String>();
			ArrayList<String> modifedFiles = new ArrayList<String>();
			ArrayList<String> deletedFiles = new ArrayList<String>();

			String cmd = "svn diff -r " + ver1 + ":" + ver2 + " --summarize \"" + svnInfo().workingCopyRootPath + "\"";// getPath();

			System.out.println("$ " + cmd);

			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}

				msg = msg.trim();

				int index = msg.indexOf(" ");

				String key = msg.substring(0, 1).toUpperCase().trim();
				String val = msg.substring(index, msg.length()).trim();

				if (val.startsWith(getPath()))
				{
					System.out.println(msg);

					if (key.equals("A"))
					{
						addedFiles.add(val);
						// addedFiles.add(val.substring(svnInfo().workingCopyRootPath.length()));
					}
					else if (key.equals("M"))
					{
						modifedFiles.add(val);
						// modifedFiles.add(val.substring(svnInfo().workingCopyRootPath.length()));
					}
					else if (key.equals("D"))
					{
						deletedFiles.add(val);
						// deletedFiles.add(val.substring(svnInfo().workingCopyRootPath.length()));
					}
				}
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");

			SvnFileDelta result = new SvnFileDelta();
			result.addedFiles = new String[addedFiles.size()];
			result.modefiedFiles = new String[modifedFiles.size()];
			result.deletedFiles = new String[deletedFiles.size()];

			result.addedFiles = addedFiles.toArray(result.addedFiles);
			result.modefiedFiles = modifedFiles.toArray(result.modefiedFiles);
			result.deletedFiles = deletedFiles.toArray(result.deletedFiles);

			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 调用命令 svn update
	 */
	public void svnUpdate()
	{
		if (!exists()) { return; }

		try
		{
			String cmd = "svn update \"" + getPath() + "\"";

			System.out.println("$ " + cmd);

			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 调用命令 svn add
	 */
	public void svnAdd()
	{
		if (!exists()) { return; }

		try
		{
			String cmd = "svn add \"" + getPath() + "\" --force";

			System.out.println("$ " + cmd);

			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 调用命令 svn delete
	 */
	public void svnDelete()
	{
		try
		{
			String cmd = "svn delete \"" + getPath() + "\"";

			System.out.println("$ " + cmd);

			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 调用命令 svn commit
	 */
	public void svnCommit(String message)
	{
		try
		{
			String cmd = "svn commit \"" + getPath() + "\" -m \"" + message + "\"";

			System.out.println("$ " + cmd);

			Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GBK"));
			String msg = null;
			while ((msg = br.readLine()) != null)
			{
				if (msg.isEmpty())
				{
					continue;
				}
				System.out.println(msg);
			}

			BufferedReader errorOutput = new BufferedReader(new InputStreamReader(p.getErrorStream(), "GBK"));
			String error = null;
			while ((error = errorOutput.readLine()) != null)
			{
				System.out.println(error);
			}

			br.close();
			p.destroy();

			System.out.println("");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
