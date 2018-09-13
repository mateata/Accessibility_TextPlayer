package com.jwlee.txtplayer.tddata;

import com.jwlee.txtplayer.R;

import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class Book {
	public static Uri CONTENT_URI = Uri.parse("content://" + TxtProvider.AUTHORITIE + "/book");
	
	public static String[] projection = new String[] { 
		DB.BOOK.INDEX._id.toString(),
		DB.BOOK.INDEX.B_ID.toString(),
		DB.BOOK.INDEX.B_TITLE.toString(), 	
		DB.BOOK.INDEX.B_CATEGORY.toString(), 		
		DB.BOOK.INDEX.B_COM.toString(),   	
		DB.BOOK.INDEX.B_PDATE.toString(), 	
		DB.BOOK.INDEX.B_FILENAME.toString(), 	
		DB.BOOK.INDEX.B_FILESIZE.toString(), 	 
		DB.BOOK.INDEX.B_BTYPE.toString(),
		DB.BOOK.INDEX.B_CHARSET.toString(), 	
		DB.BOOK.INDEX.B_DDATE.toString() 	
		
	};
	
    public static class Data {
    	public String      	id    		;
    	public String      	title 		;
    	public String      	category 	;
    	public String      	com   		;
    	public String     	pdate 		;	
    	public String     	filename 	;
    	public String     	filesize 	;
    	public int     		btype 	 	;
    	public String     	bchar 	 	;
    	public String     	ddate 		; 	
    	
        public Data(Cursor cursor) {       
        	id    		 	= cursor.getString	(DB.BOOK.INDEX.B_ID.ordinal()	 	);
        	title 		 	= cursor.getString	(DB.BOOK.INDEX.B_TITLE.ordinal() 	);
        	category 		= cursor.getString	(DB.BOOK.INDEX.B_CATEGORY.ordinal() 	);
        	com   		 	= cursor.getString	(DB.BOOK.INDEX.B_COM.ordinal()   	);
        	pdate 		 	= cursor.getString	(DB.BOOK.INDEX.B_PDATE.ordinal() 	);
        	filename 	 	= cursor.getString	(DB.BOOK.INDEX.B_FILENAME.ordinal() );
        	filesize 	 	= cursor.getString	(DB.BOOK.INDEX.B_FILESIZE.ordinal() );
        	btype 	 	  	= cursor.getInt		(DB.BOOK.INDEX.B_BTYPE.ordinal() 	);
        	bchar 	 	  	= cursor.getString	(DB.BOOK.INDEX.B_CHARSET.ordinal() 	);
        	ddate 		  	= cursor.getString	(DB.BOOK.INDEX.B_DDATE.ordinal()	);
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
