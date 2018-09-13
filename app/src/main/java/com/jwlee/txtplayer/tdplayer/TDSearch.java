package com.jwlee.txtplayer.tdplayer;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.util.JwUtils;
import com.jwlee.txtplayer.util.TalkbackEventManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;


public class TDSearch extends TDBaseActivity {
  
    private Context mContext;
		
    EditText edit_SN;

    Button btn_Ok;
    Button btn_Cancel;
    
    String str_SN;
	private TalkbackEventManager	mTalkbackManager = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

	    getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
	    
        setContentView(R.layout.p_search);
        
        mContext = this;
		
        setView();
        
		int[] resIDs = {R.id.popup_search_btn_ok, R.id.popup_search_btn_cancel };

		mTalkbackManager = new TalkbackEventManager(this);		
		mTalkbackManager.setOnViewAccessibilityFocusedListener(new TalkbackEventManager.OnViewAccessibilityFocusedListener() {
			@Override
			public void onViewAccessibilityFocused(View view) {
				InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				
				switch (view.getId()) {				
				case R.id.popup_search_btn_ok:
					mInputMethodManager.hideSoftInputFromWindow(edit_SN.getWindowToken(), 0);					
					break;	
				case R.id.popup_search_btn_cancel:
					mInputMethodManager.hideSoftInputFromWindow(edit_SN.getWindowToken(), 0);					
					break;					
				}
			}
		});

		mTalkbackManager.setResourceIDs(resIDs);
	
    }

    private void setView(){
    	
    	edit_SN = (EditText)findViewById(R.id.popup_search_edit_sn);
    	btn_Ok = (Button)findViewById(R.id.popup_search_btn_ok);
    	btn_Ok.setOnClickListener(mBtn_ClickListener);
    	btn_Cancel = (Button)findViewById(R.id.popup_search_btn_cancel);
    	btn_Cancel.setOnClickListener(mBtn_ClickListener);

    	edit_SN.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP && (keyCode ==
						KeyEvent.KEYCODE_ENTER)){
					goSearch();
				}
				return false;
			}
		});
    	
    	edit_SN.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				switch(actionId) {
				case EditorInfo.IME_ACTION_DONE:
					goSearch();
					break;
				}
				// TODO Auto-generated method stub
				return false;
			}    		
    	});
    	JwUtils.grabAccessibilityFocus(edit_SN, 500);
    }
    
  	public OnClickListener mBtn_ClickListener = new View.OnClickListener() {
  		public void onClick(View v) {
  			switch (v.getId()) {
  			case R.id.popup_search_btn_ok:
  				goSearch();
  				break;
  			case R.id.popup_search_btn_cancel:
  				onBackPressed();
  				break;
  			}  				
  		}
  	};


  	private void goSearch(){
  		str_SN = edit_SN.getText().toString().trim();
  		
  		if((str_SN==null||str_SN.length()<=0||str_SN.equals(""))){
  			Toast.makeText(mContext, getString(R.string.popup_search_null), Toast.LENGTH_SHORT).show();
  			return;
  		}
		Intent i = new Intent(); 
    	i.putExtra("SEARCH", str_SN);
    	setResult(RESULT_OK, i); 
    	finish(); 		
  	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return false;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			onBackPressed();
			return false;
		}
		
		return false;
	}
	@Override
	public void onBackPressed() {
  		Intent intent = getIntent();		
		setResult(RESULT_CANCELED,intent);
		finish();
		super.onBackPressed();
	}	
}


