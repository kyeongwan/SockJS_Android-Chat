package com.lemonlab.sockjschatservice;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by lk on 2015. 10. 23..
 */
public class ChattingService extends Service {
    private ImageView mImageView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private SeekBar mSeekBar;

    /*
     * For Floating Button
     */
    private float START_X, START_Y;
    private int PREV_X, PREV_Y;
    private int MAX_X = -1, MAX_Y = -1;
    private boolean hasLongPress;
    private LongPressClass LongPressFunction;
    private Handler mHandler = null;


    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override public boolean onTouch(View v, MotionEvent event) {

            Log.i("touch Event", event.getAction()+"");
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(MAX_X == -1)
                        setMaxPosition();
                    START_X = event.getRawX();
                    START_Y = event.getRawY();
                    PREV_X = mParams.x;
                    PREV_Y = mParams.y;

                    hasLongPress = false;
                    CheckLongClick();

                    break;
                case MotionEvent.ACTION_MOVE:

                    if(hasLongPress) {
                        int x = (int) (event.getRawX() - START_X);
                        int y = (int) (event.getRawY() - START_Y);

                        mParams.x = PREV_X + x;
                        mParams.y = PREV_Y + y;

                        optimizePosition();
                        mWindowManager.updateViewLayout(mImageView, mParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(!hasLongPress){
                        removeLongClickCallback();
                        shortClickEvent();
                    }
                    break;
            }

            return true;
        }
    };

    private void shortClickEvent() {
        mParams.x = 50;
        mParams.y = 50;
        mWindowManager.updateViewLayout(mImageView, mParams);
    }

    private void removeLongClickCallback() {
        if(LongPressFunction != null)
            mHandler.removeCallbacks(LongPressFunction);
    }

    private void CheckLongClick() {
        hasLongPress = false;
        if(LongPressFunction == null)
            LongPressFunction = new LongPressClass();

        mHandler.postDelayed(LongPressFunction, 100);
    }

    @Override
    public IBinder onBind(Intent arg0) { return null; }

    private Bitmap getMaskedBitmap(int _srcResId, float _roundInPixel)
    {
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), _srcResId);
        srcBitmap = Bitmap.createScaledBitmap(srcBitmap, 180, 180, true);

        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), srcBitmap);

        roundedDrawable.setCornerRadius( _roundInPixel );
        roundedDrawable.setAntiAlias(true);

        return roundedDrawable.getBitmap();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mImageView = new ImageView(this);

        mImageView.setImageBitmap(getMaskedBitmap(R.drawable.chaticon, 30));
        mImageView.setOnTouchListener(mViewTouchListener);
        mHandler = new Handler();

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mImageView, mParams);

        addOpacityController();
    }

    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(matrix);

        MAX_X = matrix.widthPixels - mImageView.getWidth();
        MAX_Y = matrix.heightPixels - mImageView.getHeight();
    }

    private void optimizePosition() {
        if(mParams.x > MAX_X) mParams.x = MAX_X;
        if(mParams.y > MAX_Y) mParams.y = MAX_Y;
        if(mParams.x < 0) mParams.x = 0;
        if(mParams.y < 0) mParams.y = 0;
    }

    private void addOpacityController() {
        mSeekBar = new SeekBar(this);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override public void onProgressChanged(SeekBar seekBar, int progress,	boolean fromUser) {
                mParams.alpha = progress / 100.0f;
                mWindowManager.updateViewLayout(mImageView, mParams);
            }
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;

        mWindowManager.addView(mSeekBar, params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setMaxPosition();
        optimizePosition();
    }

    @Override
    public void onDestroy() {
        if(mWindowManager != null) {
            if(mImageView != null) mWindowManager.removeView(mImageView);
            if(mSeekBar != null) mWindowManager.removeView(mSeekBar);
        }
        super.onDestroy();
    }

    private class LongPressClass implements Runnable{

        @Override
        public void run() {
            if(LongClickEvent())
                hasLongPress = true;
        }
    }

    private boolean LongClickEvent(){
        Log.i("Event", "Long");
        return true;
    }
}