package com.jwlee.txtplayer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.jwlee.txtplayer.tdplayer.TDBaseActivity;

import android.content.res.AssetManager;

public class Hanja {

	private static final Hanja instance = new Hanja();
	
	/**
	 * Unified CJK Ideographs : 4E00-9FCF
	 */
	private Map<Character, Character> cjkHangulMap = new HashMap<Character, Character>();

	/**
	 * CJK Ideographs Ext. A : 3400-4DBF
	 */
	private Map<Character, Character> cjkExtAHangulMap = new HashMap<Character, Character>();

	/**
	 * CJK Compatibility Ideographs : F900-FAFF
	 */
	private Map<Character, Character> cjkCompHangulMap = new HashMap<Character, Character>();
	
	private IllegalStateException exception = null;
	
	/**
	 * <p>생성자</p>
	 */
	private Hanja() {
		try {
			loadMap("CJK_Ideographs.txt", cjkHangulMap, (char)0x4E00);
			loadMap("CJK_Ideographs_Ext_A.txt", cjkExtAHangulMap, (char)0x3400);
			loadMap("CJK_Ideographs_Compatibility.txt", cjkCompHangulMap, (char)0xF900);
		} catch (IOException e) {
			exception = new IllegalStateException("한자-한글 맵핑 정보를 가져오는 중 에러가 발생했습니다.", e);
		}
	}
	
	/**
	 * <p>인스턴스를 가져온다.</p>
	 * 
	 * @return
	 */
	public static Hanja getInstance() {
		return instance;
	}
	
	/**
	 * <p>한자-한글 맵핑 정보를 읽어와 Map에 담는다.</p>
	 * 
	 * @param filename 파일명
	 * @param map
	 * @param beginChar
	 * @throws IOException
	 */
	private void loadMap(String filename, Map<Character, Character> map, char beginChar) throws IOException {
		BufferedReader reader = null;
		try {
			AssetManager assetMgr = TDBaseActivity.mContext.getAssets();
			reader = new BufferedReader(new InputStreamReader(assetMgr.open(filename)));
			int index = 0;
			String read = null;
			while ( (read = reader.readLine()) != null) {
				read = read.trim();
				if (read.startsWith("/")) {
					// 주석이므로 무시한다.
					continue;
				}
				String[] codeArray = read.split(",");
				if (codeArray != null && codeArray.length > 0) {
					for (String code : codeArray) {
						if (code.isEmpty() == false) {
							map.put( (char)(beginChar + (index++)), (char)(Integer.decode(code.trim()).intValue()));	
						}
					}
				}
			}
		} finally {
			if (reader != null) try { reader.close(); } catch(IOException e) {}
		}
	}
		
	/**
	 * <p>한자를 한글로 변환한다.</p>
	 * 
	 * @param ch 한자
	 * @return
	 * @throws JaruException
	 */
	public char toHangul(char ch) throws IllegalStateException {
		if (exception != null) {
			throw exception;
		}
		if (ch >= 0x4E00 && ch <= 0x9FCF) {
			return cjkHangulMap.get(ch);
		} else if (ch >= 0x3400 && ch <= 0x4DBF) { 
			return cjkExtAHangulMap.get(ch);
		} else if (ch >= 0xF900 && ch <= 0xFAFF) { 
			return cjkCompHangulMap.get(ch);
		} else {
			return ch;
		}
	}
	
	/**
	 * <p>한자를 한글로 변환한다.</p>
	 * 
	 * @param str 한자
	 * @return
	 * @throws IllegalStateException
	 */
	public String toHangul(String str) throws IllegalStateException {
		if (str == null) {
			return null;
		}
		char[] hangulCharArray = new char[str.length()];
		for (int i = 0; i < hangulCharArray.length; i++) {
			hangulCharArray[i] = toHangul(str.charAt(i));
		}
		return new String(hangulCharArray);
	}
}
