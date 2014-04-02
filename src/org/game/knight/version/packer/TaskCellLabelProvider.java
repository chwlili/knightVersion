package org.game.knight.version.packer;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.wb.swt.SWTResourceManager;

public class TaskCellLabelProvider extends CellLabelProvider
{
	@Override
	public void update(ViewerCell cell)
	{
		Object item = cell.getElement();
		if (item instanceof Log)
		{
			Log log = (Log) item;
			cell.setForeground(SWTResourceManager.getColor(0, 0, 0));
			cell.setText(log.getText()+"     "+log.getPath());
			
			if(log.getType()==0)
			{
				cell.setImage(SWTResourceManager.getImage(TaskCellLabelProvider.class, "/icons/pack_icon.gif"));
			}
			else if(log.getType()==1)
			{
				cell.setImage(SWTResourceManager.getImage(TaskCellLabelProvider.class, "/icons/log_icon.gif"));
			}
			else if(log.getType()==2)
			{
				cell.setImage(SWTResourceManager.getImage(TaskCellLabelProvider.class, "/icons/warning_icon.gif"));
			}
			else if(log.getType()==3)
			{
				cell.setForeground(SWTResourceManager.getColor(0xFF, 0, 0));
				cell.setImage(SWTResourceManager.getImage(TaskCellLabelProvider.class, "/icons/error_icon.gif"));
			}
		}
	}
}
