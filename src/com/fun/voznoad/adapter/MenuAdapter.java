package com.fun.voznoad.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fun.voznoad.R;
import com.fun.voznoad.model.SlideMenuItem;

public class MenuAdapter extends BaseAdapter {
	Context mContext;
	ArrayList<SlideMenuItem> menuitemLst;
	LayoutInflater layoutInflater;

	public MenuAdapter(Context mContext, ArrayList<SlideMenuItem> menuitemLst) {
		super();
		this.mContext = mContext;
		this.menuitemLst = menuitemLst;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		
			View view = convertView;
			if (view ==null) {
				view = layoutInflater.inflate(R.layout.adapter_menu,viewGroup, false);
			}
			TextView menuTitle = (TextView) view.findViewById(R.id.menuTitle);
			menuTitle.setText(menuitemLst.get(position).getName());
			return view ;
	}

	public int getCount() {
		return menuitemLst.size();
	}

	public Object getItem(int arg0) {
		return menuitemLst.get(arg0);
	}

	public long getItemId(int arg0) {
		return 0;
	}
}
