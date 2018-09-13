package com.jwlee.txtplayer.tddata;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.util.JwUtils;


public class TextFileParser {
	private static String TAG = "TextFileParser";
	public static ArrayList<TextFileValue> textFileParser(InputStream is, String charset) {
		ArrayList<TextFileValue> 	tValue 		= new ArrayList<TextFileValue>();
		BufferedReader 				bReader		= null;
		InputStreamReader 			iReader 	= null;
		
		try {
			iReader = new InputStreamReader(is, charset);
			bReader = new BufferedReader(iReader);			
			int 		pageNo 		= 1; 
			int 		lineNo 		= 0; 			
			String 	msg = "";
			

			while((msg = bReader.readLine())!= null){
				//msg = msg.trim();
				if(msg.length() > 0) {
					lineNo++;
					pageNo = (lineNo / AppControl.TXT_DIVIDE) + 1;
					tValue.add(new TextFileValue(pageNo, msg));					
				}
			}			
		} catch (Exception e) {
			Log.e(TAG,"Exception : " + e.toString());
			tValue.add(new TextFileValue(0, "Error"));
		}finally{
			try{
				iReader.close();
				bReader.close();
			}catch( Exception ignored){
				Log.e(TAG,"Exception22 : " + ignored.toString());
			}
		}
		
		return tValue;
	}
	
}
