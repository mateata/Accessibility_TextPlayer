package com.jwlee.txtplayer.adapter;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.FileOpen;
import com.jwlee.txtplayer.setting.PlayerSetting;
import com.jwlee.txtplayer.tddata.InfoItem;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SetFuncAdapter extends ArrayAdapter<InfoItem> implements OnCheckedChangeListener {

	private ArrayList<InfoItem> setfuncList;
	private ViewHolder vHolder = null;
	private Context fContext;
	private int mResource;
	private SharedPreferences pref;
	private SharedPreferences.Editor settingEditor;
	
	public SetFuncAdapter(Context context, int rowResourceId, 
			ArrayList<InfoItem> list) {
		super(context, rowResourceId, list);
		this.mResource = rowResourceId;
		this.setfuncList = list; 
		this.fContext = context;
		pref = fContext.getSharedPreferences("TXTPLAYER", Activity.MODE_PRIVATE);
		settingEditor = pref.edit();

	}

	public View getView(int position, View convertView, ViewGroup parent) {        
		View v = convertView;  
		if (v == null) {
			vHolder = new ViewHolder();
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);            
			v = vi.inflate(mResource, null);      


			vHolder.tv_setfunc_subtitle = (TextView)v.findViewById(R.id.tv_setfunc_subtitle);
			vHolder.l_setfunc_item = (RelativeLayout)v.findViewById(com.jwlee.txtplayer.R.id.l_setfunc_item);
			vHolder.tv_setfunc_name = (TextView)v.findViewById(R.id.tv_setfunc_name);
			vHolder.cb_setfunc_value = (CheckBox)v.findViewById(R.id.cb_setfunc_value);
			
			v.setTag(vHolder);
		}else{
			vHolder = (ViewHolder)v.getTag();
		}

		InfoItem mBook = setfuncList.get(position);
		if (mBook != null ) {
			if(position == 0 ){
				vHolder.tv_setfunc_subtitle.setVisibility(View.VISIBLE);
				vHolder.tv_setfunc_subtitle.setText(mBook.getInfoName());
				vHolder.tv_setfunc_subtitle.setContentDescription(mBook.getInfoName() + fContext.getString(R.string.common_heading));
				vHolder.l_setfunc_item.setVisibility(View.GONE);
			}else if(position == 3){
				vHolder.tv_setfunc_subtitle.setVisibility(View.VISIBLE);
				vHolder.tv_setfunc_subtitle.setText(mBook.getInfoName());
				vHolder.tv_setfunc_subtitle.setContentDescription(mBook.getInfoName() + fContext.getString(R.string.common_heading));
				vHolder.l_setfunc_item.setVisibility(View.GONE);
			} else {
				vHolder.tv_setfunc_subtitle.setVisibility(View.GONE);
				vHolder.l_setfunc_item.setVisibility(View.VISIBLE);				
			}
			vHolder.tv_setfunc_name.setText(""+mBook.getInfoName());
			vHolder.cb_setfunc_value.setTag(position);
			vHolder.cb_setfunc_value.setOnCheckedChangeListener(this);

			vHolder.cb_setfunc_value.setChecked(PlayerSetting.getSetFunction(position));
			
			vHolder.tv_setfunc_name.setTypeface(FileOpen.mTypeface);
			vHolder.tv_setfunc_subtitle.setTypeface(FileOpen.mTypeface);
		}
		return v;
	}

	class ViewHolder{
		public TextView tv_setfunc_subtitle = null;
		public TextView tv_setfunc_name = null;
		public CheckBox cb_setfunc_value = null;
		public RelativeLayout l_setfunc_item = null;

	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		PlayerSetting.setSetFunction(Integer.parseInt(""+buttonView.getTag()),isChecked);
		String[] setfunc = fContext.getResources().getStringArray(R.array.setfunction);		
		settingEditor.putBoolean(setfunc[Integer.parseInt(""+buttonView.getTag())], isChecked).commit();
	}
}

