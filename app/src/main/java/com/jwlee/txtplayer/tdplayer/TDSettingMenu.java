package com.jwlee.txtplayer.tdplayer;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.adapter.TDSettingMenuAdapter;
import com.jwlee.txtplayer.tddata.Menu;

public class TDSettingMenu extends TDBaseActivity implements OnItemClickListener, OnClickListener{

	private ArrayList<Menu> array_Menu;
	private TDSettingMenuAdapter menuAdapter;
	private ListView menuList;
	private TextView tv_header;
	private Button backBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		setContentView(R.layout.f_setting);	
		
		menuList = (ListView)findViewById(R.id.player_menu);
		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.player_menu_setting);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_back);
		
		addMenus();
		setListView();
	}

	private void addMenus() {
		array_Menu = new ArrayList<Menu>();
		Menu menu01 = new Menu(getString(R.string.setting_menu_02));
		Menu menu02 = new Menu(getString(R.string.setting_menu_03));
		Menu menu03 = new Menu(getString(R.string.setting_menu_04));

		array_Menu.add(0, menu01);
		array_Menu.add(1, menu02);
		array_Menu.add(2, menu03);
	}

	private void setListView() {

		menuAdapter = new TDSettingMenuAdapter(mContext, R.layout.i_set_menu_icon, array_Menu);
		menuList.setOnItemClickListener(this);
		menuList.setAdapter(menuAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			// 플레이어 음성설정으로 이동
	        startActivity(new Intent(mContext, TDVoiceSet.class)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		case 1:
			// 플레이어 기능설정으로 이동
	        startActivity(new Intent(mContext, TDFunctionSet.class)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		case 2:
			// 플레이어 화면설정으로 이동
	        startActivity(new Intent(mContext, TDDisplaySet.class)
			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		default:
			break;
		}

	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		setResult(RESULT_OK, i); 
		finish();
		super.onBackPressed();
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
}
