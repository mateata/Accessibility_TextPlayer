package com.jwlee.txtplayer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

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
		
	public static String INTER_MAINPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static String SD_MAINPATH = "/storage/3036-6436";
	
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

	public static HashSet<String> getExternalMounts() {
		final HashSet<String> out = new HashSet<String>();
		String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
		String s = "";
		try {
			final Process process = new ProcessBuilder().command("mount")
					.redirectErrorStream(true).start();
			process.waitFor();
			final InputStream is = process.getInputStream();
			final byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				s = s + new String(buffer);
			}
			is.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// parse output
		final String[] lines = s.split("\n");
		for (String line : lines) {
			if (!line.toLowerCase(Locale.US).contains("asec")) {
				if (line.matches(reg)) {
					String[] parts = line.split(" ");
					for (String part : parts) {
						if (part.startsWith("/"))
							if (!part.toLowerCase(Locale.US).contains("vold")) {
								if (part.contains("/mnt/media_rw")) {
									// 4.4 Kitkat 부터 기본 마운트 경로가 "/mnt/media_rw" 로 바뀌는 현상 있어서 다시 /storage로 바꿔줘야 정상인식됨
									//  https://m.blog.naver.com/PostView.nhn?blogId=liveinmatrix&logNo=220717001302&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F
									part = part.replace("/mnt/media_rw", "/storage");
								}
								out.add(part);
							}
					}
				}
			}
		}
		return out;
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
