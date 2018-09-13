package com.jwlee.txtplayer.adapter;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.tddata.Menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<Menu> implements OnCheckedChangeListener {

	private ArrayList<Menu> mMenu;
	private int mResource;
	private int itemDiv;
	private ViewHolder vHolder = null;

	
	private Context mContext;

	public MenuAdapter(Context context, int rowResourceId, ArrayList<Menu> mMenu,int itemDiv) {
		super(context, rowResourceId, mMenu);
		this.mResource = rowResourceId;
		this.mMenu = mMenu; 
		this.itemDiv = itemDiv;
		mContext = context;
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {        
		View v = convertView;  
		if (convertView == null) {         

			vHolder = new ViewHolder();
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);            
			v = vi.inflate(mResource, null); 


			vHolder.cb_menu = (CheckBox)v.findViewById(R.id.cb_sel);
			v.setTag(vHolder);

		}else{
			vHolder = (ViewHolder)v.getTag();
		}


		Menu menu = mMenu.get(position);
		if (menu != null ) {
			switch (itemDiv) {
			case 0:
				// menuactivity의 목록  
				((TextView)v.findViewById(R.id.tv_menu)).setText(""+menu.getMenuTitle());
				((TextView)v.findViewById(R.id.tv_menu)).setContentDescription(""+menu.getMenuTitle()+" "+mContext.getString(R.string.common_button));				
				switch (position) {
				case 0:
					((ImageView)v.findViewById(R.id.iv_menu)).setBackgroundResource(R.drawable.menu_icon_1);
					break;
				case 1:
					((ImageView)v.findViewById(R.id.iv_menu)).setBackgroundResource(R.drawable.menu_icon_2);
					break;
				case 2:
					((ImageView)v.findViewById(R.id.iv_menu)).setBackgroundResource(R.drawable.menu_icon_3);
					break;
				default:
					break;
				}
				break;

			case 1:
				break;

			case 2:
				// 플레이어 화면설정 - 고대비모드설정
				((TextView)v.findViewById(R.id.tv_sel_name)).setText(""+menu.getMenuTitle());
				((CheckBox)v.findViewById(R.id.cb_sel)).setButtonDrawable(R.drawable.sel_btn_check);
				((CheckBox)v.findViewById(R.id.cb_sel)).setFocusable(false);
				((CheckBox)v.findViewById(R.id.cb_sel)).setFocusableInTouchMode(false);
				((CheckBox)v.findViewById(R.id.cb_sel)).setVisibility(View.VISIBLE);
				((CheckBox)v.findViewById(R.id.cb_sel)).setChecked(menu.isChecked());

				switch (position) { 
				case 0:
					((ImageView)v.findViewById(R.id.iv_sel_contra)).setBackgroundResource(R.drawable.icon_cont_01);
					break;
				case 1:
					((ImageView)v.findViewById(R.id.iv_sel_contra)).setBackgroundResource(R.drawable.icon_cont_02);
					break;
				case 2:
					((ImageView)v.findViewById(R.id.iv_sel_contra)).setBackgroundResource(R.drawable.icon_cont_03);
					break;
				case 3:
					((ImageView)v.findViewById(R.id.iv_sel_contra)).setBackgroundResource(R.drawable.icon_cont_04);
					break;
				case 4:
					((ImageView)v.findViewById(R.id.iv_sel_contra)).setBackgroundResource(R.drawable.icon_cont_05);
					break;

				default:
					break;
				}
				break;

			case 3:
				((TextView)v.findViewById(R.id.tv_sel_name)).setText(""+menu.getMenuTitle());
				vHolder.cb_menu.setChecked(menu.isChecked());
				vHolder.cb_menu.setTag(position);
				vHolder.cb_menu.setOnCheckedChangeListener(this);
				break;
			case 4:
				((TextView)v.findViewById(R.id.tv_sel_name)).setText(""+menu.getMenuTitle());
				((CheckBox)v.findViewById(R.id.cb_sel)).setChecked(menu.isChecked());								
				break;
			default:
				break;
			}


		}else{
		}
		return v;
	}
	
	class ViewHolder{
		public CheckBox cb_menu = null;

	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub		
	}
	

}

