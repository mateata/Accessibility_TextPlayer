package com.jwlee.txtplayer.tdplayer;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.tddata.Book;
import com.jwlee.txtplayer.tddata.BookMark;
import com.jwlee.txtplayer.tddata.DB;
import com.jwlee.txtplayer.util.JwUtils;
import com.jwlee.txtplayer.util.Popup_Message;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class TDTableActivity extends TDBaseActivity implements OnClickListener, OnInitListener {
	private ListView 				mList 		= null;
	private BookMarkCursorAdapter 	mAdapter 	= null;
	private static final int	DELETE_CLICK	= 1001;	
	private static final int	DELETE_LIST	= 1002;
	private static final int	MEMO_ACTIVITY	= 1003;		

	private boolean mEditMode 	= false; // 에디트 모드 확인
	private TextView mBackBtn;	
	private TextView mEditBtn;
	private TextView mAllBtn;
	private ImageView gubun3;
	private ImageView gubun4;
	private boolean mStartChk 	= false;

	private String BOOK_CODE = "";


	private String BOOK_TITLE = "";

	private String BOOKMARK_PAGE = "";
	private int    BOOKMARK_LINE = 0;
	private String BOOKMARK_MEMO = "";
	private TextToSpeech shortTts;	
    private SparseBooleanArray sbDel;
    private int tableGbn = 3; // 1: 북마크 2: 메모 3: 목차 , 목차가 기본
    private boolean chpYn = false;
    private String markSet = "";
    private String memoSet = "";
    private boolean allChkYn = true;   
	private TextView textNoList;
	private LinearLayout editLayout; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_td_table);
		Intent i 	= getIntent();
		BOOK_CODE = i.getStringExtra("ID");
		tableGbn = i.getIntExtra("TABLE", 1);

		mList = (ListView) findViewById(R.id.listView1);
		mEditBtn = (TextView)findViewById(R.id.editBtn);		
		mBackBtn = (TextView)findViewById(R.id.backBtn);
		mAllBtn = (TextView)findViewById(R.id.allBtn);
		gubun3 = (ImageView)findViewById(R.id.easygubun3);
		gubun4 = (ImageView)findViewById(R.id.easygubun4);
		editLayout = (LinearLayout)findViewById(R.id.layout_edit);
		mBackBtn.setOnClickListener(this);		
		mEditBtn.setOnClickListener(this);
		mAllBtn.setOnClickListener(this);
		gubun3.setVisibility(View.GONE);
		gubun4.setVisibility(View.GONE);		
		shortTts = new TextToSpeech(getApplicationContext(), this);
		textNoList = (TextView) findViewById(R.id.textNoList);
		
		initBook();
		if(tableGbn == 1) {
			JwUtils.readingVoice(mContext, getString(R.string.title_marklist));
			getMark();			
		} else if(tableGbn == 2) {
			JwUtils.readingVoice(mContext, getString(R.string.title_memolist));
			getMemo();
		}
	}

	@Override
	public void onResume() {
		if(mStartChk){
			dataChanged();
		}else{
			mStartChk = true;
		}
		super.onStart();
	}

	public void initBook()	{
		String textSet = DB.BOOK.INDEX.B_ID.toString() + " = '" +  BOOK_CODE  + "'"  ; 
		markSet = DB.BOOKMARK.INDEX.B_ID.toString() + "='" + BOOK_CODE +"' AND " + DB.BOOKMARK.INDEX.M_TYPE.toString() + "= 0"; 
		memoSet = DB.BOOKMARK.INDEX.B_ID.toString() + "='" + BOOK_CODE +"' AND " + DB.BOOKMARK.INDEX.M_TYPE.toString() + "= 1"; 
					
		Cursor cursor = getContentResolver().query(Book.CONTENT_URI, Book.projection, textSet, null, null);
		if( cursor.getCount() > 0 )	{
			cursor.moveToFirst();

			if( BOOK_TITLE.length() == 0 )
				BOOK_TITLE = cursor.getString(DB.BOOK.INDEX.B_TITLE.ordinal());
		}
		cursor.close();
	}

	public void getMark(){
		textNoList.setVisibility(View.GONE);
		Cursor cursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, markSet, null, DB.BOOKMARK.INDEX.M_DATE.toString() + " DESC");
		mAdapter = new BookMarkCursorAdapter(TDTableActivity.this, cursor);
		mList.setOnItemClickListener(onClickItemListener);
		mList.setOnItemLongClickListener(onClickLongListener);
		mList.setAdapter(mAdapter);

		if( 0 == cursor.getCount() ){
			toastCustom(getString(R.string.dialog_no_text_bookmark));
			textNoList.setVisibility(View.VISIBLE);
			textNoList.setText(getString(R.string.dialog_no_text_bookmark));
			textNoList.setContentDescription(getString(R.string.dialog_no_text_bookmark));    			
		}
		editLayout.setVisibility(View.VISIBLE);		
	}
	public void getMemo(){
		textNoList.setVisibility(View.GONE);
		Cursor cursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, memoSet, null, DB.BOOKMARK.INDEX.M_DATE.toString() + " DESC");
		mAdapter = new BookMarkCursorAdapter(TDTableActivity.this, cursor);
		mList.setOnItemClickListener(onClickItemListener);
		mList.setOnItemLongClickListener(onClickLongListener);
		mList.setAdapter(mAdapter);

		if( 0 == cursor.getCount() ){
			toastCustom(getString(R.string.dialog_no_text_memo));
			textNoList.setVisibility(View.VISIBLE);
			textNoList.setText(getString(R.string.dialog_no_text_memo));
			textNoList.setContentDescription(getString(R.string.dialog_no_text_memo));  
		}
		editLayout.setVisibility(View.VISIBLE);
	}
	
	static class ViewHolder{
		TextView textTitle;
	}

	public void dataChanged(){
		String setList = "";
		String noMark = "";
		if(tableGbn == 1) {
			noMark = getString(R.string.dialog_no_text_bookmark);
			setList = markSet;
		} else if(tableGbn == 2) {
			noMark = getString(R.string.dialog_no_text_memo);
			setList = memoSet;
		}
		Cursor cursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, setList, null, DB.BOOKMARK.INDEX.M_DATE.toString() + " DESC");
		if( 0 == cursor.getCount() ){
			toastCustom(noMark);
		}

		mAdapter.changeCursor(cursor);
		mAdapter.notifyDataSetChanged();
	}

	public class BookMarkCursorAdapter extends CursorAdapter {
	    private boolean[] isCheckedConfrim;
        
		public BookMarkCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor);
			this.isCheckedConfrim = new boolean[cursor.getCount()];
			
		}
	    // CheckBox를 모두 선택하는 메서드
        public void setAllChecked(boolean ischeked) {
            int tempSize = isCheckedConfrim.length;
            for(int a=0 ; a<tempSize ; a++){
                isCheckedConfrim[a] = ischeked;
            }
        }
 
        public void setChecked(int position) {
            isCheckedConfrim[position] = !isCheckedConfrim[position];
        }
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final BookMark.Data d = new BookMark.Data(cursor);
			final BookMark.Holder h = (BookMark.Holder) view.getTag();
			//h.listnum.setText( ""+(cursor.getPosition()+1)%10);
			h.page.setVisibility(View.VISIBLE);
			h.update.setTag(cursor.getPosition());	
			h.title.setTypeface(TDBaseActivity.mTypeface, Typeface.BOLD);
			h.page.setTypeface(TDBaseActivity.mTypeface, Typeface.BOLD);
			if(tableGbn == 1) {
				h.page.setText	( d.page  + getString(R.string.common_page));
				h.page.setTag(d.category);
				h.title.setText	( d.text );
			} else if(tableGbn == 2) {
				h.title.setText	( d.page  + getString(R.string.common_page));
				h.page.setTag(d.category);
				h.page.setText	( d.text );
			}
			
			if(mEditMode){
				h.check.setVisibility(View.VISIBLE);
				h.check.setClickable(false);
				h.check.setFocusable(false);
				for(int i= 0; i < cursor.getCount(); i++) {
					h.check.setChecked(isCheckedConfrim[i]);
				}
				h.title.setContentDescription( d.text + getString(R.string.common_noselected));
				if(tableGbn == 2) {
					h.update.setVisibility(View.VISIBLE);
					h.update.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							delPosition = (Integer) h.update.getTag();
							Intent i = new Intent(mContext, MemoActivity.class);
				    		i.putExtra("MEMOTEXT", d.text);
				    		i.putExtra("EDITYN", true);
				    		i.putExtra("TEXTYN", true);
				    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    		i.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				    		startActivityForResult(i, MEMO_ACTIVITY);	
						}
					});				
				}	
			}else{    
				h.check.setVisibility(View.GONE);
				h.check.setChecked(false);
				if(tableGbn == 2) {
					h.update.setVisibility(View.GONE);				
				}	
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return new BookMark.Holder().set(LayoutInflater.from(context).inflate(R.layout.i_mybook_ad, parent, false));
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return false;
		}else if(keyCode == KeyEvent.KEYCODE_HOME ){
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			return false;
		}
		return super.onKeyUp(keyCode, event);
	}    

	private OnItemClickListener onClickItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			
			if(tableGbn == 1 || tableGbn == 2) {
			
			if(mEditMode){
				CheckBox chk = (CheckBox)v.findViewById(R.id.check);
				mAdapter.setChecked(position);
				chk.setChecked((((ListView) parent).isItemChecked(position)));
	        	boolean chkYn = ((ListView) parent).isItemChecked(position);
	        	if(chkYn) {
	        		TextView tv = (TextView)v.findViewById(R.id.title);
	        		String tit = tv.getText().toString();
		        	speakText(tit + getString(R.string.common_selected));
		        	v.setContentDescription(tit + getString(R.string.common_selected));           		
	        	} else {
	        		TextView tv = (TextView)v.findViewById(R.id.title);
	        		String tit = tv.getText().toString();
		        	speakText(tit + getString(R.string.common_noselected));
		        	v.setContentDescription(tit + getString(R.string.common_noselected));
	        	}
			}else{
				movePage(position);
			}
			}
		}
	};
	
	private void isAllChecked(boolean chkYn) {
		if(chkYn) {
			for(int i = 0; i < mList.getCount(); i++) {
				mList.setItemChecked(i, true);			
			}
			mAdapter.setAllChecked(allChkYn);
			allChkYn = false;
			mAllBtn.setText(getString(R.string.player_btn_allfree));
			speakText(getString(R.string.toast_allchk_ok));			
		} else {
			for(int i = 0; i < mList.getCount(); i++) {
				mList.setItemChecked(i, true);			
			}
			mAdapter.setAllChecked(allChkYn);
			mAllBtn.setText(getString(R.string.player_btn_allselect));
			allChkYn = true;
			speakText(getString(R.string.toast_allchk_cancel));
		}
		mAdapter.notifyDataSetChanged();
	}
    public void speakText(String textToSpeech) {
        if (shortTts != null) {
            if (shortTts.isSpeaking()) {
            	shortTts.stop();
            }
            shortTts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            startTts();
        }
    }
	
    private void startTts() {
        if (shortTts == null) {
            shortTts = new TextToSpeech(getApplicationContext(), (OnInitListener) this);
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            // startActivityForResult(checkIntent, RESULT_OK);
            startActivity(checkIntent);
        }
    }
	private void movePage(int position){		
		Cursor cursor = (Cursor) mList.getItemAtPosition(position);
		BOOKMARK_PAGE = cursor.getString(DB.BOOKMARK.INDEX.M_PAGE.ordinal());
		BOOKMARK_LINE = cursor.getInt	(DB.BOOKMARK.INDEX.M_LINE.ordinal());
		BOOKMARK_MEMO = cursor.getString(DB.BOOKMARK.INDEX.M_MEMO.ordinal());

    	Intent i = new Intent(); 
    	i.putExtra("PAGE", BOOKMARK_PAGE);
    	i.putExtra("LINE", BOOKMARK_LINE);
    	i.putExtra("MARKYN", true);
    	//i.putExtra("PAGE", BOOKMARK_PAGE);

    	setResult(RESULT_OK, i);
		cursor.close();
    	finish();
	}

	private int delPosition = 0;
	private OnItemLongClickListener onClickLongListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if(mEditMode) {
				final Cursor cursor = (Cursor) mList.getItemAtPosition(position);
				delPosition = position; 
				String msgDel = getString(R.string.dialog_select_del_select) + "\n" + cursor.getString(DB.BOOKMARK.INDEX.M_TEXT.ordinal()) + "\n" + getString(R.string.dialog_select_del);
	    		Intent i = new Intent(mContext, Popup_Message.class);
	    		i.putExtra("MSG", msgDel);
	    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivityForResult(i, DELETE_CLICK);	    
	        	cursor.close();
				dataChanged();
			}
			return false;
		}
	};

	public void onClick(View v) {
		final int key = v.getId();
		switch (key) {
		case R.id.editBtn:
			rowEdit();
			break;
		case R.id.backBtn:
			if(mEditMode) {
				cancelEdit();
			}
			break;
		case R.id.allBtn:
			isAllChecked(allChkYn);
			break;	
		default:
			break;			
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) { 
			switch (requestCode) {
			case DELETE_CLICK: 
				final Cursor cursor = (Cursor) mList.getItemAtPosition(delPosition);
				getContentResolver().delete(BookMark.CONTENT_URI_LIST,  DB.BOOKMARK.INDEX._id.toString() + "= " + cursor.getInt(DB.BOOKMARK.INDEX._id.ordinal()) + "", null);
				JwUtils.toastCustom(mContext, getString(R.string.dialog_del_ok));
				dataChanged();
				cursor.close();
				break;
			case DELETE_LIST: 			
				Cursor cursorDel = null;
				for(int i = 0; i < sbDel.size(); i++){
					if(sbDel.valueAt(i)){
						cursorDel = (Cursor) mList.getItemAtPosition(sbDel.keyAt(i));
						getContentResolver().delete(BookMark.CONTENT_URI_LIST,  DB.BOOKMARK.INDEX._id.toString() + "=" + cursorDel.getInt(DB.BOOKMARK.INDEX._id.ordinal()) + "", null);
					}
				}
				JwUtils.toastCustom(mContext, getString(R.string.dialog_del_ok));
				cancelEdit();
				break;
			case MEMO_ACTIVITY:
				String memoText = intent.getExtras().getString("MEMO");
				Cursor mcursor = (Cursor) mList.getItemAtPosition(delPosition);
				if( 0 < mcursor.getCount() ){
				mcursor.moveToFirst();
				ContentValues values = new ContentValues();
				values.put( DB.BOOKMARK.INDEX.M_DATE.toString(), 	JwUtils.getNowTime());
	        	values.put( DB.BOOKMARK.INDEX.M_MEMO.toString(), 	memoText);
	        	values.put( DB.BOOKMARK.INDEX.M_TEXT.toString(), 	memoText);
	        	int result = getContentResolver().update(BookMark.CONTENT_URI, values, DB.BOOKMARK.INDEX._id.toString() + "='" + mcursor.getString(DB.BOOKMARK.INDEX._id.ordinal()).toString() + "'", null);

				}
				
				dataChanged();				
				mcursor.close();				
				break;				
 			}
		} else {
			cancelEdit();
			mList.clearChoices();
			mAdapter.notifyDataSetChanged();
		} 
	}

	public void rowEdit(){
		if(mEditMode){
			final SparseBooleanArray sb = mList.getCheckedItemPositions();			
			sbDel = sb;
			int zeroChk = 0;
			if(sb.size() != 0){
				for(int i = 0; i < sb.size(); i++){
					if(sb.valueAt(i)){
						zeroChk++;
					}
				}

				if(0 < zeroChk){
					String msgDel =  getString(R.string.dialog_delete_list,zeroChk);
		    		Intent i = new Intent(mContext, Popup_Message.class);
		    		i.putExtra("MSG", msgDel);
		    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        	startActivityForResult(i, DELETE_LIST);	
				}
			}else{
				cancelEdit();
			}
			mEditMode = false;
		}else{
			mEditMode = true;
			allChkYn = true;
			if(JwUtils.isTalkbackRunning(mContext) == true) {
				speakText(getString(R.string.dialog_editbtn_true));
			}
			mBackBtn.setVisibility(View.VISIBLE);	
			mAllBtn.setVisibility(View.VISIBLE);
			gubun3.setVisibility(View.VISIBLE);	
			gubun4.setVisibility(View.VISIBLE);	
			mEditBtn.setText(getString(R.string.common_list_del));		
			mEditBtn.setContentDescription(getString(R.string.common_list_del));
			mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mAdapter.notifyDataSetChanged();
		}
	}
	private void cancelEdit() {
		if(mEditMode == true) {
			speakText(getString(R.string.dialog_editbtn_false));
			mList.clearChoices();
			mAdapter.notifyDataSetChanged();
			mEditMode = false;
			allChkYn = true;
		}
		mBackBtn.setVisibility(View.GONE);
		mAllBtn.setVisibility(View.GONE);
		gubun3.setVisibility(View.GONE);	
		gubun4.setVisibility(View.GONE);
		mBackBtn.setText(getString(R.string.common_back));
		mEditBtn.setText(getString(R.string.common_list_edit));
		mEditBtn.setContentDescription(getString(R.string.common_list_edit));

	}
	
	@Override
	public void onBackPressed() {
  		Intent intent = getIntent();		
		setResult(RESULT_CANCELED,intent);
		finish();
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
}