package com.YouPlay.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CategoryAdapter extends ArrayAdapter<HashMap<String, String>> {
 
    Context context;
    private String tag_ID;
    private String tag_Name;
    private String tag_url;
    private String categoryType;
    public ImageLoader imageLoader;
    
    public CategoryAdapter(Context context, int resourceId,String tag_ID,String tag_Name,String tag_url,
            ArrayList<HashMap<String, String>> items,String categoryType) {
        super(context, resourceId, items);
        this.context = context;
        this.tag_ID=tag_ID;
        this.tag_Name = tag_Name;
        this.tag_url = tag_url;
        this.categoryType = categoryType;
        imageLoader = new ImageLoader(context);
    }
 
    /*private view holder class*/
    private class ViewHolder {
    	TextView ID;
    	TextView Name;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        HashMap<String, String> rowItem = getItem(position);
        ImageView image = null;
        boolean isYoutube = true;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
        	holder = new ViewHolder();
        	if(categoryType == "SubCategoryActivity"){
            convertView = mInflater.inflate(R.layout.subcategory_item, null);
            holder.ID = (TextView) convertView.findViewById(R.id.subcategory_id);
            holder.Name = (TextView) convertView.findViewById(R.id.subcategory_name);
            image=(ImageView)convertView.findViewById(R.id.subcategory_image);
            isYoutube = true;
        	}
        	else
        	{
                convertView = mInflater.inflate(R.layout.category_item, null);
                holder.ID = (TextView) convertView.findViewById(R.id.category_id);
                holder.Name = (TextView) convertView.findViewById(R.id.category_name);
                image=(ImageView)convertView.findViewById(R.id.category_image);
                isYoutube = false;
        	}
            convertView.setTag(holder);
        } else
        	{
            	holder = (ViewHolder) convertView.getTag();
            	if(categoryType == "SubCategoryActivity")
            	{
            		image=(ImageView)convertView.findViewById(R.id.subcategory_image);
            		isYoutube = true;
            	}
            	else
            	{
            		image=(ImageView)convertView.findViewById(R.id.category_image);
            		isYoutube = false;
            	}
            	
        	}
 
        holder.ID.setText(rowItem.get(tag_ID));
        holder.Name.setText(rowItem.get(tag_Name));
        imageLoader.DisplayImage(rowItem.get(tag_url), image, "web",isYoutube);

 
        return convertView;
    }
}
