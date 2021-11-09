package com.nezha.gamelib;

import android.app.Activity;
import android.os.Bundle;

import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.bean.AutoBean;
import com.nezha.gamelib.callback.ExitCallback;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.callback.PayCallback;
import com.nezha.gamelib.callback.ReportCallback;

import androidx.annotation.Nullable;

/**
 * Created by CH
 * on 2021/11/8 15:21
 */
public class 接入文档 extends Activity {
    /**
     * AndroidManifest.xml中添加以下权限和参数
     * <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE"   tools:ignore="ProtectedPermissions" />
     * <uses-permission android:name="android.permission.WAKE_LOCK" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * <uses-permission android:name="android.permission.INTERNET" />
     *
     * <meta-data
     *   android:name="app_id"
     *   android:value="" />
     * <meta-data
     *   android:name="app_key"
     *   android:value="" />
     * <meta-data
     *   android:name="app_orient"
     *   android:value="1" />
     *   竖屏 = 0 横屏 = 1
     */
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
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

            }

            @Override
            public void exitCancel(String s) {

            }
        });
        /**
         * 开屏动画
         */
        GameSdk.getInstance().anim(activity);
        /**
         * 登录
         */
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
        /**
         * 支付
         */
        GameSdk.getInstance().pay(activity, "单位:分", "订单id", "产品名称", "产品id", "角色id", "附加信息", new PayCallback() {
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
        /**
         * 退出
         */
        GameSdk.getInstance().exitLogin(activity, new ExitCallback() {
            @Override
            public void exitSuccess(String s) {

            }

            @Override
            public void exitCancel(String s) {

            }
        });
        /**
         * 角色上报
         */
        GameSdk.getInstance().report("role_name", "role_id", "server_name", "server_id", "diamonds", "online", "power", "level", activity, new ReportCallback() {
            @Override
            public void reportSuccess(String msg) {
            }

            @Override
            public void reportFailed(String msg) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GameSdk.getInstance().onStart(activity);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GameSdk.getInstance().onStop(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GameSdk.getInstance().onResume(activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameSdk.getInstance().onPause(activity);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GameSdk.getInstance().onRestart(activity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameSdk.getInstance().onDestroy(activity);
    }
}
