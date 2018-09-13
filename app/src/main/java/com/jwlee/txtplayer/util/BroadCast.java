package com.jwlee.txtplayer.util;

import com.jwlee.txtplayer.AppControl;
import com.jwlee.txtplayer.tdplayer.TDPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class BroadCast extends BroadcastReceiver {

	@Override
	public void onReceive(Context mContext, Intent mIntent) {
		String intentAction = mIntent.getAction();

		 if((Intent.ACTION_MEDIA_BUTTON).equals(intentAction)){
			KeyEvent Xevent = (KeyEvent)mIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);    
		    if ((KeyEvent.KEYCODE_HEADSETHOOK == Xevent.getKeyCode()) && (Xevent.getAction() == KeyEvent.ACTION_DOWN)) {
		    	if (AppControl.NA_Playable == 1) {
		    		((TDPlayer) TDPlayer.mContext).togglePlay();
		    	}
		    }
		} else if(Intent.ACTION_MEDIA_SCANNER_STARTED.equals(intentAction)
                || Intent.ACTION_MEDIA_SCANNER_SCAN_FILE.equals(intentAction)) {             	
        }
	}
}
