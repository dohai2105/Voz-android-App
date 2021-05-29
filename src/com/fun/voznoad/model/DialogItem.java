package com.fun.voznoad.model;

public class DialogItem {
	String name;
	String link;

	public DialogItem(String name, String link) {
		super();
		this.name = name;
		this.link = link;
	}

	public DialogItem() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
