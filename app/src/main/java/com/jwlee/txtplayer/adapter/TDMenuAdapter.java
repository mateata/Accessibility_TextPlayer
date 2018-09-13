package com.jwlee.txtplayer.adapter;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.tddata.Menu;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TDMenuAdapter extends ArrayAdapter<Menu> {

	private ArrayList<Menu> settingMenuList;

	private int mResource;

	public TDMenuAdapter(Context context, int rowResourceId, ArrayList<Menu> list) {
		super(context, rowResourceId, list);
		this.mResource = rowResourceId;
		this.settingMenuList = list; 
	}

	public View getView(int position, View convertView, ViewGroup parent) {        
		View v = convertView;  
		if (convertView == null) {                         
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);            
			v = vi.inflate(mResource, null);        
		}

		Menu mMenu = settingMenuList.get(position);
		if (mMenu != null ) {       
			((TextView)v.findViewById(R.id.tv_menu)).setText(""+mMenu.getMenuTitle());
			((ImageView)v.findViewById(R.id.iv_menu)).setImageResource(getMenuIcon(position));
			((ImageView)v.findViewById(R.id.iv_arrow)).setImageResource(getArrowIcon(position));

			((ImageView)v.findViewById(R.id.iv_arrow)).getLayoutParams().width = 120;
			((TextView)v.findViewById(R.id.tv_menu_number)).setText("");
			((CheckBox)v.findViewById(R.id.cb_lte)).setVisibility(View.GONE);
		}
		return v;
	}

	private int getArrowIcon(int menuPosition) {
		int resourceID = 0;
		switch (menuPosition) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			resourceID = R.drawable.arr_right_g;
			break;

		default:
			break;
		}
		return resourceID;
	}

	private int getMenuIcon(int menuPosition) {
		int resourceID = 0;
		switch (menuPosition) {
		case 0:
			resourceID = R.drawable.icon_sh; // 검색
			break;
		case 1:
			resourceID = R.drawable.menu_icon_0; // 마크지정
			break;
		case 2:
			resourceID = R.drawable.menu_icon_1; // 메모삽입
			break;
		case 3:
			resourceID = R.drawable.menu_icon_2; // 마크목록
			break;
		case 4:
			resourceID = R.drawable.menu_icon_3; // 메모목록
			break;
		case 5:
			resourceID = R.drawable.menu_icon_4; // 메모목록
			break;
		case 6:
			resourceID = R.drawable.menu_icon_5; // 메모목록
			break;			
		case 7:
			resourceID = R.drawable.menu_icon_6; // 취침예약
			break;
		case 8:
			resourceID = R.drawable.menu_icon_7; // 플레이어 설정
			break;			
		default:
			break;
		}

		return resourceID;
	}
}
