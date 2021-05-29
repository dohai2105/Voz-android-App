
package com.fun.voznoad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

import com.fun.voznoad.R;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Icon;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class IconAdapter extends BaseAdapter
{

    private ArrayList<Icon> listEmo;
    private Context mContext;

    public IconAdapter(Context context, ArrayList<Icon> arraylist)
    {
        mContext = context;
        listEmo = arraylist;
    }

    public int getCount()
    {
        return listEmo.size();
    }

    public Object getItem(int i)
    {
        return listEmo.get(i);
    }

    public long getItemId(int i)
    {
        return 0L;
    }

    public View getView(int i, View convertview, ViewGroup viewgroup)
    {
    	View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_icon, viewgroup, false);
    	ImageView iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
    	ImageLoader imgLoader = ImageLoader.getInstance();
        imgLoader.displayImage("http://vozforums.com/"+listEmo.get(i).getLink(),iconImageView,getObtion());
    	return view;
        
//        ImageView imageview = new ImageView(mContext);
//        ImageLoader imgLoader = ImageLoader.getInstance();
//        imgLoader.displayImage("http://vozforums.com/"+listEmo.get(i).getLink(),imageview,getObtion());
//        imageview.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
//        int j = Constant.convertDpToPx(mContext.getApplicationContext(), 32);
//        imageview.setLayoutParams(new android.widget.AbsListView.LayoutParams(j, j));
//        return imageview;
    }
    
	private DisplayImageOptions getObtion() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();
		return options;
	}
}
