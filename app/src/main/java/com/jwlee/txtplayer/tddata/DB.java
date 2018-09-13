package com.jwlee.txtplayer.tddata;


import android.provider.BaseColumns;


public interface DB {

    public interface BOOK extends BaseColumns {
        enum INDEX {
            _id, B_ID, B_TITLE, B_CATEGORY, B_COM, B_PDATE, B_FILENAME, B_FILESIZE, B_BTYPE, B_CHARSET, B_DDATE
        };
        
        String TABLE = "BOOK";
        
        String CREATE = "CREATE TABLE "+ TABLE
                + "("+INDEX._id     	+ " INTEGER PRIMARY KEY AUTOINCREMENT"
                + ","+INDEX.B_ID    	+ " TEXT NOT NULL"
                + ","+INDEX.B_TITLE 	+ " TEXT NOT NULL" 
                + ","+INDEX.B_CATEGORY 	+ " TEXT" 
                + ","+INDEX.B_COM   	+ " TEXT"
                + ","+INDEX.B_PDATE 	+ " LONG" 
                + ","+INDEX.B_FILENAME 	+ " TEXT NOT NULL" 
                + ","+INDEX.B_FILESIZE 	+ " INTEGER DEFAULT 0" 
                + ","+INDEX.B_BTYPE 	+ " INTEGER DEFAULT 0"
                + ","+INDEX.B_CHARSET 	+ " TEXT DEFAULT 'EUC-KR'" 
                + ","+INDEX.B_DDATE 	+ " LONG NOT NULL" 
                + ");";
        
        String DROP = "DROP TABLE IF EXISTS " + TABLE;
    }
    
    public interface RECENTLY extends BaseColumns {
        enum INDEX {
            _id, B_ID, R_TITLE, R_CATEGORY, R_PAGE, R_LINE, R_TEXT, R_FILENAME, R_TYPE, R_CHARSET, R_DATE
        };
        
        String TABLE = "RECENTLY";
        
        String CREATE = "CREATE TABLE "  + TABLE
                + "("+INDEX._id     	 + " INTEGER PRIMARY KEY AUTOINCREMENT"
                + ","+INDEX.B_ID         + " TEXT NOT NULL"
                + ","+INDEX.R_TITLE		 + " TEXT NOT NULL" 
                + ","+INDEX.R_CATEGORY	 + " TEXT NOT NULL" 
                + ","+INDEX.R_PAGE       + " TEXT NOT NULL" 
                + ","+INDEX.R_LINE       + " INTEGER DEFAULT 0"                 
                + ","+INDEX.R_TEXT       + " TEXT NOT NULL" 
                + ","+INDEX.R_FILENAME   + " TEXT NOT NULL"
                + ","+INDEX.R_TYPE       + " INTEGER DEFAULT 0" 
                + ","+INDEX.R_CHARSET 	 + " TEXT DEFAULT 'EUC-KR'" 
                + ","+INDEX.R_DATE       + " LONG NOT NULL" 
                + ");";
        
        String DROP = "DROP TABLE IF EXISTS " + TABLE;
    }
    

    public interface BOOKMARK extends BaseColumns {
        enum INDEX {
            _id, B_ID, M_TITLE, M_CATEGORY, M_PAGE, M_LINE, M_TEXT, M_FILENAME, M_TYPE, M_CHARSET, M_DATE, M_MEMO        
        };
        
        String TABLE = "BOOKMARK";
        
        String CREATE = "CREATE TABLE "+ TABLE
                + "("+INDEX._id     	+ " INTEGER PRIMARY KEY AUTOINCREMENT"
                + ","+INDEX.B_ID        + " TEXT NOT NULL"
                + ","+INDEX.M_TITLE		+ " TEXT NOT NULL" 
                + ","+INDEX.M_CATEGORY  + " TEXT NOT NULL"
                + ","+INDEX.M_PAGE      + " INTEGER NOT NULL"
                + ","+INDEX.M_LINE      + " INTEGER NOT NULL" 
                + ","+INDEX.M_TEXT      + " TEXT NOT NULL"
                + ","+INDEX.M_FILENAME 	+ " TEXT NOT NULL" 
                + ","+INDEX.M_TYPE      + " INTEGER DEFAULT 0" 
                + ","+INDEX.M_CHARSET 	+ " TEXT DEFAULT 'EUC-KR'" 
                + ","+INDEX.M_DATE      + " LONG NOT NULL" 
                + ","+INDEX.M_MEMO      + " TEXT DEFAULT 'NO'"                 
                + ");";
        
        String DROP = "DROP TABLE IF EXISTS " + TABLE;
    }
}
