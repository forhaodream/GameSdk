package com.nezha.nzgamesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.bean.AutoBean;
import com.nezha.gamelib.callback.ExitCallback;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.callback.PayCallback;
import com.nezha.gamelib.callback.ReportCallback;
import com.nezha.gamelib.popup.TipPopup;

import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PHONE_STATE = 200;
    private Context context;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;
        initView();
        GameSdk.getInstance().init(activity);
        GameSdk.getInstance().onCreate(activity, new LoginCallback() {
            @Override
            public void loginSuccess(AutoBean bean, String msg) {

            }

            @Override
            public void loginFailed(String msg) {

            }

            @Override
            public void logOut(String s) {

            }
        }, new ExitCallback() {
            @Override
            public void exitSuccess(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void exitCancel(String s) {

            }
        });


        GameSdk.getInstance().onStart(activity);
        GameSdk.getInstance().onStop(activity);
        GameSdk.getInstance().onResume(activity);
        GameSdk.getInstance().onPause(activity);
        GameSdk.getInstance().onRestart(activity);
        GameSdk.getInstance().onDestroy(activity);
    }

    private void initView() {
        findViewById(R.id.anim).setOnClickListener(view -> anim());
        findViewById(R.id.login).setOnClickListener(view -> login());
        findViewById(R.id.pay).setOnClickListener(view -> pay());
        findViewById(R.id.exit).setOnClickListener(view -> exit());
        findViewById(R.id.report).setOnClickListener(view -> report());
    }

    private void setPhoneStateManifest() {
        //?????????TelephonyManager??????
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Method method = null;
        try {
            method = telephonyManager.getClass().getMethod("getDeviceId", int.class);
            //??????IMEI???
            @SuppressLint("MissingPermission")
            String imei1 = telephonyManager.getDeviceId();
            String imei2 = (String) method.invoke(telephonyManager, 1);
            //??????MEID???
            String meid = (String) method.invoke(telephonyManager, 2);
            Log.d(TAG, imei1);
            Log.d(TAG, imei2);
            Log.d(TAG, meid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String IMEI = tm.getDeviceId();
            Log.i(TAG, "IMEI:" + IMEI);
        }
    }


    /**
     * ??????
     */
    private void anim() {
        GameSdk.getInstance().anim(activity);

//        GameSdk.getInstance().getAll(activity);
    }

    /**
     * ??????
     */
    private void login() {
        GameSdk.getInstance().login(activity, new LoginCallback() {
            @Override
            public void loginSuccess(AutoBean bean, String msg) {
            }

            @Override
            public void loginFailed(String msg) {
            }

            @Override
            public void logOut(String s) {
            }
        });
    }

    /**
     * ??????
     */
    private void pay() {
        GameSdk.getInstance().pay(activity, "??????:???", "??????id"
                , "????????????", "??????id"
                , "??????id", "????????????", new PayCallback() {
                    @Override
                    public void paySuccess(String s) {
                    }

                    @Override
                    public void payFailed(String s) {
                    }

                    @Override
                    public void payCancel(String s) {
                    }
                });
    }

    /**
     * ??????
     */
    private void exit() {
        GameSdk.getInstance().exitLogin(activity, new ExitCallback() {
            @Override
            public void exitSuccess(String s) {
            }

            @Override
            public void exitCancel(String s) {
            }
        });

    }

    /**
     * ????????????
     */
    private void report() {
        /**
         * diamonds=0&game_id=27&level=31&online=0
         * &power=541&role_id=288249067849419358&role_name=?????????
         * &server_id=0&server_name=&t=1631954526&uid=28327522bdda5bc1c2266edf37f4f227edf37a
         */
        GameSdk.getInstance().report("role_name", "role_id", "server_name",
                "server_id", "diamonds", "online", "power", "level"
                , activity, new ReportCallback() {
                    @Override
                    public void reportSuccess(String msg) {
                    }

                    @Override
                    public void reportFailed(String msg) {
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        GameSdk.getInstance().onResume(activity);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        GameSdk.getInstance().getOrientation(context, newConfig.orientation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameSdk.getInstance().onDestroy(activity);
    }
}