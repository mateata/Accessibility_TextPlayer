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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MemoActivity extends TDBaseActivity {

    private Context mContext;
		   
    EditText edit_memo;

    Button btn_Ok;
    Button btn_Cancel;
    
    String str_SN;
    String outer_Memo = "";
    String[] outer_Info;
    boolean outerYn = false;
    boolean textYn = false;    
	private TalkbackEventManager	mTalkbackManager = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_memo);        
        mContext = this;
		
        setView();
        
        Intent i 	= getIntent();
        outer_Memo = i.getStringExtra("MEMOTEXT");		
        outerYn = 	i.getBooleanExtra("EDITYN", false);	
        textYn = 	i.getBooleanExtra("TEXTYN", false);	        
        if(outerYn == true && !"".equals(outer_Memo) && textYn == false) {
        	outer_Info = outer_Memo.split("#");
        	edit_memo.setText(outer_Info[0]);
        } else if(outerYn == true && !"".equals(outer_Memo) && textYn == true) {
        	outer_Info = outer_Memo.split(" / ");
        	edit_memo.setText(outer_Info[0]);
        }
		int[] resIDs = {R.id.backBtn,R.id.bt_ok, R.id.memo_edit };

		mTalkbackManager = new TalkbackEventManager(this);		
		mTalkbackManager.setOnViewAccessibilityFocusedListener(new TalkbackEventManager.OnViewAccessibilityFocusedListener() {
			@Override
			public void onViewAccessibilityFocused(View view) {
				InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				
				switch (view.getId()) {	
				case R.id.bt_ok:
					mInputMethodManager.hideSoftInputFromWindow(edit_memo.getWindowToken(), 0);					
					break;	
				case R.id.backBtn:
					mInputMethodManager.hideSoftInputFromWindow(edit_memo.getWindowToken(), 0);					
					break;					
				}
			}
		});

		mTalkbackManager.setResourceIDs(resIDs);
		JwUtils.grabAccessibilityFocus(edit_memo, 500);
    }

    private void setView(){
    	
    	edit_memo = (EditText)findViewById(R.id.memo_edit);
    	btn_Ok = (Button)findViewById(R.id.bt_ok);
    	setVisibility(R.id.bt_ok,View.VISIBLE);
    	btn_Ok.setOnClickListener(mBtn_ClickListener);
    	btn_Ok.setContentDescription(getString(R.string.title_memo));
    	btn_Ok.setContentDescription(getString(R.string.player_memo) + getString(R.string.common_ok));

    	btn_Cancel = (Button)findViewById(R.id.backBtn);    	
    	btn_Cancel.setOnClickListener(mBtn_ClickListener);
    	btn_Cancel.setContentDescription(getString(R.string.player_memo) + getString(R.string.common_cancel));
    	
    	edit_memo.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_UP && (keyCode ==
						KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)){
					goMemo();
				}
				return false;
			}
		});
    }
    
  	public OnClickListener mBtn_ClickListener = new View.OnClickListener() {
  		public void onClick(View v) {
  			switch (v.getId()) {
  			case R.id.bt_ok:
  				goMemo();
  				break;
  			case R.id.backBtn:
  				onBackPressed();
  				break;
  			}
  		}
  	};


  	private void goMemo(){
  		str_SN = edit_memo.getText().toString().trim();
  		
  		if((str_SN==null||str_SN.length()<=0||str_SN.equals(""))){
  			Toast.makeText(mContext, getString(R.string.popup_insertMemo), Toast.LENGTH_SHORT).show();
  			return;
  		}
  		if(outerYn == true && !"".equals(outer_Memo) && textYn == false) {
  			str_SN = str_SN + "#" + outer_Info[1];
  		} else if(outerYn == true && !"".equals(outer_Memo) && textYn == true) {
  			str_SN = str_SN + " / " + outer_Info[1];
  		}
		Intent i = new Intent(); 
    	i.putExtra("MEMO", str_SN);
    	setResult(RESULT_OK, i); 
    	finish(); 		
  	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			onBackPressed();
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onBackPressed() {
  		Intent intent = getIntent();		
		setResult(RESULT_CANCELED,intent);
		finish();
		super.onBackPressed();
	}	
}


