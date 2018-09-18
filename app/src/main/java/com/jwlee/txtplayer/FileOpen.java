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
import android.databinding.DataBindingUtil;
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
import com.jwlee.txtplayer.databinding.AFileOpenBinding;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


public class FileOpen extends Activity implements OnClickListener, OnItemClickListener {
	public static Context mContext;

	private ListView mList;

	private FileOpenAdapter fileOpenAdapter;
	private ArrayList<JwFile> fileList = null; 
	
	private Vector<String> folderPath = new Vector<String>();
	public static Typeface mTypeface = null;
	private boolean sdCardYn = true;
	
	private SharedPreferences pref;

	private AFileOpenBinding binding;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this,R.layout.a_file_open);
		binding.setFileOpen(this);

		mContext = this;
		AppControl.DEBUG = isDebuggable(mContext);


		if (mTypeface == null) {
			mTypeface = Typeface.createFromAsset(getAssets(), "nanum.ttf"); // 외부폰트 사용
//			mTypeface = Typeface.MONOSPACE; // 내장 폰트 사용
		}

		//본격 시작전 권한 체크
		isPermission();

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

    private void initApp() {
        init_layout();
        FontClass.setDefaultFont(this, "SERIF", "nanum.ttf");
        load_settings();

        HashSet<String> path = AppControl.getExternalMounts();
        ArrayList<String> pathStr = new ArrayList<String>();
        if(AppControl.isExternalStorageReadable() && path.size() > 0) {
            Iterator iter = path.iterator();
            while (iter.hasNext()) {
                pathStr.add(iter.next().toString());
            }
            AppControl.SD_MAINPATH = pathStr.get(0);
            AppControl.INTER_MAINPATH = Environment.getExternalStorageDirectory().getPath();
            Dlog.e("PATH : " + pathStr.get(0) + "$$: " + AppControl.INTER_MAINPATH);
        }


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
            folderOpen(AppControl.SD_MAINPATH);
        }
    }

	private void init_layout() {
	    binding.headerTitle.setText(R.string.app_name);
	    binding.backBtn.setText(R.string.common_exit);
		mList = (ListView)findViewById(R.id.listView1);
		mList.setOnItemClickListener(this);
		
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
            case R.id.bt_path:
                changeSDPath();
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


	// SD카드 - 내장메모리 메인으로 경로를 변경한다
	private void changeSDPath() {
        folderPath.removeAllElements(); // 최상위 폴더를 바꾸는 것이기 때문에 folderPath 를 초기화
        if(sdCardYn) {
	        folderOpen(AppControl.INTER_MAINPATH+"/");
	        sdCardYn = false;
            binding.btPath.setText(R.string.path_sdcard);
        } else {
            folderOpen(AppControl.SD_MAINPATH);
            sdCardYn = true;
            binding.btPath.setText(R.string.path_memory);
        }
    }

	
	//	// 경로 지정없을 경우  uppser folder FIXME
	private boolean folderOpen(){
		folderOpen("");
		return false;
	}

	private boolean folderOpen(String folderName){
	    Dlog.e("PATH : " + folderName);
		fileList.clear();
		// 폴더, 파일 리스팅 

		// 폴더 이동 설정 
		if("".equals(folderName)){
			// 상위 폴더로 이동 (경로에서 최하단 폴더 삭제)
			folderPath.remove(folderPath.size()-1);

		}else if(folderPath.size()==1
				|| (folderPath.size()>1 && (AppControl.SD_MAINPATH.equals(folderName) || AppControl.INTER_MAINPATH.equals(folderName)))==false) {
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
