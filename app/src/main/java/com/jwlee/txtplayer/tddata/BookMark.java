package com.jwlee.txtplayer.tddata;

import com.jwlee.txtplayer.R;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class BookMark {
	public static Uri CONTENT_URI 		= Uri.parse("content://" + TxtProvider.AUTHORITIE + "/bookmark");
	public static Uri CONTENT_URI_LIST 	= Uri.parse("content://" + TxtProvider.AUTHORITIE + "/bookmark_list");
	
	public static String[] projection = new String[] { 
		DB.BOOKMARK.INDEX._id.toString(),
		DB.BOOKMARK.INDEX.B_ID.toString(),
		DB.BOOKMARK.INDEX.M_TITLE.toString(), 	
		DB.BOOKMARK.INDEX.M_CATEGORY.toString(),
		DB.BOOKMARK.INDEX.M_PAGE.toString(),
		DB.BOOKMARK.INDEX.M_LINE.toString(), 
		DB.BOOKMARK.INDEX.M_TEXT.toString(),   	
		DB.BOOKMARK.INDEX.M_FILENAME.toString(), 	
		DB.BOOKMARK.INDEX.M_TYPE.toString(),
		DB.BOOKMARK.INDEX.M_CHARSET.toString(), 	
		DB.BOOKMARK.INDEX.M_DATE.toString(),
		DB.BOOKMARK.INDEX.M_MEMO.toString()	 
	};
	
    public static class Data {
    	public String      	id      ;
    	public String      	title	;
    	public String       category;
    	public int      	page    ;
    	public int      	line    ;
    	public String      	text    ;
    	public String     	filename;	
    	public int     		type    ;
    	public String   	bchar 	;
    	public String     	date	;
    	public String     	memo	;
    	
        public Data(Cursor cursor) {       
        	id      	= cursor.getString	(DB.BOOKMARK.INDEX.B_ID.ordinal()	 	);
        	title		= cursor.getString	(DB.BOOKMARK.INDEX.M_TITLE.ordinal() 	);
        	category	= cursor.getString	(DB.BOOKMARK.INDEX.M_CATEGORY.ordinal() );
        	page    	= cursor.getInt		(DB.BOOKMARK.INDEX.M_PAGE.ordinal() 	);
        	line    	= cursor.getInt		(DB.BOOKMARK.INDEX.M_LINE.ordinal() 	);
        	text    	= cursor.getString	(DB.BOOKMARK.INDEX.M_TEXT.ordinal()   	);
        	filename	= cursor.getString	(DB.BOOKMARK.INDEX.M_FILENAME.ordinal() );
        	type    	= cursor.getInt		(DB.BOOKMARK.INDEX.M_TYPE.ordinal() 	);
        	bchar    	= cursor.getString	(DB.BOOKMARK.INDEX.M_CHARSET.ordinal() 	);
        	date		= cursor.getString	(DB.BOOKMARK.INDEX.M_DATE.ordinal() 	);
        	memo		= cursor.getString	(DB.BOOKMARK.INDEX.M_MEMO.ordinal() 	);        	
        }
    }

    public static class Holder extends BaseHolder{
        public TextView 	title;
        public TextView 	page;
        public CheckBox 	check;
        public TextView		update;

        public View set(View v) {
        	title 		= (TextView) v.findViewById(R.id.title);
        	page 		= (TextView) v.findViewById(R.id.page);
        	check 		= (CheckBox) v.findViewById(R.id.check);
        	update 		= (TextView) v.findViewById(R.id.btn_update);
        	return super.set(v);
        }
    }
}
