package photos.app.photo;

public class ImgInfo
{
	public String fullname;
	public String set_name;
	public int    set_id;
	
	public ImgInfo(String uri)
	{
		int ind = uri.indexOf("/");
		fullname = AppPhoto.ImgSetPath + uri + ".jpg";
		set_name = uri.substring(0, ind);
		set_id = Integer.valueOf(uri.substring(ind+1));
	}
}
