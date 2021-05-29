package com.fun.voznoad.adapter;

import java.util.List;

import com.fun.voznoad.R;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Page1GroupItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

public class Page1ListAdp2  extends SectionedBaseAdapter {

	private List<Page1GroupItem> items;
	private SharedPreferences sharedPreferences;
	private Context mContext;

	public Page1ListAdp2(List<Page1GroupItem> items,Context mContext) {
		super();
		this.items = items;
		sharedPreferences = mContext.getSharedPreferences("voz_data",mContext.MODE_PRIVATE);
		this.mContext = mContext;
	}

	@Override
	public Object getItem(int section, int position) {
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}
	// Header adapter 's size
	@Override
	public int getSectionCount() {
		return items.size();
	}
	// Child adapter 's size
	@Override
	public int getCountForSection(int section) {
		return items.get(section).getChildItems().size();
	}

	@Override
	public View getItemView(int section, int position, View convertView,ViewGroup parent) {
		LinearLayout layout = null;
		int fontSizePlus= 0;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator.inflate(R.layout.list_item, null);
			fontSizePlus = Integer.parseInt(sharedPreferences.getString("fontsize", "0"));
		} else {
			layout = (LinearLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.textItem)).setText(items.get(section).getChildItems().get(position).getTitle());
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			((TextView) layout.findViewById(R.id.textItem)).setTextColor(Color.parseColor("#bbbbbb"));
			layout.setBackgroundColor(Color.parseColor("#222222"));
		}
		((TextView) layout.findViewById(R.id.textItem)).setTextSize(Constant.pixelsToSp(mContext, ((TextView) layout.findViewById(R.id.textItem)).getTextSize()) +fontSizePlus);
		return layout;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		LinearLayout layout = null;
		int fontSizePlus =0;
		if (convertView == null) {
			fontSizePlus = Integer.parseInt(sharedPreferences.getString("fontsize", "0"));
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.headeritemtext)).setText(items.get(section).getTitle());
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			((TextView) layout.findViewById(R.id.headeritemtext)).setTextColor(Color.parseColor("#cccccc"));
			layout.setBackgroundColor(Color.parseColor("#111111"));
		}
 		((TextView) layout.findViewById(R.id.headeritemtext)).setTextSize(Constant.pixelsToSp(mContext, ((TextView) layout.findViewById(R.id.headeritemtext)).getTextSize()) +fontSizePlus);
		return layout;
	}
	
//	public void initThemeItemTitle(TextView textview,LinearLayout view){
//		textview.setTextColor(Color.parseColor("#FFFFFF"));
//		view.setBackgroundColor(Color.parseColor("#000000"));
//	}
//	
//	public void initThemeHeadTitle(TextView textview,LinearLayout view){
//		textview.setTextColor(Color.parseColor("#FFFFFF"));
//		view.setBackgroundColor(Color.parseColor("#000000"));
//	}
}
