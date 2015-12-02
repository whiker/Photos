package photos.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import photos.app.AppCenter;
import photos.server.http.*;
import photos.task.Task;

public class ServiceTask implements Task
{
	public int     id = 0;
	public boolean isIdle = true;
	
	public Socket        m_sock;
	public InputStream   m_input;
	public OutputStream  m_output;
	
	private AppCenter    m_app = AppCenter.getInstance();
	private HttpRequest  m_req = new HttpRequest();
	private HttpResponse m_rep = new HttpResponse();
	
	// ready
	public boolean ready(Socket conn)
	{
		try {
			isIdle   = false;
			m_sock   = conn;
			m_input  = conn.getInputStream();
			m_output = conn.getOutputStream();
			
			m_sock.setSoTimeout(2000);
			return true;
		}
		catch (IOException e) {
			closeConnect();
			isIdle = true;
			return false;
		}
	}
	
	@Override
	public void task()
	{
		try
		{
			System.out.println(String.format("%d>> 0 ", id));
			m_req.ready(m_input);
			m_rep.ready(m_output);
			System.out.println(String.format("%d>> uri:%s", id, m_req.uri));
			m_app.service(m_req, m_rep);
		}
		catch (Exception e)
		{ printException (e); }
		finally
		{
			closeConnect();
			isIdle = true;
			System.out.println(String.format("%d>> 1", id));
		}
	}
	
	private void closeConnect()
	{
		try { m_sock.close(); }
		catch (IOException e)
		{ printException (e); }
	}
	
	private void printException(Exception e)
	{
		String error = e.toString().split("\n")[0];
		System.out.println(String.format("%d>> %s", id, error));
	}
}
