package org.game.knight.version.packer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TaskContentProvider implements ITreeContentProvider
{

	@Override
	public void dispose()
	{
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof LogList)
		{
			return ((LogList)parentElement).getChildren();
		}
		return null;
	}

	@Override
	public Object getParent(Object element)
	{
		if(element instanceof Log)
		{
			return ((Log)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if(element instanceof LogList)
		{
			return ((LogList)element).getChildren().length>0;
		}
		return false;
	}

}
