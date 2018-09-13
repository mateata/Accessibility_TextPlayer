package com.jwlee.txtplayer.tdplayer;


import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.util.JwUtils;
import com.jwlee.txtplayer.util.TalkbackEventManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


public class TDPageMove extends TDBaseActivity implements OnClickListener{
	
	private EditText mQuickEdit	= null;
	private int mTotal 			= 1;
	private int mPage 			= 1;
	private TalkbackEventManager	mTalkbackManager = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_td_pagemove);
		
		mQuickEdit = (EditText)findViewById(R.id.quickEdit);	
		TextView pageView = (TextView)findViewById(R.id.pageView);
		findViewById(R.id.okBtn).setOnClickListener(this);
		findViewById(R.id.cancelBtn).setOnClickListener(this);
		findViewById(R.id.okBtn).setContentDescription(getString(R.string.title_pagemove) + " " + getString(R.string.common_ok)); 
		findViewById(R.id.cancelBtn).setContentDescription(getString(R.string.title_pagemove) + " " + getString(R.string.common_cancel)); 
		
		Intent i 	= getIntent();
		mTotal 		= i.getIntExtra		("TOTAL", 1);
		mPage 		= i.getIntExtra		("PAGE", 1);		
		pageView.setText(getString(R.string.list_count_format_no_parenthesis, mPage, mTotal));
		pageView.setContentDescription(getString(R.string.popup_count_format, mTotal, mPage)  );				
		mQuickEdit.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP && keyCode ==
						KeyEvent.KEYCODE_ENTER){
					if(check(mQuickEdit.getText().toString().trim())){
		        		Intent i = new Intent(); 
			        	i.putExtra("PAGE", mQuickEdit.getText().toString().trim());
			        	setResult(RESULT_OK, i); 
			        	finish();
		        	}
				}
				return false;
			}
		});
		
		int[] resIDs = {R.id.pageView, R.id.okBtn, R.id.cancelBtn };

		mTalkbackManager = new TalkbackEventManager(this);		
		mTalkbackManager.setOnViewAccessibilityFocusedListener(new TalkbackEventManager.OnViewAccessibilityFocusedListener() {
			@Override
			public void onViewAccessibilityFocused(View view) {
				InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				
				switch (view.getId()) {	
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
	        	String chk = mQuickEdit.getText().toString().trim();
	        	if(check(chk)){
	        		Intent i = new Intent(); 
		        	i.putExtra("PAGE", Integer.parseInt(chk));
		        	setResult(RESULT_OK, i); 
		        	finish();
	        	}
	            break;
	        case R.id.cancelBtn:
	        	onBackPressed();
	            break;	            
        }
    }
	
	private boolean check(String chk) {
        if ("".equals(chk)) {
        	toastCustom(getString(R.string.popup_ex_pageno_zero));
            return false;
        }
        int page = 0;
        if(JwUtils.isNumber(chk)) {            
            page = Integer.parseInt(chk);        	
        } else {
        	toastCustom(getString(R.string.popup_ex_pageno_int));
        	return false;
        }
        if (mTotal < page) {
        	toastCustom(getString(R.string.popup_ex_pageno_up));
        	mQuickEdit.setText("");
            return false;
        } else if (page <= 0) {
        	toastCustom(getString(R.string.popup_ex_pageno_zero));
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

