package com.jwlee.txtplayer.tdplayer;

import java.lang.Character.UnicodeBlock;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.FileOpen;
import com.jwlee.txtplayer.setting.ScreenSetting;
import com.jwlee.txtplayer.setting.SpeechSetting;
import com.jwlee.txtplayer.tddata.BookMark;
import com.jwlee.txtplayer.tddata.DB;
import com.jwlee.txtplayer.tddata.Recently;
import com.jwlee.txtplayer.util.JwUtils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class TDBaseActivity extends Activity {
	public static Context mContext;
	public String INTENT_ACTION_TTS 	= "com.android.settings.TTS_SETTINGS"; 
	public String INTENT_ACTION_DISPLAY = "com.android.settings.DISPLAY_SETTINGS"; 

	public static Typeface mTypeface = null;
	Dialog mDialog;

	InputMethodManager imm;

	public String downDate = "0";
	public String dateResult ="";
	private SharedPreferences pref;
	private SharedPreferences.Editor settingEditor;
	public  Handler mDateCheck	= new Handler();
	public static int mPraiseNo = 0;	
    
	// TDBaseActivity 에 tts 관련 절대 넣지 말것! TDBaseActivity를 extend하는 다른 클래스에 영향 미침.	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		pref = mContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		settingEditor = pref.edit();
		
		if (mTypeface == null) {
			mTypeface = FileOpen.mTypeface;
		}			
	}

	protected void toastCustom(String text) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.p_toast_border, (ViewGroup) findViewById(R.id.toast_layout_root));
		TextView contents = (TextView) layout.findViewById(R.id.text);
		Toast toast = new Toast(mContext);
		contents.setText(text);
		toast.setGravity(Gravity.BOTTOM, 0, 50);
		toast.setDuration(Toast.LENGTH_SHORT);

		toast.setView(layout);
		toast.show();
	}

    public void ctrlVoiceSpeed(Context mContext, boolean incYn) {
    	int playSpeed = SpeechSetting.getSetPlaySpeed();
    	if(AppControl.NA_Playable == 1) {
    		if(incYn && playSpeed<100 ){
				playSpeed += 10;
				SpeechSetting.setSetPlaySpeed(playSpeed);
				settingEditor.putInt(mContext.getString(R.string.setting_voice_speed), playSpeed).commit();
				JwUtils.readingVoice(mContext, mContext.getString(R.string.setting_voicespeedSet,playSpeed));	
	    		((TDPlayer) TDPlayer.mContext).settingConfirm();
			} else if(incYn == false && playSpeed>0) {
				playSpeed -= 10;
				SpeechSetting.setSetPlaySpeed(playSpeed);
				settingEditor.putInt(mContext.getString(R.string.setting_voice_speed), playSpeed).commit();
				JwUtils.readingVoice(mContext, mContext.getString(R.string.setting_voicespeedSet,playSpeed));	
	    		((TDPlayer) TDPlayer.mContext).settingConfirm();
			}
    	}
    }
    	    
	protected void setVisibility(int resid, int visibility) {
		final View v = findViewById(resid);

		if (v == null)
			throw new IllegalArgumentException("Error1");
		v.setVisibility(visibility);
	}

	protected void setVisibility(int resid, boolean visibility) {
		setVisibility(resid, visibility ? View.VISIBLE : View.GONE);
	}

	protected void setText(int resid, String text) {
		final View v = findViewById(resid);
		if (v == null)
			throw new IllegalArgumentException("Error2");

		if (!(v instanceof TextView))
			throw new IllegalArgumentException("");

		final TextView tv = (TextView) v;
		tv.setText(text);
	}

	public void showDialog(String message, String positiveButtonText, DialogInterface.OnClickListener positiveListener) {
		AlertDialog m_adlg = null; 
			m_adlg = new AlertDialog.Builder(this).setMessage(message)
					.setPositiveButton(positiveButtonText, positiveListener)
					.create();
			m_adlg.setCanceledOnTouchOutside(false);
			m_adlg.show();
	}
	public void showDialog(String message, String positiveButtonText, DialogInterface.OnClickListener positiveListener, String negativeButtonText, DialogInterface.OnClickListener negativeListener) {
		AlertDialog m_adlg = null; 
			m_adlg = new AlertDialog.Builder(this).setMessage(message)
					.setPositiveButton(positiveButtonText, positiveListener)
					.setNegativeButton(negativeButtonText, negativeListener)
					.create();
			m_adlg.setCanceledOnTouchOutside(false);
			m_adlg.show();
	}

	public void showDialog(String message, String positiveButtonText, DialogInterface.OnClickListener positiveListener, String negativeButtonText, DialogInterface.OnClickListener negativeListener,DialogInterface.OnDismissListener dismissListener) {
		AlertDialog m_adlg = null; 
			m_adlg = new AlertDialog.Builder(this).setMessage(message)
					.setPositiveButton(positiveButtonText, positiveListener)
					.setNegativeButton(negativeButtonText, negativeListener)
					.create();
			m_adlg.setCanceledOnTouchOutside(false);
			m_adlg.setOnDismissListener(dismissListener);
			m_adlg.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void cancelAlert(Context mContext, int mMsg){
		AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
		mDialog.setMessage(mMsg)
		.setPositiveButton(R.string.common_ok,  new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}).setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		mDialog.show();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void createDialog(String mMsg) {
		final View innerView = getLayoutInflater().inflate(R.layout.p_wait_dialog, null);

		mDialog = new Dialog(this);
		mDialog.setTitle(mMsg);
		mDialog.setContentView(innerView);

		// Back키 눌렀을 경우 Dialog Cancle 여부 설정
		mDialog.setCancelable(true);

		// Dialog 생성시 배경화면 어둡게 하지 않기
		mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		// Dialog 밖을 터치 했을 경우 Dialog 사라지게 하기
		mDialog.setCanceledOnTouchOutside(false);

		// Dialog 밖의 View를 터치할 수 있게 하기 (다른 View를 터치시 Dialog Dismiss)
		//	        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, 
		//	                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

		// Dialog 자체 배경을 투명하게 하기
		//	      mDialog.getWindow().setBackgroundDrawable
		//	              (new ColorDrawable(android.graphics.Color.TRANSPARENT));

		// Dialog Cancle시 Event 받기
		mDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

			}
		});

		mDialog.show();

	}

	protected void dismissDialog() {
		if(mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	public void moveFocus(final EditText mView,final View targetView){	
		mView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT ) {	
					imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
					JwUtils.grabAccessibilityFocus(targetView, 500);	
				}
				return true;
			}
		});
	}

	public void allDelete(String BOOK_CODE){
		int result = getContentResolver().delete(BookMark.CONTENT_URI_LIST,  DB.BOOKMARK.INDEX.B_ID.toString() + "='" + BOOK_CODE +"'", null);
	}
	
	public boolean isHanja(String str) { 
		boolean blnRet = false;
		for(int i =0; i < str.length(); i++ ) {
		char chrChar = str.charAt(i);
		Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(chrChar);
		if (
	   UnicodeBlock.CJK_COMPATIBILITY.equals(unicodeBlock)
	   || UnicodeBlock.CJK_COMPATIBILITY_FORMS.equals(unicodeBlock)
	   || UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(unicodeBlock)
	   || UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT.equals(unicodeBlock)
	   || UnicodeBlock.CJK_RADICALS_SUPPLEMENT.equals(unicodeBlock)
	   || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(unicodeBlock)
	   || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(unicodeBlock)
	   || UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B.equals(unicodeBlock)
		) {
			blnRet = true;
			} 
		}
		return blnRet; 
	 }

}


