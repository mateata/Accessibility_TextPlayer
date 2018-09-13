package com.jwlee.txtplayer.tdplayer;

import java.util.ArrayList;
import java.util.HashMap;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.adapter.SetFuncAdapter;
import com.jwlee.txtplayer.tddata.InfoItem;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TDFunctionSet extends TDBaseActivity implements OnClickListener{

	private ListView funcList;
	private SetFuncAdapter setfuncAdapter;
	private ArrayList<InfoItem> infoItemList = null; 

	private SharedPreferences pref;
	private TextView tv_header;
	private Button backBtn;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.f_set_function);
		
		pref = mContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		pref.edit();

		init_layout();
		setValues();
	}

	private HashMap<String, String> setValues() {

		HashMap<String, String> setfuncMap = new HashMap<String,String>();


		for(String s : getResources().getStringArray(R.array.setfunction)){
			String[] split = s.split(":");
			
			InfoItem infoItem = new InfoItem(split[1],split[0]);
			setfuncAdapter.add(infoItem);

		}


		return setfuncMap;
	}


	private void init_layout() {
		
		tv_header = (TextView)findViewById(R.id.tv_header_title);
		tv_header.setText(R.string.setting_menu_03);		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		backBtn.setText(R.string.common_back);	
		
		funcList = (ListView)findViewById(R.id.listView1);		
		infoItemList = new ArrayList<InfoItem>();
		setfuncAdapter = new SetFuncAdapter(mContext, R.layout.i_set_func,infoItemList);
		
		funcList.setAdapter(setfuncAdapter);
				
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
