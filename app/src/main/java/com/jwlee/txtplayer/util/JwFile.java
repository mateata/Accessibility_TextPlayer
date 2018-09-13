package com.jwlee.txtplayer.util;

public class JwFile {

	private boolean isFolder = false;
	private String fileName="";
	
	public JwFile(boolean isFolder,String fileName){
		this.isFolder = isFolder;
		this.fileName = fileName;
	}
	
	
	public JwFile(String fileName){
		this.fileName = fileName;
	}
	
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean getIsfolder(){
		return isFolder;
	}
	


}
