package org.game.knight.version.packer.files;

import java.io.File;

import org.chw.util.FileUtil;
import org.chw.util.MD5Util;
import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.GamePackerHelper;
import org.game.knight.version.packer.base.ZipConfig;

public class FilesExporter
{
	private GamePackerHelper helper;
	private File outputFolder;

	private ZipConfig oldZip;
	private ZipConfig newZip;

	/**
	 * 构造函数
	 * 
	 * @param src
	 * @param dst
	 */
	public FilesExporter(GamePackerHelper helper, File dst)
	{
		this.helper = helper;
		this.outputFolder = dst;

		this.oldZip = new ZipConfig(new File(dst + File.separator + "ver.zip"));
		this.newZip = new ZipConfig();
	}

	/**
	 * 发布
	 * 
	 * @throws Exception
	 */
	public Boolean publish()
	{
		try
		{
			GamePacker.beginTask("导出文件");

			StringBuilder sb = new StringBuilder();
			sb.append("<fileSet>\n");
			File[] files = helper.listFiles(helper.fileInputFolder, "*");
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];
				String ext = helper.getFileExtName(file);
				String url = file.getPath().substring(helper.fileInputFolder.getPath().length()).replaceAll("\\\\", "/");

				GamePacker.progress(String.format("处理文件(%s/%s)：%s", i + 1, files.length, url));

				String writeMD5 = MD5Util.md5(file);
				String writeURL = oldZip.getGameFiles().get(writeMD5);
				if (writeURL == null)
				{
					writeURL = oldZip.getVersionNextGameFileURL(ext);

					FileUtil.writeFile(new File(outputFolder.getPath() + writeURL), MD5Util.addSuffix(FileUtil.getFileBytes(file)));
				}

				newZip.getGameFiles().put(writeMD5, writeURL);
				newZip.getVersionFiles().add("/" + outputFolder.getName() + writeURL);

				sb.append(String.format("\t<file path=\"%s\" url=\"%s\" size=\"%s\"/>\n", url, "/" + outputFolder.getName() + writeURL, new File(outputFolder.getPath() + writeURL).length()));

				if (GamePacker.isCancel())
				{
					return false;
				}
			}
			sb.append("</fileSet>");

			//
			newZip.setVersion("");
			newZip.setVersionProps(oldZip.getVersionProps());
			newZip.getCfgFiles().put("$FileSet.xml", sb.toString().getBytes("UTF-8"));
			newZip.getVersionFiles().add("/" + oldZip.getFile().getName());
			newZip.saveTo(oldZip.getFile());

			GamePacker.log("完成");

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			GamePacker.endTask();
		}

		return false;
	}
}
