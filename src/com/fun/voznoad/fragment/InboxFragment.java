package com.fun.voznoad.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.fun.voznoad.R;
import com.fun.voznoad.SendMessageActivity;
import com.fun.voznoad.ViewPagerActivity;
import com.fun.voznoad.adapter.InboxAdapter;
import com.fun.voznoad.adapter.Page2ListAdp;
import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.common.Constant;
import com.fun.voznoad.model.InboxItem;
import com.fun.voznoad.model.Page2Item;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class InboxFragment extends Fragment{
	private PullToRefreshListView mPullRefreshListView;
	ArrayList<InboxItem> inboxList ;
	private OnNotifyMainListener notifyMainListener;
	private SharedPreferences sharedPreferences;
	private View view;
	private String url;
	private InboxAdapter inboxListAdp;
	private View mLoadMoreFooter;
	private ListView lv;
	
	public void setNotifyMain (OnNotifyMainListener x){
		notifyMainListener = x;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_inbox, container, false);
		mLoadMoreFooter = inflater.inflate(R.layout.footer_loadmore, null);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initUI();
		initCONTROL();
		new GetInboxTask().execute();
		sharedPreferences = getActivity().getSharedPreferences("voz_data",getActivity().MODE_PRIVATE);
		if (sharedPreferences.getString("theme", "null").equals("black")) {
			initTheme();
		}
	}
	
	public void initTheme(){
		mLoadMoreFooter.setBackgroundColor(Color.parseColor("#191919"));
	}
	
	public void initUI(){
		inboxList = new ArrayList<InboxItem>();
		url = "http://vozforums.com/private.php";
		//threadList = new ArrayList<Page2Item>();
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		inboxListAdp = new InboxAdapter(inboxList, getActivity());
		//threadListAdp = new Page2ListAdp(threadList, getActivity());
		mPullRefreshListView.setAdapter(inboxListAdp);
		lv = mPullRefreshListView.getRefreshableView();
		lv.addFooterView(mLoadMoreFooter);
		notifyMainListener.onSetActionBarTitle("Inbox");
	}
	
	public void initCONTROL(){
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,long arg3) {
					 Intent intent = new Intent(getActivity(), SendMessageActivity.class);
					 intent.putExtra("url",  inboxList.get(position-1).getLink());
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					 startActivity(intent);
			}
		});
	}
	
	public class GetInboxTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Document doc = Jsoup.connect(url).timeout(Constant.timeout).cookies(notifyMainListener.onGetCookie()).get();
				Elements inboxs = doc.select(".alt1.alt1Active");
				for(int i = 0 ; i<inboxs.size();i++){
					try{
						String link = "http://vozforums.com/"+ inboxs.get(i).select("a").attr("href");
						String title = inboxs.get(i).select("a").text();
						String date = inboxs.get(i).select("span.smallfont").text();
						String time = inboxs.get(i).select("span.time").text();
						String user = inboxs.get(i).select("span").last().text();
						InboxItem inboxItem = new InboxItem(title, user, date, time, link);
						inboxList.add(inboxItem);
					}catch(Exception e){
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			lv.removeFooterView(mLoadMoreFooter);
			inboxListAdp.notifyDataSetChanged();
		}
		
	}
}
