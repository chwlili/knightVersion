package org.game.knight.version.packer.view;

import java.util.ArrayList;

public class ViewEntityGroup
{
	private ViewEntityWriter writer;
	private ArrayList<ViewEntity> entitys = new ArrayList<ViewEntity>();
	private ArrayList<ViewEntityBag> bags = new ArrayList<ViewEntityBag>();

	public ViewEntityGroup(ViewEntityWriter writer)
	{
		this.writer = writer;
	}

	public ArrayList<ViewEntityBag> getBags()
	{
		return bags;
	}

	public void addEntity(ViewEntity entity)
	{
		entitys.add(entity);
		entity.setOutputGroup(this);
	}

	public void measureBag()
	{
		ViewEntityBag bag = new ViewEntityBag(writer);
		for (ViewEntity entity : entitys)
		{
			bag.add(entity);
		}
		bags.add(bag);
	}
}
