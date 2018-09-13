package com.jwlee.txtplayer.adapter;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.tddata.Menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class TDSettingMenuAdapter extends ArrayAdapter<Menu> {

	private ArrayList<Menu> settingMenuList;

	private int mResource;

	public TDSettingMenuAdapter(Context context, int rowResourceId, ArrayList<Menu> list) {
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
			resourceID = R.drawable.menu_icon_4;
			break;
		case 1:
			resourceID = R.drawable.menu_icon_7;
			break;
		case 2:
			resourceID = R.drawable.menu_icon_3;
			break;
		default:
			break;
		}

		return resourceID;
	}
}
