package com.nezha.gamelib.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;


import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.callback.ExitCallback;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.utils.DeviceUtil;
import com.nezha.gamelib.utils.DisplayUtils;

import java.lang.reflect.Field;


public class LogoWindow {
    private static final String TAG = "LogoWindow";

    private static WindowManager wm;
    private static WindowManager.LayoutParams params;

    static Activity mActivity;
    private static int screenHeigh;

    private static LogoWindow mLogowindow;

    public static LogoWindow getInstants(Activity mActivity, LoginCallback loginCallback, ExitCallback exitCallback) {
        if (mLogowindow == null) {
            Log.d(TAG, "重新new了logoWindow");
            mLogowindow = new LogoWindow(mActivity, loginCallback, exitCallback);
        } else {
            Log.d(TAG, "logoWindow不为空");
        }
        return mLogowindow;

    }

    private LogoWindow(Activity activity, LoginCallback loginCallback, ExitCallback exitCallback) {
        this.mActivity = activity;
        this.loginCallback = loginCallback;
        this.exitCallback = exitCallback;
        createView();
    }

    public static Handler mhandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            // TODO Auto-generated method stub
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    if (myviewicon != null && params.x == 0) {
                        myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_full));
                    }
                case 2:
                    myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_right));
                case 521:
                    if (mActivity.hasWindowFocus() && !hasview) {
                        addView();
                    } else {
                        if (hasview) {

                        } else {
                            mhandler.sendEmptyMessageDelayed(521, 500);
                        }

                    }

                    break;

                default:
                    break;
            }
        }
    };
    private boolean ishelpshow = false;

    private static ImageView myviewiconmanager;
    private static ImageView myviewicon;
    private static RelativeLayout myview;

    public static Point phonePoint;
    private LoginCallback loginCallback;
    private ExitCallback exitCallback;

    @SuppressLint("WrongConstant")
    private void createView() {
        Log.d(TAG, "onCreate");
        DisplayMetrics dm = new DisplayMetrics();

        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        phonePoint = getHeightAndWidth(mActivity);

        screenHeigh = dm.heightPixels;
        wm = ((WindowManager) mActivity.getSystemService("window"));
        if (myview == null) {

            myview = new RelativeLayout(mActivity);
            LayoutParams layoutParams = new LayoutParams(-2,
                    machSize(100));
            layoutParams.setMargins(0, 0, 0, 0);
            myview.setLayoutParams(layoutParams);

            myviewicon = new ImageView(mActivity);
            //小助手icon
            myviewicon.setLayoutParams(new RelativeLayout.LayoutParams(-2, machSize(100)));
            myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_full));
            //开局就隐藏
            new Handler().postDelayed(() -> myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_right)), 1000);

            //整个小助手
            myviewiconmanager = new ImageView(mActivity);
            // 创建时设置view的正常参数
            myviewiconmanager.setLayoutParams(new LayoutParams(machSize(346),
                    machSize(100)));
            myview.addView(myviewicon);


            myview.setOnTouchListener(new OnTouchListener() {

                float x;
                float y;
                private float mTouchX;
                private float mTouchY;
                private float mdownTempX;
                private float mdownTempY;
                private long ontouchtime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    x = event.getRawX();
                    y = event.getRawY(); // statusBarHeight是系统状态栏的高度

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                            // 获取相对View的坐标，即以此View左上角为原点
                            mTouchX = event.getX();
                            mTouchY = event.getY();

                            mdownTempX = event.getRawX();
                            mdownTempY = event.getRawY();
                            ontouchtime = System.currentTimeMillis();
                            myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_full));
                            break;
                        case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
                            updateViewPosition();
                            int distance_x = (int) event.getRawX() - (int) mdownTempX;
                            int distance_y = (int) event.getRawY() - (int) mdownTempY;
                            if (Math.abs(distance_x) > 40
                                    && Math.abs(distance_y) > 40) {

                                if (!ishelpshow) {
                                    ishelpshow = true;
                                }

                            }
                            break;
                        case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                            distance_x = (int) event.getRawX() - (int) mdownTempX;
                            distance_y = (int) event.getRawY() - (int) mdownTempY;
                            if (Math.abs(distance_x) <= 40
                                    && Math.abs(distance_y) <= 40) {

                                if ((System.currentTimeMillis() - ontouchtime) > 1500) {
                                    // 这里关闭了永久隐藏

                                } else {
                                    Log.d(TAG, "打开账户");
                                    onClick();
                                }
                            } else {
                                if (event.getRawX() < phonePoint.x / 2) {
                                    updateViewPosition1();
                                } else {
                                    updateViewPosition2();
                                }
                            }
                            ishelpshow = false;
                            break;
                    }
                    return true;
                }

                private void updateViewPosition() {
                    params.x = (int) (x - mTouchX);
                    params.y = (int) (screenHeigh - y - mTouchY);
                    wm.updateViewLayout(myview, params);
                }

                private void updateViewPosition1() {
                    params.x = 0;
                    params.y = (int) (screenHeigh - y - mTouchY);
                    Log.d(TAG, "我要更新ui去隐藏了");
                    wm.updateViewLayout(myview, params);
                    new Handler().postDelayed(() -> myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_right)), 500);
                    myviewiconmanager.setVisibility(View.GONE);
                }

                private void updateViewPosition2() {
                    params.x = (int) phonePoint.x;
                    params.y = (int) (screenHeigh - y - mTouchY);
                    Log.d(TAG, "我要更新ui去隐藏了");
                    wm.updateViewLayout(myview, params);
                    //半透明隐藏图标
                    new Handler().postDelayed(() -> myviewicon.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.icon_float_left)), 500);
                    myviewiconmanager.setVisibility(View.GONE);
                }
            });

        }

        params = new WindowManager.LayoutParams();
        params.format = PixelFormat.RGBA_8888;
        params.type = 1000;
        params.flags = 40;
        params.gravity = 83;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.alpha = (float) 1;
        params.x = 0;
        if (GameSdk.appOrient == 1) {
            params.y = machSize(360);
        } else {
            params.y = machSize(600);
        }
    }

    private Point getHeightAndWidth(Activity activity) {
        return new Point(activity.getWindowManager().getDefaultDisplay().getWidth(), activity.getWindowManager().getDefaultDisplay().getHeight());
    }

    // 添加
    private static void addView() {
        wm.addView(myview, params);
        hasview = true;
    }

    private void onClick() {
        GameSdk.getInstance().logoClick(mActivity, loginCallback, exitCallback);
    }

    public void start() {
        // 停止摇一摇监听
        mhandler.sendEmptyMessageAtTime(521, 1500);
    }

    public static boolean hasview = false;

    public void Stop() {
        if (hasview) {
            wm.removeView(myview);
            mLogowindow = null;
            hasview = false;
        }
    }

    /**
     * 将720像素转成其他像素值
     */
    private static int machSize(int size) {
        return DisplayUtils.dealWihtSize(size, mActivity);
    }


}
