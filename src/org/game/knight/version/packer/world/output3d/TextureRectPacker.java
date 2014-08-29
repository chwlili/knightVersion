package org.game.knight.version.packer.world.output3d;

import java.util.ArrayList;

import org.game.knight.version.packer.GamePacker;
import org.game.knight.version.packer.world.model.ImageFrame;

public class TextureRectPacker
{
	/**
	 * 依次尝试其它参数，使用最好的方式。
	 */
	public static final int Best = 0;

	/**
	 * BSSF: Positions the rectangle against the short side of a free rectangle
	 * into which it fits the best.
	 */
	public static final int ShortSideFit = 1;

	/**
	 * BLSF: Positions the rectangle against the long side of a free rectangle
	 * into which it fits the best.
	 */
	public static final int LongSideFit = 2;

	/**
	 * BAF: Positions the rectangle into the smallest free rect into which it
	 * fits.
	 */
	public static final int AreaFit = 3;

	/**
	 * BL: Does the Tetris placement.
	 */
	public static final int BottomLeft = 4;

	/**
	 * CP: Choosest the placement where the rectangle touches other rects as
	 * much as possible.
	 */
	public static final int ContactPoint = 5;

	private int binWidth;
	private int binHeight;
	private boolean allowRotations;

	private ArrayList<Rect> usedRectangles = new ArrayList<TextureRectPacker.Rect>();
	private ArrayList<Rect> freeRectangles = new ArrayList<TextureRectPacker.Rect>();

	private ArrayList<Rect> inputs = new ArrayList<Rect>();
	private ArrayList<Rect> outputs = new ArrayList<TextureRectPacker.Rect>();
	private ArrayList<RectSet> rectSets = new ArrayList<RectSet>();

	/**
	 * 构造函数
	 * 
	 * @param width
	 * @param height
	 * @param allowRotations
	 */
	public TextureRectPacker(int width, int height, boolean allowRotations)
	{
		this.reset(width, height, allowRotations);
	}

	/**
	 * 获取矩形集列表
	 * 
	 * @return
	 */
	public ArrayList<RectSet> getRectSets()
	{
		return rectSets;
	}

	/**
	 * 重设
	 */
	public void reset()
	{
		reset(binWidth, binHeight, allowRotations);
	}

	/**
	 * 重设
	 * 
	 * @param w
	 * @param h
	 */
	public void reset(int w, int h)
	{
		reset(w, h, false);
	}

	/**
	 * 重设
	 */
	public void reset(int w, int h, boolean allowRotations)
	{
		this.binWidth = w;
		this.binHeight = h;
		this.allowRotations = allowRotations;

		usedRectangles.clear();
		freeRectangles.clear();

		inputs.clear();
		outputs.clear();

		rectSets.clear();
	}

	/**
	 * 添加一个矩形
	 * 
	 * @param data
	 * @param width
	 * @param height
	 */
	public void push(Object data, int width, int height)
	{
		Rect rect = new Rect(0, 0, width, height);
		rect.data = data;
		inputs.add(rect);
	}

	/**
	 * 打包
	 * 
	 * @throws Exception
	 */
	public void pack() throws Exception
	{
		pack(TextureRectPacker.Best);
	}

	/**
	 * 打包
	 * 
	 * @param method
	 * @throws Exception
	 */
	public void pack(int method) throws Exception
	{
		if (method < 0 || method > ContactPoint)
		{
			throw new Error("参数无效!");
		}

		for (Rect rect : inputs)
		{
			if (rect.width > binWidth || rect.height > binHeight)
			{
				GamePacker.error("图像的大小超出了最大(" + binWidth + "," + binHeight + "," + ((ImageFrame) rect.data).file.url + ")!");
			}
		}

		int[] methods = new int[] {};
		if (method == Best)
		{
			methods = new int[] { ShortSideFit, LongSideFit, AreaFit, BottomLeft, ContactPoint };
		}
		else
		{
			methods = new int[] { method };
		}

		pack(methods);
	}

