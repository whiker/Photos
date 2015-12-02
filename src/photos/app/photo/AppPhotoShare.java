package photos.app.photo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import photos.app.Share;

public class AppPhotoShare implements Share
{
	private String m_img_set = null;
	
	private ArrayList<byte[]>  m_imgs   = new ArrayList<byte[]>();
	private ArrayList<Integer> m_set_id = new ArrayList<Integer>();
	
	private ReadWriteLock m_lock = new ReentrantReadWriteLock();
	
	String getShareSetname() { return m_img_set; }
	
	@Override
	public byte[] share(String uri)
	{
		int share_id;
		
		try { share_id = Integer.valueOf(uri); }
		catch (Exception e) { return null; }
		
		m_lock.readLock().lock();
		share_id %= m_imgs.size();
		byte[] ret = m_imgs.get(share_id);
		m_lock.readLock().unlock();
		
		return ret;
	}
	
	// Setting
	boolean setShareImg(ImgInfo img)
	{
		if (!img.set_name.equals(m_img_set))
		{
			m_img_set = img.set_name;
			m_set_id.clear();
			
			m_lock.writeLock().lock();
			Collections.fill(m_imgs, null);
			m_imgs.clear();
			m_lock.writeLock().unlock();
		}
		else if (m_set_id.indexOf(img.set_id) >= 0)
			return true;
		
		byte[] buf = getImgBuf(img.fullname);
		if (buf != null)
		{
			m_set_id.add(img.set_id);
			m_lock.writeLock().lock();
			m_imgs.add(buf);
			m_lock.writeLock().unlock();
		}
		return buf != null;
	}
	
	boolean setUnshareImg(ImgInfo img)
	{
		int ind = m_set_id.indexOf(img.set_id);
		if (ind < 0)
			return false;
		
		m_set_id.remove(ind);
		m_lock.writeLock().lock();
		m_imgs.set(ind, null);
		m_imgs.remove(ind);
		m_lock.writeLock().unlock();
		
		return true;
	}
	
	boolean isShare(int set_id)
	{
		if (m_set_id.indexOf(set_id) >= 0)
			return true;
		else
			return false;
	}
	
	private byte[] getImgBuf(String img_name)
	{
		File img = new File(img_name);
		if (img.exists() && img.isFile())
		{
			int size = (int) img.length();
			byte[] buf = new byte[size];
			
			try {
				FileInputStream in = new FileInputStream(img);
				in.read(buf, 0, size);
				in.close();
				return buf;
			}
			catch (IOException e) {
				return null;
			}
		}
		else
			return null;
	}
}
