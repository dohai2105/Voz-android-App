package com.fun.voznoad.adapter;


import com.fun.voznoad.callback.OnNotifyMainListener;
import com.fun.voznoad.callback.OnNotifyViewPagerActivity;
import com.fun.voznoad.fragment.Page3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	
	public  OnNotifyViewPagerActivity notifyMainListener;
	public String url ;
	public int totalPage;
	public ViewPagerAdapter(FragmentManager fm,OnNotifyViewPagerActivity notifyMainListener,String url,int totalpage) {
		super(fm);
		this.notifyMainListener = notifyMainListener;
		this.url = url ;
		this.totalPage = totalpage;
	}

	@Override
	public Fragment getItem(int position) {
		Page3 fragmentDetail = new Page3();
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("page", position+1);
		fragmentDetail.setArguments(bundle);
		fragmentDetail.setNotifyMain(notifyMainListener);
		return fragmentDetail;
	}
	
	public void setCount(int totalPage){
		this.totalPage = totalPage;
	}

	@Override
	public int getCount() {
		return totalPage;
	}

}
