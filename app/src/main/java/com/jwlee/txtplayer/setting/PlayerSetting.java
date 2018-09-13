package com.jwlee.txtplayer.setting;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;

import android.content.Context;


/**
 * @author hphong
 *
 */
public class PlayerSetting {

	private static boolean setStartAuto = true;
	private static boolean setStartBookmark = true; 
	
	
	private static boolean[] setFunction={true,true,true,true,true
											,true,true,true,true,true
											,true,true};
	
	
	private static boolean setUsePage = true;	
	private static boolean setUseMark = true;
	private static boolean setUseMemo = true;
	private static boolean setUseSearchWord = true;
	private static boolean setUseCharacter = true;
	private static boolean setUseWord = true;
	private static boolean setUseLine = true;
	private static boolean setUse10per = true;
	
	
	public static boolean isSetStartAuto() {
		return setStartAuto;
	}
	public static void setSetStartAuto(boolean setStartAuto) {
		PlayerSetting.setStartAuto = setStartAuto;
	}
	public static boolean isSetStartBookmark() {
		return setStartBookmark;
	}
	public static void setSetStartBookmark(boolean setStartBookmark) {
		PlayerSetting.setStartBookmark = setStartBookmark;
	}
	public static boolean isSetUsePage() {
		return setUsePage;
	}
	public static void setSetUsePage(boolean setUsePage) {
		PlayerSetting.setUsePage = setUsePage;
	}
	public static boolean isSetUseMark() {
		return setUseMark;
	}
	public static void setSetUseMark(boolean setUseMark) {
		PlayerSetting.setUseMark = setUseMark;
	}
	public static boolean isSetUseMemo() {
		return setUseMemo;
	}
	public static void setSetUseMemo(boolean setUseMemo) {
		PlayerSetting.setUseMemo = setUseMemo;
	}
	public static boolean isSetUseSearchWord() {
		return setUseSearchWord;
	}
	public static void setSetUseSearchWord(boolean setUseSearchWord) {
		PlayerSetting.setUseSearchWord = setUseSearchWord;
	}
	public static boolean isSetUseCharacter() {
		return setUseCharacter;
	}
	public static void setSetUseCharacter(boolean setUseCharacter) {
		PlayerSetting.setUseCharacter = setUseCharacter;
	}
	public static boolean isSetUseWord() {
		return setUseWord;
	}
	public static void setSetUseWord(boolean setUseWord) {
		PlayerSetting.setUseWord = setUseWord;
	}
	public static boolean isSetUseLine() {
		return setUseLine;
	}
	public static void setSetUseLine(boolean setUseLine) {
		PlayerSetting.setUseLine = setUseLine;
	}
		
	public static boolean isSetUse10per() {
		return setUse10per;
	}
	public static void setSetUse10per(boolean setUse10per) {
		PlayerSetting.setUse10per = setUse10per;
	}
	public static boolean getSetFunction(int setIndex) {
		return setFunction[setIndex];
	}
	
	public static void setSetFunction(int setIndex,boolean setFunction) {
		PlayerSetting.setFunction[setIndex] = setFunction;
		setPreference(setIndex,setFunction);
	}

	public static void setPreference(int st, boolean func) {
			switch(st) { // 0,3 번은 머릿말이므로 제외
				case 1:
					setSetStartAuto(func);
					break;
				case 2:
					setSetStartBookmark(func);
					break;					
				case 4:
					setSetUsePage(func);
					break;
				case 5:
					setSetUseMark(func);					
					break;				
				case 6:
					setSetUseMemo(func);					
					break;				
				case 7:
					setSetUseSearchWord(func);					
					break;				
				case 8:
					setSetUseCharacter(func);					
					break;				
				case 9:
					setSetUseWord(func);					
					break;				
				case 10:
					setSetUseLine(func);					
					break;	
				default:
					break;
			}
	}
	
	public static ArrayList<String[]> getTxtMove(Context context) {
		ArrayList<String[]> mvList = new ArrayList<String[]>();		
			String[] mvText1 = { "page", context.getString(R.string.player_page)};
			mvList.add(mvText1);
			String[] mvText5 = { "alpha", context.getString(R.string.player_alpha)};
			mvList.add(mvText5);
			String[] mvText6 = { "word", context.getString(R.string.player_word)};
			mvList.add(mvText6);
			String[] mvText7 = { "line", context.getString(R.string.player_line)};
			mvList.add(mvText7);
			String[] mvText2 = { "mark", context.getString(R.string.player_mark)};
			mvList.add(mvText2);
			String[] mvText3 = { "memo", context.getString(R.string.player_memo)};
			mvList.add(mvText3);
			String[] mvText4 = { "search", context.getString(R.string.common_search)};
			mvList.add(mvText4);
		return mvList;		
	}
	
}
