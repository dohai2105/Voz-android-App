package com.fun.voznoad.callback;

import java.util.Map;

import android.support.v4.app.Fragment;

public interface OnNotifyMainListener {
	public Map<String, String> onGetCookie();
	public void replaceFragment(Fragment frag);
	public void onSetActionBarTitle(String title );
	public void onMenuClick(int intExtra);
	
	//public void onPostDataReceive(String poststarttime,String posthash,String userid,String securitytoken,String f);
}
