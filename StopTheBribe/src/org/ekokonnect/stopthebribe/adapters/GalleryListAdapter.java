package org.ekokonnect.stopthebribe.adapters;

import java.util.ArrayList;

import org.ekokonnect.stopthebribe.R;
import org.ekokonnect.stopthebribe.models.Gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GalleryListAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<Gallery> data;
    private static LayoutInflater inflater;
	
	public GalleryListAdapter(Activity a, ArrayList<Gallery> d) {
		activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.gallery_item, null);
                
        ImageView thumb =(ImageView)vi.findViewById(R.id.gallery_item_imageview); // thumb image
        
        Gallery galleryItem = data.get(position);
        
        thumb.setImageBitmap(galleryItem.thumb);
        
        return vi;
	}

}
