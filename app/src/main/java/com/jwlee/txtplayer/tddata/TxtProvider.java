package com.jwlee.txtplayer.tddata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class TxtProvider extends ContentProvider {
	// android 내부 폴더에 저장 될 database 파일명
	// 실제 위치는 /data/data/패키지명/databases/
	private static final String DATABASE_NAME 	= "txtplayer.db"; 
	private static final int 	DATABASE_VERSION 	= 2;
	
	private SQLiteDatabase mNLDB;
	
	static final String AUTHORITIE 					= "com.jwlee.txtplayer";
	
	private static final int BOOK_DATA 				= 1;
	private static final int RECENTLY_DATA 			= 2;
	private static final int BOOKMARK_DATA 			= 3;
	private static final int BOOKMARK_DATA_LIST		= 4;

	private static final UriMatcher Matcher;
	
	static {
	    Matcher = new UriMatcher(UriMatcher.NO_MATCH);
	    Matcher.addURI(AUTHORITIE, "book", 				BOOK_DATA);
	    Matcher.addURI(AUTHORITIE, "recently", 			RECENTLY_DATA);
	    Matcher.addURI(AUTHORITIE, "bookmark", 			BOOKMARK_DATA);
	    Matcher.addURI(AUTHORITIE, "bookmark_list", 	BOOKMARK_DATA_LIST);
	}
	
	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;  // 삭제 한 갯수
        
        switch(Matcher.match(uri)) {
	        case BOOK_DATA :
	            count = mNLDB.delete( DB.BOOK.TABLE , selection, selectionArgs );
	            break;
	        case RECENTLY_DATA :
	            count = mNLDB.delete( DB.RECENTLY.TABLE , selection, selectionArgs );
	            break;
	        case BOOKMARK_DATA_LIST :
	            count = mNLDB.delete( DB.BOOKMARK.TABLE , selection, selectionArgs );
	            break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    } 



	@Override
    public String getType(Uri uri) {
        switch(Matcher.match(uri)) {
	        case BOOK_DATA :
	            return "vnd.txtplayer.cursor/book";
	        case RECENTLY_DATA :
	            return "vnd.txtplayer.cursor/recently";
	        case BOOKMARK_DATA :
	            return "vnd.txtplayer.cursor/bookmark";
	        case BOOKMARK_DATA_LIST :
	            return "vnd.txtplayer.cursor/bookmark_list";
	            
	        default :
	            return null;
        }
    } 


	@Override
    public Uri insert(Uri uri, ContentValues values) {
        long id; // 추가하고 리턴 받은 id값

        switch(Matcher.match(uri)){
	        case BOOK_DATA :
	            id = mNLDB.insert(DB.BOOK.TABLE, null, values);
	            if( id > 0 ){// 완성된 URI 형태로 넘겨주기 위한 코드
	            	// 정상대로 저장 했으면 uri에 noti를 해줌 DB 갱신 했다고 알려주는 것임
	                Uri notiuri = ContentUris.withAppendedId(Book.CONTENT_URI, id);
	                getContext().getContentResolver().notifyChange(notiuri,null);
	                return notiuri;
	            }
	            break;
	        case RECENTLY_DATA :
	            id = mNLDB.insert(DB.RECENTLY.TABLE, null, values);
	            if( id > 0 ){
	                Uri notiuri = ContentUris.withAppendedId(Recently.CONTENT_URI, id);
	                getContext().getContentResolver().notifyChange(notiuri,null);
	                return notiuri;
	            }
	            break;
	        case BOOKMARK_DATA :
	            id = mNLDB.insert(DB.BOOKMARK.TABLE, null, values);
	            if( id > 0 ){
	                Uri notiuri = ContentUris.withAppendedId(BookMark.CONTENT_URI, id);
	                getContext().getContentResolver().notifyChange(notiuri,null);
	                return notiuri;
	            }
	            break;
	    }
        return null;
    } 

    @Override
    public boolean onCreate() {
        mNLDB = new NLDatabase(getContext(), DATABASE_NAME, 
                                   null, DATABASE_VERSION).getWritableDatabase();
        return (mNLDB == null)? false : true;
        // DB 객체 상태에 따라서 false, true를 반환 하는데 true면 해당 class가 사용 가능 상태,       
        // false 면 해당 class가  사용 중지
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        switch(Matcher.match(uri)){
    	// 질의 요청 Cursor 형태로 return 됨 그리고 좀 찾아보면 좋은거 있음
    	// GroupBy, 라던지 Having이라던지 이런것도 가능 하게 해줌  
	        case BOOK_DATA :  
	            return mNLDB.query(DB.BOOK.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
	        case RECENTLY_DATA :
	            return mNLDB.query(DB.RECENTLY.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
	        case BOOKMARK_DATA :
	            return mNLDB.query(DB.BOOKMARK.TABLE, projection, selection, selectionArgs, DB.BOOKMARK.INDEX.B_ID.toString() , null, sortOrder);
	        case BOOKMARK_DATA_LIST :
	            return mNLDB.query(DB.BOOKMARK.TABLE, projection, selection, selectionArgs, null , null, sortOrder);    
        default :
            return null;
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;// DB에 업뎃한 갯수 

        switch(Matcher.match(uri)){
	        case BOOK_DATA :
	            count = mNLDB.update(DB.BOOK.TABLE, values, selection, selectionArgs);
	            break;
	        case RECENTLY_DATA :
	            count = mNLDB.update(DB.RECENTLY.TABLE, values, selection, selectionArgs);
	            break;
	        case BOOKMARK_DATA :
	            count = mNLDB.update(DB.BOOKMARK.TABLE, values, selection, selectionArgs);
	            break;
	        default :
	            count = 0;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    } 


    // SQL문 쓸수 있게 해주는 class
    private class NLDatabase extends SQLiteOpenHelper {
        public NLDatabase(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
 
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB.BOOK.CREATE);
            db.execSQL(DB.RECENTLY.CREATE);
            db.execSQL(DB.BOOKMARK.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion <= 1) {
        		db.execSQL("ALTER TABLE" + DB.RECENTLY.TABLE + "MODIFY COLUMN PAGE TEXT NOT NULL");
        	}
        	db.execSQL("DROP TABLE IF EXISTS " + DB.BOOK.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DB.RECENTLY.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DB.BOOKMARK.TABLE);
            onCreate(db);
        }
    }
}

