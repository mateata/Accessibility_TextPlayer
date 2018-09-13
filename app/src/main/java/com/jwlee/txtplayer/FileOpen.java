package com.jwlee.txtplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jwlee.txtplayer.adapter.FileOpenAdapter;
import com.jwlee.txtplayer.setting.PlayerSetting;
import com.jwlee.txtplayer.setting.ScreenSetting;
import com.jwlee.txtplayer.setting.SpeechSetting;
import com.jwlee.txtplayer.tdplayer.TDPlayer;
import com.jwlee.txtplayer.util.Dlog;
import com.jwlee.txtplayer.util.FontClass;
import com.jwlee.txtplayer.util.JwFile;
import com.jwlee.txtplayer.util.JwUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class FileOpen extends Activity implements OnClickListener, OnItemClickListener {
	public static Context mContext;

	private ListView mList;
	private TextView tv_header;
	private Button backBtn;
	
	private FileOpenAdapter fileOpenAdapter;
	private ArrayList<JwFile> fileList = null; 
	
	private Vector<String> folderPath = new Vector<String>();
	public static Typeface mTypeface = null;
	private boolean sdCardYn = false;
	
	private SharedPreferences pref;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_file_open);
		
		mContext = this;
		AppControl.DEBUG = isDebuggable(mContext);


		if (mTypeface == null) {
			mTypeface = Typeface.createFromAsset(getAssets(), "nanum.ttf"); // 외부폰트 사용
//			mTypeface = Typeface.MONOSPACE; // 내장 폰트 사용
		}

		//본격 시작전 권한 체크
		isPermission();


	}

	private void initApp() {
		init_layout();
		FontClass.setDefaultFont(this, "SERIF", "nanum.ttf");
		load_settings();

		Intent intent = getIntent();

		if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
			// 연결 프로그램으로 실행됐을 경우
			AppControl.DIRECT_CONNECT = true;
			String filePath = intent.getData().getPath();
			File txtFile = new File(filePath);
			startActivity(new Intent(mContext, TDPlayer.class)
					.putExtra("ID", filePath)
					.putExtra("TITLE", txtFile.getName())
					.putExtra("FILE", filePath)
					.putExtra("ZIPYN", false)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		} else {
			// 직접 실행됐을 경우
			AppControl.DIRECT_CONNECT = false;
			folderOpen(AppControl.MAIN_PATH);
		}
	}

	private void isPermission() {
		PermissionListener permissionlistener = new PermissionListener() {
			@Override
			public void onPermissionGranted() {
				JwUtils.toastCustom(mContext,"권한 허가");
				initApp();
			}

			@Override
			public void onPermissionDenied(List<String> deniedPermissions) {
				JwUtils.toastCustom(mContext, "권한이 거부되어 앱을 종료\n" + deniedPermissions.toString());
				finish();
			}

		};

		TedPermission.with(mContext)
				.setPermissionListener(permissionlistener)
				.setRationaleMessage("파일 접근 권한이 필요해요")
				.setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
				.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE)
				.setGotoSettingButton(true)
				.setGotoSettingButtonText("설정")
				.check();

	}


	private boolean isDebuggable(Context context) {
		boolean debuggable = false;

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
			debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
		} catch (PackageManager.NameNotFoundException e) {
			/* debuggable variable will remain false */
		}

		return debuggable;
	}


	private void init_layout() {		
		
		mList = (ListView)findViewById(R.id.listView1);
		mList.setOnItemClickListener(this);
		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.app_name);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_exit);
		
		fileList = new ArrayList<JwFile>(); 
		fileOpenAdapter = new FileOpenAdapter(mContext, R.layout.i_set_file, fileList);
		mList.setAdapter(fileOpenAdapter);
	
		fileOpenAdapter.notifyDataSetChanged();
		
	}

	private void load_settings() {
		pref = getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		
		// 01. 플레이어 음성설정
		SpeechSetting.setSetPlaySpeed(pref.getInt(getString(R.string.setting_voice_speed),50));
		SpeechSetting.setSetPlayTone(pref.getInt(getString(R.string.setting_voice_tone),50));
		SpeechSetting.setSetPlayVolume(pref.getInt(getString(R.string.setting_voice_volume),50));

		SpeechSetting.setSetReadKorean(pref.getBoolean(getString(R.string.setting_read_korean), true));
		SpeechSetting.setSetReadEnglish(pref.getBoolean(getString(R.string.setting_read_english), true));
		SpeechSetting.setSetReadNumber(pref.getBoolean(getString(R.string.setting_read_number), true));
		SpeechSetting.setSetReadPunct(pref.getBoolean(getString(R.string.setting_read_punct), true));
		SpeechSetting.setSetReadSpecials(pref.getBoolean(getString(R.string.setting_read_special), true));
		SpeechSetting.setSetReadCHLetter(pref.getBoolean(getString(R.string.setting_read_chletter), true));

		SpeechSetting.setSetRepeat(pref.getBoolean(getString(R.string.setting_repeat), true));
		SpeechSetting.setSetRepeatCount(pref.getInt(getString(R.string.setting_repeat_count), 3));

		// 02. 플레이어 기능설정
		String[] setfunc = getResources().getStringArray(R.array.setfunction);
		for(int i = 0; i < setfunc.length; i++ ) {
			PlayerSetting.setSetFunction(i,pref.getBoolean(setfunc[i], true));
		}
		
		// 03. 플레이어 화면 설정
		ScreenSetting.setBgColor(pref.getInt(getString(R.string.setting_bg_color),Color.WHITE));
		ScreenSetting.setTextColor(pref.getInt(getString(R.string.setting_text_color),Color.BLACK));
		ScreenSetting.setSize(pref.getInt(getString(R.string.setting_text_size), 50));
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(folderPath.size()>1 && position ==0){
			// 루트폴더 이외의 폴더에서 상위 폴더로 이동 
			folderOpen();
		}else if(fileList.get(position).getIsfolder() == true){
			// 클릭 아이템이 폴더 일 경우 폴더 오픈 
			folderOpen( fileList.get(position).getFileName());
		}else{
			selectFile(getFolderName(), fileList.get(position).getFileName());
		}
	}

	private void selectFile(String folder, String fName) { // 나중에 활용시 대문자 처리 수정필요
		if(fName.endsWith(".txt")) {
		    startActivity(new Intent(mContext, TDPlayer.class)
		    .putExtra("ID", folder+fName)
			.putExtra("TITLE", fName)
			.putExtra("FILE", folder+fName)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));			
		} 
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;
		default:
			break;
		}
	}	

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		 if(folderPath.size() <= 1) { // 메인 폴더라면 종료 메세지
			 showDialog(getString(R.string.dialog_endapp), getString(R.string.common_yes), new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		        		finishAffinity();
		        		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
		            		finishAndRemoveTask();    			
		        		}
		            }
		        }, getString(R.string.common_no), null
			);			 			 
		 } else { // 메인이 아니라면 상위 폴더로 이동		
			 folderOpen();
		 }
	}

	
	//	// 경로 지정없을 경우  uppser folder FIXME
	private boolean folderOpen(){
		folderOpen("");
		return false;
	}

	private boolean folderOpen(String folderName){
		fileList.clear();
		// 폴더, 파일 리스팅 

		JwUtils.toastCustom(mContext,folderName+"/"+isExternalStorageReadable());

		Dlog.e(Environment.getExternalStorageDirectory().toString());



		// 폴더 이동 설정 
		if("".equals(folderName)){
			// 상위 폴더로 이동 (경로에서 최하단 폴더 삭제)
			folderPath.remove(folderPath.size()-1);

		}else if(folderPath.size()==1 
				|| (folderPath.size()>1 && AppControl.MAIN_PATH.equals(folderName))==false){
			// 하위폴더 중 folderName로 이동 
			folderPath.add(folderName);
		}
		
		// 상위 폴더로 이동 아이템 생성 
		if(folderPath.size()> 1 ){
			// 루트폴더가 아닐 경우 
			fileList.add( 0, new JwFile(false, getString(R.string.move_upperfolder) ));
			
		}
		JwFile mFile;
		File tdDIR = new File(getFolderName());
		if(tdDIR.exists() && tdDIR.listFiles()!=null){
			// 폴더 리스팅
			for (File child : tdDIR.listFiles()){
				if(child.isDirectory() == true){
					mFile = new JwFile(child.isDirectory(), child.getName());
					fileList.add(mFile);
				}
			}
			
			// 파일 리스팅
			for (File child : tdDIR.listFiles()){
				if(child.isDirectory() == false){
					if(child.getName().endsWith(".txt") || child.getName().endsWith(".zip") || child.getName().endsWith(".opf")){ // 나중에 활용시 대문자 처리 수정필요
						mFile = new JwFile(child.isDirectory(), child.getName());
						fileList.add(mFile);
						
					}					
				}
			}
		}else{
			
		}
		
		fileOpenAdapter.notifyDataSetChanged();
				
		return false;
	}

	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
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

	private String getFolderName() {
		String rStr = "";
		
		for(String mName : folderPath){
			if("/".equals(mName)== true){
				rStr = mName;
			}else{
				rStr += (mName +"/");
			}
		}
		
		return rStr;
	}

}
