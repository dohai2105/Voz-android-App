package com.fun.voznoad.model;

import java.util.ArrayList;

public class Page1GroupItem {
	public String title;
	public ArrayList<Page1ChildItem> childItems;

	public Page1GroupItem(String title, ArrayList<Page1ChildItem> childItems) {
		super();
		this.title = title;
		this.childItems = childItems;
	}

	public Page1GroupItem() {
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Page1ChildItem> getChildItems() {
		return childItems;
	}

	public void setChildItems(ArrayList<Page1ChildItem> childItems) {
		this.childItems = childItems;
	}

}
