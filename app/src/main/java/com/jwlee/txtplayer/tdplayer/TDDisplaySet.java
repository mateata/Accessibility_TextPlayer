package com.jwlee.txtplayer.tdplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.adapter.MenuAdapter;
import com.jwlee.txtplayer.setting.ScreenSetting;
import com.jwlee.txtplayer.tddata.Menu;
import com.jwlee.txtplayer.util.JwUtils;

public class TDDisplaySet extends TDBaseActivity implements OnClickListener, OnItemClickListener{
	public Context mContext;


	private ListView mList;
	private ArrayList<Menu> array_Menu;
	private MenuAdapter menuAdapter;

	private Button bt_size_down;
	private Button bt_size_up;
	
	private int fontSize = 50;
	private TextView tv_fontsize;
	private SharedPreferences pref;
	private SharedPreferences.Editor settingEditor;
	private TextView tv_header;
	private Button backBtn;
		

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.f_set_display);		
		mContext = this;
		pref = mContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		settingEditor = pref.edit();
		init_layout();

		addMenus();

	}
	
	private void addMenus() {
		Menu menu0 = new Menu(getString(R.string.set_contrast0));
		Menu menu1 = new Menu(getString(R.string.set_contrast1));
		Menu menu2 = new Menu(getString(R.string.set_contrast2));
		Menu menu3 = new Menu(getString(R.string.set_contrast3));
		Menu menu4 = new Menu(getString(R.string.set_contrast4));
		
		int cbCheck = pref.getInt(mContext.getString(R.string.setting_color_set), 0);		
		switch (cbCheck) {
			case 0:
				menu0.setChecked(true);
				break;
			case 1:
				menu1.setChecked(true);
				break;
			case 2:
				menu2.setChecked(true);
				break;
			case 3:
				menu3.setChecked(true);
				break;
			case 4:
				menu4.setChecked(true);
				break;				
		}

		array_Menu.add(0, menu0);
		array_Menu.add(1, menu1);
		array_Menu.add(2, menu2);
		array_Menu.add(3, menu3);
		array_Menu.add(4, menu4);

		menuAdapter.notifyDataSetChanged();
	}

	private void init_layout() {
	
		bt_size_down = (Button)findViewById(R.id.bt_size_down);
		bt_size_down.setOnClickListener(this);
		
		bt_size_up = (Button)findViewById(R.id.bt_size_up);
		bt_size_up.setOnClickListener(this);
				
		fontSize = pref.getInt(getString(R.string.setting_text_size), 50);
		tv_fontsize = (TextView)findViewById(R.id.tv_fontsize);
		tv_fontsize.setText(""+fontSize);
		
		

		array_Menu = new ArrayList<Menu>();
		menuAdapter = new MenuAdapter(mContext, R.layout.i_sel_contra,array_Menu,2);

		mList = (ListView)findViewById(R.id.listView1);
		mList.setOnItemClickListener(this);
		mList.setAdapter(menuAdapter);

		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.setting_menu_04);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_back);
	}


	@Override
	public void onClick(View v) {
		
		
		switch (v.getId()) {
		case R.id.backBtn:
			onBackPressed();
			break;			
		case R.id.bt_size_down:
			if(fontSize > 0) {
				fontSize -= 10;
				settingEditor.putInt(getString(R.string.setting_text_size), fontSize).commit();
				ScreenSetting.setSize(fontSize);
				tv_fontsize.setText(""+fontSize);
				JwUtils.toastCustom(mContext, getString(R.string.setting_decrease,fontSize));	
			}
			break;
			
		case R.id.bt_size_up:
			if(fontSize < 100) {
				fontSize += 10;
				settingEditor.putInt(getString(R.string.setting_text_size), fontSize).commit();
				ScreenSetting.setSize(fontSize);
				tv_fontsize.setText(""+fontSize);
				JwUtils.toastCustom(mContext, getString(R.string.setting_increase,fontSize));	
			}
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		for(int mIdx = 0 ; mIdx<array_Menu.size(); mIdx++){
			if(mIdx == position){
				array_Menu.get(mIdx).setChecked(true);
			}else {
				array_Menu.get(mIdx).setChecked(false);
			}
		}
		menuAdapter.notifyDataSetChanged();		
		setColors(position);
		
	}

	
	private void setColors(int mPosition) {
		int settingBGColor = 0;
		int settingTextColor = 0;
		switch (mPosition) {
		case 0:
			settingBGColor = Color.BLACK;
			settingTextColor =  Color.WHITE;
			break;
		case 1:
			settingBGColor = Color.BLACK;
			settingTextColor =  Color.WHITE;
			break;
		case 2:
			settingBGColor = Color.WHITE;
			settingTextColor =  Color.BLACK;
			break;
		case 3:
			settingBGColor = Color.BLACK;
			settingTextColor =  Color.YELLOW;
			break;
		case 4:
			settingBGColor = Color.BLUE;
			settingTextColor =  Color.YELLOW;		
			break;
		default:
			break;
		}
		
		settingEditor.putInt(getString(R.string.setting_color_set), mPosition );
		settingEditor.putInt(getString(R.string.setting_bg_color),settingBGColor );
		settingEditor.putInt(getString(R.string.setting_text_color),settingTextColor);
		settingEditor.commit();
		ScreenSetting.setBgColor(settingBGColor);
		ScreenSetting.setTextColor(settingTextColor);
	}
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
}
