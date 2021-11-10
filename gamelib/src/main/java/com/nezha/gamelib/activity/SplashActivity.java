package com.nezha.gamelib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import com.nezha.gamelib.R;
import androidx.annotation.Nullable;

/**
 * Created by CH
 * on 2021/8/26 16:11
 * desc
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(this::finish, 2000);
    }

}
