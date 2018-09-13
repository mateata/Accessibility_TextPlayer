package com.jwlee.txtplayer.util;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.tdplayer.TDBaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class Popup_Message extends TDBaseActivity{
	
	public static Context mContext;

	private Button btn_Ok;
    private Button btn_Cancel;
    private String popMessage;
    private TextView msgView;
    private String bookCode ="";
    private String data_form ="";
    private String org_url ="";
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
		                 WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		setContentView(R.layout.p_message);

    	btn_Ok = (Button)findViewById(R.id.popup_message_btn_ok);
    	btn_Ok.setOnClickListener(mBtn_ClickListener);
    	btn_Cancel = (Button)findViewById(R.id.popup_message_btn_cancel);
    	btn_Cancel.setOnClickListener(mBtn_ClickListener);
    	msgView = (TextView)findViewById(R.id.popup_message_text_ment);
    	
    	bookCode =  getIntent().getExtras().getString("bookCode","");
    	data_form =  getIntent().getExtras().getString("data_form","DT");
    	org_url =  getIntent().getExtras().getString("org_url","");
    	popMessage 	= getIntent().getExtras().getString("MSG","");	
    	   	
		
    	msgView.setText(popMessage);
		if("".equals(popMessage))  {
			goCancel();
		}
		
		JwUtils.grabAccessibilityFocus(msgView,500);
	}
	
  	public OnClickListener mBtn_ClickListener = new View.OnClickListener() {
  		public void onClick(View v) {
  			switch (v.getId()) {
  			case R.id.popup_message_btn_ok:
  				goOK();
  				break;
  			case R.id.popup_message_btn_cancel:
  				goCancel();
  				break;
  			}
  		}
  	};
  	
  	private void goCancel(){
  		Intent intent = getIntent();
		
		setResult(RESULT_CANCELED,intent);
		finish();
  	}
  	
	private void goOK() {
		Intent i = new Intent(); 
		i.putExtra("bookCode", bookCode);
		i.putExtra("data_form", data_form);
		i.putExtra("org_url", org_url);
    	setResult(RESULT_OK, i); 
    	finish();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}	
}
