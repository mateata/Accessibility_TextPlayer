package com.jwlee.txtplayer.tddata;

public class InfoItem {

	private String infoName="";
	private String infoValue="";
	private boolean isSelected=false;
	private String infoCate ="";
	
	public InfoItem(String infoName){
		this.infoName = infoName;
	}
	
	public InfoItem(String infoName, String infoValue) {
		this.infoName = infoName;
		this.infoValue = infoValue;
	}
	
	public InfoItem(String infoName, boolean isSelected) {
		this.infoName = infoName;
		this.setSelected(isSelected);
	}
	
	public InfoItem(String infoName, String infoValue, String infoCate){
		this.infoName = infoName;
		this.infoValue = infoValue;
		this.infoCate = infoCate;
	}
	
	public String getInfoCate(){
		return infoCate;
	}
	
	public String getInfoName() {
		return infoName;
	}
	public void setInfoName(String infoName) {
		this.infoName = infoName;
	}
	public String getInfoValue() {
		return infoValue;
	}
	public void setInfoValue(String infoValue) {
		this.infoValue = infoValue;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
