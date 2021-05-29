package com.fun.voznoad.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fun.voznoad.R;
import com.fun.voznoad.model.DialogItem;

public class AdapterDialog extends BaseAdapter{
	private ArrayList<DialogItem> dialogItemLst;
	private LayoutInflater layoutInflater;
	private Context mContext;
	private MyHolder mHolder;
	
	public AdapterDialog(ArrayList<DialogItem> dialogItemLst, Context mContext) {
		super();
		this.dialogItemLst = dialogItemLst;
		this.mContext = mContext;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return dialogItemLst.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertview, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		View view = convertview;
		mHolder = null;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.dialog_adapter, viewGroup, false);
			mHolder = new MyHolder(view);
			view.setTag(mHolder);
		}else{
			mHolder = (MyHolder) view.getTag();
		}
		mHolder.dialogNameTv.setText(dialogItemLst.get(position).getName());
		return view;
	}
	
	public class MyHolder {
		 
		TextView dialogNameTv;
		 
		
		public MyHolder(View view) {
			dialogNameTv = (TextView) view.findViewById(R.id.dialogNameTv);
		}
	}

}
