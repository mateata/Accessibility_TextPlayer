package com.jwlee.txtplayer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.view.*;
import android.view.accessibility.*;

public class JwUtils {
	      
	public static void readingVoice(Context context, String title){
		AccessibilityManager 	mManager 	= null;
		AccessibilityEvent 		event 		= null;
		
		mManager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		
		if (mManager.isEnabled() == true) {
			event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
			event.getText().add(title);
			mManager.sendAccessibilityEvent(event);
		}
	}	
	
	public static String getCurrentTimeStamp(){
	    try {
	    	Calendar cal;
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	        String datec= dateFormat.format(new Date()); // Find todays date
	        dateFormat.parse(datec);
            cal = dateFormat.getCalendar();
             
            Timestamp time = new Timestamp( cal.getTime().getTime() );
            System.out.println(time.getTime() / 1000);          
            System.out.println(time);
            String currentTimeStamp = ""+(time.getTime() / 1000);
            
	        return currentTimeStamp;
	    } catch (Exception e) {
	        e.printStackTrace();

	        return null;
	    }
	}
	
	static public void DeleteRecursive(File fileOrDirectory, boolean bDeleteRootDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				DeleteRecursive(child, true);
			}
			if (bDeleteRootDirectory)   {
				fileOrDirectory.delete();
			}
		} else {
			File newFile = new File(fileOrDirectory.getPath() + fileOrDirectory.getName() + System.currentTimeMillis());
			fileOrDirectory.renameTo(newFile);
			newFile.delete();
		}
	}

	public static boolean isEnableAccessibility(Context context) {
		
		AccessibilityManager accessibilityManager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		return (accessibilityManager != null) ? accessibilityManager.isEnabled() : false;
	}

	public static void SendAccessibiltiyEvent(Context context, View view, int eventType) {
		
		if( isEnableAccessibility(context) ) {
			view.sendAccessibilityEvent(eventType);
		}
	}

	//톡백 실행여부를 확인해준다.
	public static Boolean isTalkbackRunning(Context context){

		String TALKBACK_PACKAGE_NAME = "com.google.android.marvin.talkback";
		AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (manager == null){
			return false;
		}
		
		boolean isTalkbackRunning = manager.isEnabled(); // 삼성폰의 커스텀 톡백 이슈로 AccessibilityManager 미동작 이외엔 전부 true로 리턴		
		try {
			List<AccessibilityServiceInfo> services = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
			for (AccessibilityServiceInfo service : services) {
				if (service.getId().contains(TALKBACK_PACKAGE_NAME)){
					isTalkbackRunning = true;
				}
			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			return isTalkbackRunning;
		}
		return isTalkbackRunning;
	}	
	
	
	public static void SendAccessibiltiyEvent4Focused(Context context, View view) {
		
		if( isEnableAccessibility(context) ) {
			view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
			view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
		}
	}
	public static void grabAccessibilityFocus(final View mView, int delay) {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
				if( mView == null ) {
					return;
				}
				
				mView.clearFocus();
				mView.requestFocus();
				mView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_HOVER_ENTER);
//				mView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED);
				mView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
			}
		}, delay);

	}
	public static void toastCustom(Context context, String text) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View layout = inflater.inflate(R.layout.p_toast_border, (ViewGroup)((Activity) context).findViewById(R.id.toast_layout_root));
		TextView contents = (TextView) layout.findViewById(R.id.text);
		Toast toast = new Toast(context);
		contents.setText(text);
		toast.setGravity(Gravity.BOTTOM, 0, 50);
		toast.setDuration(Toast.LENGTH_SHORT);

		toast.setView(layout);
		toast.show();
	}


	public static Runnable voiceRunnabble(final Context context, final String msg){
		Runnable myRun = new Runnable() {
			@Override
			public void run() {
				JwUtils.readingVoice(context, msg);
			}
		};
		return myRun;
	}

	
	/**
	 * 17.07.21 이장원 
	 * @param data : 텍스트 파일의 맨 처음 2개 바이트로 인코딩을 판정
	 * @return 인코딩
	 * 
	 * 자바에서는 byte[] 자료형에서 맨 앞이 1인경우 (ex : 1111 1011 ) 부호를  - 로 보기 때문에
	 * 단순히 data[0]  == 0xFE 으로 비교시 같은 값을 같다고 인식 못하는 현상 있음
	 * 따라서, (data[0]&0xFF) == 0xFE 이런식으로 붙여서 자료형을 바꿀 필요성이 있음
	 */
	public static String getCharsetFromBOM(byte[] data)
	{
		String charset = "";
		
		if( data.length >= 2 ) {
			if( (data[0]&0xFF) == 0xFE &&
				       (data[1]&0xFF) == 0xFF) {
				// UTF-16 BE
				return "UTF-16BE";
			} else if( (data[0]&0xFF) == 0xFF &&
				       (data[1]&0xFF) == 0xFE) {
				// UTF-16 LE
				return"UTF-16LE";
			}
		}
	
		if( data.length >= 3 ) {
			
			if( (data[0]&0xFF) == 0xEF &&
					(data[1]&0xFF) == 0xBB &&
					(data[2]&0xFF) == 0xBF) {
						// UTF-8
				return "UTF-8"; 
			} else if( (data[0]&0xFF) == 0x0E &&
				       (data[1]&0xFF) == 0xFE &&
				       (data[2]&0xFF) == 0xFF) {
				// SCSU
				return "SCSU"; 
			} else if( (data[0]&0xFF) == 0xFB &&
				       (data[1]&0xFF) == 0xEE &&
				       (data[2]&0xFF) == 0x28) {
				// BOCU-1
				return "BOCU-1"; 
			}
		}
		
		if( data.length >= 4 ) {
			if( (data[0]&0xFF) == 0xFF &&
				       (data[1]&0xFF) == 0xFE &&
				       (data[2]&0xFF) == 0x00 &&
				       (data[3]&0xFF) == 0x00) {
					// UTF-32 LE
				return "UTF-32LE"; 
			} else if( (data[0]&0xFF) == 0x00 &&
				       (data[1]&0xFF) == 0x00 &&
				       (data[2]&0xFF) == 0xFE &&
				       (data[3]&0xFF) == 0xFF) {
				// UTF-32 BE
				return "UTF-32BE"; 
			} else if( (data[0]&0xFF) == 0xDD &&
				       (data[1]&0xFF) == 0x73 &&
				       (data[2]&0xFF) == 0x66 &&
				       (data[3]&0xFF) == 0x73) {
				// UTF-EBCDIC
				return ""; 
			} 
		}
			
		return "ANSI";
	}

	public static String getCharsetFromTextFile(String textfile)
	{
		byte[] data = null;
		int    nReadTotal = 0;
		int    nReadCount = 0;
		int    nIndex = 0;
		
		int 	nContinuedCheck = -1;
		
		String charset = "ANSI";
		
		try {
			FileInputStream inStream = new FileInputStream(textfile);
			if( inStream == null )
				return "";
			
			data = new byte[1024];
			
			if( (nReadCount = inStream.read(data,0,1024)) < 0 )
			{
				inStream.close();
				return "";
			}
			
			charset = getCharsetFromBOM(data);
			
			if( charset.equals("ANSI") )
			{
				boolean bFoundCharset = false;
				
				int nCheckUTF8Count = 0;
				byte ucCheckedUTF8 = 0x00;
				
				do
				{
					nIndex = 0;
					
					for( nIndex = 0 ; nIndex < nReadCount ; nIndex++ ){
						
						if( (data[nIndex] & 0x80) > 0 ) {
							if( data[nIndex] == 0xE2 ) {
								if( nIndex + 2 < nReadCount &&
									data[nIndex+1] == 0x80 &&
									data[nIndex+2] == 0xA8 ) {
									
									// UTF-8 개행문자
									charset = "UTF-8";
									bFoundCharset = true;
									break;
								}
							}
							
							if( nCheckUTF8Count > 0 ) {
								
								if( (data[nIndex] & 0xC0) == 0x80 ) {
									nCheckUTF8Count--;
								} else {
									bFoundCharset = true;
									break;
								}
								
							} else {
								
								if( (data[nIndex] & 0xC0) == 0x80 ) {
									
								} else if( (data[nIndex] & 0xE0) == 0xC0 ) {
									nCheckUTF8Count = 1;
								} else if( (data[nIndex] & 0xF0) == 0xE0 ) {
									nCheckUTF8Count = 2;
								} else if( (data[nIndex] & 0xF8) == 0xF0 ) {
									nCheckUTF8Count = 3;
								} else if( (data[nIndex] & 0xFC) == 0xF8 ) {
									nCheckUTF8Count = 4;
								} else if( (data[nIndex] & 0xFE) == 0xFC ) {
									nCheckUTF8Count = 5;
								} 
								
								ucCheckedUTF8 |= nCheckUTF8Count;
								
							}
							
						} else {
							
							if( nCheckUTF8Count > 0 ) {
								bFoundCharset = true;
								break;
							} else if( nIndex + 2 < nReadCount ) {
								if( data[nIndex+1] == 0x00 && data[nIndex+2] != 0x00 ) {
									if( nIndex % 2 == 0 ) {
										charset = "UTF-16BE";
									} else {
										charset = "UTF-16LE";
									}
									bFoundCharset = true;
									break;
								}
							} 
						}
						
					}
					
					nReadTotal += nReadCount;
					
				} while(bFoundCharset == false && (nReadCount = inStream.read(data,0,1024)) != -1 );
				
				if( bFoundCharset == false ) {
					if( ucCheckedUTF8 != 0x00 && nCheckUTF8Count == 0x00 )
						charset = "UTF-8";
				}
			}
			
			inStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			data = null;
			
			return "";
		}
		
		data = null;
		
		return charset;
	}
	
	public static int getListPageMax(int listTotalNum){
		if(listTotalNum>0){
			return ((listTotalNum-1)/AppControl.NA_LIST_SIZE)+1; 
		}
		return 1;
	}
	
	public static String getNowTime() {
		long time = System.currentTimeMillis();

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);

		return formater.format(date);
	}
		
	public static void okDialog(Context context, String title,String message) {
		new AlertDialog.Builder(context)
		.setTitle(title)
    	.setMessage(message)
    	.setPositiveButton(context.getString(R.string.common_ok), new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int which) {

    		}
       	})
        .setCancelable(false)
    	.create()
    	.show();
	}
    
    public static boolean isNumber(String str){
        boolean result = false; 
                  
        try{
        	Double.parseDouble(str);
            result = true ;
        }catch(Exception e){}
                  
        return result ;
    }
    
}


