	private void pack(int[] methods) throws Exception
	{
		int bestMethod = 0;

		float bestSuccessArea = 0;
		for (int i = 0; i < methods.length; i++)
		{
			ArrayList<Rect> tempInput = new ArrayList<TextureRectPacker.Rect>();
			ArrayList<Rect> tempOutput = new ArrayList<TextureRectPacker.Rect>();
			for (Rect rect : inputs)
			{
				tempInput.add(rect);
			}

			usedRectangles.clear();
			freeRectangles.clear();
			freeRectangles.add(new Rect(0, 0, binWidth, binHeight));

			insert(tempInput, tempOutput, methods[i]);

			float successArea = 0;
			for (Rect rect : tempOutput)
			{
				successArea += rect.width * rect.height;
			}

			if (successArea > bestSuccessArea)
			{
				bestMethod = methods[i];
				bestSuccessArea = successArea;
			}
		}

		outputs.clear();
		usedRectangles.clear();
		freeRectangles.clear();
		freeRectangles.add(new Rect(0, 0, binWidth, binHeight));

		insert(inputs, outputs, bestMethod);

		if (outputs.size() == 0 && inputs.size() > 0)
		{
			throw new Exception("有图像无法插入!");
		}
		else
		{
			if (outputs.size() > 0)
			{
				ArrayList<Rect> textureRects = new ArrayList<TextureRectPacker.Rect>();
				RectSet texture = new RectSet(binWidth, binHeight, textureRects);

				for (Rect rect : outputs)
				{
					textureRects.add(rect);
				}

				rectSets.add(texture);
			}
			else
			{
				throw new Exception("没有插入任何图像!");
			}

			if (inputs.size() > 0)
			{
				pack(methods);
			}
		}
	}

	/**
	 * 
	 * @param rects
	 * @param dst
	 * @param method
	 */
	private void insert(ArrayList<Rect> rects, ArrayList<Rect> dst, int method)
	{
		while (rects.size() > 0)
		{
			int bestScore1 = Integer.MAX_VALUE;
			int bestScore2 = Integer.MAX_VALUE;
			int bestRectIndex = -1;

			Rect bestNode = null;

			for (int i = 0; i < rects.size(); i++)
			{
				Point xy = new Point(0, 0);

				int score1 = xy.x;
				int score2 = xy.y;

				Rect newNode = scoreRect(rects.get(i).width, rects.get(i).height, method, xy);

				score1 = xy.x;
				score2 = xy.y;

				if (score1 < bestScore1 || (score1 == bestScore1 && score2 < bestScore2))
				{
					bestScore1 = score1;
					bestScore2 = score2;
					bestNode = newNode;
					bestRectIndex = i;
				}
			}

			if (bestRectIndex == -1)
			{
				return;
			}

			placeRect(bestNode);

			Rect old = rects.remove(bestRectIndex);

			old.x = bestNode.x;
			old.y = bestNode.y;
			dst.add(old);
		}
	}

	/**
	 * 放置矩形
	 * 
	 * @param node
	 */
	private void placeRect(Rect node)
	{
		int numRectanglesToProcess = freeRectangles.size();
		for (int i = 0; i < numRectanglesToProcess; i++)
		{
			if (splitFreeNode(freeRectangles.get(i), node))
			{
				freeRectangles.remove(i);
				i--;
				numRectanglesToProcess--;
			}
		}

		pruneFreeList();

		usedRectangles.add(node);
	}

	/**
	 * 评价节点
	 * 
	 * @param width
	 * @param height
	 * @param method
	 * @param xy
	 * @return
	 */
	private Rect scoreRect(int width, int height, int method, Point xy)
	{
		Rect newNode = new Rect(0, 0, 0, 0);

		xy.x = Integer.MAX_VALUE;
		xy.y = Integer.MAX_VALUE;

		switch (method)
		{
			case ShortSideFit:
				newNode = findPositionByShortSideFit(width, height, xy);
				break;
			case BottomLeft:
				newNode = findPositionByBottomLeft(width, height, xy);
				break;
			case ContactPoint:
				newNode = findPositionByContactPoint(width, height, xy);
				xy.x = -xy.x;// ..
				// score1 = -score1; // Reverse since we are minimizing, but for
				// contact point score bigger is better.
				break;
			case LongSideFit:
				newNode = findPositionByLongSideFit(width, height, xy);
				break;
			case AreaFit:
				newNode = findPositionByAreaFit(width, height, xy);
				break;
		}

		// Cannot fit the current rectangle.
		if (newNode.height == 0)
		{
			xy.x = Integer.MAX_VALUE;
			xy.y = Integer.MAX_VALUE;
		}

		return newNode;
	}

