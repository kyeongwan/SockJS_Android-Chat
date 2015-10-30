package com.lemonlab.sockjschatservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lk on 2015. 10. 23..
 */
public class ChattingService extends Service {

    /**
     * For ChatHead
     */
    private ImageView mImageView;
    private TextView mTextView;
    private EditText mEditText;
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mParams2;
    private WindowManager.LayoutParams mParams3;
    private WindowManager mWindowManager;
    private SeekBar mSeekBar;
    private ListView mChatList;

    /**
     * For Floating Button
     */
    private float start_X, start_Y;
    private int PREV_X, PREV_Y;
    private int max_X = -1, max_Y = -1;
    private boolean hasLongPress;
    private LongPressClass LongPressFunction;
    private Handler mHandler = null;
    private final int DEFULT_START_X = 0;
    private final int DEFULT_START_Y = 0;

    private RelativeLayout chatheadView;
    private short showView;

    private ArrayList<String> chatdata;
    private ChatListAdapter adapter;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        chatheadView = (RelativeLayout)inflater.inflate(R.layout.chathead, null);

        initView();


        mHandler = new Handler();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;

        mParams2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        mParams3  = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT);

        mWindowManager.addView(mImageView, mParams);


        try {
            SockJSImpl sockJS = new SockJSImpl("http://172.16.101.158:8080/eventbus", "BroadcastNewsfeed") {

                @Override
                void parseSockJS(String s) {
                    try {
                        s = s.replace("\\\"", "\"");
                        s = s.substring(3, s.length() - 2); // a[" ~ "] 없애기

                        JSONObject json = new JSONObject(s);
                        String type = json.getString("type");
                        String address = json.getString("address");
                        final String body = json.getString("body");

                        if ("to.client.BroadcastNewsfeed".equals(address))
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    chatdata.add(body);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        System.out.println(body);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            boolean b = sockJS.connectBlocking();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        mParams2 = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_DIM_BEHIND,
//                PixelFormat.TRANSLUCENT);
//        mParams2.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
//
//        mParams2.gravity = Gravity.LEFT | Gravity.TOP;
//
//        mWindowManager.addView(mEditText, mParams2);

//        shortClickEvent();
        //addOpacityController();
    }

    private void initView() {
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(getMaskedBitmap(R.drawable.chaticon, 30));
        mImageView.setOnTouchListener(mViewTouchListener);

        mChatList = (ListView) chatheadView.findViewById(R.id.chatlist);
        chatdata = new ArrayList<>();
        adapter = new ChatListAdapter(getApplicationContext(), chatdata);
        mChatList.setAdapter(adapter);
        mChatList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mChatList.setSelection(adapter.getCount() - 1);
            }
        });
        adapter.notifyDataSetChanged();
    }

    private Bitmap getMaskedBitmap(int _srcResId, float _roundInPixel) {
        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), _srcResId);
        srcBitmap = Bitmap.createScaledBitmap(srcBitmap, 180, 180, true);

        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), srcBitmap);

        roundedDrawable.setCornerRadius(_roundInPixel);
        roundedDrawable.setAntiAlias(true);

        return roundedDrawable.getBitmap();
    }

    private void addOpacityController() {
        mSeekBar = new SeekBar(this);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(100);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable); }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        Log.i("lotation", "=== onConfigurationChanged is called !!! ===");

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && showView > 0 ) { // 세로 전환시 발생
            mWindowManager.removeView(chatheadView);
            mWindowManager.addView(chatheadView,mParams2);
            Log.i("lotation", "=== Configuration.ORIENTATION_PORTRAIT !!! ===");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && showView > 0) { // 가로 전환시 발생
            mWindowManager.removeView(chatheadView);
            mWindowManager.addView(chatheadView,mParams2);
            Log.i("lotation", "=== Configuration.ORIENTATION_LANDSCAPE !!! ===");
        }

        setMaxPosition();
        optimizePosition();
    }

    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(matrix);

        max_X = matrix.widthPixels - mImageView.getWidth();
        max_Y = matrix.heightPixels - mImageView.getHeight();
    }

    private void optimizePosition() {
        if (mParams.x > max_X) mParams.x = max_X;
        if (mParams.y > max_Y) mParams.y = max_Y;
        if (mParams.x < 0) mParams.x = 0;
        if (mParams.y < 0) mParams.y = 0;
    }

    @Override
    public void onDestroy() {
        if (mWindowManager != null) {
            if (mImageView != null) mWindowManager.removeView(mImageView);
            if (mSeekBar != null) mWindowManager.removeView(mSeekBar);
        }
        super.onDestroy();
    }


    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Log.i("touch Event", event.getAction() + "");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (max_X == -1)
                        setMaxPosition();
                    start_X = event.getRawX();
                    start_Y = event.getRawY();
                    PREV_X = mParams.x;
                    PREV_Y = mParams.y;

                    hasLongPress = false;
                    CheckLongClick();
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (hasLongPress) {
                        int x = (int) (event.getRawX() - start_X);
                        int y = (int) (event.getRawY() - start_Y);

                        mParams.x = PREV_X + x;
                        mParams.y = PREV_Y + y;

                        optimizePosition();
                        mWindowManager.updateViewLayout(mImageView, mParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!hasLongPress) {
                        removeLongClickCallback();
                        shortClickEvent();
                    }
                    break;
            }
            return true;
        }
    };


    private void removeLongClickCallback() {
        if (LongPressFunction != null)
            mHandler.removeCallbacks(LongPressFunction);
    }

    private void CheckLongClick() {
        hasLongPress = false;
        if (LongPressFunction == null)
            LongPressFunction = new LongPressClass();

        mHandler.postDelayed(LongPressFunction, 100);
    }


    private void shortClickEvent() {

        switch (showView){
            case 0:         // If No View
                mParams.x = DEFULT_START_X;
                mParams.y = DEFULT_START_Y;

                chatheadView.setFocusable(false);
                mWindowManager.addView(chatheadView, mParams2);
                mWindowManager.removeView(mImageView);
                mWindowManager.addView(mImageView,mParams);
                showView++;
                break;

            case 1:         // If No Focus View
                chatheadView.setFocusable(true);
                mWindowManager.removeView(chatheadView);
                mWindowManager.addView(chatheadView, mParams3);
                mWindowManager.removeView(mImageView);
                mWindowManager.addView(mImageView, mParams);
                showView++;
                break;

            case 2:
                mWindowManager.removeView(chatheadView);
                showView = 0;
        }


    }

    private class LongPressClass implements Runnable {
        @Override
        public void run() {
            if (LongClickEvent())
                hasLongPress = true;
        }
    }

    private boolean LongClickEvent() { return true; }


}