package com.jwlee.txtplayer;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

public class AppControl {
	public static int NA_Playable = 0;	 // 0: 메인화면  1: 텍스트 파일, 2: 플레이어 탭   3: 기타
	/* tts 종류 */
	public static int NA_TTS_SPEED = 3;
	public static int NA_TTS_TONE = 3;
	public static final int     DB_VERSION  = 2;
	public static int NA_LIST_SIZE = 20;
	public static int MAIN_LIST_IDX = 0;
	
	public static String mTTSEngine = "";
	public static final int TXT_DIVIDE	= 20; // 텍스트 파일의 한 페이지를 몇 줄로 규정하는가.
		
	public static String MAIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	public static String INTER_MAINPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String SD_MAINPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	public static boolean FIRST_RUN = true;
	public static boolean DEL_FILE  = false;
	public static boolean DIRECT_CONNECT  = false;
	public static boolean DEBUG = false;
	
	public static void mAlert(Context mContext, int mMsg) {
		AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
		mDialog.setMessage(mMsg).setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		mDialog.show();
	}

	public static void mAlert(Context mContext, String mMsg) {
		AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
		mDialog.setMessage(mMsg).setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		mDialog.show();
	}

}
