package photos.iobuffer;

import java.io.IOException;
import java.io.InputStream;

public class MyInputBuffer
{
	public  final static int MaxLineLength  = 256;
	private final static int MaxLineNum     = 16;
	private final static int nOnce          = 64;
	private final static int Buffsize       = 8 * 1024;
	
	private byte[] m_buf = new byte[Buffsize];
	
	private int[]  m_lineIndex = new int[MaxLineNum];
	private int    m_lineId, m_lineNum;
	
	private InputStream m_in;
	
	private int m_pos, m_end;
	
	private int m_flag;
	
	public void ready(InputStream in) throws Exception
	{
		m_in = in;
		readMsgHeader();
		m_flag = 1;
	}
	
	private void readMsgHeader() throws Exception
	{
		m_pos = m_end = m_lineId = 0;
		int pos = 0, prev = 0, nRead;
		
		for (int line_count = 0; line_count < MaxLineNum;)
		{
			nRead = m_in.read(m_buf, pos, nOnce);
			m_end += nRead;
			
			for (int i = 0; i < nRead; ++i, ++pos)
			{
				if (m_buf[pos] == '\n')
				{
					m_lineIndex[line_count++] = pos;
					prev = pos - prev;
					if (prev <= 2)
					{
						m_lineNum = line_count;
						return;
					}
					if (prev > MaxLineLength)
					{
						throw new Exception
						("http line length > " + MaxLineLength);
					}
					prev = pos;
				}
			}
			
			if (nRead < nOnce)
				throw new Exception("client close");
		}
		
		throw new Exception("http line num > " + MaxLineNum);
	}
	
	public int getLine(byte[] line)
	{
		if (m_lineId >= m_lineNum)
			return 0;
		
		int pos = m_lineIndex[m_lineId++];
		int length = pos - m_pos;
		
		if (length > 1)
		{
			if (m_buf[pos-1] == '\r')
				--length;
			System.arraycopy(m_buf, m_pos, line, 0, length);
		}
		else
			length = 0;
		
		m_pos = pos + 1;
		return length;
	}
	
	public int directRead() throws IOException
	{
		if (m_flag > 1) return -1;
		if (m_pos != 0) adjust();
		
		int n = m_in.read(m_buf, m_end, Buffsize-m_end);
		if (n < 0)
		{
			m_flag = 2;
			return -1;
		}
		else
			return m_end + n;
	}
	
	public byte[] getBuf()
	{
		m_pos = m_end = 0;
		return m_buf;
	}
	
	private void adjust()
	{
		if (m_pos < m_end)
		{
			int len = m_end - m_pos;
			byte[] temp = new byte[len];
			
			System.arraycopy(m_buf, m_pos, temp, 0, len);
			System.arraycopy(temp, 0, m_buf, 0, len);
			
			m_pos = 0;
			m_end = len;
		}
		else
			m_pos = m_end = 0;
	}
}
