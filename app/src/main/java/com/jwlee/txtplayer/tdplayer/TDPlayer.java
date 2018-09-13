package com.jwlee.txtplayer.tdplayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.setting.PlayerSetting;
import com.jwlee.txtplayer.setting.ScreenSetting;
import com.jwlee.txtplayer.setting.SpeechSetting;
import com.jwlee.txtplayer.tddata.BookMark;
import com.jwlee.txtplayer.tddata.DB;
import com.jwlee.txtplayer.tddata.Recently;
import com.jwlee.txtplayer.tddata.TextFileParser;
import com.jwlee.txtplayer.tddata.TextFileValue;
import com.jwlee.txtplayer.util.BroadCast;
import com.jwlee.txtplayer.util.Hanja;
import com.jwlee.txtplayer.util.JwUtils;
import com.jwlee.txtplayer.util.Popup_Message;
import com.jwlee.txtplayer.util.SleepSet;
import com.jwlee.txtplayer.util.TalkbackEventManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class TDPlayer extends TDBaseActivity implements OnInitListener, OnClickListener, SensorEventListener {
	//onActivityResult 를 위한 구분값
	private static final int	QUICK_ACTIVITY		= 2001;
	private static final int	TABLE_ACTIVITY		= 2002;
	private static final int	MEMO_ACTIVITY		= 2003;	
	private static final int	SETTING_ACTIVITY	= 2004;	
	private static final int	SEARCH_ACTIVITY	= 2005;	
	private static final int	MEMU_ACTIVITY		= 2006;	
	private static final int	SLEEP_ACTIVITY		= 2007;
	private static final int	BOOKMARK_ACTIVITY	= 2008;
	private static final int	LASTPAGE_ACTIVITY	= 2009;
	private static final int	MOVESET_ACTIVITY	= 2010;		
	

	private TextToSpeech 			mTts 				= null;
	private ProgressDialog 			mProgressDialog 	= null;
	private Handler 				mHandler 			= null;
	private Handler	 				mLastPage			= null;
	private Handler 				mTTSPlay			= null;
	private Handler					mSleepTimer			= null;
	private Handler 				mSpanText			= null;	
	private Handler 				mScreenOff			= null;
	private Handler 				mbuttonOn			= null;

	private AccessibilityManager 	mManager			= null;
		
	private RelativeLayout 			mControlView 		= null;	
	private TextView 				mBookView 			= null;
	private TextView 				mTitleView 			= null;	
	private ImageButton 			mStartBtn			= null;
	private ImageButton 			mPrevBtn			= null;
	private ImageButton				mNextBtn			= null;		
	private Button					pageNumView         = null;
	private Button					moveSetBtn          = null;
	
	private int 					mLineNo 			= 0; 		
	private int 					mPageNo 			= 1; 		
	private int 					mTotalNo 			= 0; 		
	
	// 사용자가 직접 재생/정지 버튼을 누른경우
	private boolean 				ttsplayYn 			= false;
	
	// 사용자가 직접 버튼 누른경우 + Talkback이나 Receiver등에서 재생되는 경우 등 모든 경우의 재생 상태.
	private boolean 				mStartStopChk 		= true; 	 	
	private boolean				mEndChk				= true;

	
	private String 					mReadingText 		= ""; 

	private ArrayList 				mtxtlist 			= new ArrayList();    
	private ArrayList<String[]>		mMovelist 			= new ArrayList(); 	
	
	private String					mId					= "";
	private String 					mTitle				= "";
	private String 					mFile				= "";
	private String 					mText				= "";
	
	private int 					mOldLineNo 			= 0;   
	
	
	private TelephonyManager telephonyManager = null;

	private int						setNo				= 0; //페이지 기본
	private String					moveVal = "";
	private String msText = ""; 
	private String memoText = "";
	private String markText = "";	
	private String memoTalk = "";
	private String tempmemoTalk = ""; 
	private boolean memoYn = false;
	private boolean memoView = true;
	private boolean tempmemoView = false;
	private String outerMemo = "NO";
	private static boolean markSuccess = true;
	private ArrayList<HashMap<String, Integer>> mBookmarklist = new ArrayList<HashMap<String, Integer>>();	
	private ArrayList<HashMap<String, Integer>> mMemolist = new ArrayList<HashMap<String, Integer>>();	
	private int mMarkNo = 0;
	private int mMemoNo = 0;	
	private boolean markPrepare = false;	
	private boolean memoPrepare = false;		
	private boolean addYn = false;	
	private int mTTSNo = 0;
	private int mLength = 0;	
	private int markWordNo = 0;
	private String bookMsg = "";
	int wCi = 0;
	int aCi = 0;
	int lCi = 0;
	int pCi = 0;
    private Runnable mRunnable2; //단어검색 용    
    private Runnable mRunble; // 슬립타이머 용
    private Runnable mScreenRunnable; // 스크린 오프용 
    private Runnable mSpanRunnable; // 스크린 오프용 
    private Runnable mBtnRunnable2; // 단어/프레이즈용   
	private ArrayList<String> ttsSt = new ArrayList<String>(); // 기본 본문 TTS(줄단위 분할)
	InputMethodManager imm;	
	private String speakCutText;
	private boolean talkbackYn = false;
	private boolean pageYn = false;
	private boolean pConBackYn = false;
	private int sleepT = 0;
	public static Context mContext; // 브로드캐스트 연동때문에 일부러 static으로 설정
	private boolean firstYn = true;
	private boolean moveYn = false;
	private boolean mReplayAfterAccessibilityFocused = false;
	private int			mViewFocusedID = -1;
	private boolean	callYn = false;
	private boolean	homeYn = false;
	ArrayList<String[]> silHash = new ArrayList<String[]>();
	ArrayList<String[]> disHash = new ArrayList<String[]>();
	HashMap<String, String> myHash = new HashMap<String, String>();
	private TalkbackEventManager	mTalkbackManager = null;
	private SharedPreferences pref;
	private SharedPreferences.Editor settingEditor;
    SensorManager m_sensor_manager;
    Sensor m_sensor;
    PowerManager pm;
    WakeLock wakeLock;
    int per = 0;
	private String mFolderId;
    private ArrayList 	mMarklist = new ArrayList(); 
	private boolean recentYn = false;
	private String smilRef = "";
	private String	mCharSet = "EUC-KR";
    private ArrayList<String> wordSt = new ArrayList<String>();
	private boolean recentCall = false;
	private int ofs = 0; // 검색에 필요
	private String wordText = "";

	
	/**
	 * @Method Name  : phoneStateListener
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 플레이 중 전화 왔을 때 상태 변화에 따라 동작하는 Listener
	 * @param delayTime
	 */
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            try {
            // 전화가	 온 경우 or 받은 경우
            if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
            	if(ttsplayYn && homeYn == false) { // 플레이어 재생중 && 홈버튼으로 나가지 않았음
            		pageController("stop");
            		callYn = true; // 전화받은 상태체크 true로
    				recently(); //현재상태 저장
            	}
            } else if(state == TelephonyManager.CALL_STATE_IDLE) { // 전화를 끊음 or 대기상태
            	if(ttsplayYn && callYn && homeYn == false) { // 플레이어 재생중 && 전화받던 중 && 홈버튼으로 나가지 않았음
            		//callYn을 체크하지 않으면 대기상태일때 한번 더 동작함.
                	ttsPlay(); //tts 재생 재개
                	setStartImg(true); // 재생아이콘 재생 상태로
                	callYn = false; // 전화받은 상태체크 false로 
            	}
            }
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
    };
 
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final int layerid = R.layout.a_td_player;
		setContentView(layerid);
		
		AppControl.NA_Playable = 1;

		mContext = this;
		
		((AudioManager)getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(new ComponentName(this, BroadCast.class));
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mStartBtn 		= (ImageButton)findViewById(R.id.startBtn);
		
		mBookView 		= (TextView) findViewById(R.id.booklistView);
		mControlView	= (RelativeLayout) findViewById(R.id.controlView);
		mTitleView 		= (TextView) findViewById(R.id.tv_header_title);
		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		mPrevBtn = (ImageButton)findViewById(R.id.chapterBackBtn);
		mPrevBtn.setOnClickListener(this);
		mNextBtn = (ImageButton)findViewById(R.id.chapterNextBtn);
		mNextBtn.setOnClickListener(this);
		
		pageNumView = (Button)findViewById(R.id.pageNumView);
		pageNumView.setOnClickListener(this);			
		moveSetBtn = (Button)findViewById(R.id.moveSetBtn);
		moveSetBtn.setOnClickListener(this);			
		mTts = new TextToSpeech(getApplicationContext(), this);	
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setCancelable(false);         
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getString(R.string.dialog_loading));
		mManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);
		pref = mContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		settingEditor = pref.edit();
		       
 
		// ScreenOnOff Receiver에서 수신하는 상태변경 내용
		IntentFilter intentFilter = new IntentFilter(); 
	    intentFilter.addAction(Intent.ACTION_SCREEN_OFF); // 화면 꺼짐
	    intentFilter.addAction(Intent.ACTION_SCREEN_ON); //화면 켜짐
	    intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON); //이어폰,헤드셋 등의 정지/재생 버튼
	    intentFilter.addAction(Intent.ACTION_USER_PRESENT); //잠금화면을 해제함
	    intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION"); // 볼륨버튼 누름
		mScreenOff = new Handler();
		registerReceiver(screenOnOff, intentFilter);
        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        m_sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // SensorManager 를 이용해서 근접 센서 객체를 얻는다.
        m_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE); 
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotDimScreen"); 
		init(); // 화면 구성 후 기능실행으로 이동

		
		// 현재 플레이어 화면 내부에 있는 Talkback Focus 사용 개체 id
		int[] resIDs = {R.id.back_btn, R.id.tv_header_title, R.id.playermenuBtn, R.id.booklistView, 
				R.id.startBtn,R.id.chapterBackBtn,R.id.chapterNextBtn, R.id.pageNumView, R.id.moveSetBtn };

		//Talkback 포커스 관련 이벤트 수신		
		mTalkbackManager = new TalkbackEventManager(this);
		mTalkbackManager.setOnViewFocusedListener(new TalkbackEventManager.OnViewFocusedListener() {
			@Override
			public void onViewFocused(View view) {
				// TODO Auto-generated method stub
				mViewFocusedID = view.getId();
			}
		});
		// AccessibilityFocus 가 해당 객체로 왔을때 동작하는 Listener
		mTalkbackManager.setOnViewAccessibilityFocusedListener(new TalkbackEventManager.OnViewAccessibilityFocusedListener() {
			@Override
			public void onViewAccessibilityFocused(View view) {

				// Talkback Focus가 갔을때 포커스 진입 방법에 따라 읽는 텍스트 길이가 달라지므로, 2.5초씩 추가
				int delayAlpha = (view.getId() == mViewFocusedID) ? 2500 : 0; 
				int delayTime = 0;
				mReplayAfterAccessibilityFocused = true;
				if(firstYn == false) {
					if(mTTSNo > -1) {
						mTTSNo = mTTSNo -1;
					}
	       		 	mTts.stop();
	       		 	mStartStopChk = false;
				}
			
				switch (view.getId()) {												
				case R.id.back_btn:
					delayTime = 2000;
					break;										
				case R.id.playermenuBtn:
					delayTime = 2000;
					break;
				case R.id.tv_header_title:
					delayTime = 2000 + delayAlpha;
					break;						
				case R.id.pageNumView:
					delayTime = 2000;
					break;												
				case R.id.startBtn:
					delayTime = 2000 + delayAlpha;
					break;										
				case R.id.chapterBackBtn:
					delayTime = 2000 + delayAlpha;
					break;															
				case R.id.chapterNextBtn:
					delayTime = 2000 + delayAlpha;
					break;						
				}
				
				//현재 재생중인 경우, 위에서 얻은 delayTime 뒤에 재생 재개.
				if(ttsplayYn) {
					playTtsDelayed(delayTime); 
				}
				
				
				mViewFocusedID = -1;
			}
		});

		// AccessibilityEvent가 일어나도 관련 동작을 안하도록(Talkback에서 해당 내용을 읽어주는) 처리. 
		mTalkbackManager.setOnInitializeAccessibilityEventListener(new TalkbackEventManager.OnInitializeAccessibilityEventListener() {
			@Override
			public boolean onInitializeAccessibilityEvent(View host,
					AccessibilityEvent event) {
					boolean eventYn = false;
					if(host.getId() == R.id.defaultLayout || host.getId() == R.id.booklistView) { //책 내용이 있는 텍스트뷰는 Talkback 로 읽지 않음
						eventYn = false;
						return false;
					} else if (firstYn == true && event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
						playTtsDelayed(1000);
						eventYn = true;
					} else {
						eventYn = true;
					}
					return eventYn;
			}
		});
		mTalkbackManager.setResourceIDs(resIDs);
	}

	/**
	 * @Method Name  : playTtsDelayed
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 :
	 * @param delayTime
	 */
	private void playTtsDelayed(int delayTime) {
		mScreenOff.postDelayed(mScreenRunnable, delayTime);
	}


	//화면 꺼지거나, 화면이 꺼진 상태에서 다시 켜질 경우 재생을 계속함
	BroadcastReceiver screenOnOff = new BroadcastReceiver() {
		public static final String ScreenOff = "android.intent.action.SCREEN_OFF";
		public static final String ScreenOn = "android.intent.action.SCREEN_ON";
		
		private Date timeScreenOn = null;

		
		public void onReceive(Context contex, Intent intent) {
			mReplayAfterAccessibilityFocused = true;
			if (intent.getAction().equals(ScreenOff)) { // 화면이 꺼지는 경우
				Log.e("PROMIX", "근접센서 작동으로 화면 꺼짐 : ");
				if(mManager.isEnabled()) { //접근성 기능 사용 가능 상태일때
					talkbackYn = JwUtils.isTalkbackRunning(mContext); // talkback 실행상태 체크(특정어플 동작상태도 체크 가능)
					// talkback 실행중 && 현재 재생중 && 홈버튼 누른 상태 아님 && 통화중 아님
					if(talkbackYn && ttsplayYn && homeYn == false && callYn == false) {
						playTtsDelayed(4000);
					} 
				}
			} else if (intent.getAction().equals(ScreenOn)) { // 화면이 켜지는 경우
				if(mManager.isEnabled()) {
					talkbackYn = JwUtils.isTalkbackRunning(mContext);
					// talkback 실행중 && 현재 재생중 && 홈버튼 누른 상태 아님 && 통화중 아님
					if(talkbackYn && ttsplayYn && homeYn == false && callYn == false) {
						playTtsDelayed(5000); // 화면이 꺼질때와 켜질때 읽어주는 메세지의 길이가 다름
		    			speakCutText = disHash.get(mTTSNo)[1];
					} else {
						pageController("stop");
					}
				} else {
					//접근성 기능이 사용 가능한 상태가 아니라도 재생중 && 통화중이 아닌경우 재생 재개.
					if(ttsplayYn && callYn == false && homeYn == false) {
						playTtsDelayed(2000);
		    			speakCutText = disHash.get(mTTSNo)[1];
					} else {
						pageController("stop");
					}
				}
			} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) { // 락스크린을 사용자가 해제
				
				if(mManager.isEnabled()) {
					Date timeNow = new Date(System.currentTimeMillis());
					if( timeScreenOn == null || (timeNow.getTime() - timeScreenOn.getTime()) >= 3500 )  {					
						talkbackYn = JwUtils.isTalkbackRunning(mContext);
						// talkback 실행중 && 현재 재생중 && 홈버튼 누른 상태 아님 && 통화중 아님
						if(talkbackYn && ttsplayYn && homeYn == false && callYn == false) {
							playTtsDelayed(2000);
						} else {
							pageController("stop");
						}
					}
				} else if(mStartStopChk == false){
					pageController("stop");
				}
			} else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) { //이어폰 등의 재생/정지 버튼을 누르면 재생/일시정지
	            togglePlay();
			} else if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) { // 볼륨 조절 버튼을 누른경우
				talkbackYn = JwUtils.isTalkbackRunning(mContext);
				if(talkbackYn && ttsplayYn && homeYn == false && callYn == false) { 
					playTtsDelayed(7000); //볼륨 조절 버튼을 누른경우 Talkback 안내 메세지가 매우 김.
				}
			}
		}
	};
	
	
	/**
	 * @Method Name  : init
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 플레이어 화면 구성 후 초기기능 실행 (실행순서 2번째)
	 */
	public void init(){
		mHandler = new Handler(); //텍스트뷰 화면 구성하고 페이지 설정 Handler
		
		mTTSPlay = new Handler(){ //tts 재생 Handler
			 public void handleMessage(Message msg){
				if(!mReadingText.equals("") && PlayerSetting.isSetStartAuto()){
					setStartImg(true);
					ttsPlay();
				}
			 }
		};
			
		mLastPage = new Handler(){  // 마지막 페이지 안내 팝업 Handler
			 public void handleMessage(Message msg){
				 setStartImg(false);
				 mTts.stop();
 				 pageController("stop");
	    		 Intent i = new Intent(mContext, Popup_Message.class);
		    	 i.putExtra("MSG", getString(R.string.dialog_player_end_book));
		    	 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		         startActivityForResult(i, LASTPAGE_ACTIVITY);
			 }
		};
	
		mRunnable2 = new Runnable() {
	         @Override
	        public void run() {
	        	 mBookView.setText(mReadingText);
	     		 mBookView.setTextSize(ScreenSetting.getTextSize());
	     		 mBookView.setTextColor(ScreenSetting.getTextColor());
	     		 mBookView.setBackgroundColor(ScreenSetting.getBgColor());
	        	 setPageNo();
	        }
	    };	
		mScreenRunnable = new Runnable() { // Talkback 활성화시 delayTime후 재생 재개하는 Handler
	         @Override
	        public void run() {
	        	 if( mReplayAfterAccessibilityFocused ) {
	        		 mReplayAfterAccessibilityFocused = false;
	        		 mStartStopChk = ttsplayYn;
	        		 setStartImg(true);
	        		 ttsPlay(); 	        		 
	        	 }
	        }
	    };	
	    
		mSpanRunnable = new Runnable() {
	         @Override
	        public void run() {
	        	 spanWordText(wordText);
	        }
	    };
		mBtnRunnable2 = new Runnable() { //사용자 메모 재생 관련 Runnable
	         @Override
	        public void run() {
    			// Talkback 사용중 && 화면 들어와서 처음 재생 && 메모 내용이 없는 경우
    			if(mManager.isEnabled() && firstYn && (tempmemoView == false)) {   	        	

    			}   // Talkback 사용중 && 화면 들어와서 처음 재생 && 메모 내용이 있는 경우
    			else if(mManager.isEnabled() && firstYn && tempmemoView) { 
   	     			addYn = true; // 해당 값이 True면 TTSMini(단문재생 TTS)에서 Queue 초기화 대신 add
   	     			JwUtils.readingVoice(mContext, tempmemoTalk);//메모 먼저 TTS로재생
   	     			tempmemoView = false;
   	     			ttsPlay(); // 재생 재개
    			} // Talkback 미사용 && 화면 들어와서 처음 재생 && 메모 내용이 있는 경우
    			else if(mManager.isEnabled() == false && firstYn && tempmemoView) {
   	     			addYn = true;
   	     			JwUtils.readingVoice(mContext, tempmemoTalk);//메모 먼저 TTS로재생
   	     			tempmemoView = false;
   	     			ttsPlay();
    			} // Talkback 미사용 && 화면 들어와서 처음 재생  
    			else if(mManager.isEnabled() == false && firstYn){
    				JwUtils.readingVoice(mContext, mTitle);
   	     			ttsPlay();
    			}
	        }
	    };

    	mSleepTimer = new Handler();  // 수면 타이머 
		mSpanText = new Handler();
		mbuttonOn = new Handler();
		mDateCheck = new Handler();
		String filePath ="";
		// 도서상세화면에서 넘어온 Parameter 구성
		Intent i 	= getIntent();
		mId			= i.getStringExtra("ID"); // 도서 ID
		mTitle		= i.getStringExtra("TITLE"); // 책 제목
		filePath 	= i.getStringExtra("FILE"); // 파일 Path
		int page 	= 1; // 페이지 정보(북마크 등에서 오는경우, 없으면 1페이지)
		outerMemo	= i.getStringExtra("MEMO"); // 메모 내용0
		mFolderId	= i.getStringExtra("FOLDER"); // 폴더 ID
		if(mFolderId == null || "".equals(mFolderId)) {
			mFolderId = "0";
		}
		if(outerMemo == null || "".equals(outerMemo)) {
			outerMemo = "NO";
		}
		mFile = filePath;
		mTitleView.setText(mTitle);
		
		if(page > 0) {
			mPageNo = page;
		}
		
		settingConfirm(); // 환경설정에서 설정한 내용 적용		
		parsingText();
	}

	
	private void parsingText() {
		String charset = JwUtils.getCharsetFromTextFile(mFile);
		
		if( !charset.equals("ANSI") ) 
			mCharSet = charset;	
		
		if(textReader(mFile, mCharSet)){ 	
			lastPage();
			if(mtxtlist.size() < 2){  // 내용정보 구성 실패시 에러메세지 띄우고 종료.
				toastCustom(getString(R.string.player_parsing_error));
				onBackPressed();
			}
			//pageText(ttsplayYn);				
			
		} else {
			toastCustom(getString(R.string.player_parsing_error));
			finish();
		}

		//플레이어 기능설정 - 자료 열때 자동재생
		if(PlayerSetting.isSetStartAuto()) {
			mbuttonOn.postDelayed(mBtnRunnable2, 2000);
		} else {
			setStartImg(false);
		}
		mMovelist = PlayerSetting.getTxtMove(mContext);
		moveListSet();		
		if(recentCall == false) {
			recentCall();			
		} else {
	        pageText(false);
		}
	}
	
	private void recentCall() {
		recentCall = true;
		String recSet = DB.RECENTLY.INDEX.B_ID.toString() + "='" + mId +"'"; 
		Cursor cursor = getContentResolver().query(Recently.CONTENT_URI, Recently.projection, recSet, null, null);
        if(cursor.getCount() > 0) {
        	cursor.moveToFirst();
        	if(recentYn == false) {
        		mPageNo = Integer.parseInt(cursor.getString(DB.RECENTLY.INDEX.R_PAGE.ordinal()));
        	}
        	mTTSNo = cursor.getInt	(DB.RECENTLY.INDEX.R_LINE.ordinal());
        }
        cursor.close();
		if(recentYn || cursor.getCount() == 0) {
			JwUtils.readingVoice(mContext, mTitle +"" +getString(R.string.player_first_start));
		} else {
			if(mTTSNo == 0) {
				mTTSNo = 1;
			}
			JwUtils.readingVoice(mContext, mTitle +""+getString(R.string.player_td_start, mPageNo, mTTSNo));
		}
        pageText(false); 
	}
	
 
	/**
	 * @Method Name  : textReader
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 텍스트 파일 Parsing (실행순서 3번째)
	 * @param fileDir : init()에서 받은 txt파일 실제 경로
	 * @return
	 */
	public boolean textReader(String fileDir, String charset){
		boolean txtCheck = false;
		try {
			mProgressDialog.show();
			txtCheck = true;
			InputStream is = new FileInputStream(fileDir);
			mtxtlist = TextFileParser.textFileParser(is, charset);
			
			if(mtxtlist.size() == 0){
				mProgressDialog.dismiss();
				toastCustom(getString(R.string.player_parsing_error));
				finish();
			}else{
				txtCheck = true;
				mProgressDialog.dismiss();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return txtCheck;
	}	
	
	// 텍스트 파일의 마지막 페이지를 구함
	public void lastPage(){
		if(mtxtlist.size() > 0){
			int getValue = mtxtlist.size()-1; //맨 마지막 프레이즈 구함
			TextFileValue sv = (TextFileValue)mtxtlist.get(getValue);
			mTotalNo = sv.getPage(); //맨 마지막 프레이즈의 페이지가 몇페이인지 설정.
		}else{
			toastCustom(getString(R.string.player_parsing_error));
			finish();
		}
	}
	
	/**
	 * @Method Name  : pageDone
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 페이지 마지막까지 온 경우 다음 페이지로 이동
	 */
	private void pageDone() {
		mTTSNo = 0;			
		if(mLineNo >= mtxtlist.size() && mEndChk){ //마지막 페이지의 마지막 프레이즈
			new Thread(){
				 public void run(){
					 Message msg = mLastPage.obtainMessage(); //마지막 페이지 안내 팝업 
					 mLastPage.sendMessage(msg);
				 }
			}.start();
		}else {  // 자동재생 설정이 되어있는 경우
			pageSetting(true);			
			pageYn = true;
			mTTSNo = 0;
			pageText(ttsplayYn); // 다음 페이지 내용 구성
		}
		
		if(mLineNo >= mtxtlist.size() && !mEndChk){ 
			mEndChk = true;
		}		
	}
	
	public void setTts(TextToSpeech tts) {
		mTts = tts;
		mTts.setSpeechRate(SpeechSetting.getPlaySpeed());
		mTts.setPitch(SpeechSetting.getplayPitch());
		// Utterance ID를 TTS에 설정해 놓은 경우, Listener 발생
		mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override                
			public void onDone(String utteranceId){ // TTS를 다 읽었을 때
				
				if("TTSMINI".equalsIgnoreCase(utteranceId)) { //재생하는 줄이 단문인 경우
					if(firstYn) {
						playTtsDelayed(3000); //3초 뒤에 재생 재개
    					firstYn = false;
					}
				} else if("TTS MESSAGE".equalsIgnoreCase(utteranceId)) { // 재생한 줄이 책 내용인 경우
					firstYn = false;
					mTTSNo = mTTSNo + 1;
					if(mTTSNo < mLength && mLength > 0) {
						ttsRead(mStartStopChk);						
					} else { // 현재 재생중인 줄이 페이지 마지막까지 재생 완료한 경우
						pageDone(); //페이지 넘김 처리
					}				
				} 
			}
			
			@Override                
			public void onError(String utteranceId){

			}
			
			@Override                
			public void onStart(String utteranceId){
				if("TTS MESSAGE".equalsIgnoreCase(utteranceId) && ttsplayYn && mStartStopChk) {
					if(mTTSNo < mLength && mTTSNo >= 0 ) {
						speakCutText = disHash.get(mTTSNo)[1];
					} 					
				}
			}
		});        			    
	}
	
    // 해당 액티비티가 시작되면 근접 데이터를 얻을 수 있도록 리스너를 등록한다.   
    protected void onStart() {
        super.onStart();
        // 센서 값을 이 컨텍스트에서 받아볼 수 있도록 리스너를 등록한다.
        m_sensor_manager.registerListener(this, m_sensor, SensorManager.SENSOR_DELAY_UI );
    }

    // 해당 액티비티가 멈추면 근접 데이터를 얻어도 소용이 없으므로 리스너를 해제한다.
    public void onStop() {
        super.onStop();
        // 센서 값이 필요하지 않는 시점에 리스너를 해제해준다.
        m_sensor_manager.unregisterListener(this);
    	mScreenOff.removeCallbacks(mScreenRunnable);
    }
   
    // 정확도 변경시 호출되는 메소드. 센서의 경우 거의 호출되지 않으므로 따로 처리안함.
    public void onAccuracyChanged(Sensor sensor, int accuracy){    
    }    
    
    // 근접센서를 이용해 통화상태(폰을 얼굴가까이)가 되거나 커버 케이스를 덮으면 일시정지.
    public void onSensorChanged(SensorEvent event) {
        // 정확도가 낮은 측정값인 경우
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            // 몇몇 기기의 경우 accuracy 가 SENSOR_STATUS_UNRELIABLE 값을
            // 가져서 측정값을 사용하지 못하는 경우가 있기때문에 임의로 return ; 을 막는다.
            //return;
        }
        // 센서값을 측정한 센서의 종류가 근접 센서인 경우
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
        	if(event.sensor.getMaximumRange() > event.values[0]) {
            	if(ttsplayYn && callYn == false && homeYn == false) { // 플레이어 재생중 && 전화받던 중 아님 && 홈버튼으로 나가지 않았음
            		//callYn을 체크하지 않으면 대기상태일때 한번 더 동작함.            		
            		try{
            			mReplayAfterAccessibilityFocused = true;
						playTtsDelayed(3000); //3초 뒤에 재생 재개
            	    }
            	    catch(Exception e){
            	        //probably already released
            	    }
            	}
        	}
        	
        }
    }
    
    public void setBright(float value) { 
        Window mywindow = getWindow();      
        WindowManager.LayoutParams lp = mywindow.getAttributes(); 
        lp.screenBrightness = value; 
        mywindow.setAttributes(lp); 
    } 

	public void settingConfirm(){
		if(pageNumView != null && TDBaseActivity.mTypeface != null){
			pageNumView.setTypeface(TDBaseActivity.mTypeface, Typeface.BOLD);
		}
		mMovelist = PlayerSetting.getTxtMove(mContext);
		moveListSet();
		
		mTts.setSpeechRate(SpeechSetting.getPlaySpeed());
		mTts.setPitch(SpeechSetting.getplayPitch());
		mHandler.postDelayed(mRunnable2, 10);		
	}
		
	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			setTts(mTts);		
		} else {
			mTts = new TextToSpeech(getApplicationContext(), this);	
		}
		if(PlayerSetting.isSetStartAuto()) {
		new Thread(){  		
			public void run(){
				Message msg = mTTSPlay.obtainMessage();
				mTTSPlay.sendMessage(msg);
			}
		}.start();
		}
	}
		
	private void pageSetting(boolean nextYn) {	
		if(nextYn) {
			mPageNo = mPageNo + 1;
		} else { 
			mPageNo = mPageNo - 1;
		}
	}
	
	// 0부터 시작하면 페이지 계산이 잘못될 수 있음
	public int getLineNo(int page) {
		int line = 0;
		for(int i = 1; i < mtxtlist.size(); i++){ // for문 돌면서 현재 페이지에 맞는 Praise 탐색
			TextFileValue txt = (TextFileValue)mtxtlist.get(i);
			if(txt.getPage() == page){ 
				line = i;
				break;
			}
		}
		return line;
	}
	
	/**
	 * @Method Name  : pageText
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 플레이어 화면 중 책 내용을 실제로 구성 (실행순서 7/8 )
	 * @param chk : true시 ttsPlay() 실행. false면 책 내용만 구성하고 tts는 플레이 하지 않음.
	 */
	public void pageText(boolean chk){
		mReadingText = "";
		mLineNo = getLineNo(mPageNo);
		mOldLineNo = mLineNo;

		TextFileValue txt;
		if(mPageNo < 2) {		
			mPageNo = 1;
		}
		if(mLineNo >= mtxtlist.size()){  // 파일 맨 마지막 페이지&& 맨 마지막 프레이즈까지 온 경우.
			JwUtils.readingVoice(mContext, getString(R.string.player_page_last));// 마지막 페이지입니다 메세지 출력.(종료여부 물어보는 팝업은 다른곳에서 구성)
			mLineNo--;
		}

		ttsSt = new ArrayList<String>(); 
		for(int i = mLineNo; i < mtxtlist.size(); i ++ ) {
			txt = (TextFileValue)mtxtlist.get(mLineNo);
			if(txt.getPage() == mPageNo) {
				ttsSt.add(txt.getText().trim()); // tts에서 읽을 텍스트는 trim, 화면에는 빈칸도 정보이므로 그대로 보여줌		
				mLineNo++;
				mReadingText += txt.getText()+"\n";
			} else {
				break;
			}
		}
		mHandler.postDelayed(mRunnable2, 10);
		recently(); // 임시로 책갈피 저장(홈버튼 종료, 전원꺼짐 등 대비)		
		if(chk) {//pageText(chk) 가 true인 경우 (false면 화면에 책내용만 보여주고 TTS 재생은 하지 않음)
			ttsPlay(); // TTS 재생.
		}else if(pageYn){
			ttsMini(ttsSt.get(0));
			mTTSNo = 1;
			pageYn = false;
		}
	}

	// 페이지 번호 세팅
	private void setPageNo() {
		double rate = (double)((double)mPageNo/(double)mTotalNo) * 100;  // 디스크립션의 퍼센트 구하는 공식
		per = (int)rate;
		pageNumView.setText(getString(R.string.list_count_format_per_no_parenthesis, mPageNo+"", mTotalNo, per)); //텍스트는 1/n 으로만 표시
		pageNumView.setContentDescription(getString(R.string.list_count_format, mTotalNo, mPageNo+"", per));	
	}

	
	
	/**
	 * @Method Name  : pageController
	 * @작성일   : 2017. 6. 22.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 페이지 이동단위 관리(글자, 단어, 줄, 페이지, 헤딩, 프레이즈, 북마크 등. 검색은 따로 관리)
	 * @param action
	 */
	public void pageController(String action){
		// 이동단위 '일시정지'
		if(action.equals("stop")){
			setStartImg(false); // 재생/정지 아이콘을 일시정지 상태로	
			mStartStopChk = false; // tts 재생상태를 정지 상태로 
			mTts.stop(); // tts엔진을 stop (tts에는 일시정지 상태가 없음)
			mTTSNo = mTTSNo - 1;
			if(mTTSNo < 0) {
				mTTSNo = 0;
			}
		} //이동단위 글자단위
		else if(action.equals("alphaBack") || action.equals("alphaNext")){ 
			setStartImg(false); // 재생/정지 아이콘을 일시정지 상태로	
			mStartStopChk = false; // tts 재생상태를 정지 상태로 
			mTts.stop(); // tts엔진을 stop (tts에는 일시정지 상태가 없음)	
			ttsplayYn = false; // 사용자가 직접 일시정지 한것으로 간주

			//현재 페이지의 텍스트를 가져와서 글자별로 분할
			bookMsg = silHash.get(mTTSNo)[1];
			bookMsg = bookMsg.trim().replaceAll(" ","");
			bookMsg = bookMsg.trim().replaceAll("\n","");
			wordSt.clear();
			String[] tempSt = bookMsg.split("(?!^)\\b|\\B"); 			
			for(int i = 0; i < tempSt.length; i++) {
				wordSt.add(i, tempSt[i]);
			}
			
			if(pConBackYn) { //현재 누른 버튼이 뒤로가기 버튼 & 이전 페이지	
				aCi = wordSt.size();
				pConBackYn = false;
			}
			
			if(action.equals("alphaBack")) {
				aCi--; //이전 글자면 현 위치에서 -1 글자
			} else if (action.equals("alphaNext")) {
				aCi++; //다음 글자면 현 위치에서 +1 글자
			}
			
			if( (aCi <= 0 || aCi >= wordSt.size() )&&  mTTSNo >= 0 && mTTSNo + 1 < mLength){ // 줄을 넘김
				if(action.equals("alphaBack")) {					
					mTTSNo = mTTSNo - 1;
				} else if (action.equals("alphaNext")) {
					mTTSNo = mTTSNo + 1;
				}
				bookMsg = silHash.get(mTTSNo)[1];
				//sendBR();
				wordSt.clear();
				bookMsg = bookMsg.trim().replaceAll(" ","");
				bookMsg = bookMsg.trim().replaceAll("\n","");
				String[] nextSt = bookMsg.split("(?!^)\\b|\\B"); 
				for(int i = 0; i < nextSt.length; i++) {
					wordSt.add(i, nextSt[i]);
				}
				if(action.equals("alphaBack")) {					
					aCi = wordSt.size() - 1;
				} else if (action.equals("alphaNext")) {
					aCi = 0;
				}
				ttsMini(wordSt.get(aCi));
				wordText = wordSt.get(aCi);
				mSpanText.postDelayed(mSpanRunnable, 500);				
			} else if(aCi >= wordSt.size() && action.equals("alphaNext") && mTTSNo >= mLength -1) { // 페이지 넘김
				ttsMini(getString(R.string.player_word_last));
				aCi = 0;
				pageYn = false;
				mTTSNo = 0;
				pageSetting(true);
				if(mPageNo > mTotalNo) {
					pageController("stop");
					// 단 페이지 마지막입니다 경고는 출력
					JwUtils.readingVoice(mContext, getString(R.string.player_page_last));
					mPageNo = mPageNo - 1;
				} else {
					pageText(false);
					silMake(); // 페이지를 변경했으므로 silHash를 다시 제작
					pageController("alphaNext");
				}
			} else if(aCi <= 0 && action.equals("alphaBack") && mTTSNo <= 0 ) {				
				aCi = 0;
				pageYn = false;
				mTTSNo = 0;
				pageSetting(false);
				if(mPageNo == 0) {
					pageController("stop");
					JwUtils.readingVoice(mContext, getString(R.string.player_page_first));
					mPageNo = 1;
				} else {
					pConBackYn = true;
					pageText(false);
					silMake(); // 페이지를 변경했으므로 silHash를 다시 제작
					pageController("alphaBack");
				}
			} else {	
				ttsMini(wordSt.get(aCi));
				wordText = wordSt.get(aCi);
				mSpanText.postDelayed(mSpanRunnable, 500);
			}
			
		} // 이동단위 단어. (이동단위 글자와 처리 방법 같음) 
		else if(action.equals("wordBack") || action.equals("wordNext")){ 
			setStartImg(false); // 재생/정지 아이콘을 일시정지 상태로	
			mStartStopChk = false; // tts 재생상태를 정지 상태로 
			mTts.stop(); // tts엔진을 stop (tts에는 일시정지 상태가 없음)
			ttsplayYn = false;
			bookMsg = silHash.get(mTTSNo)[1];
			bookMsg = bookMsg.trim();
			wordSt.clear();
			String[] tempSt = bookMsg.split(" ");
			for(int i = 0; i < tempSt.length; i++) {
				wordSt.add(i, tempSt[i]);
			}
			if(pConBackYn) { //현재 누른 버튼이 뒤로가기 버튼 & 이전 페이지	
				aCi = wordSt.size();
				pConBackYn = false;
			}
			if(action.equals("wordBack")) {
				wCi--;
			} else if (action.equals("wordNext")) {
				wCi++;
			}
			if( (wCi <= 0 || wCi >= wordSt.size() )&& mTTSNo >= 0 && mTTSNo + 1 < mLength){
				if(action.equals("wordBack")) {					
					mTTSNo = mTTSNo - 1;
				} else if (action.equals("wordNext")) {
					mTTSNo = mTTSNo + 1;
				}
				bookMsg = silHash.get(mTTSNo)[1];
				bookMsg = bookMsg.trim();
				//sendBR();
				wordSt.clear();
				String[] nextSt = bookMsg.split(" "); 
				for(int i = 0; i < nextSt.length; i++) {
					wordSt.add(i, nextSt[i]);
				}
				if(action.equals("wordBack")) {					
					wCi = wordSt.size() - 1;
				} else if (action.equals("wordNext")) {
					wCi = 0;
				}
				ttsMini(wordSt.get(wCi));
				wordText = wordSt.get(wCi);
				mSpanText.postDelayed(mSpanRunnable, 500);				
			} else if(wCi >= wordSt.size() && action.equals("wordNext") && mTTSNo >= mLength -1) {
				//ttsMini(getString(R.string.player_word_last));
				wCi = 0;
				pageYn = false;
				mTTSNo = 0;
				pageSetting(true);
				if(mPageNo > mTotalNo) {
					pageController("stop");
					JwUtils.readingVoice(mContext, getString(R.string.player_page_last));
					mPageNo = mPageNo - 1;
				} else {
					pageText(false);
					silMake(); // 페이지를 변경했으므로 silHash를 다시 제작
					pageController("wordNext");
				}
			} else if(wCi <= 0 && action.equals("wordBack") && mTTSNo <= 0 ) {				
				//ttsMini(getString(R.string.player_word_first));
				wCi = 0;
				pageYn = false;
				mTTSNo = 0;
				pageSetting(false);
				if(mPageNo == 0) {
					pageController("stop");
					JwUtils.readingVoice(mContext, getString(R.string.player_page_first));
					mPageNo = 1;
				} else {
					pConBackYn = true;
					pageText(false);
					silMake(); // 페이지를 변경했으므로 silHash를 다시 제작
					pageController("wordBack");
				}
			} else {
				ttsMini(wordSt.get(wCi));
				wordText = wordSt.get(wCi);
				mSpanText.postDelayed(mSpanRunnable, 500);
			}
		} // 이동단위 줄(줄)인 경우
		else if(action.equals("lineBack") || action.equals("lineNext")){ 
			if(pConBackYn && action.equals("lineBack")) {
				lCi = mLength;			
				pConBackYn = false;
			}else if(pConBackYn && action.equals("lineNext")) {
				lCi = -1;
				pConBackYn = false;
			}else {
				lCi = mTTSNo; // 줄의 경우는 ttsPlay()에서 이미 나눠놓은 줄 배열을 이용
			}
			if(action.equals("lineBack")) {
				if(mStartStopChk == true) { //재생 중인 경우는 이미 줄 번호가 다음줄이기 때문에 2줄 이전으로 감
					lCi = lCi - 2;
				} else {
					lCi = lCi - 1;
				}
			} else if (action.equals("lineNext")) {
				lCi = lCi + 1;
			}
			if(lCi <= -1){ // 페이지 처음인 경우 이전 페이지로(단어,글자와 처리방식 거의같음)
				addYn = true;
				lCi = 0;
				pageYn = false;
				mTTSNo = 0;
				pageSetting(false);
				if(mPageNo == 0) {
					pageController("stop");
					JwUtils.readingVoice(mContext, getString(R.string.player_page_first));// 첫 페이지입니다 메세지 출력
					mPageNo = 1;
				} else {
					pConBackYn = true;
					pageText(ttsplayYn);
					silMake();
					pageController("lineBack");
				}
			} else if(lCi >= mLength) { // 페이지 마지막인경우 다음 페이지로 이동
				addYn = true;
				lCi = 0;
				mTTSNo = 0;
				pageYn = false;
				pageSetting(true);
				if(mPageNo > mTotalNo) {
					pageController("stop");
					JwUtils.readingVoice(mContext, getString(R.string.player_page_last));// 마지막 페이지입니다 메세지 출력
					mPageNo = mPageNo - 1;
				} else {
					pConBackYn = true;
					pageText(ttsplayYn);
					silMake();
					pageController("lineNext");
				}			
			}else {
				mTTSNo = lCi;
				if(mStartStopChk == true) { //현재 재생중이면 한줄 건너뛰어서 연속 재생
					ttsPlay();
				} else { // 현재 일시정지 중이면 해당 줄만 읽고 종료.
					ttsMini(silHash.get(lCi)[1]);
					wordText = silHash.get(lCi)[1];
					mSpanText.postDelayed(mSpanRunnable, 500);
				}
			}
		} else if(action.equals("pageBack")){ 
			
			//페이지 이동을 위해 일시정지, TTS를 맨 처음줄로 이동, 이동단위 재설정 등을 처리
			mTts.stop();
			mTTSNo = 0;
			pConBackYn = true;
			setStartImg(ttsplayYn); 		
			// 현 페이지 - 1 해서 이전페이지로 이동 
			pageSetting(false);
			if(mPageNo <= 0) {
				pageController("stop");
				JwUtils.readingVoice(mContext, getString(R.string.player_page_first));
				mPageNo = 1;
			} else {
				pageText(ttsplayYn);
				recently();
			}
		} else if(action.equals("pageNext")){  //이동단위 다음 페이지
			//페이지 이동을 위해 일시정지, TTS를 맨 처음줄로 이동, 이동단위 재설정 등을 처리
			mTts.stop();	
			mTTSNo = 0;
			pConBackYn = false;
			setStartImg(ttsplayYn);

			if(mLineNo == mtxtlist.size()-1) {// 맨 마지막 프레이즈 인 경우
				mEndChk = true; // 맨마지막 페이지 안내팝업 상태 설정
			}		
			pageSetting(true);
			if(mPageNo > mTotalNo) { // 마지막 페이지에서 또 다음페이지로 이동할 경우 종료 팝업 띄움
				pageController("stop");
				mPageNo = mTotalNo;
				new Thread(){
					 public void run(){
						 Message msg = mLastPage.obtainMessage();
						 mLastPage.sendMessage(msg);
					 }
				}.start();
			} else {			
				pageText(ttsplayYn);
				recently();
			}
		} else if(action.equals("markBack") || action.equals("markNext")){ 
			mTts.stop();
			mTTSNo = 0;
			moveYn = true;
			if(markPrepare == false || mBookmarklist == null) {
				bookmarkPrepared(action);
			} else {
				bookmarkMoved(action);
			}			
		} else if(action.equals("memoBack") || action.equals("memoNext")){ 
			mTts.stop();
			mTTSNo = 0;
			moveYn = true;
			if(memoPrepare == false || mMemolist == null) {
				memoPrepared(action);
			} else {
				memoMoved(action);
			}
		} else if(action.equals("searchBack")){ 
			if(msText == null || msText.isEmpty()) {
				toastCustom(getString(R.string.popup_search_noword));
				return;
			}
			mTts.stop();
			mStartStopChk = false;
			setStartImg(false); // 재생/정지 아이콘을 일시정지 상태로	
			mStartStopChk = false;
        	String tvt = ""; 
        	int ofe = -1;
        	for(int i = mTTSNo-1; i >= 0; i-- ){
				tvt = disHash.get(i)[1];
				ofe = tvt.indexOf(msText, ofs);
				if(ofe != -1) {
					mTTSNo = i;
					break;
				}
			}
        	if(ofe != -1) {
        		speakCutText = disHash.get(mTTSNo)[1];
				ttsMini(speakCutText);
        		mSpanText = new Handler();
		    	mSpanText.postDelayed(mSpanRunnable, 1000);
        	} else {
        		ofs = 0;
    			mOldLineNo--;
    			int seText = 0;
				for(int i = mOldLineNo; i > 0; i--){
					TextFileValue txt = (TextFileValue)mtxtlist.get(i);
					seText =  txt.getText().indexOf(msText,0);
	
	       			if(i <= 1){ 
	       				toastCustom(getString(R.string.player_search_fail));
	        			break;
	    			}
					if(seText != -1) {
						for(int j = 0; j < mtxtlist.size(); j++){
							TextFileValue btxt = (TextFileValue)mtxtlist.get(j);
							if(btxt.getPage() == txt.getPage()){
								mPageNo = j;
								break;
							}
						}
	   					pageText(false);
	   					silMake();
	   		        	for(int k = 0; k < disHash.size(); k++ ){
	   						tvt = disHash.get(k)[1];
	   						ofe = tvt.indexOf(msText, ofs);
	   						if(ofe != -1) {
	   							mTTSNo = k;
	   							break;
	   						}
	   					}	   					
	            		speakCutText = disHash.get(mTTSNo)[1];
						ttsMini(mPageNo + getString(R.string.common_page) +" "+ speakCutText);
	           		    mSpanText = new Handler();
	           		    mSpanText.postDelayed(mSpanRunnable, 1000);
	 					break;
					} //if 
	        	} //for
			} //else	
		} else if(action.equals("searchNext")){ 
			if(msText == null || msText.isEmpty()) {
				toastCustom(getString(R.string.popup_search_noword));
				return;
			}
			mTts.stop();
			setStartImg(false); // 재생/정지 아이콘을 일시정지 상태로	
			mStartStopChk = false;
			String tvt = "";
        	int ofe = -1;
        	for(int i = mTTSNo+1; i < silHash.size(); i++ ){
				tvt = disHash.get(i)[1];
				ofe = tvt.indexOf(msText, ofs);
				if(ofe != -1) {
					mTTSNo = i;
					break;
				}
			}
        	if(ofe != -1) {
        		speakCutText = disHash.get(mTTSNo)[1];
				ttsMini(speakCutText);
        		mSpanText = new Handler();
		    	mSpanText.postDelayed(mSpanRunnable, 1000);
        	} else {
        		ofs = 0;
    			mLineNo++;
      			if(mLineNo >= mtxtlist.size()){ 
      				toastCustom(getString(R.string.player_search_fail));
      			}
      			
				for(int i = mLineNo; i < mtxtlist.size(); i++){
					TextFileValue txt = (TextFileValue)mtxtlist.get(i);
					int seText =  txt.getText().indexOf(msText,0);
	       			if(i >= mtxtlist.size() - 1){
	       				toastCustom(getString(R.string.player_search_fail));
	        			if("".equals(moveVal)) {
	    					setNo = 0; //검색 결과가 없으므로 디폴트로 복원
	    					moveVal = getString(R.string.player_page_en);   
	        			}
	    				break;
	    			}
					if(seText != -1) {
	   					mPageNo = txt.getPage();
	   					pageYn = false;
	   					pageText(false);
	   					silMake();
	   		        	for(int j = 0; j < disHash.size(); j++ ){
	   						tvt = disHash.get(j)[1];
	   						ofe = tvt.indexOf(msText, ofs);
	   						if(ofe != -1) {
	   							mTTSNo = j;
	   							break;
	   						}
	   					}
	   					
	            		speakCutText = disHash.get(mTTSNo)[1];
	    				ttsMini(mPageNo + getString(R.string.common_page) +" "+ speakCutText);
	   	        		mSpanText = new Handler();
	   			    	mSpanText.postDelayed(mSpanRunnable, 1000);
	 					break;
					} //if 
	        	} //for
			} //else	
		}
	}
	
	//마크 이동 준비
	private void bookmarkPrepared(String action) {
		int min = 10000;
		int des = 0;
		int accPage = 0;
		int accMark = 0;
		int accLine = 0;
    	Cursor cursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, DB.BOOKMARK.INDEX.B_ID.toString() + "='" + mId +"' and " + DB.BOOKMARK.INDEX.M_TYPE + " =" + 0 , null, DB.BOOKMARK.INDEX.M_PAGE.toString() + " ASC");
    
        if( 0 == cursor.getCount() ){
    		showDialog(getString(R.string.dialog_no_text_bookmark), getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	
                }
            });
    		cursor.close();
    	} else {
    		while(cursor.moveToNext()) {
    			HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
    			tempMap.put("PAGE", cursor.getInt(DB.BOOKMARK.INDEX.M_PAGE.ordinal()));
    			tempMap.put("LINE", cursor.getInt(DB.BOOKMARK.INDEX.M_LINE.ordinal()));
    			mBookmarklist.add(tempMap);
    		}
    		
    		for(int i = 0; i < mBookmarklist.size(); i++ ){
    			HashMap<String, Integer> takeMap = (HashMap<String,Integer>)mBookmarklist.get(i);
    			int tempP = takeMap.get("PAGE");
    			int tempL = takeMap.get("LINE");
    			if(mPageNo < tempP ) {
    				des = tempP - mPageNo; 
    			} else if (mPageNo > tempP ) {
    				des = mPageNo - tempP;
    			}
    			if(des < min) {
    				min = des;
    				accPage = tempP;
    				accMark = i;
    				accLine = tempL;
    			}
    		}   
    		cursor.close();
    		if(accPage != 0 && accPage <= mTotalNo) {
    			mMarkNo = accMark;
    			mPageNo = accPage;
    			mTTSNo = accLine;
    			markPrepare = true;
    			pageText(ttsplayYn);    			
    		}
    	}
	}
	
	// 마크에서 마크로 이동
	private void bookmarkMoved(String action) {
		if(action.equals("markNext")) {
			mMarkNo++;
		} else if (action.equals("markBack")) {
			mMarkNo--;
		}
		if(mMarkNo >= mBookmarklist.size()) {
			toastCustom(getString(R.string.player_lastmark));
			mMarkNo = mBookmarklist.size() - 1;
		} else if (mMarkNo < 0) {
			toastCustom(getString(R.string.player_firstmark));
			mMarkNo = 0;
		}
		mPageNo = mBookmarklist.get(mMarkNo).get("PAGE");
		mTTSNo = mBookmarklist.get(mMarkNo).get("LINE");
		pageText(ttsplayYn); 		
	}
	
	// 메모이동 준비
	private void memoPrepared(String action) {
		int min = 10000;
		int des = 0;
		int accPage = 0;
		int accMark = 0;
		int accLine = 0;
    	Cursor cursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, DB.BOOKMARK.INDEX.B_ID.toString() + "='" + mId +"' and " + DB.BOOKMARK.INDEX.M_TYPE + " =" + 1 , null, DB.BOOKMARK.INDEX.M_PAGE.toString() + " ASC");
    
        if( 0 == cursor.getCount() ){
    		showDialog(getString(R.string.dialog_no_text_memo), getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	
                }
            });
    		cursor.close();
    	} else {
    		while(cursor.moveToNext()) {
    			HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
    			tempMap.put("PAGE", cursor.getInt(DB.BOOKMARK.INDEX.M_PAGE.ordinal()));
    			tempMap.put("LINE", cursor.getInt(DB.BOOKMARK.INDEX.M_LINE.ordinal()));
    			mMemolist.add(tempMap);
    		}
    		
    		for(int i = 0; i < mMemolist.size(); i++ ){
    			HashMap<String, Integer> takeMap = (HashMap<String,Integer>)mMemolist.get(i);
    			int tempP = takeMap.get("PAGE");
    			int tempL = takeMap.get("LINE");
    			if(mPageNo < tempP ) {
    				des = tempP - mPageNo; 
    			} else if (mPageNo > tempP ) {
    				des = mPageNo - tempP;
    			}
    			if(des < min) {
    				min = des;
    				accPage = tempP;
    				accMark = i;
    				accLine = tempL;
    			}
    		}   
    		cursor.close();
    		if(accPage != 0 && accPage <= mTotalNo) {
    			mMemoNo = accMark;
    			mPageNo = accPage;
    			mTTSNo = accLine;
    			memoPrepare = true;
    			pageText(ttsplayYn);    			
    		}
    	}
	}
	
	// 메모에서 메모로 이동
	private void memoMoved(String action) {
		if(action.equals("memoNext")) {
			mMemoNo++;
		} else if (action.equals("memoBack")) {
			mMemoNo--;
		}
		if(mMemoNo >= mMemolist.size()) {
			toastCustom(getString(R.string.player_lastmemo));
			mMemoNo = mMemolist.size() - 1;
		} else if (mMemoNo < 0) {
			toastCustom(getString(R.string.player_firstmemo));
			mMemoNo = 0;
		}
		mPageNo = mMemolist.get(mMemoNo).get("PAGE");
		mTTSNo = mMemolist.get(mMemoNo).get("LINE");
		pageText(ttsplayYn); 		
	}
	
	private void ttsMini(String lineText){
		HashMap<String, String> miniHash = new HashMap<String, String>();
		miniHash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
		miniHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TTSMINI");
        if (mTts.isSpeaking()) {
            mTts.stop();
        }
		if(addYn){
			mTts.speak(lineText, TextToSpeech.QUEUE_ADD, miniHash);
			addYn = false;
		} else {
			mTts.speak(lineText, TextToSpeech.QUEUE_FLUSH, miniHash);			
		}
	}
	
	private void searchConfirm(String ett) {
		if(ett == null || ett.isEmpty() || ett.trim().length() == 0) {
			toastCustom(getString(R.string.popup_search_noword));
			return;
		} else {
			msText = ett;
			moveVal = "search";   					
			for(int i = 0; i < mMovelist.size(); i++) {
				if("search".equals(mMovelist.get(i)[0])) {
					setNo = i; //텍스트 검색인 경우만 해당. 버튼 한번 더 누르면 글자로.
					moveSetBtn.setText(mMovelist.get(setNo)[1].toString());
					break;
				}
			}

        	String tvt = "";
        	int ofe = -1;					
			
        	for(int i = mTTSNo+1; i < disHash.size(); i++ ){
				tvt = disHash.get(i)[1];
				ofe = tvt.indexOf(ett, ofs);
				if(ofe != -1) {
					mTTSNo = i;
					break;
				}
			}
        	
        	if(ofe != -1) {
        		speakCutText = disHash.get(mTTSNo)[1];
				ttsMini(speakCutText);
        		mSpanText = new Handler();
		    	mSpanText.postDelayed(mSpanRunnable, 1000);
        	} else {
        		ofs = 0;
        	for(int i = mLineNo; i < mtxtlist.size(); i++){
    			TextFileValue txt = (TextFileValue)mtxtlist.get(i);
    	
    			if(i >= mtxtlist.size() - 2){ 
    				mLineNo--;
    				toastCustom(getString(R.string.player_search_fail));
    				if("".equals(moveVal)) {
    					setNo = 0; //검색 결과가 없으므로 디폴트로 복원
    					moveSetting();
    				}
    				break;
    			}
    			int seText =  txt.getText().indexOf(ett,0);
    			if(seText != -1) {
    				mPageNo = txt.getPage();				
    				pageText(false);    
   					silMake();
   		        	for(int j = 0; j < disHash.size(); j++ ){
   						tvt = disHash.get(j)[1];
   						ofe = tvt.indexOf(msText, ofs);
   						if(ofe != -1) {
   							mTTSNo = j;
   							break;
   						}
   					}
   					
            		speakCutText = disHash.get(mTTSNo)[1];
    				ttsMini(mPageNo + getString(R.string.common_page) +" "+ speakCutText);
            		mSpanText = new Handler();
    		    	mSpanText.postDelayed(mSpanRunnable, 1000);
					break;
    			} //if
        	} // for
        } // else      	
    }
	}
      	
	/**
	 * @Method Name  : ttsPlay
	 * @작성일   : 2017. 6. 18.
	 * @작성자   : leee
	 * @변경이력  :
	 * @Method 설명 : 책 내용 화면구성을 마친 후 TTS 재생 (실행순서 8/8)
	 */

	public void ttsPlay(){ 
		if(!mReadingText.equals("")){ // 화면 내용이 존재하는 경우.	
			mStartStopChk = ttsplayYn;
			mTts.setSpeechRate(SpeechSetting.getPlaySpeed());
			mTts.setPitch(SpeechSetting.getplayPitch());

			myHash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
			myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TTS MESSAGE"); // TTS UTTERANCE_ID 설정
					
			if(!"NO".equals(outerMemo) && !"".equals(outerMemo) && outerMemo !=null && memoView) { // 사용자 메모가 있는경우 처리
				memoTalk = "사용자 메모"+outerMemo +" 메모 끝";
				tempmemoTalk = memoTalk;
				outerMemo = "NO"; // 메모 보여줬으므로 초기화(DB와는 상관없음)
				tempmemoView = true;
    			memoView = false;
			}
			silMake();

			if (moveYn == true) {
				mTTSNo = 0;
				moveYn = false;
			}
							
			if(mTTSNo >= mLength || mTTSNo < 0) { //플레이어 외부에서 받아온 줄 정보가 실제 재생될 수 없는 줄인 경우
				mTTSNo = 0; //맨 첫번째 줄부터 재생
			}
			if(memoTalk.contains("사용자")) { //사용자 메모가 있는경우
				ttsSt.add(mTTSNo, memoTalk); // 메모 내용부터 플레이
				//mTts.speak(memoTalk, TextToSpeech.QUEUE_FLUSH, myHash);
			}

			ttsRead(mStartStopChk);			
		}
	}

	private void silMake() {		
		String silTemp ="";
		
		silHash.clear();
		disHash.clear();
		for(int i = 0; i < ttsSt.size(); i++) { // For문 돌면서 실제 재생 내용을 만듬.
			silTemp = ttsSt.get(i);
			if(!"".equals(silTemp) && !" ".equals(silTemp) && !"/'/g".equals(silTemp) && !"\"".equals(silTemp) ) {
				String[] drSize = {""+i, silTemp}; 
				disHash.add(drSize);
				silTemp = SpeechSetting.getRepeatClear(silTemp);
				silTemp = SpeechSetting.getPunctString(silTemp, mContext); // TTS읽기 설정 적용
				if(SpeechSetting.setReadCHLetter == true && isHanja(silTemp)) {
					Hanja hanja = Hanja.getInstance();
					silTemp = hanja.toHangul(silTemp);
				}				
				String[] prSize = {""+i, silTemp}; 
				silHash.add(prSize);					
			}

		}	

		mLength = silHash.size();
		//pLength = ttsSt.size();
	}
	
	
	private void ttsRead(Boolean play) {
		if(play) {
			String sd = silHash.get(mTTSNo)[1];
			mTts.speak(sd, TextToSpeech.QUEUE_FLUSH, myHash);
		} else {
			mTts.stop();
		}
	}
	
	public void moveValSet() {
		setNo++;			
		
		if (setNo >= mMovelist.size()) {
			setNo = 0;			
		} else if(setNo < 0) {
			setNo = mMovelist.size()-1;
		}
		moveSetting();
	}

	private void moveSetting() {
		moveVal = mMovelist.get(setNo)[0].toString();
		mPrevBtn.setContentDescription(getString(R.string.common_back) + mMovelist.get(setNo)[1].toString());
		mNextBtn.setContentDescription(getString(R.string.common_next) + mMovelist.get(setNo)[1].toString());
		moveSetBtn.setText(mMovelist.get(setNo)[1].toString());
		JwUtils.readingVoice(mContext, getString(R.string.player_move_size_sel,mMovelist.get(setNo)[1].toString())); 
		if(ttsplayYn) {
			playTtsDelayed(3000);
		}
	}
	private void moveListSet() {	
		Cursor markcursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, DB.BOOKMARK.INDEX.B_ID.toString() + "='" + mId +"' and " + DB.BOOKMARK.INDEX.M_TYPE + " =" + 0 , null, DB.BOOKMARK.INDEX.M_PAGE.toString() + " ASC");
		Cursor memocursor = getContentResolver().query(BookMark.CONTENT_URI_LIST, BookMark.projection, DB.BOOKMARK.INDEX.B_ID.toString() + "='" + mId +"' and " + DB.BOOKMARK.INDEX.M_TYPE + " =" + 1 , null, DB.BOOKMARK.INDEX.M_PAGE.toString() + " ASC");
		for(int i =0 ; i < mMovelist.size(); i++) {
			if(markcursor.getCount() == 0 && "mark".equals(mMovelist.get(i)[0])) {
				mMovelist.remove(i);
			}			
			if(memocursor.getCount() == 0 && "memo".equals(mMovelist.get(i)[0])) {
				mMovelist.remove(i);
			}
		}
		moveVal = mMovelist.get(setNo)[0].toString();
		mPrevBtn.setContentDescription(getString(R.string.common_back) + mMovelist.get(setNo)[1].toString());
		mNextBtn.setContentDescription(getString(R.string.common_next) + mMovelist.get(setNo)[1].toString());
		
		markcursor.close();
		memocursor.close();
	}	
	public void chapterBack(){
		if(mPageNo == 1 && (moveVal.equals(getString(R.string.player_page_en, "")))){
			JwUtils.readingVoice(mContext, getString(R.string.player_page_first));
		}
		if(moveVal.equals("")){
			moveVal = getString(R.string.player_page_en, "");
		}           
		pageController(moveVal+"Back");
	}

	public void chapterNext(){
		if(mTotalNo <= mPageNo && (moveVal.equals(getString(R.string.player_page_en, "")))){
			JwUtils.readingVoice(mContext, getString(R.string.player_page_last));
		}
		if(moveVal.equals("")){
			moveVal = getString(R.string.player_page_en, "");
		}
		pageController(moveVal+"Next");		
	}
	
	public void togglePlay() {
		if(mStartStopChk){			
			pageController("stop");
			ttsplayYn = false;
			mStartStopChk = false;
			JwUtils.readingVoice(mContext, getString(R.string.player_stop)); // 일시정지 문구는 반드시 퀵메세지
			recently();
		}else{ 
			mTts.stop();			
			ttsplayYn = true;
			mStartStopChk = true;
			setStartImg(true);
			mTTSNo = mTTSNo -1;
			if(mTTSNo < 0) {
				mTTSNo = 0;
			}
			ttsPlay();
		}	
	}
	
	public void setStartImg(boolean chk){
		if(chk){
			mStartBtn.setBackgroundResource(R.drawable.play_icon_ps);			
			mStartBtn.setContentDescription(getString(R.string.player_stop));				
		}else{
			mStartBtn.setBackgroundResource(R.drawable.play_icon_p);			
			mStartBtn.setContentDescription(getString(R.string.player_start));						
		}
	}	
	
    private void setTimer(int sleepTime) {
    	mRunble = new Runnable() {
    		@Override
    		public void run() {
        		mSleepTimer.removeMessages(0);
        		if(sleepT != 0) {
        			recently(); 
        			settingEditor.putInt(getString(R.string.setting_sleep), 0).commit();
        			if (mTts != null){
        	            pageController("stop");
        	        }
        		}
        	}
    	};

    	if(sleepTime == 0) {
    		mSleepTimer.removeCallbacks(mRunble);
    		Toast.makeText(mContext, getString(R.string.player_timer_off), Toast.LENGTH_LONG).show();
    	} else {
        	Toast.makeText(mContext, sleepTime + getString(R.string.player_timer_on), Toast.LENGTH_LONG).show();
    		int sleep = sleepTime * 1000 * 60;
    		mSleepTimer.postDelayed(mRunble, sleep);
    	}
    }
    
	public void bookMark(){
		String 	qWhere 	= "";
		int 	line 	= 0;
		int 	page 	= 0;
		int		mType	= 0;
		String 	text 	= markText;
		line = markWordNo;
		if(disHash.size() > 1 && disHash.size() >= mTTSNo +1 && memoYn == true) { // 메모일 경우 텍스트 변경
			text = memoText + " / " + disHash.get(mTTSNo)[1];
		} 
		try {
			URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String popupText = getString(R.string.dialog_book_mark);

		if(memoYn) {
			mType = 1;
			popupText = getString(R.string.popup_confirmMemo);
		} else {
			text = markText;			
			memoText = "";
		}
		
		if(markSuccess) {
			try {
		qWhere = DB.BOOKMARK.INDEX.B_ID.toString() + " = '" + mId + "' AND " + DB.BOOKMARK.INDEX.M_PAGE.toString() + " = '" + page + "' AND " + DB.BOOKMARK.INDEX.M_LINE.toString() + " = '" + line + "'";
		
		
		Cursor cursor = getContentResolver().query(BookMark.CONTENT_URI, BookMark.projection, qWhere, null, null);
		ContentValues values = new ContentValues();
		if(cursor == null || cursor.getCount() ==0) {
	        values.put( DB.BOOKMARK.INDEX.B_ID.toString(), 			mId);		
	        values.put( DB.BOOKMARK.INDEX.M_TITLE.toString(), 		mTitle);
        	values.put( DB.BOOKMARK.INDEX.M_PAGE.toString(), 		mPageNo);
        	values.put( DB.BOOKMARK.INDEX.M_LINE.toString(), 		line);
	        values.put( DB.BOOKMARK.INDEX.M_CATEGORY.toString(), 	""+page);
	        values.put( DB.BOOKMARK.INDEX.M_TEXT.toString(), 		text);	
	        values.put( DB.BOOKMARK.INDEX.M_FILENAME.toString(), 	smilRef);
	        values.put( DB.BOOKMARK.INDEX.M_TYPE.toString(), 		mType); // 0: 텍스트 마크, 1:텍스트 메모, 2: 오디오마크, 3:오디오메모, 4: 녹음 마크, 5: 녹음 메모
	        values.put( DB.BOOKMARK.INDEX.M_DATE.toString(), 		JwUtils.getNowTime());
	        if(memoYn) {
	        	values.put( DB.BOOKMARK.INDEX.M_MEMO.toString(),	memoText);	 	        
	        }
	        getContentResolver().insert(BookMark.CONTENT_URI, 		values);			
		} else if ( 0 < cursor.getCount() ){
			cursor.moveToFirst();
			values.put( DB.BOOKMARK.INDEX.M_DATE.toString(), 		JwUtils.getNowTime());
			values.put( DB.BOOKMARK.INDEX.M_TYPE.toString(), 		mType);
	        if(memoYn) {
	        	values.put( DB.BOOKMARK.INDEX.M_MEMO.toString(), 	memoText);	 			
	        } else {
		        values.put( DB.BOOKMARK.INDEX.M_TEXT.toString(), 		text);
	        }
			getContentResolver().update(BookMark.CONTENT_URI, values, DB.BOOKMARK.INDEX.B_ID.toString() + "='" + mId + "' ", null);			
		}
   
		cursor.close();

		} catch (Exception e){
			e.printStackTrace();
			if(memoYn) {
				popupText = getString(R.string.popup_bookmemo_fail);
			} else {
				popupText = getString(R.string.popup_bookmark_fail);				
			}
		}
		} else {
			if(memoYn) {
				popupText = getString(R.string.popup_bookmemo_fail);
			} else {
				popupText = getString(R.string.popup_bookmark_fail);				
			}
		}
       showDialog(popupText, getString(R.string.common_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	markPrepare = false;
            	memoPrepare = false;
				moveListSet();		
				mTTSNo = mTTSNo -1;
				if(mTTSNo < 0) {
					mTTSNo = 0;
				}
		        memoYn = false;
		        memoText = "";
		        markText = "";
		        if(ttsplayYn) {
		        	pageController("stop");
					playTtsDelayed(2000);
		        } else {
		        	pageController("stop");
		        }        
            }
        });
	}
	
	public void recently(){
		String 	qWhere 	= "";
		String currPage = mPageNo + ""; 
		if(mPageNo >= mTotalNo) {
			currPage = "1";
			markWordNo = 0;
		} 
		qWhere = DB.RECENTLY.INDEX.B_ID.toString() + " = '" + mId + "'";
		Cursor cursor = mContext.getContentResolver().query(Recently.CONTENT_URI, Recently.projection, qWhere, null, null);
		ContentValues values = new ContentValues();
		if( 0 < cursor.getCount() ){ 
			cursor.moveToFirst();
	        values.put( DB.RECENTLY.INDEX.R_PAGE.toString(), 		currPage);
	        values.put( DB.RECENTLY.INDEX.R_LINE.toString(), 		mTTSNo);	        
	        values.put( DB.RECENTLY.INDEX.R_TEXT.toString(), 		mText);
	        values.put( DB.RECENTLY.INDEX.R_CATEGORY.toString(), 	"");
	        values.put( DB.RECENTLY.INDEX.R_TYPE.toString(), 		0);
	        values.put( DB.RECENTLY.INDEX.R_DATE.toString(), 		JwUtils.getNowTime());
	        mContext.getContentResolver().update(Recently.CONTENT_URI, values, DB.RECENTLY.INDEX.B_ID.toString() + "='" + mId + "' ", null);
		}else{
			values.put( DB.RECENTLY.INDEX.B_ID.toString(), 			mId);		
	        values.put( DB.RECENTLY.INDEX.R_TITLE.toString(), 		mTitle);
	        values.put( DB.RECENTLY.INDEX.R_PAGE.toString(), 		currPage);
	        values.put( DB.RECENTLY.INDEX.R_LINE.toString(), 		mTTSNo);
	        values.put( DB.RECENTLY.INDEX.R_CATEGORY.toString(), 	"");
	        values.put( DB.RECENTLY.INDEX.R_TEXT.toString(), 		mText);
	        values.put( DB.RECENTLY.INDEX.R_FILENAME.toString(), 	smilRef);
	        values.put( DB.RECENTLY.INDEX.R_TYPE.toString(), 		0);
	        values.put( DB.RECENTLY.INDEX.R_DATE.toString(), 		JwUtils.getNowTime());
	        mContext.getContentResolver().insert(Recently.CONTENT_URI, 		values);
		}
		cursor.close();
	}
	
	public void onClick(View v) {
		final int key = v.getId();
		
		mReplayAfterAccessibilityFocused = false;
		double rate = (double)((double)mPageNo/(double)mTotalNo) * 100; 
		per = (int)rate;
		Intent i;
		switch (key) {							
			case R.id.startBtn:
				togglePlay();
				break;				
			case R.id.chapterBackBtn:
				chapterBack();
				break;
			case R.id.chapterNextBtn:
				chapterNext();
				break;	
			case R.id.backBtn:
				onBackPressed();
				break;
			case R.id.playermenuBtn:
				menuOpen();
				break;				
			case R.id.moveSetBtn:
				moveValSet();
				break;
			case R.id.pageNumView:
				goPageMove();
				break;
			default:
				break;
		}
	}
	
	public void addMark(){
		Intent i = new Intent(mContext, MarkActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, BOOKMARK_ACTIVITY);
	}
		
	private void goSearch() {
		Intent i = new Intent(mContext, TDSearch.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, SEARCH_ACTIVITY);
	}
		
	private void goPercentMove() {
		Intent i = new Intent(mContext, TDPerMove.class);
		i.putExtra("TOTAL", mTotalNo);
		i.putExtra("PER", per);
		i.putExtra("AUDIO", false);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, QUICK_ACTIVITY);		
	}
	private void goPageMove() {		
		Intent i = new Intent(mContext, TDPageMove.class);
		i.putExtra("TOTAL", mTotalNo);
		i.putExtra("PAGE", mPageNo);    		
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, QUICK_ACTIVITY);	
	}
	
	// onActivityResult 에 재생/정지는 넣지 말것. 이유는 알수 없으나 TTS재생에 이상현상 발생
	//(해당 페이지 재생이 끝날때까지 tts.stop() 이나 이전/다음 페이지 등 컨트롤 동작 안함)
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		mReplayAfterAccessibilityFocused = true;
		if (resultCode == RESULT_OK) { 
			switch (requestCode) {
				case QUICK_ACTIVITY: 
				mTts.stop();
					mTTSNo = 0;
					mOldLineNo = 0;					
					moveYn = true;
					mPageNo = intent.getExtras().getInt("PAGE");
					JwUtils.readingVoice(mContext, mPageNo + getString(R.string.common_page)); 
					if(ttsplayYn) {
						setStartImg(true);
						pageText(false);
						playTtsDelayed(1000);
					} else {
						pageText(false);						
					}
					break;
				case TABLE_ACTIVITY: 
					mTts.stop();
					moveYn = true;
					mOldLineNo = 0;
					String sPage = intent.getExtras().getString("PAGE");
					mPageNo = Integer.parseInt(sPage);
					mTTSNo = intent.getExtras().getInt("LINE");
					JwUtils.readingVoice(mContext, mPageNo + getString(R.string.common_page)); 
					moveListSet();
					if(ttsplayYn) {
						setStartImg(true);
						pageText(false);
						playTtsDelayed(1000);
					} else {
						pageText(false);						
					}
					break;						
				case MEMO_ACTIVITY: 
					memoText = intent.getExtras().getString("MEMO");
					memoYn = true;
					bookMark();			
					break;	
				case SETTING_ACTIVITY: 
					settingConfirm();			
					pageController("stop");
			        if(ttsplayYn) {
			        	pageController("stop");
						playTtsDelayed(2000);
			        } else {
			        	pageController("stop");
			        }
					break;
				case MEMU_ACTIVITY: 
					int menuS = intent.getExtras().getInt("MENU");
					menuSelect(menuS);
					break;		
				case SLEEP_ACTIVITY:
					sleepT = intent.getExtras().getInt("SLEEP");
					setTimer(sleepT);
			        if(ttsplayYn) {
			        	pageController("stop");
						playTtsDelayed(2000);
//			        	mScreenOff.postDelayed(mScreenRunnable, 700);
			        } else {
			        	pageController("stop");
			        }
					break;
				case BOOKMARK_ACTIVITY:
					markText = intent.getExtras().getString("MARK");
					memoYn = false;
					bookMark();
					break;	
				case LASTPAGE_ACTIVITY:
					finish();
					break;	
				case SEARCH_ACTIVITY:
					mTts.stop();
					mTTSNo = 0;
					mOldLineNo = 0;					
					moveYn = true;
					msText = intent.getExtras().getString("SEARCH");
					searchConfirm(msText);
					break;	
				case MOVESET_ACTIVITY:
					setNo = intent.getExtras().getInt("MOVESET");
					moveSetting();
					break;						
			}
		} else {
			if(requestCode != LASTPAGE_ACTIVITY){
				if (JwUtils.isEnableAccessibility(mContext)) {
		            AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT);
		            event.getText().add(getString(R.string.title_player));
		            mManager.sendAccessibilityEvent(event);
		        }
		        if(ttsplayYn) {
		        	pageController("stop");
					playTtsDelayed(2000);
		        } else {
		        	pageController("stop");
		        }
			}
		}
		AppControl.NA_Playable = 1;
	}

   
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) { 
		if(keyCode==KeyEvent.KEYCODE_HEADSETHOOK){ // 이어폰 버튼 누르면 재생/정지
			mReplayAfterAccessibilityFocused  = false;
			togglePlay();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){			
			mReplayAfterAccessibilityFocused  = false;
			onBackPressed();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			mReplayAfterAccessibilityFocused  = false;
	        setStartImg(false);
			pageController("stop");
			menuOpen();
			//openContextMenu(top_menu);				
			return true;
		}
		
		return false;
	}
		
	private void menuOpen() {
		Intent i = new Intent(mContext, TDPlayerMenu.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra("PLAYER", AppControl.NA_Playable);
		startActivityForResult(i, MEMU_ACTIVITY);	
	}
	
	public void menuSelect(int sel) {
    	Intent i;
		mTts.stop();
		pageController("stop");
		AppControl.NA_Playable = 3;
		if (sel == 0) { // 단어 검색			  	
			goSearch();    	
		} else if (sel == 1) {  	
			addMark();				
		} else if (sel == 2) {
			i = new Intent(mContext, MemoActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(i, MEMO_ACTIVITY);
		} else if (sel == 3) {  	
			goChapter(1);	    	
		} else if (sel == 4) {  	
			goChapter(2);    	
		} else if (sel == 5) {  	
			goPageMove();	    	
		} else if (sel == 6) {  	
			goPercentMove();	    	
		} else if (sel == 7) {
    		i = new Intent(mContext, SleepSet.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivityForResult(i, SLEEP_ACTIVITY);			
		} else if (sel == 8) {
    		i = new Intent(mContext, TDSettingMenu.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivityForResult(i, SETTING_ACTIVITY);
		}
	} 
	
	private void spanWordText(String wordSpan) {
        if (wordSpan.trim().length() > 0) {
            String tvt = mBookView.getText().toString();
            mBookView.setText(tvt);
            Spannable wordtoSpan = new SpannableString(mBookView.getText());
            int ofe = tvt.indexOf(wordSpan, 0);
            wordtoSpan.setSpan(new BackgroundColorSpan(0xff408000), ofe, ofe
            + wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBookView.setText(wordtoSpan, TextView.BufferType.SPANNABLE);
        }
	}
	
	private void goChapter(int gubun) {
		Intent i;
        mTts.stop();
		pageController("stop");
		i = new Intent(mContext, TDTableActivity.class);
		i.putExtra("ID", mId);
		i.putExtra("TABLE", gubun);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, TABLE_ACTIVITY);
	}
	
	@Override
	protected void onPause() {
		if(homeYn) {
			mStartStopChk = false;
		} 
		AppControl.NA_Playable = 3;
		super.onPause();
	}
	@Override
	protected void onResume() {		
		settingConfirm();
		homeYn = false;
		AppControl.NA_Playable = 1;
		super.onResume();
	}
	
	public void onUserLeaveHint() {
    	pageController("stop");   	
		homeYn = true;
	}
	@Override
	public void onBackPressed() {			
		AppControl.NA_Playable = 0;
		if (mTts != null){
			JwUtils.readingVoice(mContext, getString(R.string.common_exit));
            mTts.stop();
    		mTts.shutdown();
   			recently();
        }
    	if(mSleepTimer != null) {
    		mSleepTimer.removeMessages(0);
    	}

    	mScreenOff.removeCallbacks(mScreenRunnable);
    	mSpanText.removeCallbacks(mSpanRunnable);
    	if(AppControl.DIRECT_CONNECT) {
    		finishAffinity();
    		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        		finishAndRemoveTask();    			
    		}
    	} else {
        	finish();    		
    	}
		super.onBackPressed();
	}
	
	@Override
	public void onDestroy() {
		if (mTts != null){
            mTts.stop();
            mTts.shutdown();
        }
		if(mSleepTimer != null) {
			mSleepTimer.removeCallbacks(mRunble);
	    	mScreenOff.removeCallbacks(mScreenRunnable);
	    	mSpanText.removeCallbacks(mSpanRunnable);
		}
		super.onDestroy();
		try {
			if(screenOnOff != null) {				
				unregisterReceiver(screenOnOff);				
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}		
	}
	
}
