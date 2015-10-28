//package com.lemonlab.sockjschatservice;
//
//import android.app.Service;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.PixelFormat;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
///**
// * Created by lk on 15. 10. 26..
// */
//public class ChatTest extends Service {
//    private WindowManager windowManager;
//    private RelativeLayout chatheadView, removeView, messview;
//    private LinearLayout txtView, txt_linearlayout;
//    private ImageView chatheadImg, removeImg;
//    private TextView  txt1;
//    ArrayList<String> sms_id = new ArrayList<String>();
//    ArrayList<String> sms_num = new ArrayList<String>();
//    ArrayList<String> sms_Name = new ArrayList<String>();
//    ArrayList<String> sms_dt = new ArrayList<String>();
//    ArrayList<String> sms_body = new ArrayList<String>();
//
//
//    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin, iLife = 0;
//    private Point szWindow = new Point();
//    private boolean isLeft = true;
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public void onCreate() {
//        System.out.println("Started Service in ChatHeadService");
//        super.onCreate();
//        Log.d(Utility.LogTag, "ChatHeadService.onCreate()");
//
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
//        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
//
//
//        removeView = (RelativeLayout)inflater.inflate(R.layout.remove, null);
//        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;
//
//        removeView.setVisibility(View.GONE);
//
//        removeImg = (ImageView)removeView.findViewById(R.id.remove_img);
//        windowManager.addView(removeView, paramRemove);
//
//
//        chatheadView = (RelativeLayout) inflater.inflate(R.layout.chathead, null);
//        chatheadImg = (ImageView)chatheadView.findViewById(R.id.chathead_img);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            windowManager.getDefaultDisplay().getSize(szWindow);
//        } else {
//            int w = windowManager.getDefaultDisplay().getWidth();
//            int h = windowManager.getDefaultDisplay().getHeight();
//            szWindow.set(w, h);
//        }
//
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.x = 0;
//        params.y = 100;
//        windowManager.addView(chatheadView, params);
//
//        chatheadView.setOnTouchListener(new View.OnTouchListener() {
//            long time_start = 0, time_end = 0;
//            boolean isLongclick = false, inBounded = false;
//            int remove_img_width = 0, remove_img_height = 0;
//
//            Handler handler_longClick = new Handler();
//            Runnable runnable_longClick = new Runnable() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    Log.d(Utility.LogTag, "Into runnable_longClick");
//
//                    isLongclick = true;
//                    removeView.setVisibility(View.VISIBLE);
//                    chathead_longclick();
//                }
//            };
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
//
//                int x_cord = (int) event.getRawX();
//                int y_cord = (int) event.getRawY();
//                int x_cord_Destination, y_cord_Destination;
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        time_start = System.currentTimeMillis();
//                        handler_longClick.postDelayed(runnable_longClick, 600);
//
//                        remove_img_width = removeImg.getLayoutParams().width;
//                        remove_img_height = removeImg.getLayoutParams().height;
//
//                        x_init_cord = x_cord;
//                        y_init_cord = y_cord;
//
//                        x_init_margin = layoutParams.x;
//                        y_init_margin = layoutParams.y;
//
//                        if(txtView != null){
//                            txtView.setVisibility(View.GONE);
//                            myHandler.removeCallbacks(myRunnable);
//                        }
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int x_diff_move = x_cord - x_init_cord;
//                        int y_diff_move = y_cord - y_init_cord;
//
//                        x_cord_Destination = x_init_margin + x_diff_move;
//                        y_cord_Destination = y_init_margin + y_diff_move;
//
//                        if(isLongclick){
//                            int x_bound_left = (szWindow.x - removeView.getWidth()) / 2 - 250;
//                            int x_bound_right = (szWindow.x + removeView.getWidth()) / 2 + 100;
//
//                            int y_bound_top = szWindow.y - (removeView.getHeight() + getStatusBarHeight()) - 200;
//
//                            if((x_cord_Destination >= x_bound_left && x_cord_Destination <= x_bound_right) && y_cord_Destination >= y_bound_top){
//                                inBounded = true;
//
//                                layoutParams.x = (szWindow.x - chatheadView.getWidth()) / 2;
//                                layoutParams.y = szWindow.y - (removeView.getHeight() + getStatusBarHeight()) + 70;
//
//                                if(removeImg.getLayoutParams().height == remove_img_height){
//                                    removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
//                                    removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);
//
//                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
//                                    int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
//                                    int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));
//                                    param_remove.x = x_cord_remove;
//                                    param_remove.y = y_cord_remove;
//
//                                    windowManager.updateViewLayout(removeView, param_remove);
//                                }
//
//
//                                windowManager.updateViewLayout(chatheadView, layoutParams);
//                                break;
//                            }else{
//                                inBounded = false;
//                                removeImg.getLayoutParams().height = remove_img_height;
//                                removeImg.getLayoutParams().width = remove_img_width;
//
//                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
//                                int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
//                                int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );
//
//                                param_remove.x = x_cord_remove;
//                                param_remove.y = y_cord_remove;
//
//                                windowManager.updateViewLayout(removeView, param_remove);
//                            }
//
//                        }
//
//
//                        layoutParams.x = x_cord_Destination;
//                        layoutParams.y = y_cord_Destination;
//
//                        windowManager.updateViewLayout(chatheadView, layoutParams);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        isLongclick = false;
//                        removeView.setVisibility(View.GONE);
//                        removeImg.getLayoutParams().height = remove_img_height;
//                        removeImg.getLayoutParams().width = remove_img_width;
//                        handler_longClick.removeCallbacks(runnable_longClick);
//
//                        if(inBounded){
//                            if(MyDialog.active){
//                                MyDialog.myDialog.finish();
//                            }
//
//                            stopService(new Intent(ChatHeadService.this, ChatHeadService.class));
//                            inBounded = false;
//                            break;
//                        }
//
//
//                        int x_diff = x_cord - x_init_cord;
//                        int y_diff = y_cord - y_init_cord;
//
//                        if(x_diff < 5 && y_diff < 5){
//                            time_end = System.currentTimeMillis();
//                            if((time_end - time_start) < 300){
//                                chathead_click();
//                            }
//                        }
//
//
//                        x_cord_Destination = x_init_margin + x_diff;
//                        y_cord_Destination = y_init_margin + y_diff;
//
//                        int x_start;
//                        x_start = x_cord_Destination;
//
//
//                        int BarHeight =  getStatusBarHeight();
//                        if (y_cord_Destination < 0) {
//                            y_cord_Destination = 0;
//                        } else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
//                            y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight );
//                        }
//                        layoutParams.y = y_cord_Destination;
//
//                        inBounded = false;
//                        resetPosition(x_start);
//
//                        break;
//                    default:
//                        Log.d("henrytest", "chatheadView.setOnTouchListener  -> event.getAction() : default");
//                        break;
//                }
//                return true;
//            }
//        });
//
//
//        txtView = (LinearLayout)inflater.inflate(R.layout.txt, null);
//        txt1 = (TextView) txtView.findViewById(R.id.txt1);
//        txt_linearlayout = (LinearLayout)txtView.findViewById(R.id.txt_linearlayout);
//
//
//        WindowManager.LayoutParams paramsTxt = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_PHONE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                PixelFormat.TRANSLUCENT);
//        paramsTxt.gravity = Gravity.TOP | Gravity.LEFT;
//
//        txtView.setVisibility(View.GONE);
//        windowManager.addView(txtView, paramsTxt);
//
//    }
//
//    private void resetPosition(int x_cord_now) {
//        int w = chatheadView.getWidth();
//
//        if(x_cord_now == 0 || x_cord_now == szWindow.x - w){
//
//        } else if(x_cord_now + w / 2<= szWindow.x / 2){
//            isLeft = true;
//            moveToLeft(x_cord_now);
//
//        } else if(x_cord_now + w / 2 > szWindow.x / 2){
//            isLeft = false;
//            moveToRight(x_cord_now);
//
//        }
//
//    }
//    private void moveToLeft(int x_cord_now){
//
//        final int x = x_cord_now;
//        new CountDownTimer(500, 5) {
//            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
//            public void onTick(long t) {
//                long step = (500 - t)/5;
//                mParams.x = (int)(double)bounceValue(step,x);
//                windowManager.updateViewLayout(chatheadView, mParams);
//            }
//            public void onFinish() {
//                mParams.x = 0;
//                windowManager.updateViewLayout(chatheadView, mParams);
//            }
//        }.start();
//    }
//    private  void moveToRight(int x_cord_now){
//        final int x = x_cord_now;
//        new CountDownTimer(500, 5) {
//            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
//            public void onTick(long t) {
//                long step = (500 - t)/5;
//                mParams.x = szWindow.x + (int)(double)bounceValue(step,x) - chatheadView.getWidth();
//                windowManager.updateViewLayout(chatheadView, mParams);
//            }
//            public void onFinish() {
//                mParams.x = szWindow.x - chatheadView.getWidth();
//                windowManager.updateViewLayout(chatheadView, mParams);
//            }
//        }.start();
//    }
//
//    private double bounceValue(long step, long scale){
//        double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
//        return value;
//    }
//
//    private int getStatusBarHeight() {
//        int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
//        return statusBarHeight;
//    }
//
//    private void chathead_click(){
//
//
//        if(messview == null) {
//            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
//            mParams.x = 0;
//            mParams.y = 0;
//            windowManager.updateViewLayout(chatheadView, mParams);
//
//
//            LayoutInflater inflater1 = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
//            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.TYPE_PHONE,
//                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    PixelFormat.TRANSLUCENT);
//            params.gravity = Gravity.BOTTOM | Gravity.LEFT | Gravity.RIGHT;
//            params.x = 0;
//            params.y = 25;
//            messview = (RelativeLayout)inflater1.inflate(R.layout.messageslayout, null);
//            windowManager.addView(messview, params);
//            EditText message = (EditText)messview.findViewById(R.id.replymessage);
//            message.clearFocus();
//            TextView name = (TextView)messview.findViewById(R.id.contactname);
//            name.setText(SMSListener.contactName);
//
//            Uri myMessage = Uri.parse("content://sms/");
//            ContentResolver cr = this.getContentResolver();
//            Cursor c = cr.query(myMessage, new String[] { "_id", "address", "date", "body", "read" }, "address = '" + SMSListener.contactNumber + "'", null, null);
//            getSmsLogs(c, ChatTest.this);
//        } else {
//            windowManager.removeView(messview);
//            messview = null;
//        }
//
//        try {
//            if (MyDialog.active) {
//                MyDialog.myDialog.finish();
//
//            } else {
//                Intent it = new Intent(this, MyDialog.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(it);
//            }
//        } catch (Exception ex) {
//
//        }
//
//    }
//
//    private void chathead_longclick(){
//        Log.d("henrytest", "Into ChatHeadService.chathead_longclick() ");
//
//
//        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
//        int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
//        int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );
//
//        param_remove.x = x_cord_remove;
//        param_remove.y = y_cord_remove;
//
//        windowManager.updateViewLayout(removeView, param_remove);
//    }
//
//    private void showMsg(String sMsg){
//        if(txtView != null && chatheadView != null ){
//            Log.d("henrytest", "ChatHeadService.showMsg -> sMsg=" + sMsg);
//            txt1.setText(sMsg);
//            myHandler.removeCallbacks(myRunnable);
//            ;
//            WindowManager.LayoutParams param_chathead = (WindowManager.LayoutParams) chatheadView.getLayoutParams();
//            WindowManager.LayoutParams param_txt = (WindowManager.LayoutParams) txtView.getLayoutParams();
//
//            txt_linearlayout.getLayoutParams().height = chatheadView.getHeight();
//            txt_linearlayout.getLayoutParams().width = szWindow.x / 2;
//
//            if(isLeft){
//                param_txt.x = param_chathead.x + chatheadImg.getWidth();
//                param_txt.y = param_chathead.y;
//
//                txt_linearlayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//            }else{
//                param_txt.x = param_chathead.x - szWindow.x / 2;
//                param_txt.y = param_chathead.y;
//
//                txt_linearlayout.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//            }
//
//            txtView.setVisibility(View.VISIBLE);
//            windowManager.updateViewLayout(txtView, param_txt);
//
//            myHandler.postDelayed(myRunnable, 4000);
//
//        }
//
//    }
//
//    Handler myHandler = new Handler();
//    Runnable myRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            if(txtView != null){
//                txtView.setVisibility(View.GONE);
//            }
//        }
//    };
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // TODO Auto-generated method stub
//        Log.d("henrytest", "ChatHeadService.onStartCommand() -> iLife=" + iLife);
//        Bundle bd = null;
//        try
//        {
//            bd = intent.getExtras();
//        }
//        catch(NullPointerException ex)
//        {
//
//        }
//        if(bd != null){
//            final String sMsg = bd.getString("extra_msg");
//            Log.d("henrytest", "ChatHeadService.onStartCommand() -> EXTRA_MSG=" + sMsg);
//
//            if(iLife > 0)
//                showMsg(sMsg);
//            else{
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        showMsg(sMsg);
//                    }
//                }, 300);
//            }
//        }
//
//        iLife++;
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//
//        if(chatheadView != null){
//            windowManager.removeView(chatheadView);
//        }
//
//        if(txtView != null){
//            windowManager.removeView(txtView);
//        }
//
//        if(removeView != null){
//            windowManager.removeView(removeView);
//        }
//
//    }
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO Auto-generated method stub
//        Log.d(Utility.LogTag, "ChatHeadService.onBind()");
//        return null;
//    }
//
//    public void getSmsLogs(Cursor c, Context con) {
//
//        if (sms_num.size() > 0) {
//            sms_id.clear();
//            sms_num.clear();
//            sms_Name.clear();
//            sms_body.clear();
//            sms_dt.clear();
//        }
//        ListView lv = (ListView)messview.findViewById(R.id.listView);
//        ArrayAdapter<String> adapter = null;
//        try {
//            if (c.moveToFirst()) {
//                do {
//                    if (c.getString(c.getColumnIndexOrThrow("address")) == null) {
//                        c.moveToNext();
//                        continue;
//                    }
//                    String Number = c.getString(
//                            c.getColumnIndexOrThrow("address")).toString();
//                    // Log.e("Number-->", "" + Number);
//                    // if (Number.equalsIgnoreCase("+918000912692")) {
//                    String _id = c.getString(c.getColumnIndexOrThrow("_id"))
//                            .toString();
//                    String dat = c.getString(c.getColumnIndexOrThrow("date"))
//                            .toString();
//
//                    // String as = (String) get_dt(dat,
//                    // "dd/MM/yyyy, hh.mma");
//                    String Body = c.getString(c.getColumnIndexOrThrow("body"))
//                            .toString();
//
//                    // if (name.length() <= 0 || name.length() == 1) {
//                    // name = "no name";
//                    // }
//                    adapter = new ArrayAdapter<String>(this, R.layout.messagelistview, R.id.message, sms_body);
//
//                    Log.e("Body-->", "" + Body);
//                    sms_id.add(_id);
//                    sms_num.add(Number);
//                    sms_body.add(Body);
//                } while (c.moveToNext());
//                lv.setAdapter(adapter);
//            }
//            c.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}