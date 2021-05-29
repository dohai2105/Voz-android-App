package com.fun.voznoad.model;

public class Page3Item {
	String time ;
	String number;
	String avarta;
	String name;
	String type ;
	String post;
	String data ;
	String joindate;
	boolean isEdit = false;
	String userThreadId;
	
	Boolean isquote = true;
	String postid;
	
	public Boolean getIsquote() {
		return isquote;
	}
	public void setIsquote(Boolean isquote) {
		this.isquote = isquote;
	}
	public Page3Item() {
		super();
	}
	public String getUserThreadId() {
		return userThreadId;
	}
	public void setUserThreadId(String userThreadId) {
		this.userThreadId = userThreadId;
	}
	public Page3Item(String userThreadId,String joindate,boolean isEdit ,String time, String number, String avarta, String name,String type, String post, String data,String postid) {
		super();
		this.userThreadId = userThreadId;
		this.time = time;
		this.number = number;
		this.avarta = avarta;
		this.name = name;
		this.type = type;
		this.post = post;
		this.data = data;
		this.postid = postid;
		this.isEdit = isEdit;
		this.joindate = joindate;
	}
	
	
	public boolean isEdit() {
		return isEdit;
	}
	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getAvarta() {
		return avarta;
	}
	public void setAvarta(String avarta) {
		this.avarta = avarta;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getPostid() {
		return postid;
	}
	public void setPostid(String postid) {
		this.postid = postid;
	}
	public String getJoindate() {
		return joindate;
	}
	public void setJoindate(String joindate) {
		this.joindate = joindate;
	}
	
	
}
