package com.nezha.gamelib.popup;

import android.content.Context;
import android.widget.TextView;

import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;

import androidx.annotation.NonNull;

/**
 * Created by CH
 * on 2021/9/7 10:01
 * desc
 */
public class TipPopup extends CenterPopupView {

    private TextView tipText;
    private final String tip;

    public TipPopup(@NonNull Context context, String tip) {
        super(context);
        this.tip = tip;
    }

    @Override
    protected int getImplLayoutId() {
        int layout = 0;
        if (GameSdk.appOrient == 1) {
            layout = R.layout.popup_tip_land;
        } else {
            layout = R.layout.popup_tip;
        }
        return layout;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        tipText = findViewById(R.id.text_tip);
        tipText.setText(tip + "");

    }

    @Override
    protected int getMaxWidth() {
        return super.getMaxWidth();
    }

    @Override
    protected int getMaxHeight() {
        return 720;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return super.getPopupAnimator();
    }


    protected int getPopupWidth() {
        return 0;
    }


    protected int getPopupHeight() {
        return 0;
    }

}
