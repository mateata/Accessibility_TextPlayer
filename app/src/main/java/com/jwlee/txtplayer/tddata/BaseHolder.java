package com.jwlee.txtplayer.tddata;

import android.view.View;

public class BaseHolder {
    public BaseHolder() {
    }

    public BaseHolder(View v) {
        set(v);
    }

    public View set(View v) {
        v.setTag(this);
        return v;
    }
}