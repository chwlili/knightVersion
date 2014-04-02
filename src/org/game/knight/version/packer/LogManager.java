package org.game.knight.version.packer;

public class LogManager extends LogList
{
	private Log currLog;
	private LogList currLogSet;

	private Log currState;

	private boolean cancel;

	/**
	 * 构造函数
	 */
	public LogManager()
	{
		super(null, 0, "", "");
	}

	/**
	 * 获取当前行
	 * 
	 * @return
	 */
	public Log getCurrLog()
	{
		if (currState != null && currState.getParent() != null) { return currState; }
		return currLog;
	}

	/**
	 * 开始任务
	 * 
	 * @param name
	 */
	public void beginTask(String name)
	{
		resetState();
		currLogSet = (LogList) this.addChild(new LogList(this, 0, name, ""));
		currLog = currLogSet;
	}

	/**
	 * 开始日志集
	 * 
	 * @param name
	 */
	public void beginLogSet(int type, String name, String path)
	{
		resetState();
		currLogSet = (LogList) currLogSet.addChild(new LogList(currLogSet, type, name, path));
		currLog = currLogSet;
	}

	/**
	 * 停止日志集
	 */
	public void endLogSet()
	{
		resetState();
		currLogSet = currLogSet.getParent();
	}

	/**
	 * 停止任务
	 */
	public void endTask()
	{
		resetState();
		currLogSet = this;
	}

	/**
	 * 输出日志信息
	 * 
	 * @param text
	 */
	public void log(String text, String path)
	{
		resetState();
		currLog = currLogSet.addChild(new Log(currLogSet, 1, text, path));
	}

	/**
	 * 输出警告信息
	 * 
	 * @param text
	 * @param path
	 */
	public void warning(String text, String path)
	{
		resetState();
		currLog = currLogSet.addChild(new Log(currLogSet, 2, text, path));
	}

	/**
	 * 输出错误信息
	 * 
	 * @param text
	 */
	public void error(String text, String path)
	{
		resetState();
		currLog = currLogSet.addChild(new Log(currLogSet, 3, text, path));
	}

	/**
	 * 输出进度信息
	 */
	public void progress(String text, String path)
	{
		if (currState == null)
		{
			currState = new Log(currLogSet, 1, text, path);
			currLogSet.addChild(currState);
		}

		currState.setText(text);
		currState.setPath(path);
	}

	private void resetState()
	{
		if (currState != null)
		{
			currState.getParent().removeChild(currState);
			currState.setParent(null);
			currState = null;
		}
	}

	/**
	 * 取消
	 */
	public void cancel()
	{
		if (!cancel)
		{
			cancel = true;
		}
	}

	/**
	 * 是否已取消
	 * 
	 * @return
	 */
	public boolean isCancel()
	{
		return cancel;
	}
}
