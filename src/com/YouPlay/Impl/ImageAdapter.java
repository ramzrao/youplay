package com.YouPlay.Impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.util.ServiceException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter implements Parcelable{
	private String clientID;
	private Context mContext;
	public static ArrayList<Bitmap> mThumbIds;
	 private Activity activity;
	    private String[] data;
	    private String[] urlNames;
	    private static LayoutInflater inflater=null;
	    public ImageLoader imageLoader; 
	
	public ImageAdapter(Activity a, String[] d,String[] urlNames){
		
		mThumbIds = new ArrayList<Bitmap>();
		activity = a;
        data=d;
        this.urlNames = urlNames;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		ImageView imageView = new ImageView(mContext);
//		
//		imageView.setImageBitmap(mThumbIds.get(position));
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
//        return imageView;
		 View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.post_item, null);

	        TextView text=(TextView)vi.findViewById(R.id.text);;
	        ImageView image=(ImageView)vi.findViewById(R.id.image);
	        text.setText(urlNames[position]);
	        imageLoader.DisplayImage(data[position], image,"web",true);
	        return vi;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeList(mThumbIds);
	}
	
	public String getText(String url)
	{
		String text = "";
		clientID = "ramTest";
		YouTubeMediaGroup mediaGroup= new YouTubeMediaGroup();
		YouTubeService service = new YouTubeService("AI39si6ZjpE0rF9Rfacp6iCf8ctkLyzJaZ0M4PohRDI5-tApJbszXx-yPL_3nnMvWfokqOmCaPE-tsIIZnsaKEHL5KhF_6lpAA");
		try {
			VideoEntry videoEntry = service.getEntry(new URL("http://gdata.youtube.com/feeds/api/videos/"+url), VideoEntry.class);
			mediaGroup = videoEntry.getMediaGroup();
			text=mediaGroup.getTitle().getPlainTextContent();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

}
