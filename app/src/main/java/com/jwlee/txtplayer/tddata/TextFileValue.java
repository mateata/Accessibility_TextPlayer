package com.jwlee.txtplayer.tddata;

public class TextFileValue {
	
	private int 	mPageNo; 		
	private String 	mText; 			
	
	public TextFileValue( int page, String text){
		super();
		this.mPageNo 		= page;
		this.mText 			= text;
	}

	public void setPage(){
		this.mPageNo = mPageNo;
	}
	
	public int getPage(){
		return mPageNo;
	}
	
	public void setText(){
		this.mText = mText;
	}
	
	public String getText(){
		return mText;
	}
}
