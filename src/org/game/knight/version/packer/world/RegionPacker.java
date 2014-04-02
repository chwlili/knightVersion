package org.game.knight.version.packer.world;

import java.awt.Rectangle;

public class RegionPacker
{
	Node root;

	/**
	 * 区域打包工具
	 * @param width
	 * @param height
	 * @param padding
	 * @param duplicateBorder
	 */
	public RegionPacker(int width, int height)
	{
		this.root = new Node(0, 0, width, height, null, null, null);
	}

	/**
	 * 插入区域
	 * @param name
	 * @param region
	 * @return
	 */
	public boolean insertRegion(Region region,String name)
	{
		Rectangle rect = new Rectangle(0, 0, Math.min(2048,region.getClipW()), Math.min(2048, region.getClipH()));
		Node node = insert(root, rect);

		if (node != null)
		{
			node.leaveName=name;
			region.setTextureX(node.rect.x);
			region.setTextureY(node.rect.y);
			return true;
		}
		
		return false;
	}

	/**
	 * 插入节点
	 * @param node
	 * @param rect
	 * @return
	 */
	private Node insert(Node node, Rectangle rect)
	{
		if (node.leaveName == null && node.leftChild != null && node.rightChild != null)
		{
			Node newNode = null;

			newNode = insert(node.leftChild, rect);
			if (newNode == null)
				newNode = insert(node.rightChild, rect);

			return newNode;
		}
		else
		{
			if (node.leaveName != null)
				return null;

			if (node.rect.width == rect.width && node.rect.height == rect.height)
				return node;

			if (node.rect.width < rect.width || node.rect.height < rect.height)
				return null;

			node.leftChild = new Node();
			node.rightChild = new Node();

			int deltaWidth = node.rect.width - rect.width;
			int deltaHeight = node.rect.height - rect.height;

			if (deltaWidth > deltaHeight)
			{
				node.leftChild.rect.x = node.rect.x;
				node.leftChild.rect.y = node.rect.y;
				node.leftChild.rect.width = rect.width;
				node.leftChild.rect.height = node.rect.height;

				node.rightChild.rect.x = node.rect.x + rect.width;
				node.rightChild.rect.y = node.rect.y;
				node.rightChild.rect.width = node.rect.width - rect.width;
				node.rightChild.rect.height = node.rect.height;
			}
			else
			{
				node.leftChild.rect.x = node.rect.x;
				node.leftChild.rect.y = node.rect.y;
				node.leftChild.rect.width = node.rect.width;
				node.leftChild.rect.height = rect.height;

				node.rightChild.rect.x = node.rect.x;
				node.rightChild.rect.y = node.rect.y + rect.height;
				node.rightChild.rect.width = node.rect.width;
				node.rightChild.rect.height = node.rect.height - rect.height;
			}

			return insert(node.leftChild, rect);
		}
	}
	
	/**
	 * 打包节点
	 * @author tt
	 *
	 */
	static final class Node
	{
		public Node leftChild;
		public Node rightChild;
		public Rectangle rect;
		public String leaveName;

		public Node(int x, int y, int width, int height, Node leftChild, Node rightChild, String leaveName)
		{
			this.rect = new Rectangle(x, y, width, height);
			this.leftChild = leftChild;
			this.rightChild = rightChild;
			this.leaveName = leaveName;
		}

		public Node()
		{
			rect = new Rectangle();
		}
	}
}
