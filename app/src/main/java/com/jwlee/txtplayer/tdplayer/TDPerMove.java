package com.jwlee.txtplayer.tdplayer;


import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.util.JwUtils;
import com.jwlee.txtplayer.util.TalkbackEventManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


public class TDPerMove extends TDBaseActivity implements OnClickListener{
	
	private EditText mQuickEdit	= null;
	private int mTotal 			= 1;
	private int mPer 			= 1;
	private boolean audioYn = false;
	private TalkbackEventManager	mTalkbackManager = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_td_permove);
		

		mQuickEdit = (EditText)findViewById(R.id.quickEdit);	
		TextView pageView = (TextView)findViewById(R.id.pageView);
		findViewById(R.id.okBtn).setOnClickListener(this);
		findViewById(R.id.cancelBtn).setOnClickListener(this);
		findViewById(R.id.okBtn).setContentDescription(getString(R.string.title_permove) + " " + getString(R.string.common_ok)); 
		findViewById(R.id.cancelBtn).setContentDescription(getString(R.string.title_permove) + " " + getString(R.string.common_cancel)); 
		
		
		Intent i 	= getIntent();
		mTotal 		= i.getIntExtra		("TOTAL", 1);
		mPer 		= i.getIntExtra		("PER", 0);
		audioYn		= i.getBooleanExtra("AUDIO", false);
		pageView.setText(mPer + getString(R.string.count_format_per));
		pageView.setContentDescription(getString(R.string.popup_count_format_percent, mPer)  );				
		mQuickEdit.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP && (keyCode ==
						KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)){
					if(check()){
		        		calPercent();
		        	}
				}
				return false;
			}
		});
		
		int[] resIDs = {R.id.pageView, R.id.okBtn, R.id.cancelBtn, R.id.tv_header_title };

		mTalkbackManager = new TalkbackEventManager(this);		
		mTalkbackManager.setOnViewAccessibilityFocusedListener(new TalkbackEventManager.OnViewAccessibilityFocusedListener() {
			@Override
			public void onViewAccessibilityFocused(View view) {
				InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				
				switch (view.getId()) {	
				case R.id.tv_header_title:
					mInputMethodManager.hideSoftInputFromWindow(mQuickEdit.getWindowToken(), 0);					
					break;				
				case R.id.pageView:
					mInputMethodManager.hideSoftInputFromWindow(mQuickEdit.getWindowToken(), 0);					
					break;	
				case R.id.okBtn:
					mInputMethodManager.hideSoftInputFromWindow(mQuickEdit.getWindowToken(), 0);					
					break;
				case R.id.cancelBtn:
					mInputMethodManager.hideSoftInputFromWindow(mQuickEdit.getWindowToken(), 0);					
					break;						
				}
			}
		});

		mTalkbackManager.setResourceIDs(resIDs);
		JwUtils.grabAccessibilityFocus(mQuickEdit, 500);	
	}
	
	public void onClick(View v) {
        final int key = v.getId();
        switch (key) {
	        case R.id.okBtn:
	        	if(check()){
	        		calPercent();
	        	}
	            break;
	        case R.id.cancelBtn:
	        	onBackPressed();
	            break;	            
        }
    }
	
	private void calPercent() {
		int perCal = 1 ;
		int perc = Integer.parseInt(mQuickEdit.getText().toString().trim());
		if(audioYn == false) {

		if(perc == 0) {
			perCal = 1;
		} else if(perc == 100){
			perCal = mTotal;
		} else {
			double rate = (double)((double)mTotal / (double)100 ) * (double)perc;
			perCal = (int)rate;
		}
		} else {
			perCal = perc;
		}
		
		Intent i = new Intent(); 
    	i.putExtra("PAGE", perCal);
    	setResult(RESULT_OK, i); 
    	finish();
    	
	}
	
	private boolean check() {
        if (mQuickEdit.getText().toString().trim().equals("")) {
        	toastCustom(getString(R.string.popup_ex_percent_up));
            return false;
        }
        int page = 0;
        if(JwUtils.isNumber(mQuickEdit.getText().toString().trim())) {            
            page = Integer.parseInt(mQuickEdit.getText().toString().trim());    	
        } else {
        	toastCustom(getString(R.string.popup_ex_percent_up));
        	return false;
        }

        if (page < 0 || page > 100) {
        	toastCustom(getString(R.string.popup_ex_percent_up));
        	mQuickEdit.setText("");        	
            return false;
        }
        return true;
    }

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void onBackPressed() {
  		Intent intent = getIntent();		
		setResult(RESULT_CANCELED,intent);
		finish();
		super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return false;
		}
		
		return super.onKeyUp(keyCode, event);
	}
}

