package com.jwlee.txtplayer.setting;

import android.graphics.Color;


public class ScreenSetting {

	private static int textSizeBase = 16;

	private static int textColor = Color.parseColor("#333333");

	private static int bgColor = Color.parseColor("#f7f7f7");
	
	public static int getTextSize() {
		return textSizeBase;
	}
	public static void setTextSize(int textSize) {
		ScreenSetting.textSizeBase = textSize;
	}
	public static int getBgColor() {
		return bgColor;
	}
	public static void setBgColor(int bgColor) {
		ScreenSetting.bgColor = bgColor;
	}
	public static int getTextColor() {
		return textColor;
	}
	public static void setTextColor(int textColor) {
		ScreenSetting.textColor = textColor;
	}

	public static void setSize(int size) {
		int realSize = 16;
		switch (size) {
		case 0:
			realSize = 8;
			ScreenSetting.setTextSize(realSize);
			break;
		case 10:
			realSize = 9;
			ScreenSetting.setTextSize(realSize);
			break;
		case 20:
			realSize = 10;
			ScreenSetting.setTextSize(realSize);
			break;
		case 30:
			realSize = 12;
			ScreenSetting.setTextSize(realSize);
			break;
		case 40:
			realSize = 14;
			ScreenSetting.setTextSize(realSize);
			break;
		case 50:
			realSize = 16;
			ScreenSetting.setTextSize(realSize);
			break;
		case 60:
			realSize = 18;
			ScreenSetting.setTextSize(realSize);
			break;
		case 70:
			realSize = 20;
			ScreenSetting.setTextSize(realSize);
			break;
		case 80:
			realSize = 24;
			ScreenSetting.setTextSize(realSize);
			break;
		case 90:
			realSize = 28;
			ScreenSetting.setTextSize(realSize);
			break;
		case 100:
			realSize = 32;
			ScreenSetting.setTextSize(realSize);
			break;
		default:
			break;
		}	
	}

	

}
