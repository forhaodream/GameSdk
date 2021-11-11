package com.nezha.gamelib.popup;

import android.content.Context;
import android.widget.Toast;

import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.callback.ExitCallback;
import com.nezha.gamelib.utils.SpUtil;

import androidx.annotation.NonNull;

/**
 * Created by CH
 * on 2021/9/15 13:31
 * desc 退出登录
 */
public class ExitPopup extends CenterPopupView {
    private final Context context;
    private final ExitCallback exitCallback;

    public ExitPopup(@NonNull Context context, ExitCallback exitCallback) {
        super(context);
        this.context = context;
        this.exitCallback = exitCallback;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.btn_sure).setOnClickListener(v -> {
            SpUtil.put(context, SpUtil.UID, 0);
            exitCallback.exitSuccess("success");
            Toast.makeText(context, "退出登录", Toast.LENGTH_SHORT).show();
            dismiss();
        });
        findViewById(R.id.cancel).setOnClickListener(v -> {
            exitCallback.exitCancel("cancel");
            dismiss();
        });
    }


    @Override
    protected int getImplLayoutId() {
        if (GameSdk.appOrient == 1)
            return R.layout.popup_exit_land;
        return R.layout.popup_exit;
    }

    @Override
    protected int getMaxWidth() {
        return super.getMaxWidth();
    }

    @Override
    protected int getMaxHeight() {
        return super.getMaxHeight();
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
