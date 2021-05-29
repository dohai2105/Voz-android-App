package com.fun.voznoad.model;

public class InboxItem {
	String title;
	String user;
	String date;
	String time;
	String link;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String data) {
		this.date = data;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public InboxItem(String title, String user, String date, String time,String link) {
		super();
		this.title = title;
		this.user = user;
		this.date = date;
		this.time = time;
		this.link = link;
	}
	
}
