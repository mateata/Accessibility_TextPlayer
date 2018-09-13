package com.jwlee.txtplayer.setting;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.util.JwUtils;

/**
 * @author hphong
 *
 */
public class SpeechSetting {
	// TTS 재생 속도 (0~100) 기본값: 50
	private static int setPlaySpeed = 50;
	// TTS 톤 높이
	private static int setPlayTone = 50;
	// TTS 볼륨 크기
	private static int setPlayVolume = 50;
	
	// 한글 읽기 설정
	private static boolean setReadKorean = true;
	// 영어 읽기 설정
	private static boolean setReadEnglish = true;
	// 숫자 읽기 설정
	private static boolean setReadNumber = true;
	// 구둣점 읽기 설정
	private static boolean setReadPunct = true;
	// 특수기호 읽기 설정
	private static boolean setReadSpecials = true;
	// 한자 읽기 설정
	public static boolean setReadCHLetter = true;
	
	// 반복 읽기 설정
	private static boolean setRepeat = true;
	// 반복 읽기 횟수
	private static int setRepeatCount = 3;
	
	public static int getSetPlaySpeed() {
		return setPlaySpeed;
	}
	public static void setSetPlaySpeed(int setPlaySpeed) {
		SpeechSetting.setPlaySpeed = setPlaySpeed;
	}
	public static int getSetPlayTone() { return setPlayTone; }
	
	public static void setSetPlayTone(int setPlayTone) {
		SpeechSetting.setPlayTone = setPlayTone;
	}
	public static int getSetPlayVolume() { return setPlayVolume; }
	
	public static void setSetPlayVolume(int setPlayVolume) {
		SpeechSetting.setPlayVolume = setPlayVolume;
	}
	public static boolean isSetReadKorean() { return setReadKorean; }
	
	public static void setSetReadKorean(boolean setReadKorean) {
		SpeechSetting.setReadKorean = setReadKorean;
	}
	public static boolean isSetReadEnglish() { return setReadEnglish; }
	
	public static void setSetReadEnglish(boolean setReadEnglish) {
		SpeechSetting.setReadEnglish = setReadEnglish;
	}
	public static boolean isSetReadNumber() { return setReadNumber; }
	
	public static void setSetReadNumber(boolean setReadNumber) {
		SpeechSetting.setReadNumber = setReadNumber;
	}
	public static boolean isSetReadPunct() { return setReadPunct; }
	
	public static void setSetReadPunct(boolean setReadPunct) {
		SpeechSetting.setReadPunct = setReadPunct;
	}
	public static boolean isSetReadSpecials() { return setReadSpecials; }
	
	public static void setSetReadSpecials(boolean setReadSpecials) {
		SpeechSetting.setReadSpecials = setReadSpecials;
	}
	public static boolean isSetReadCHLetter() { return setReadCHLetter; }
	
	public static void setSetReadCHLetter(boolean setReadCHLetter) {
		SpeechSetting.setReadCHLetter = setReadCHLetter;
	}

	public static boolean isSetRepeat() { return setRepeat; }
	
	public static void setSetRepeat(boolean setRepeat) {
		SpeechSetting.setRepeat = setRepeat;
	}
	public static int getSetRepeatCount() { return setRepeatCount; }
	
	public static void setSetRepeatCount(int setRepeatCount) {
		SpeechSetting.setRepeatCount = setRepeatCount;
	}
	
	
	// 음성세팅 작업 
	public static String getPunctString(String mStr, Context context){
		if(mStr == null || "".equals(mStr) || mStr.length() == 0) {
			return "";
		}

		String strResult = mStr;

		if(setReadNumber == false){
			// 숫자제거
			strResult = strResult.replaceAll("[0-9]", "");
		}
			
		if(setReadPunct == true){
			ArrayList<String> punctArray = new ArrayList<String>();
			ArrayList<String> nameArray = new ArrayList<String>();
			
			for(String s : context.getResources().getStringArray(R.array.wordpunctitem)){
				punctArray.add(s);
			}
			for(String s : context.getResources().getStringArray(R.array.wordpunctname)){
				nameArray.add(s);
			}				
			for(String s : context.getResources().getStringArray(R.array.mathpunctitem)){
				punctArray.add(s);
			}
			for(String s : context.getResources().getStringArray(R.array.mathpunctname)){
				nameArray.add(s);
			}				
			for(String s : context.getResources().getStringArray(R.array.etcpunctitem)){
				punctArray.add(s);
			}
			for(String s : context.getResources().getStringArray(R.array.etcpunctname)){
				nameArray.add(s);
			}
			for(int i = 0; i < punctArray.size(); i ++) {			
				strResult = strResult.replace(punctArray.get(i), nameArray.get(i));
			}			
		
		}			
		
		return strResult;
	}
	
