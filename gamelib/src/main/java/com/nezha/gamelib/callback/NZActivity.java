package com.nezha.gamelib.callback;

import android.app.Activity;
import android.content.Context;

/**
 * Created by CH
 * on 2021/8/30 13:11
 * desc
 */
public interface NZActivity {
    void onCreate(Activity paramActivity, LoginCallback loginCallback, ExitCallback exitCallback);

    void onStart(Activity paramActivity);

    void onStop(Activity paramActivity);

    void onResume(Activity paramActivity);

    void onPause(Activity paramActivity);

    void onRestart(Activity paramActivity);

    void onDestroy(Activity paramActivity);
}
