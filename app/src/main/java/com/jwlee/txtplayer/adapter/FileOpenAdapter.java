package com.jwlee.txtplayer.adapter;

import java.util.ArrayList;

import com.jwlee.txtplayer.R;
import com.jwlee.txtplayer.util.JwFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class FileOpenAdapter extends ArrayAdapter<JwFile> {

	private ArrayList<JwFile> mFile;
	private int mResource;
	private ViewHolder vHolder = null;
	
	private Context mContext;

	public FileOpenAdapter(Context context, int rowResourceId, ArrayList<JwFile> mFile) {
		super(context, rowResourceId, mFile);
		this.mResource = rowResourceId;
		this.mFile = mFile; 
		mContext = context;
		
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {        
		View v = convertView;  
		if (convertView == null) {         

			vHolder = new ViewHolder();
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);            
			v = vi.inflate(mResource, null); 


//			vHolder.cb_menu = (CheckBox)v.findViewById(R.id.cb_sel);
			v.setTag(vHolder);

		}else{
			vHolder = (ViewHolder)v.getTag();
		}


		JwFile menu = mFile.get(position);
		if (menu != null ) {
			
				// 파일 이름(화면에 보이는) SET 
				((TextView)v.findViewById(com.jwlee.txtplayer.R.id.tv_sorting))
					.setText(menu.getFileName());

				// 대체 텍스트 SET 				
				((TextView)v.findViewById(com.jwlee.txtplayer.R.id.tv_sorting))
					.setContentDescription(menu.getFileName()
							+(menu.getIsfolder()?mContext.getString(R.string.str_folder):""));
				// 폴더라면 폴더 아이콘을 보여줌
				if(menu.getIsfolder()) {
					((ImageView)v.findViewById(R.id.iv_folder)).setImageResource(R.drawable.img_folder);								
					((ImageView)v.findViewById(R.id.iv_folder)).setVisibility(View.VISIBLE);					
				} else {
					((ImageView)v.findViewById(R.id.iv_folder)).setVisibility(View.GONE);
				}
		

		}else{

		}
		return v;
	}
	
	
	class ViewHolder{
		public CheckBox cb_menu = null;

	}
	


}