	public static String getRepeatClear(String rep) {
		if(rep == null || "".equals(rep) || rep.length() == 0) {
			return "";
		}
		if(rep.length() < 2 || SpeechSetting.isSetRepeat() == false) { // 반복문자 처리설정 off나 짧아서 처리대상 아님 
			return rep;
		}
		
		int repeatNum = 0;
		char[] strArray = rep.toCharArray(); // 받은 문장(혹은 프레이즈)를  chararray로 분해
		int strArrayLength = strArray.length;
		char currentChar = strArray[0];
		String currentTempStr = String.valueOf(strArray[0]);
		String repTemp = "";
		String repChange = "";
		String replaceT = "";
		
		for(int i=1; i<strArrayLength; i++){		   
			if(currentChar == strArray[i] && !JwUtils.isNumber(strArray[i]+"") && !" ".equals(strArray[i]+"")){ // 앞의 글자와 현재 글자가 같은 경우 && 숫자 아님.						
				currentTempStr = currentTempStr + String.valueOf(strArray[i]); //중복되는 글자를 추가
				repTemp = Character.toString(currentChar); //중복 글자가 어떤 글자인지 기록
				if(i == strArrayLength -1) {
					if(currentTempStr.length() <= setRepeatCount){ // 현재까지 누적된 중복글자의 수가 설정값을 넘지 않으면 
						repChange = repChange + currentTempStr;
						currentTempStr = String.valueOf(strArray[i]); // 현재 글자부터 다시 검사하도록 초기화
						currentChar = strArray[i]; //누적된 중복글자 초기화

					}else{ // 현재까지 누적된 중복글자의 수가 설정값을 초과하면
						repeatNum = currentTempStr.length();
						for(int j = 0; j < setRepeatCount; j++) { // 해당 중복글자를 replace하기 위해 설정값만큼 중복글자 배치
							replaceT += repTemp;
						}						
						repChange = repChange + replaceT + " " + repeatNum + " 번 반복";
						repTemp = "";
						replaceT = "";
						currentChar = strArray[i];
						currentTempStr = String.valueOf(strArray[i]);
					}	
					repeatNum = 0;
				}
			}else{ // 글자 반복이 끝난경우
				if(currentTempStr.length() <= setRepeatCount){ // 현재까지 누적된 중복글자의 수가 설정값을 넘지 않으면 
					repChange = repChange + currentTempStr;
					if(i == strArrayLength -1) {
						repChange = repChange + String.valueOf(strArray[i]);
					}
					currentTempStr = String.valueOf(strArray[i]); // 현재 글자부터 다시 검사하도록 초기화
					currentChar = strArray[i]; //누적된 중복글자 초기화

				}else{ // 현재까지 누적된 중복글자의 수가 설정값을 초과하면
					repeatNum = currentTempStr.length();
					for(int j = 0; j < setRepeatCount; j++) { // 해당 중복글자를 replace하기 위해 설정값만큼 중복글자 배치
						replaceT += repTemp;
					}
					repChange = repChange + replaceT + " " + repeatNum + " 번 반복";
					repTemp = "";
					replaceT = "";
					currentChar = strArray[i];
					currentTempStr = String.valueOf(strArray[i]);
				}	
				repeatNum = 0;

			}
		}
		return repChange;		
	}
	
	
	public static float getPlaySpeed() {
		float playSpeed = 1.0f;
		int disPS = getSetPlaySpeed();
		switch (disPS) {
			case 0:
				playSpeed = 0.3f;
				break;
			case 10:
				playSpeed = 0.5f;
				break;
			case 20:
				playSpeed = 0.6f;
				break;
			case 30:
				playSpeed = 0.7f;
				break;
			case 40:
				playSpeed = 0.8f;
				break;
			case 50:
				playSpeed = 1.0f;
				break;
			case 60:
				playSpeed = 1.5f;
				break;
			case 70:
				playSpeed = 2.0f;
				break;
			case 80:
				playSpeed = 2.5f;
				break;
			case 90:
				playSpeed = 3.0f;
				break;
			case 100:
				playSpeed = 4.0f;
				break;		
		}	
		return playSpeed;
	}
	
	public static float getplayPitch() {
		float playPitch = 1.0f;
		int disPT = getSetPlayTone();
		if("lg.speech.tts".equalsIgnoreCase(AppControl.mTTSEngine)) {
			switch (disPT) {
			case 0:
				playPitch = 0.8f;
				break;
			case 10:
				playPitch = 0.8f;
				break;
			case 20:
				playPitch = 0.9f;
				break;
			case 30:
				playPitch = 0.9f;
				break;
			case 40:
				playPitch = 1.0f;
				break;
			case 50:
				playPitch = 1.0f;
				break;
			case 60:
				playPitch = 1.0f;
				break;
			case 70:
				playPitch = 1.1f;
				break;
			case 80:
				playPitch = 1.1f;
				break;
			case 90:
				playPitch = 1.2f;
				break;
			case 100:
				playPitch = 1.2f;
				break;		
		}		
		} else {
		switch (disPT) {
			case 0:
				playPitch = 0.5f;
				break;
			case 10:
				playPitch = 0.6f;
				break;
			case 20:
				playPitch = 0.7f;
				break;
			case 30:
				playPitch = 0.8f;
				break;
			case 40:
				playPitch = 0.9f;
				break;
			case 50:
				playPitch = 1.0f;
				break;
			case 60:
				playPitch = 1.1f;
				break;
			case 70:
				playPitch = 1.2f;
				break;
			case 80:
				playPitch = 1.3f;
				break;
			case 90:
				playPitch = 1.4f;
				break;
			case 100:
				playPitch = 1.5f;
				break;		
		}	
		}
		return playPitch;
	}	
	
/*	public static void setSetPlaySpeed(int setPlaySpeed) {
		SpeechSetting.setPlaySpeed = setPlaySpeed;
	}
	public static int getSetPlayTone() { return setPlayTone; }
	
	public static void setSetPlayTone(int setPlayTone) {
		SpeechSetting.setPlayTone = setPlayTone;
	}
	public static int getSetPlayVolume() { return setPlayVolume; }
	
	public static void setSetPlayVolume(int setPlayVolume) {
		SpeechSetting.setPlayVolume = setPlayVolume;
	}*/

}
