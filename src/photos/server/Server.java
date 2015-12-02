package photos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread
{
	private ServiceCenter m_service;
	private ServerSocket  m_sock = null;
	
	private int m_num;
	public Server(int n) { m_num = n; }
	
	@Override
	public void run()
	{
		m_service = new ServiceCenter(m_num);
		
		// bind socket
		try {
			m_sock = new ServerSocket(8080, m_num);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// listen socket
		while (true)
		{
			try {
				Socket conn = m_sock.accept();  // throw Exception when "close m_sock"
				m_service.handle(conn);
			}
			catch (IOException e) {
				String err = e.toString().split(": ")[1];
				if (err.equals("Socket closed"))
					return;
			}
		}
	}
	
	public void exit()
	{
		try {
			m_sock.close();  // make "m_sock.accept()" return
			this.join();
		} catch (Exception e) { }
		
		m_service.exit();
	}
}
