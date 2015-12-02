package photos.server.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import photos.app.AppCenter;
import photos.iobuffer.MyOutputBuffer;

public class HttpResponse
{
	private final static byte[] state200 = "HTTP/1.1 200 OK".getBytes();
	private final static byte[] state404 = "HTTP/1.1 404 NotFound".getBytes();
	private final static byte[] headerCookie = "\nSet-Cookie: a=".getBytes();
	private final static byte[] headerContentLength = "\nContent-Length: ".getBytes();
	private final static byte[] headerEnd = "\n\n".getBytes();
	
	private MyOutputBuffer m_out = new MyOutputBuffer();
	
	public int cookie;
	
	public void ready(OutputStream out)
	{
		m_out.ready(out);
		cookie = -1;
	}
	
	public void send200() throws Exception
	{
		out200Header(0, true);
	}
	
	public void send404() throws Exception
	{
		m_out.toBuffer(state404);
		m_out.toBuffer(headerEnd);
		m_out.flush();
	}
	
	public void send(byte[] data, long length) throws Exception
	{
		out200Header(length, false);
		
		if (length > 0)
			m_out.toBuffer(data);
		
		m_out.flush();
	}
	
	public void sendFile(String filename) throws Exception
	{
		File file = new File(AppCenter.AppPath + filename);
		
		if (file.exists() && file.canRead())
		{
			out200Header(file.length(), true);
			
			byte[] buf = m_out.useBuffer();
			int len = buf.length;
			
			FileInputStream fin = new FileInputStream(file);
			for (int n; (n = fin.read(buf, 0, len)) > 0;)
				m_out.directWrite(n);
			fin.close();
		}
		else
			throw new IOException("file no exists " +
					file.getAbsolutePath());
	}
	
	private void out200Header(long length, boolean isFlush)
			throws Exception
	{
		m_out.toBuffer(state200);
		
		if (cookie > 0)
		{
			m_out.toBuffer(headerCookie);
			m_out.toBuffer(new Integer(cookie).toString().getBytes());
		}
		
		if (length > 0)
		{
			m_out.toBuffer(headerContentLength);
			m_out.toBuffer(new Long(length).toString().getBytes());
		}
		
		m_out.toBuffer(headerEnd);
		if (isFlush)
			m_out.flush();
	}
}
