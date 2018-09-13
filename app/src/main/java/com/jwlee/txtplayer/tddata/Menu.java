package com.jwlee.txtplayer.tddata;

public class Menu {

	private String menuTitle="";
	private boolean hasImage = false;
	private boolean isChecked = false;
	
	public Menu(String menuTitle){
		this.menuTitle = menuTitle;
		this.hasImage = false;
	}
	
	public Menu(String menuTitle, boolean hasImage){
		this.menuTitle = menuTitle;
		this.hasImage = hasImage;
	}
	
	public String getMenuTitle() {
		return menuTitle;
	}
	
	public boolean isHasImage() {
		return hasImage;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	


}
