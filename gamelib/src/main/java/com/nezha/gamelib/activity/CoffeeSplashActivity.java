package com.nezha.gamelib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.nezha.gamelib.R;
import androidx.annotation.Nullable;

/**
 * Created by CH
 * on 2021/9/27 10:48
 * desc
 */
public class CoffeeSplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_land);
        new Handler().postDelayed(() -> {
            findViewById(R.id.layout_first).setVisibility(View.GONE);
            findViewById(R.id.image_sec).setVisibility(View.VISIBLE);
        }, 2000);
        new Handler().postDelayed(this::finish, 4000);
    }

}

