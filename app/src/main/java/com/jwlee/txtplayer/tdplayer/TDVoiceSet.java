package com.jwlee.txtplayer.tdplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.setting.SpeechSetting;
import com.jwlee.txtplayer.util.JwUtils;

public class TDVoiceSet extends TDBaseActivity implements OnClickListener, OnCheckedChangeListener, OnInitListener {

	public Context mContext;

	private SharedPreferences pref;
	private SharedPreferences.Editor settingEditor;

	CheckBox cb_readkorean;
	CheckBox cb_readenglish;
	CheckBox cb_readnumber;
	CheckBox cb_readpunc;
	CheckBox cb_repeat;
	CheckBox cb_readaccent;
	CheckBox cb_readchlt;
	CheckBox cb_readschar;

	private Button bt_speed_dec;
	private Button bt_speed_inc;
	private Button bt_vol_dec;
	private Button bt_vol_inc;
	private Button bt_tone_dec;
	private Button bt_tone_inc;

	private TextView tv_speed;
	private TextView tv_vol;
	private TextView tv_tone;

	private EditText cb_repeatCount;
	InputMethodManager imm;

	private TextToSpeech mTTS;
	private TextView tv_header;
	private Button backBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.f_set_voice);
		
		pref = mContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		settingEditor = pref.edit();
		
		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.setting_menu_02);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_back);		
		
		mTTS = new TextToSpeech(mContext, (OnInitListener) this, "");
		AppControl.mTTSEngine = mTTS.getDefaultEngine(); 		
		init_layout();
	}


	private void init_layout() {
		tv_speed = (TextView)findViewById(R.id.tv_speed);
		tv_speed.setText(""+SpeechSetting.getSetPlaySpeed());
		tv_vol   = (TextView)findViewById(R.id.tv_vol);
		tv_vol.setText(""+SpeechSetting.getSetPlayVolume());
		tv_tone  = (TextView)findViewById(R.id.tv_tone);
		tv_tone.setText(""+SpeechSetting.getSetPlayTone());


		bt_speed_dec = (Button)findViewById(R.id.bt_speed_dec);
		bt_speed_dec.setOnClickListener(this);
		bt_speed_inc = (Button)findViewById(R.id.bt_speed_inc);
		bt_speed_inc.setOnClickListener(this);
		bt_vol_dec = (Button)findViewById(R.id.bt_vol_dec);
		bt_vol_dec.setOnClickListener(this);
		bt_vol_inc = (Button)findViewById(R.id.bt_vol_inc);
		bt_vol_inc.setOnClickListener(this);
		bt_tone_dec = (Button)findViewById(R.id.bt_tone_dec);
		bt_tone_dec.setOnClickListener(this);
		bt_tone_inc = (Button)findViewById(R.id.bt_tone_inc);
		bt_tone_inc.setOnClickListener(this);

		cb_readkorean = (CheckBox)findViewById(R.id.cb_readkorean);
		cb_readkorean.setOnCheckedChangeListener(this);
		cb_readkorean.setChecked(SpeechSetting.isSetReadKorean());

		cb_readenglish = (CheckBox)findViewById(R.id.cb_readenglish);
		cb_readenglish.setOnCheckedChangeListener(this);
		cb_readenglish.setChecked(SpeechSetting.isSetReadEnglish());

		cb_readnumber = (CheckBox)findViewById(R.id.cb_readnumber);
		cb_readnumber.setOnCheckedChangeListener(this);
		cb_readnumber.setChecked(SpeechSetting.isSetReadNumber());

		cb_readpunc = (CheckBox)findViewById(R.id.cb_readpunc);
		cb_readpunc.setOnCheckedChangeListener(this);
		cb_readpunc.setChecked(SpeechSetting.isSetReadPunct());

		cb_readschar = (CheckBox)findViewById(R.id.cb_readschar);
		cb_readschar.setOnCheckedChangeListener(this);
		cb_readschar.setChecked(SpeechSetting.isSetReadSpecials());

		cb_readchlt = (CheckBox)findViewById(R.id.cb_readchlt);
		cb_readchlt.setOnCheckedChangeListener(this);
		cb_readchlt.setChecked(SpeechSetting.isSetReadCHLetter());
		
		cb_repeat = (CheckBox)findViewById(R.id.cb_repeat);
		cb_repeat.setOnCheckedChangeListener(this);
		cb_repeat.setChecked(SpeechSetting.isSetRepeat());
		
		
		// 반복문자 설정값 Edittext
		cb_repeatCount = (EditText)findViewById(R.id.cb_repeatcount);
		cb_repeatCount.setText(SpeechSetting.getSetRepeatCount()+"");
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(cb_repeatCount.getWindowToken(), 0);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId() ) {
		case R.id.cb_readkorean:
			settingEditor.putBoolean(getString(R.string.setting_read_korean), isChecked).commit();
			SpeechSetting.setSetReadKorean(isChecked);
			break;
		case R.id.cb_readenglish:
			settingEditor.putBoolean(getString(R.string.setting_read_english), isChecked).commit();
			SpeechSetting.setSetReadEnglish(isChecked);
			break;
		case R.id.cb_readnumber:
			settingEditor.putBoolean(getString(R.string.setting_read_number), isChecked).commit();
			SpeechSetting.setSetReadNumber(isChecked);
			break;
		case R.id.cb_readpunc:
			settingEditor.putBoolean(getString(R.string.setting_read_punct), isChecked).commit();
			SpeechSetting.setSetReadPunct(isChecked);
			break;
		case R.id.cb_readschar:
			settingEditor.putBoolean(getString(R.string.setting_read_special), isChecked).commit();
			SpeechSetting.setSetReadSpecials(isChecked);
			break;
		case R.id.cb_readchlt:
			settingEditor.putBoolean(getString(R.string.setting_read_chletter), isChecked).commit();
			SpeechSetting.setSetReadCHLetter(isChecked);
			break;
		case R.id.cb_repeat:
			settingEditor.putBoolean(getString(R.string.setting_repeat), isChecked).commit();
			SpeechSetting.setSetRepeat(isChecked);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		int playSpeed = SpeechSetting.getSetPlaySpeed();
		int playVol = SpeechSetting.getSetPlayVolume();
		int playTone = SpeechSetting.getSetPlayTone();
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;		
		case R.id.bt_speed_dec:
			if(playSpeed>0){
				playSpeed -= 10;
				SpeechSetting.setSetPlaySpeed(playSpeed);
				settingEditor.putInt(getString(R.string.setting_voice_speed), playSpeed).commit();
				tv_speed.setText(""+playSpeed);
				JwUtils.toastCustom(mContext, getString(R.string.setting_decrease,playSpeed));											
			}

			break;
		case R.id.bt_speed_inc:
			if(playSpeed<100){
				playSpeed += 10;
				SpeechSetting.setSetPlaySpeed(playSpeed);
				settingEditor.putInt(getString(R.string.setting_voice_speed), playSpeed).commit();
				tv_speed.setText(""+playSpeed);
				JwUtils.toastCustom(mContext, getString(R.string.setting_increase,playSpeed));															
			}
			break;
		case R.id.bt_vol_dec:
			if(playVol>0){
				playVol -= 10;
				SpeechSetting.setSetPlayVolume(playVol);
				settingEditor.putInt(getString(R.string.setting_voice_volume), playVol).commit();
				tv_vol.setText(""+playVol);
				JwUtils.toastCustom(mContext, getString(R.string.setting_decrease,playVol));															
			}
			break;
		case R.id.bt_vol_inc:
			if(playVol<100){
				playVol += 10;
				SpeechSetting.setSetPlayVolume(playVol);
				settingEditor.putInt(getString(R.string.setting_voice_volume), playVol).commit(); 
				tv_vol.setText(""+playVol);
				JwUtils.toastCustom(mContext, getString(R.string.setting_increase,playVol));								
			}
			break;
		case R.id.bt_tone_dec:
			if(playTone>0){
				playTone -= 10;
				SpeechSetting.setSetPlayTone(playTone);
				settingEditor.putInt(getString(R.string.setting_voice_tone), playTone).commit();
				tv_tone.setText(""+playTone);
				JwUtils.toastCustom(mContext, getString(R.string.setting_decrease,playTone));																			
			}
			break;
		case R.id.bt_tone_inc:
			if(playTone<100){
				playTone += 10;
				SpeechSetting.setSetPlayTone(playTone);
				settingEditor.putInt(getString(R.string.setting_voice_tone), playTone).commit();
				tv_tone.setText(""+playTone);
				JwUtils.toastCustom(mContext, getString(R.string.setting_increase,playTone));															
			}
			break;
		default:
			break;

		}

	}


	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
	}
	public void onDestroy() {
		super.onDestroy();
		if (mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
	}

	@Override
	public void onBackPressed() {
		imm.hideSoftInputFromWindow(cb_repeatCount.getWindowToken(), 0);
		int rpCount = Integer.parseInt(cb_repeatCount.getText().toString().trim());
		SpeechSetting.setSetRepeatCount(rpCount);
		settingEditor.putInt(getString(R.string.setting_repeat_count), rpCount).commit();
		finish();
		super.onBackPressed();
	}
	
}
