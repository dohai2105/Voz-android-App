package com.fun.voznoad.callback;

public interface OnNotifyViewPagerActivity {
	public void onSetActionBarTitle(String title , String subTitle);
	public void onSetTokentDta(String token , String postid , String userid);
	
	public void onNotifyQuote (String quote);
	
	public void onNotifyTopToBottomSwipe();
	
	public void onNotifyBottomToTopSwipe();
	
	public void onNotifyEdit (String quote,String title,String poststarttime,String posthash,String p);
	
	public void onNotifyQuoteList (String threadID , String currentPage);
	
	public void onNotifyTotalPage (String totalPage);
}
