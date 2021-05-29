package com.fun.voznoad.model;

public class Image {
	public String link;
	public boolean isIcon;
	
	public Image() {
		super();
		isIcon = false;
	}

	public Image(String link, boolean isIcon) {
		super();
		this.link = link;
		this.isIcon = isIcon;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isIcon() {
		return isIcon;
	}

	public void setIcon(boolean isIcon) {
		this.isIcon = isIcon;
	}
	
	
}
