package com.jwlee.txtplayer.tdplayer;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.adapter.TDMenuAdapter;
import com.jwlee.txtplayer.tddata.Menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class TDPlayerMenu extends TDBaseActivity implements OnItemClickListener, OnClickListener {

	public static Context mContext;
	private ArrayList<Menu> array_Menu;
	private TDMenuAdapter menuAdapter;
	private ListView menuList;
	private TextView tv_header;
	private Button backBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.f_setting);			
		menuList = (ListView)findViewById(R.id.player_menu);
		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.title_playermenu);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_back);
		
		addMenus();
		AppControl.NA_Playable = 3;
	}
	
	private void addMenus() {
		array_Menu = new ArrayList<Menu>();
		Menu menu00 = new Menu(getString(R.string.player_menu_search));
		Menu menu01 = new Menu(getString(R.string.player_menu_bookmark));
		Menu menu02 = new Menu(getString(R.string.player_menu_memo));
		Menu menu03 = new Menu(getString(R.string.player_menu_marklist));
		Menu menu04 = new Menu(getString(R.string.player_menu_memolist));
		Menu menu05 = new Menu(getString(R.string.player_menu_movepage));
		Menu menu06 = new Menu(getString(R.string.player_menu_movepercent));
		Menu menu07 = new Menu(getString(R.string.player_menu_sleep));
		Menu menu08 = new Menu(getString(R.string.player_menu_setting));

					
		array_Menu.add(0, menu00);
		array_Menu.add(1, menu01);
		array_Menu.add(2, menu02);
		array_Menu.add(3, menu03);
		array_Menu.add(4, menu04);
		array_Menu.add(5, menu05);
		array_Menu.add(6, menu06);
		array_Menu.add(7, menu07);
		array_Menu.add(8, menu08);

		setListView();		
	}

	private void setListView() {

		menuAdapter = new TDMenuAdapter(mContext, R.layout.i_set_menu_icon, array_Menu);
		menuList.setOnItemClickListener(this);
		menuList.setAdapter(menuAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //왼쪽볼륨, 오른쪽볼륨, 우선순위, 반복횟수 : -1 무한반복 / 0 반복안함, 재생속도
            	menuList.setSelection(0);
            }
        },1500);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		moveMenu(position);
	}	


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			onBackPressed();
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			onBackPressed();
		}
		
		return false;
	}
	
	private void moveMenu(int menuNumber) {
		Intent i = new Intent(); 
		i.putExtra("MENU", menuNumber);
		setResult(RESULT_OK, i); 
    	finish();
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(); 
		setResult(RESULT_CANCELED,i);
		finish();
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
