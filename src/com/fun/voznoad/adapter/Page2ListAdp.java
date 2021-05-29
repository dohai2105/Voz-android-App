package com.fun.voznoad.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fun.voznoad.R;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Page2Item;

public class Page2ListAdp extends BaseAdapter {
	
	LayoutInflater layoutInflater;
	ArrayList<Page2Item> threadList;
	Context mContext;
	ViewHolder mHolder;
	private SharedPreferences sharedPreferences;
	
	public Page2ListAdp(ArrayList<Page2Item> threadList, Context mContext) {
		super();
		this.threadList = threadList;
		this.mContext = mContext;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sharedPreferences = mContext.getSharedPreferences("voz_data",mContext.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return threadList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup viewGroup) {
		View view = convertview;
		int fontSizePlus =0;
		if (view == null) {
			fontSizePlus = Integer.parseInt(sharedPreferences.getString("fontsize", "0"));
			view = layoutInflater.inflate(R.layout.adapter_page2, viewGroup, false);
			mHolder = new ViewHolder(view);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}	    
		
		
		mHolder.titleTv.setText(threadList.get(position).getTitle());
		mHolder.subTitleTv.setText(Html.fromHtml(threadList.get(position).getUser()));
		mHolder.replyTv.setText(threadList.get(position).getReply());
		mHolder.timeTv.setText(threadList.get(position).getTime());
		mHolder.userTv.setText(threadList.get(position).getUser());
		if (threadList.get(position).isSticky==true) {
			mHolder.titleTv.setTextColor(Color.parseColor("#ffff0000"));
			if (sharedPreferences.getString("theme", "null").equals("black")) {
				initTheme(view,true);
			}
		}else{
			mHolder.titleTv.setTextColor(Color.parseColor("#ff23497c"));
			if (sharedPreferences.getString("theme", "null").equals("black")) {
				initTheme(view,false);
			}
		}
		
		mHolder.titleTv.setTextSize(Constant.pixelsToSp(mContext,mHolder.titleTv.getTextSize()) +fontSizePlus);
		mHolder.subTitleTv.setTextSize(Constant.pixelsToSp(mContext,mHolder.subTitleTv.getTextSize()) +fontSizePlus);
	
		return view;
	}
	
	public void initTheme(View view,Boolean isSteaky){
		mHolder.subTitleTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.replyTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.timeTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.userTv.setTextColor(Color.parseColor("#bbbbbb"));
		view.setBackgroundColor(Color.parseColor("#222222"));
		if (isSteaky) {
			mHolder.titleTv.setTextColor(Color.parseColor("#ffad26"));
		}else {
			mHolder.titleTv.setTextColor(Color.parseColor("#bbbbbb"));
		}
	}
	
	public class ViewHolder {
		TextView titleTv;
		TextView subTitleTv;
		TextView replyTv;
		TextView timeTv;
		TextView userTv;
		public ViewHolder(View v) {
			titleTv = (TextView) v.findViewById(R.id.titleTv);
			subTitleTv = (TextView) v.findViewById(R.id.subTitleTv);
			replyTv = (TextView) v.findViewById(R.id.replyTv);
			timeTv = (TextView) v.findViewById(R.id.timeTv);
			userTv = (TextView) v.findViewById(R.id.userTv);
		}
	}

}
