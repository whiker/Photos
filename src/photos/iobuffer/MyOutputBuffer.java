package photos.iobuffer;

import java.io.IOException;
import java.io.OutputStream;

public class MyOutputBuffer
{
	public final static int Buffsize = 8 * 1024;
	
	private byte[] m_buf = new byte[Buffsize];
	
	private OutputStream m_out;
	
	private int m_len;
	
	public void ready(OutputStream out)
	{
		m_out = out;
		m_len = 0;
	}
	
	public void toBuffer(byte[] b) throws Exception
	{
		int len = b.length;
		
		if (m_len + len < Buffsize)
		{
			System.arraycopy(b, 0, m_buf, m_len, len);
			m_len += len;
		}
		else
		{
			int pos = Buffsize - m_len;
			System.arraycopy(b, 0, m_buf, m_len, pos);
			m_out.write(m_buf, 0, Buffsize);
			m_len = 0;
			
			len -= pos;
			while (len > Buffsize)
			{
				m_out.write(b, pos, Buffsize);
				pos += Buffsize;
				len -= Buffsize;
			}
			
			if (len > 0)
			{
				System.arraycopy(b, pos, m_buf, 0, len);
				m_len = len;
			}
		}
	}
	
	public void flush() throws IOException
	{
		if (m_len > 0)
		{
			m_out.write(m_buf, 0, m_len);
			m_len = 0;
		}
	}
	
	public byte[] useBuffer() throws IOException
	{
		flush();
		return m_buf;
	}
	
	public void directWrite(int len) throws IOException
	{
		m_out.write(m_buf, 0, len);
	}
}
