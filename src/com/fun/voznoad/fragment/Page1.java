package com.fun.voznoad.fragment;

import java.util.ArrayList;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView.OnItemClickListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fun.voznoad.R;
import com.fun.voznoad.adapter.Page1ListAdp2;
import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Page1ChildItem;
import com.fun.voznoad.model.Page1GroupItem;

public class Page1 extends Fragment{
	private View view;
	private OnNotifyMainListener notifyMainListener;
	//private Page1ListAdp expandListAdp;
	private ArrayList<Page1GroupItem> groupItems; 
	private PinnedHeaderListView listView ;
	Page1ListAdp2 sectionListAdp ;
	private TextView f17QuickTv;
	private TextView f33QuickTv;
	private TextView f47QuickTv;
	private TextView f31QuickTv;
	private LinearLayout quickPage;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	public void setNotifyMain (OnNotifyMainListener x){
		notifyMainListener = x;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_page1, container, false);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initUI();
		initCONTROL();
		initQUICKMENU();
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
		
	}
	
	public void initTheme(){
		f17QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		f33QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		f47QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		f31QuickTv.setTextColor(Color.parseColor("#bbbbbb"));
		quickPage.setBackgroundColor(Color.parseColor("#000000"));
		(view.findViewById(R.id.splitView)).setBackgroundColor(Color.parseColor("#ffffff"));
		view.setBackgroundColor(Color.parseColor("#000000"));
	}
	
	public void initUI(){
	    listView = (PinnedHeaderListView) view.findViewById(R.id.pinnedListView);
	    initData();
	    sectionListAdp = new Page1ListAdp2(groupItems, getActivity());
	    listView.setAdapter(sectionListAdp);
	    f17QuickTv = (TextView) view.findViewById(R.id.f17QuickTv);
	    f33QuickTv = (TextView) view.findViewById(R.id.f33QuickTv);
	    f47QuickTv = (TextView) view.findViewById(R.id.f47QuickTv);
	    f31QuickTv = (TextView) view.findViewById(R.id.f31QuickTv);
		quickPage = (LinearLayout) view.findViewById(R.id.quickPage);
	}
	
	public void initQUICKMENU(){
		sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
		editor = sharedPreferences.edit();
		String quick1 = sharedPreferences.getString("quick1", "17");
		String quick2 = sharedPreferences.getString("quick2", "33");
		String quick3 = sharedPreferences.getString("quick3", "47");
		String quick4 = sharedPreferences.getString("quick4", "31");
		
		f17QuickTv.setText("f"+quick1);
		f33QuickTv.setText("f"+quick2);
		f47QuickTv.setText("f"+quick3);
		f31QuickTv.setText("f"+quick4);
	}
	
	public void initData(){
		String[] group_array = getResources().getStringArray(R.array.groupname);
		String[] item_array = getResources().getStringArray(R.array.childname);
		String[] link_array = getResources().getStringArray(R.array.childlink);
		groupItems = new ArrayList<Page1GroupItem>();
		 
		for(int i = 0 ;i<group_array.length;i++){
			ArrayList<Page1ChildItem> childItems = new ArrayList<Page1ChildItem>();
			Page1GroupItem groupItem = new Page1GroupItem();
			groupItem.setTitle(group_array[i].trim());
			String[] child_array = item_array[i].split(",");
			String[] child_link_array = link_array[i].split(",");
			for(int j = 0 ;j<child_array.length;j++){
				Page1ChildItem childItem = new Page1ChildItem();
				childItem.setTitle(child_array[j].trim());
				childItem.setLink(child_link_array[j].trim());
				childItem.setView("1");
				childItems.add(childItem);
			}
			groupItem.setChildItems(childItems);
			groupItems.add(groupItem);
		}
		notifyMainListener.onSetActionBarTitle("Voz");
	}
	
	public void initCONTROL(){
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,int section, int position, long id) {
				String url = groupItems.get(section).getChildItems().get(position).getLink();
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				bundle.putString("title", groupItems.get(section).getChildItems().get(position).getTitle());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(groupItems.get(section).getChildItems().get(position).getTitle());
			}

			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view,int section, long id) {
			}
		});
		
		
		listView.setOnScrollListener(new OnScrollListener() {
			private int mLastFirstVisibleItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)  {
			}
			
			@Override
			public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int total = firstVisibleItem + visibleItemCount;
				if (mLastFirstVisibleItem < firstVisibleItem) {
					scrollDown();
				}
				if (mLastFirstVisibleItem > firstVisibleItem) {
					scrollUp();
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});
		
		f17QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f17QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f17QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f17QuickTv.getText().toString());
			}
			
		});
		
		f17QuickTv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick1",f17QuickTv);
				return true;
			}
		});
		
		f33QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f33QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f33QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f33QuickTv.getText().toString());
			}
		});
		
		f33QuickTv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick2",f33QuickTv);
				return true;
			}
		});
	
		f47QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f47QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f47QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f47QuickTv.getText().toString());
			}
		});
		
		f47QuickTv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick3",f47QuickTv);
				return true;
			}
		});
		
		f31QuickTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("url", "http://vozforums.com/forumdisplay.php?f="+f31QuickTv.getText().toString().replace("f", ""));
				bundle.putString("title", f31QuickTv.getText().toString());
				Page2 ft = new Page2();
				ft.setArguments(bundle);
				ft.setNotifyMain(notifyMainListener);
				notifyMainListener.replaceFragment(ft);
				notifyMainListener.onSetActionBarTitle(f31QuickTv.getText().toString());
			}
		});
		
		f31QuickTv.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				initQuickMenuCreate("quick4",f31QuickTv);
				return true;
			}
		});
	}
	
	public void initQuickMenuCreate(final String quickName, final TextView quickTv){
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("�?i�?n quick menu:");
		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						String value = input.getText().toString();
						quickTv.setText("f"+value);
						editor.putString(quickName, value);
						editor.commit();
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
						dialog.cancel();
					}
				});
		alert.show();
	}
	
	public void scrollDown(){
		quickPage.setVisibility(View.GONE);
	}
	
	public void scrollUp(){
		quickPage.setVisibility(View.VISIBLE);
	}
}