	/**
	 * 以ShortSideFit方式定位新节点
	 * 
	 * @param width
	 * @param height
	 * @param xy
	 * @return
	 */
	private Rect findPositionByShortSideFit(int width, int height, Point xy)
	{
		Rect bestNode = new Rect(0, 0, 0, 0);

		int bestShortSideFit = xy.x;
		int bestLongSideFit = xy.y;

		bestShortSideFit = Integer.MAX_VALUE;

		for (int i = 0; i < freeRectangles.size(); ++i)
		{
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width && freeRectangles.get(i).height >= height)
			{
				int leftoverHoriz = Math.abs(freeRectangles.get(i).width - width);
				int leftoverVert = Math.abs(freeRectangles.get(i).height - height);
				int shortSideFit = Math.min(leftoverHoriz, leftoverVert);
				int longSideFit = Math.max(leftoverHoriz, leftoverVert);

				if (shortSideFit < bestShortSideFit || (shortSideFit == bestShortSideFit && longSideFit < bestLongSideFit))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestShortSideFit = shortSideFit;
					bestLongSideFit = longSideFit;
				}
			}

			if (allowRotations && freeRectangles.get(i).width >= height && freeRectangles.get(i).height >= width)
			{
				int flippedLeftoverHoriz = Math.abs(freeRectangles.get(i).width - height);
				int flippedLeftoverVert = Math.abs(freeRectangles.get(i).height - width);
				int flippedShortSideFit = Math.min(flippedLeftoverHoriz, flippedLeftoverVert);
				int flippedLongSideFit = Math.max(flippedLeftoverHoriz, flippedLeftoverVert);

				if (flippedShortSideFit < bestShortSideFit || (flippedShortSideFit == bestShortSideFit && flippedLongSideFit < bestLongSideFit))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestShortSideFit = flippedShortSideFit;
					bestLongSideFit = flippedLongSideFit;
				}
			}
		}

		xy.x = bestShortSideFit;
		xy.y = bestLongSideFit;

		return bestNode;
	}

	/**
	 * 以LongSideFit方式定位新节点
	 * 
	 * @param width
	 * @param height
	 * @param xy
	 * @return
	 */
	private Rect findPositionByLongSideFit(int width, int height, Point xy)
	{
		Rect bestNode = new Rect(0, 0, 0, 0);

		int bestShortSideFit = xy.y;
		int bestLongSideFit = xy.x;

		bestLongSideFit = Integer.MAX_VALUE;

		for (int i = 0; i < freeRectangles.size(); ++i)
		{
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width && freeRectangles.get(i).height >= height)
			{
				int leftoverHoriz = Math.abs(freeRectangles.get(i).width - width);
				int leftoverVert = Math.abs(freeRectangles.get(i).height - height);
				int shortSideFit = Math.min(leftoverHoriz, leftoverVert);
				int longSideFit = Math.max(leftoverHoriz, leftoverVert);

				if (longSideFit < bestLongSideFit || (longSideFit == bestLongSideFit && shortSideFit < bestShortSideFit))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestShortSideFit = shortSideFit;
					bestLongSideFit = longSideFit;
				}
			}

			if (allowRotations && freeRectangles.get(i).width >= height && freeRectangles.get(i).height >= width)
			{
				int leftoverHoriz = Math.abs(freeRectangles.get(i).width - height);
				int leftoverVert = Math.abs(freeRectangles.get(i).height - width);
				int shortSideFit = Math.min(leftoverHoriz, leftoverVert);
				int longSideFit = Math.max(leftoverHoriz, leftoverVert);

				if (longSideFit < bestLongSideFit || (longSideFit == bestLongSideFit && shortSideFit < bestShortSideFit))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestShortSideFit = shortSideFit;
					bestLongSideFit = longSideFit;
				}
			}
		}

		xy.y = bestShortSideFit;
		xy.x = bestLongSideFit;

		return bestNode;
	}

	/**
	 * 以AreaFit方式定位新节点
	 * 
	 * @param width
	 * @param height
	 * @param xy
	 * @return
	 */
	private Rect findPositionByAreaFit(int width, int height, Point xy)
	{
		Rect bestNode = new Rect(0, 0, 0, 0);

		int bestAreaFit = xy.x;
		int bestShortSideFit = xy.y;

		bestAreaFit = Integer.MAX_VALUE;

		for (int i = 0; i < freeRectangles.size(); ++i)
		{
			int areaFit = freeRectangles.get(i).width * freeRectangles.get(i).height - width * height;

			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width && freeRectangles.get(i).height >= height)
			{
				int leftoverHoriz = Math.abs(freeRectangles.get(i).width - width);
				int leftoverVert = Math.abs(freeRectangles.get(i).height - height);
				int shortSideFit = Math.min(leftoverHoriz, leftoverVert);

				if (areaFit < bestAreaFit || (areaFit == bestAreaFit && shortSideFit < bestShortSideFit))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestShortSideFit = shortSideFit;
					bestAreaFit = areaFit;
				}
			}

			if (allowRotations && freeRectangles.get(i).width >= height && freeRectangles.get(i).height >= width)
			{
				int leftoverHoriz = Math.abs(freeRectangles.get(i).width - height);
				int leftoverVert = Math.abs(freeRectangles.get(i).height - width);
				int shortSideFit = Math.min(leftoverHoriz, leftoverVert);

				if (areaFit < bestAreaFit || (areaFit == bestAreaFit && shortSideFit < bestShortSideFit))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestShortSideFit = shortSideFit;
					bestAreaFit = areaFit;
				}
			}
		}

		xy.x = bestAreaFit;
		xy.y = bestShortSideFit;

		return bestNode;
	}

	/**
	 * 以BottomLeft方式定位新节点
	 * 
	 * @param width
	 * @param height
	 * @param xy
	 * @return
	 */
	private Rect findPositionByBottomLeft(int width, int height, Point xy)
	{
		Rect bestNode = new Rect(0, 0, 0, 0);

		int bestX = xy.y;
		int bestY = xy.x;

		bestY = Integer.MAX_VALUE;

		for (int i = 0; i < freeRectangles.size(); ++i)
		{
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width && freeRectangles.get(i).height >= height)
			{
				int topSideY = freeRectangles.get(i).y + height;
				if (topSideY < bestY || (topSideY == bestY && freeRectangles.get(i).x < bestX))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestY = topSideY;
					bestX = freeRectangles.get(i).x;
				}
			}
			if (allowRotations && freeRectangles.get(i).width >= height && freeRectangles.get(i).height >= width)
			{
				int topSideY = freeRectangles.get(i).y + width;
				if (topSideY < bestY || (topSideY == bestY && freeRectangles.get(i).x < bestX))
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestY = topSideY;
					bestX = freeRectangles.get(i).x;
				}
			}
		}

		xy.y = bestX;
		xy.x = bestY;

		return bestNode;
	}

	/**
	 * 以ContactPoint方式定位新节点
	 * 
	 * @param width
	 * @param height
	 * @param xy
	 * @return
	 */
	private Rect findPositionByContactPoint(int width, int height, Point xy)
	{
		Rect bestNode = new Rect(0, 0, 0, 0);

		int bestContactScore = xy.x;

		bestContactScore = -1;

		for (int i = 0; i < freeRectangles.size(); i++)
		{
			// Try to place the rectangle in upright (non-flipped) orientation.
			if (freeRectangles.get(i).width >= width && freeRectangles.get(i).height >= height)
			{
				int score = contactPointScoreNode(freeRectangles.get(i).x, freeRectangles.get(i).y, width, height);
				if (score > bestContactScore)
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = width;
					bestNode.height = height;
					bestContactScore = score;
				}
			}
			if (allowRotations && freeRectangles.get(i).width >= height && freeRectangles.get(i).height >= width)
			{
				int score = contactPointScoreNode(freeRectangles.get(i).x, freeRectangles.get(i).y, height, width);
				if (score > bestContactScore)
				{
					bestNode.x = freeRectangles.get(i).x;
					bestNode.y = freeRectangles.get(i).y;
					bestNode.width = height;
					bestNode.height = width;
					bestContactScore = score;
				}
			}
		}

		xy.x = bestContactScore; // ..

		return bestNode;
	}

	/**
	 * 计算触点评价
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private int contactPointScoreNode(int x, int y, int width, int height)
	{
		int score = 0;

		if (x == 0 || x + width == binWidth)
			score += height;
		if (y == 0 || y + height == binHeight)
			score += width;

		for (int i = 0; i < usedRectangles.size(); ++i)
		{
			if (usedRectangles.get(i).x == x + width || usedRectangles.get(i).x + usedRectangles.get(i).width == x)
				score += commonIntervalLength(usedRectangles.get(i).y, usedRectangles.get(i).y + usedRectangles.get(i).height, y, y + height);
			if (usedRectangles.get(i).y == y + height || usedRectangles.get(i).y + usedRectangles.get(i).height == y)
				score += commonIntervalLength(usedRectangles.get(i).x, usedRectangles.get(i).x + usedRectangles.get(i).width, x, x + width);
		}
		return score;
	}

	// / Returns 0 if the two intervals i1 and i2 are disjoint, or the length of
	// their overlap otherwise.
	private int commonIntervalLength(int i1start, int i1end, int i2start, int i2end)
	{
		if (i1end < i2start || i2end < i1start)
		{
			return 0;
		}
		return Math.min(i1end, i2end) - Math.max(i1start, i2start);
	}

	/**
	 * 裁切自由区域
	 * 
	 * @param freeNode
	 * @param usedNode
	 * @return
	 */
	private boolean splitFreeNode(Rect freeNode, Rect usedNode)
	{
		// Test with SAT if the rectangles even intersect.
		if (usedNode.x >= freeNode.x + freeNode.width || usedNode.x + usedNode.width <= freeNode.x || usedNode.y >= freeNode.y + freeNode.height || usedNode.y + usedNode.height <= freeNode.y)
		{
			return false;
		}

		if (usedNode.x < freeNode.x + freeNode.width && usedNode.x + usedNode.width > freeNode.x)
		{
			// New node at the top side of the used node.
			if (usedNode.y > freeNode.y && usedNode.y < freeNode.y + freeNode.height)
			{
				Rect newNode = new Rect(freeNode.x, freeNode.y, freeNode.width, freeNode.height);
				newNode.height = usedNode.y - newNode.y;
				freeRectangles.add(newNode);
			}

			// New node at the bottom side of the used node.
			if (usedNode.y + usedNode.height < freeNode.y + freeNode.height)
			{
				Rect newNode = new Rect(freeNode.x, freeNode.y, freeNode.width, freeNode.height);
				newNode.y = usedNode.y + usedNode.height;
				newNode.height = freeNode.y + freeNode.height - (usedNode.y + usedNode.height);
				freeRectangles.add(newNode);
			}
		}

		if (usedNode.y < freeNode.y + freeNode.height && usedNode.y + usedNode.height > freeNode.y)
		{
			// New node at the left side of the used node.
			if (usedNode.x > freeNode.x && usedNode.x < freeNode.x + freeNode.width)
			{
				Rect newNode = new Rect(freeNode.x, freeNode.y, freeNode.width, freeNode.height);
				newNode.width = usedNode.x - newNode.x;
				freeRectangles.add(newNode);
			}

			// New node at the right side of the used node.
			if (usedNode.x + usedNode.width < freeNode.x + freeNode.width)
			{
				Rect newNode = new Rect(freeNode.x, freeNode.y, freeNode.width, freeNode.height);
				newNode.x = usedNode.x + usedNode.width;
				newNode.width = freeNode.x + freeNode.width - (usedNode.x + usedNode.width);
				freeRectangles.add(newNode);
			}
		}

		return true;
	}

	private void pruneFreeList()
	{
		// / Go through each pair and remove any rectangle that is redundant.
		for (int i = 0; i < freeRectangles.size(); ++i)
		{
			for (int j = i + 1; j < freeRectangles.size(); ++j)
			{
				if (isContainedIn(freeRectangles.get(i), freeRectangles.get(j)))
				{
					freeRectangles.remove(i);
					i--;
					break;
				}
				if (isContainedIn(freeRectangles.get(j), freeRectangles.get(i)))
				{
					freeRectangles.remove(j);
					j--;
				}
			}
		}
	}

	//
	private Boolean isContainedIn(Rect a, Rect b)
	{
		return a.x >= b.x && a.y >= b.y && a.x + a.width <= b.x + b.width && a.y + a.height <= b.y + b.height;
	}

	// ------------------------------------------------------------------
	//
	// 内部类型
	//
	// ------------------------------------------------------------------

	/**
	 * 点
	 * 
	 * @author chw
	 * 
	 */
	private static class Point
	{
		public int x;
		public int y;

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * 矩形
	 * 
	 * @author chw
	 * 
	 */
	public static class Rect
	{
		public Object data;
		public int x;
		public int y;
		public int width;
		public int height;

		public Rect(int x, int y, int width, int height)
		{
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	/**
	 * 矩形集合
	 * 
	 * @author chw
	 * 
	 */
	public static class RectSet
	{
		private int width;
		private int height;
		private ArrayList<Rect> rects;

		public RectSet(int width, int height, ArrayList<Rect> rects)
		{
			this.width = width;
			this.height = height;
			this.rects = rects;
		}

		public int getWidth()
		{
			return width;
		}

		public int getHeight()
		{
			return height;
		}

		public ArrayList<Rect> getRects()
		{
			return rects;
		}
	}
}