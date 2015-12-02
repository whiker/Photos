package photos.task;

class Worker extends Thread
{
	private Manager m_manager = null;
	private Task    m_task    = null;

	// Manager
	synchronized boolean beUsedBy(Manager m)
	{
		if (m_manager != null)
			return false;
		
		m_manager = m;
		return true;
	}
	
	void release()
	{
		m_manager = null;
	}
	
	synchronized void acceptTask(Task task)
	{
		m_task = task;
		this.notify();
	}

	@Override
	public void run()
	{
		while (!isInterrupted())
		{
			// wait for task
			synchronized (this)
			{ 
				while (m_task == null)
				{
					try { this.wait(); }
					catch (InterruptedException e) { }
				}
			}
			
			// work
			m_task.task();
			m_task = null;
			
			// notify manager
			if (m_manager != null)
				m_manager.taskCompleted(this);
		}
	}
}
