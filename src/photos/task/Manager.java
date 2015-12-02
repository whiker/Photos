package photos.task;

import java.util.concurrent.LinkedBlockingDeque;

public class Manager
{
	private LinkedBlockingDeque<Worker> m_queue;
	
	public Manager(int n)
	{
		m_queue = new LinkedBlockingDeque<Worker>(n);
	}
	
	// 雇佣 解雇 Worker ---------------------------------------
	public void hireWorker()
	{
		Worker w = new Worker();
		w.beUsedBy(this);
		w.start();
		add_idle(w);
	}
	
	public boolean fireWorker()
	{
		Worker w = get_idle();
		if (w == null)
			return false;
		
		w.interrupt();
		return true;
	}
	
	// 添加 释放 Worker ---------------------------------------
	public boolean addWorker(Worker w)
	{
		if (w.beUsedBy(this))
		{
			add_idle(w);
			return true;
		}
		else  // 传入的Worker被其他Manager占用as
			return false;
	}
	
	public Worker releaseWorker()
	{
		Worker w = get_idle();
		w.release();
		return w;
	}
	
	public Worker tryReleaseWorker()
	{
		Worker w = try_get_idle();
		if (w != null)
			w.release();
		return w;
	}
	
	// 接受任务 Task ------------------------------------------
	public void acceptTask(Task task)
	{
		Worker w = get_idle();
		w.acceptTask(task);
	}
	
	public boolean tryAcceptTask(Task task)
	{
		Worker w = try_get_idle();
		if (w == null) return false;
		w.acceptTask(task);
		return true;
	}
	
	// Worker完成任务 -----------------------------------------
	public void taskCompleted(Worker w)
	{
		add_idle(w);
	}
	
	// 阻塞队列 -----------------------------------------------
	private void add_idle(Worker w)
	{
		try {
			m_queue.put(w);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	private Worker get_idle()
	{
		try {
			Worker w = m_queue.take();
			return w;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
	
	private Worker try_get_idle()
	{
		Worker w = m_queue.poll();
		return w;
	}
}
