package com.lemonlab.sockjschatservice;

import android.app.Service;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by lk on 2015. 10. 23..
 */
public class ChattingService extends Service implements View.OnClickListener {

    /**
     * For ChatHead
     */
    private ImageView mImageView;
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mParams2;
    private WindowManager.LayoutParams mParams3;


    private WindowManager.LayoutParams mParamsbt1;
    private WindowManager.LayoutParams mParamsbt2;
    private WindowManager.LayoutParams mParamsbt3;
    private WindowManager.LayoutParams mParamsbt4;

    private WindowManager mWindowManager;
    private SeekBar mSeekBar;
    private ListView mChatList;
    private Button mChatSend;
    private EditText mEditText;

    private ImageButton bt1;
    private ImageButton bt2;
    private ImageButton bt3;
    private ImageButton bt4;

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

    Runnable n1;
    double time1;
    int animationR;
    boolean showButton = false;


    private RelativeLayout chatheadView;
    private boolean showView = false;
    private short showchat = 0;

    private ArrayList<String> chatdata;
    private ChatListAdapter adapter;
    private SockJSImpl sockJS;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);

        initView();
        initParams();

        mHandler = new Handler();


        mWindowManager.addView(mImageView, mParams);


        try {
            sockJS = new SockJSImpl("http://133.130.113.101:7030/eventbus", "channel_id") {

                @Override
                void parseSockJS(String s) {
                    try {
                        //System.out.println(s);
                        s = s.replace("\\\"", "\"");
                        s = s.replace("\\\\", "\\");
//                        s = s.replace("\\\\\"", "\"");
                        s = s.substring(3, s.length() - 2); // a[" ~ "] 없애기
                        Log.i("Reci", s);

                        JSONObject json = new JSONObject(s);
                        String type = json.getString("type");
                        String address = json.getString("address");
//                        final JSONObject body = json.getJSONObject("body");
                        final JSONObject body = new JSONObject(json.getString("body"));
                        String bodyType = body.getString("type");
                        String msg = body.getString("msg");
                        String nickname = body.getString("sender_nick");

                        final String data =  bodyType + "/&" +nickname + "/&" + msg;
                        if ("to.channel.channel_id".equals(address))
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    chatdata.add(data);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        System.out.println("body = " + body);
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
    }

    private void initParams() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mParamsbt1 = new WindowManager.LayoutParams(
                150, 150,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParamsbt1.gravity = Gravity.TOP | Gravity.LEFT;

        mParamsbt2 = new WindowManager.LayoutParams(
                150, 150,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParamsbt2.gravity = Gravity.TOP | Gravity.LEFT;

        mParamsbt3 = new WindowManager.LayoutParams(
                150, 150,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParamsbt3.gravity = Gravity.TOP | Gravity.LEFT;

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.LEFT;

        mParamsbt4 = new WindowManager.LayoutParams(
                150, 150,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParamsbt4.gravity = Gravity.TOP | Gravity.LEFT;

        mParams2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        mParams2.alpha = 90;
        mParams3 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT);
    }

    private void initView() {
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(getMaskedBitmap(R.drawable.chaticon, 30));
        mImageView.setOnTouchListener(mViewTouchListener);

        bt1 = new ImageButton(this);
        bt1.setBackground(getResources().getDrawable(R.drawable.setting));
        bt1.setOnClickListener(this);

        bt2 = new ImageButton(this);
        bt2.setBackground(getResources().getDrawable(R.drawable.chaticon));
        bt2.setOnClickListener(this);

        bt3 = new ImageButton(this);
        bt3.setBackground(getResources().getDrawable(R.drawable.chaticon));
        bt3.setOnClickListener(this);

        bt4 = new ImageButton(this);
        bt4.setBackground(getResources().getDrawable(R.drawable.chaticon));
        bt4.setOnClickListener(this);

        mChatList = (ListView) chatheadView.findViewById(R.id.lv_chathead_chatlist);

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

        mEditText = (EditText) chatheadView.findViewById(R.id.et_chathead_chat);
        mEditText.setOnKeyListener(new View.OnKeyListener() {
                                       @Override
                                       public boolean onKey(View v, int keyCode, KeyEvent event) {

                                           if (event.getAction()!=KeyEvent.ACTION_DOWN)
                                               return true;


                                           if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                               Log.d("Send", "KeyEvent.KEYCODE_ENTER");
                                               JSONObject obj = new JSONObject();
                                               try {
                                                   obj.put("type", "publish");
                                                   obj.put("address", "to.server.channel");
                                                   JSONObject body = new JSONObject();
                                                   body.put("type", "notice");
                                                   body.put("channel_id", "channel_id");
                                                   body.put("sender_id", "aaa");
                                                   body.put("sender_nick", "닉넴");
                                                   body.put("app_id", "com.aaa.aaa");
                                                   body.put("msg", mEditText.getText().toString());
                                                   obj.put("body", body);
                                               } catch (JSONException e) {
                                                   e.printStackTrace();
                                                   Log.e("onClick", e.toString());
                                               }
                                               if("".equals(mEditText.getText().toString()))
                                                   return true;
                                               sockJS.send(obj);
                                               Log.i("fff", "send event");
                                               mEditText.setText("");
                                               return true;
                                           }

                                           return false;
                                       }
                                   }

        );

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

    private void runOnUiThread(Runnable runnable) {
        mHandler.post(runnable);
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
                    buttonClick();
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

        Log.i("XY", mParams.x + "/" + mParams.y + "/" + showView);
        if (mParams.x < max_X / 2)
            Log.i("dd", "right");
        else
            Log.i("gg:", "left");
        if (!showView) {
            mParamsbt1.x = mParams.x;
            mParamsbt2.x = mParams.x;
            mParamsbt3.x = mParams.x;
            mParamsbt4.x = mParams.x;
            mParamsbt1.y = mParams.y;
            mParamsbt2.y = mParams.y;
            mParamsbt3.y = mParams.y;
            mParamsbt4.y = mParams.y;
            mWindowManager.addView(bt1, mParamsbt1);
            mWindowManager.addView(bt2, mParamsbt2);
            mWindowManager.addView(bt3, mParamsbt3);
            mWindowManager.addView(bt4, mParamsbt4);

            showButton = true;
            showView = true;

            n1 = new Runnable() {
                @Override
                public void run() {
                    animationR = (int) (0.264 * Math.pow(time1, 4) - 7.277 * Math.pow(time1, 3) + 64.646 * Math.pow(time1, 2) - 167.18 * time1 + 116.33);
                    mParamsbt1.x = (int) (mParams.x + (mImageView.getWidth() / 2) + animationR * Math.cos(Math.toRadians(80)));
                    mParamsbt1.y = (int) (mParams.y + animationR * Math.sin(Math.toRadians(80)));
                    mParamsbt2.x = (int) (mParams.x + (mImageView.getWidth() / 2) + animationR * Math.cos(Math.toRadians(27)));
                    mParamsbt2.y = (int) (mParams.y + animationR * Math.sin(Math.toRadians(27)));
                    mParamsbt3.x = (int) (mParams.x + (mImageView.getWidth() / 2) + animationR * Math.cos(Math.toRadians(-26)));
                    mParamsbt3.y = (int) (mParams.y + animationR * Math.sin(Math.toRadians(-26)));
                    mParamsbt4.x = (int) (mParams.x + (mImageView.getWidth() / 2) + animationR * Math.cos(Math.toRadians(-80)));
                    mParamsbt4.y = (int) (mParams.y + animationR * Math.sin(Math.toRadians(-80)));
                    time1 += 1;
                    mWindowManager.updateViewLayout(bt1, mParamsbt1);
                    mWindowManager.updateViewLayout(bt2, mParamsbt2);
                    mWindowManager.updateViewLayout(bt3, mParamsbt3);
                    mWindowManager.updateViewLayout(bt4, mParamsbt4);
                    //Log.i("jj", mParamsbt1.x + "x1");
                    if (time1 < 11)
                        mHandler.postDelayed(n1, 30);
                    else time1 = 0;
                }
            };
//
            mHandler.postDelayed(n1, 30);

        } else {
            buttonClick();
        }


    }

    private void buttonClick() {
        if (showButton || showView) {
            mWindowManager.removeView(bt1);
            mWindowManager.removeView(bt2);
            mWindowManager.removeView(bt3);
            mWindowManager.removeView(bt4);
        }
        showView = false;
        showButton = false;
    }


    @Override
    public void onClick(View v) {
        if (v.getBackground() == bt1.getBackground()) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            buttonClick();
        } else if (v.getBackground() == bt2.getBackground()) {

        } else if (v.getBackground() == bt3.getBackground()) {

        } else if (v.getBackground() == bt4.getBackground()) {
            buttonClick();
            if (showchat == 0) {
                mWindowManager.addView(chatheadView, mParams2);
                mWindowManager.removeView(mImageView);
                mWindowManager.addView(mImageView, mParams);
                showchat++;
            } else if (showchat == 1) {
                mParams2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                        PixelFormat.TRANSLUCENT);
                mParams2.alpha = 90;
                mWindowManager.updateViewLayout(chatheadView, mParams2);
                mWindowManager.removeView(mImageView);
                mWindowManager.addView(mImageView, mParams);
                showchat++;
            } else {
                mWindowManager.removeView(chatheadView);
                mParams2 = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        PixelFormat.TRANSLUCENT);
                mParams2.alpha = 90;
                showchat = 0;
            }
        }
    }

    private class LongPressClass implements Runnable {
        @Override
        public void run() {
            if (LongClickEvent())
                hasLongPress = true;
        }

    }

    private boolean LongClickEvent() {
        return true;
    }


}