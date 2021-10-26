package com.nezha.gamelib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nezha.gamelib.R;

import androidx.annotation.Nullable;

/**
 * Created by CH
 * on 2021/9/17 16:02
 * desc
 */
public class LandSplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(this::finish, 2000);
    }


}

