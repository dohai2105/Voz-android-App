package com.fun.voznoad.adapter;

import java.util.ArrayList;

import com.fun.voznoad.R;
import com.fun.voznoad.adapter.Page2ListAdp.ViewHolder;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Icon;
import com.fun.voznoad.model.InboxItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InboxAdapter extends BaseAdapter {
	private ArrayList<InboxItem> listInbox;
    private Context mContext;
	private SharedPreferences sharedPreferences;
	ViewHolder mHolder;
	LayoutInflater layoutInflater;
	
	public InboxAdapter(ArrayList<InboxItem> listInbox, Context mContext) {
		super();
		this.listInbox = listInbox;
		this.mContext = mContext;
		sharedPreferences = mContext.getSharedPreferences("voz_data",mContext.MODE_PRIVATE);
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listInbox.size();
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
			view = layoutInflater.inflate(R.layout.adapter_inbox, viewGroup, false);
			mHolder = new ViewHolder(view);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}	  
		
		mHolder.titleTv.setText(listInbox.get(position).getTitle());
		mHolder.dateTv.setText(listInbox.get(position).getDate());
		mHolder.timeTv.setText(listInbox.get(position).getTime());
		mHolder.userTv.setText(listInbox.get(position).getUser());
		
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme(view);
		}
		mHolder.titleTv.setTextSize(Constant.pixelsToSp(mContext,mHolder.titleTv.getTextSize()) +fontSizePlus);
		return view;
	}
	
	public void initTheme(View view){
		mHolder.dateTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.timeTv.setTextColor(Color.parseColor("#bbbbbb"));
		mHolder.userTv.setTextColor(Color.parseColor("#bbbbbb"));
		view.setBackgroundColor(Color.parseColor("#222222"));
		mHolder.titleTv.setTextColor(Color.parseColor("#bbbbbb"));
	}
	
	
	public class ViewHolder {
		TextView titleTv;
		TextView dateTv;
		TextView timeTv;
		TextView userTv;
		public ViewHolder(View v) {
			titleTv = (TextView) v.findViewById(R.id.titleTv);
			dateTv = (TextView) v.findViewById(R.id.replyTv);
			timeTv = (TextView) v.findViewById(R.id.timeTv);
			userTv = (TextView) v.findViewById(R.id.userTv);
		}
	}

}
