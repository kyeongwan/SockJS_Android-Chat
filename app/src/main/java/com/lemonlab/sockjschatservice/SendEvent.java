package com.lemonlab.sockjschatservice;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by lk on 15. 11. 6..
 */
public class SendEvent implements View.OnKeyListener {

    String MY_TAG = "KEY";

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //엔터키
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d(MY_TAG, "KeyEvent.KEYCODE_ENTER");
            return true;
            }

        return false;
        }
}