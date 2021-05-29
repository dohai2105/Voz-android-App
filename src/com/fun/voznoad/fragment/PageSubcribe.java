package com.fun.voznoad.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.bool;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fun.voznoad.R;
import com.fun.voznoad.ViewPagerActivity;
import com.fun.voznoad.adapter.Page2ListAdp;
import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.Page2Item;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class PageSubcribe extends Fragment{
	private View view;
	private String url;
	private String f;
	private boolean isLoading;
	private int currentpage;
	private ArrayList<Page2Item> threadList;
	private PullToRefreshListView mPullRefreshListView;
	private Page2ListAdp threadListAdp;
	private View mLoadMoreFooter;
	private OnNotifyMainListener notifyMainListener;
	private SharedPreferences sharedPreferences;
	
	public void setNotifyMain (OnNotifyMainListener x){
		notifyMainListener = x;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_page2, container, false);
		mLoadMoreFooter = inflater.inflate(R.layout.footer_loadmore, null);
		(view.findViewById(R.id.quickPage)).setVisibility(View.GONE);
		return view;
	}
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		url = "http://vozforums.com/subscription.php?do=viewsubscription&daysprune=-1&folderid=all";
		initUI();
		initCONTROL();
		isLoading = true;
		currentpage=1;
		new GetDataTask().execute();
		sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
	}
	
	public void initTheme(){
		mLoadMoreFooter.setBackgroundColor(Color.parseColor("#191919"));
	}

	private void initCONTROL() {
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				isLoading= true;
				currentpage=1;		
				threadList.clear();
				new GetDataTask().execute();
			}
		});
		
		ListView listview = mPullRefreshListView.getRefreshableView();
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
				AlertDialog builder = new AlertDialog.Builder(getActivity())
				.setTitle("Bạn có muốn unsubcribe?")
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								new UnSubcribeTask().execute(threadList.get(position-1).getLink().split("=")[1]);
								threadList.remove(position-1);
								return;
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
							}
						}).show();
				return false;
			}
		});
		
		
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
					 Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
					 intent.putExtra("url",  threadList.get(position-1).getLink());
					 intent.putExtra("totalpage", threadList.get(position-1).getPage());
					 intent.putExtra("title", threadList.get(position-1).getTitle());
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 startActivity(intent);
			}
		});
	}

	private void initUI() {
		threadList = new ArrayList<Page2Item>();
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		threadListAdp = new Page2ListAdp(threadList, getActivity());
		mPullRefreshListView.setAdapter(threadListAdp);
		final ListView lv = mPullRefreshListView.getRefreshableView();
		lv.addFooterView(mLoadMoreFooter);
		notifyMainListener.onSetActionBarTitle("Subscriptions");
	}
	
	
	public class GetDataTask extends AsyncTask<Void, Void, Void>{
		private String page;
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Document doc = Jsoup.connect(url).timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
				Element formE = doc.select("body > div > div.page > div > table > tbody > tr > td > form > table.tborder").get(1);
				Elements listthread = formE.select("tr");
				for(int i =2; i<listthread.size()-1;i++){

					Element dataContainer = listthread.get(i).select("td:nth-child(2)").first();
 					Elements divElement = dataContainer.select("div > a");
					String reply = "Trả l�?i: "+listthread.get(i).select("td:nth-child(4)").first().text();
					String time = listthread.get(i).select("td:nth-child(3)").first().text();
					if (divElement.isEmpty()) {
						dataContainer = listthread.get(i).select("td:nth-child(3)").first();
						divElement = dataContainer.select("div > a");
						reply = "Trả l�?i: "+listthread.get(i).select("td:nth-child(5)").first().text();
						time = listthread.get(i).select("td:nth-child(4)").first().text();
					}
					try{
						page = dataContainer.select("span.smallfont > a").last().attr("href").split("page=")[1];
					}catch(Exception e){
						page = "1";
					}
 					int indexA = 0;
					if (divElement.size()>1) {
						indexA=1;
					}
					String link = divElement.get(indexA).attr("href");
					String title = divElement.get(indexA).text().trim();
					String user = dataContainer.select("div").get(1).text();
					Page2Item threadDetail = new Page2Item(title, user, reply, time, link, false);
					threadDetail.setPage(page);
					threadList.add(threadDetail);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			threadListAdp.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			isLoading= false;
			mLoadMoreFooter.setVisibility(View.GONE);
		}
	}
	
	public class UnSubcribeTask extends AsyncTask<String, Void, Void>{
		public boolean isUnsubcribe = true ;
		@Override
		protected Void doInBackground(String... params) {
			try {
				Jsoup.connect("http://vozforums.com/subscription.php?do=removesubscription&t="+params[0]).timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
			} catch (IOException e) {
				e.printStackTrace();
				isUnsubcribe = false;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isUnsubcribe) {
				threadListAdp.notifyDataSetChanged();
				Toast.makeText(getActivity(), "Thành công", Toast.LENGTH_SHORT).show();
			}
 		}
	}
}
