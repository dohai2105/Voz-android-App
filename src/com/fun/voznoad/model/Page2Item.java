package com.fun.voznoad.model;

public class Page2Item {
	public String title;
	public String user;
	public String reply;
	public String time;
	public String link;
	public boolean isSticky;
	public String page;
	//public String subTitle ;
 
	
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Page2Item(String title, String user, String reply, String time,
			String link, boolean isSticky) {
		super();
		this.title = title;
		this.user = user;
		this.reply = reply;
		this.time = time;
		this.link = link;
		this.isSticky = isSticky;
	}
//	public String getSubTitle() {
//		return subTitle;
//	}
//	public void setSubTitle(String subTitle) {
//		this.subTitle = subTitle;
//	}
 
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
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
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
	public boolean isSticky() {
		return isSticky;
	}
	public void setSticky(boolean isSticky) {
		this.isSticky = isSticky;
	}
	public Page2Item() {
		super();
	}
	
}
