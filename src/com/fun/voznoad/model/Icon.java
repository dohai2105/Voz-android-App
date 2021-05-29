package com.fun.voznoad.model;

public class Icon {
	public String link;
	public String iconSymbol;
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getIconSymbol() {
		return iconSymbol;
	}
	public void setIconSymbol(String iconSymbol) {
		this.iconSymbol = iconSymbol;
	}
	public Icon(String link, String iconSymbol) {
		super();
		this.link = link;
		this.iconSymbol = iconSymbol;
	}
	
}
