package com.jwlee.txtplayer.util;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;




import com.jwlee.txtplayer.adapter.MenuAdapter;
import com.jwlee.txtplayer.tddata.Menu;
import com.jwlee.txtplayer.tdplayer.TDBaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SleepSet extends TDBaseActivity implements OnItemClickListener, OnClickListener {


	private ListView mList;
	private ArrayList<Menu> array_Menu;
	private MenuAdapter menuAdapter;

	private SharedPreferences pref;
	private SharedPreferences.Editor settingEditor;
    private int sleepSet = 0;
    private int sleepPos = 0;
	private TextView tv_header;
	private Button backBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.a_sleep);
		
		pref = mContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		settingEditor = pref.edit();
		init_layout();
		int def = pref.getInt(getString(R.string.setting_sleep), 0);
		addMenus(def);

	}
	
	private void addMenus(int def) {
		Menu menu0 = new Menu(getString(R.string.set_sleep0));
		Menu menu1 = new Menu(getString(R.string.set_sleep1));
		Menu menu2 = new Menu(getString(R.string.set_sleep2));
		Menu menu3 = new Menu(getString(R.string.set_sleep3));
		Menu menu4 = new Menu(getString(R.string.set_sleep4));

		array_Menu.add(0, menu0);
		array_Menu.add(1, menu1);
		array_Menu.add(2, menu2);
		array_Menu.add(3, menu3);
		array_Menu.add(4, menu4);
		array_Menu.get(def).setChecked(true);
		menuAdapter.notifyDataSetChanged();
	}

	private void init_layout() {			

		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.player_menu_sleep);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_back);
		
		array_Menu = new ArrayList<Menu>();
		menuAdapter = new MenuAdapter(mContext, R.layout.i_sel_contra,array_Menu,4);

		mList = (ListView)findViewById(R.id.listView1);
		mList.setOnItemClickListener(this);
		mList.setAdapter(menuAdapter);
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
		
		setSep(position);		
		
	}
	private void setSep(int pos) {
		sleepPos = pos;
		switch (pos) {
		case 0:
			sleepSet = 0;
			break;
		case 1:
			sleepSet = 5;
			break;
		case 2:
			sleepSet = 15;
			break;
		case 3:
			sleepSet = 30;
			break;
		case 4:
			sleepSet = 60;
			break;
		default:
			break;			
		}
	}
	
  		
	@Override
	public void onBackPressed() {
		settingEditor.putInt(getString(R.string.setting_sleep), sleepPos).commit();
  		Intent intent = getIntent();		
    	intent.putExtra("SLEEP", sleepSet);
    	setResult(RESULT_OK, intent); 
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
