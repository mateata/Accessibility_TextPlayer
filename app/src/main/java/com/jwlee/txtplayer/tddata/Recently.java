package com.jwlee.txtplayer.tddata;

import com.jwlee.txtplayer.R;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class Recently {
	public static Uri CONTENT_URI = Uri.parse("content://" + TxtProvider.AUTHORITIE + "/recently");
	
	public static String[] projection = new String[] { 
		DB.RECENTLY.INDEX._id.toString(),
		DB.RECENTLY.INDEX.B_ID.toString(),
		DB.RECENTLY.INDEX.R_TITLE.toString(),
		DB.RECENTLY.INDEX.R_CATEGORY.toString(),
		DB.RECENTLY.INDEX.R_PAGE.toString(),
		DB.RECENTLY.INDEX.R_LINE.toString(), 		
		DB.RECENTLY.INDEX.R_TEXT.toString(), 		
		DB.RECENTLY.INDEX.R_FILENAME.toString(),   	
		DB.RECENTLY.INDEX.R_TYPE.toString(),
		DB.RECENTLY.INDEX.R_CHARSET.toString(), 	
		DB.RECENTLY.INDEX.R_DATE.toString() 	
	};
	
    public static class Data {
    	public String      	id       ;
    	public String      	title    ;
    	public String      	category ;
    	public String  		page     ;  
    	public int     	line     ;     	
    	public String      	text     ;  
    	public String      	filename ;
    	public int     		type     ;  
    	public String     	bchar 	 ;
    	public String      	date     ;  

    	
        public Data(Cursor cursor) {       
        	id    		 	= cursor.getString	(DB.RECENTLY.INDEX.B_ID.ordinal()	 	);
        	title 		 	= cursor.getString	(DB.RECENTLY.INDEX.R_TITLE.ordinal() 	);
        	category 		= cursor.getString	(DB.RECENTLY.INDEX.R_CATEGORY.ordinal() 	);
        	page 		 	= cursor.getString	(DB.RECENTLY.INDEX.R_PAGE.ordinal() 	);
        	line 		 	= cursor.getInt		(DB.RECENTLY.INDEX.R_LINE.ordinal() 	);        	
        	text 		 	= cursor.getString	(DB.RECENTLY.INDEX.R_TEXT.ordinal() 	);
        	filename   		= cursor.getString	(DB.RECENTLY.INDEX.R_FILENAME.ordinal() );
        	type 		 	= cursor.getInt		(DB.RECENTLY.INDEX.R_TYPE.ordinal() 	);
        	bchar 		 	= cursor.getString	(DB.RECENTLY.INDEX.R_CHARSET.ordinal() 	);
        	date 	 		= cursor.getString	(DB.RECENTLY.INDEX.R_DATE.ordinal() 	);
        }
    }

    public static class Holder extends BaseHolder{
    	public TextView 	title;
        public CheckBox 	check;        

        public View set(View v) {   	
        	title 		= (TextView) v.findViewById(R.id.title);
        	check 		= (CheckBox) v.findViewById(R.id.check);
            return super.set(v);
        }
    }
}
