package org.game.knight.version.packer.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class ViewFileGroup
{
	private HashSet<ViewFile> files=new HashSet<ViewFile>();
	private ArrayList<ViewFileBag> bags=new ArrayList<ViewFileBag>();
	
	/**
	 * ���һ�������ļ�
	 * @param file
	 */
	public void add(ViewFile file)
	{
		files.add(file);
	}
	
	/**
	 * ��ȡ�ļ����б�
	 * @return
	 */
	public ArrayList<ViewFileBag> getBags()
	{
		return bags;
	}
	
	/**
	 * ��ͼ�ļ�������
	 */
	private Comparator<ViewFile> viewFileSorter=new Comparator<ViewFile>()
	{
		@Override
		public int compare(ViewFile o1, ViewFile o2)
		{
			return o1.getInnerPath().compareTo(o2.getInnerPath());
		}
	};
	
	/**
	 * �����ļ���
	 * @throws Exception 
	 */
	public void buildBags(ViewExport manager) throws Exception
	{
		//������ͼ�ļ��б�
		ArrayList<ViewFile> list=new ArrayList<ViewFile>();
		for(ViewFile file:files)
		{
			list.add(file);
		}
		
		//������ͼ�ļ��б�
		Collections.sort(list, viewFileSorter);
		
		//��ʼ�ְ�
		bags=new ArrayList<ViewFileBag>();
		for(int index=0;index<list.size();)
		{
			long size=0;
			long maxSize=1024*1024;//500*1024;
			
			ArrayList<ViewFile> temp=new ArrayList<ViewFile>();
			
			for(;index<list.size();)
			{
				ViewFile item=list.get(index);
				int itemSize=item.getContents().length;
				
				if(size+itemSize<=maxSize)
				{
					temp.add(item);
					size+=itemSize;
					index++;
				}
				else
				{
					if(size==0)
					{
						temp.add(item);
						size+=itemSize;
						index++;
					}
					else
					{
						break;
					}
				}
			}
			
			if(temp.size()>0)
			{
				ViewFile[] files=new ViewFile[temp.size()];
				files=temp.toArray(files);
				
				StringBuilder keySB = new StringBuilder();
				for (int i = 0; i < files.length; i++)
				{
					keySB.append(files[i].getFileKey());
					if (i < files.length - 1)
					{
						keySB.append("+");
					}
				}
				bags.add(new ViewFileBag(keySB.toString(),files));
			}
		}
		
		for(int i=0;i<bags.size();i++)
		{
			manager.addOutputBag(bags.get(i));
		}
	}
}
