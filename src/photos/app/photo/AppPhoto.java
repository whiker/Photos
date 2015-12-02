package photos.app.photo;

import java.io.File;
import java.io.FileOutputStream;

import photos.app.App;
import photos.app.AppCenter;
import photos.app.Share;
import photos.server.http.HttpRequest;
import photos.server.http.HttpResponse;

public class AppPhoto implements App
{
	static final String ImgSetPath =
			AppCenter.AppPath + "photo/imgs/";
	
	private HttpRequest  m_req;
	private HttpResponse m_rep;
	
	
	// share ---------------------------------------------------
	private AppPhotoShare m_share = new AppPhotoShare();
	
	@Override
	public String getAppName() { return "photo"; }
	
	@Override
	public Share getShare() { return m_share; }
	
	
	// service -------------------------------------------------
	@Override
	public void service(HttpRequest req, HttpResponse rep)
			 throws Exception
	{
		String uri = req.uri;
		m_req = req;
		m_rep = rep;
		
		if (req.isStatic)
			rep.sendFile(uri);
		else if (uri.startsWith("list", 6))
			imgsetList();
		else if (uri.startsWith("open", 6))
			imgsetOpen();
		else if (uri.startsWith("create", 6))
			createNewImgset();
		else if (uri.startsWith("upload", 6))
			uploadImg();
		else if (uri.startsWith("isshare", 6))
			isShare();
		else if (uri.startsWith("share", 6))
			shareImg(true);
		else if (uri.startsWith("unshare", 6))
			shareImg(false);
		else if (uri.startsWith("delete", 6))
			deleteImg();
		else
			rep.sendFile("photo/photo.html");
	}
	
	// list
	private void imgsetList() throws Exception
	{
		File dir = new File(ImgSetPath);
		if (dir.exists() && dir.isDirectory())
		{
			StringBuilder str = new StringBuilder();
			for (String sub_dir : dir.list())
				str.append(sub_dir).append("\n");
			byte[] content = str.toString().getBytes();
			m_rep.send(content, content.length-1);
		}
		else
			m_rep.send404();
	}
	
	// open img set
	private void imgsetOpen() throws Exception
	{
		File img_set = new File(ImgSetPath + m_req.uri.substring(11));
		if (img_set.exists() && img_set.isDirectory())
		{
			StringBuilder str = new StringBuilder();
			str.append(img_set.list().length);
			str.append("\n");
			str.append(m_share.getShareSetname());
			byte[] content = str.toString().getBytes();
			m_rep.send(content, content.length);
		}
		else
			m_rep.send404();
	}
	
	// create new img set
	private void createNewImgset() throws Exception
	{
		File new_set = new File(ImgSetPath + m_req.uri.substring(13));
		if (!new_set.exists() && new_set.mkdirs())
			m_rep.send200();
		else
			m_rep.send404();
	}
	
	// upload img
	private void uploadImg() throws Exception
	{
		String path = ImgSetPath + m_req.uri.substring(13);
		System.out.println("在服务器上的保存位置 : " + path);
		File set_dir = new File(path);
		if (set_dir.exists() && set_dir.isDirectory())
		{
			int n = m_req.getContent();
			if (n > 0)
			{
				int img_num = set_dir.list().length;
				FileOutputStream out = new FileOutputStream
					(path + "/" + img_num + ".jpg");
				
				byte[] temp = m_req.content;
				int t1 = -1, t2 = 0;  // t1是前一\n位置, t2是当前\n位置
				for (; t2 < n; t2++)
				{
					if (temp[t2] == '\n')
					{
						if (t1 > 0 && t2-t1 <= 2)
							break;
						t1 = t2;
					}
				}
				t2++;  // 再跳过一个\n
				
				// System.out.println(new String(temp, 0, t2));
				
				out.write(temp, t2, n-t2);
				
				while ((n = m_req.getContent()) > 0)
					out.write(m_req.content, 0, n);
				out.close();
			}
		}
		else
			m_rep.send404();
	}
	
	// check this img_set_id is shared
	private void isShare() throws Exception
	{
		int set_id = Integer.valueOf(m_req.uri.substring(14));
		if (m_share.isShare(set_id))
			m_rep.send200();
		else
			m_rep.send404();
	}
	
	// share img
	private void shareImg(boolean share) throws Exception
	{
		int start = share ? 12 : 14;
		String uri = m_req.uri.substring(start);  // [img_set/img_set_id]
		ImgInfo img = new ImgInfo(uri);
		
		if ( ( share && m_share.setShareImg(img))   ||
			 (!share && m_share.setUnshareImg(img)) )
			m_rep.send200();
		else
			m_rep.send404();
	}
	
	// delete img
	private void deleteImg() throws Exception
	{
		String uri = m_req.uri.substring(13);  // [img_set/img_set_id]
		ImgInfo img = new ImgInfo(uri);
		
		File f = new File(img.fullname);
		if (f.exists())
		{
			f.delete();
			m_share.setUnshareImg(img);
		}
		m_rep.send200();
	}
}
