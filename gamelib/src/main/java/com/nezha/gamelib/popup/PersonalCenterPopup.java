package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.nezha.gamelib.R;
import com.nezha.gamelib.activity.WebActivity;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.callback.ExitCallback;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.utils.ButtonUtils;
import com.nezha.gamelib.utils.SpUtil;
import com.nezha.gamelib.view.LogoWindow;

import androidx.annotation.NonNull;

/**
 * Created by CH
 * on 2021/9/1 16:22
 * desc 个人中心
 */
public class PersonalCenterPopup extends CenterPopupView {

    private static final String TAG = PersonalCenterPopup.class.getSimpleName();

    private final Context context;
    private final Activity activity;
    private TextView textUserName;
    private TextView textUid;
    private final LoginCallback callback;
    private ExitCallback exitCallback;

    public PersonalCenterPopup(@NonNull Context context, Activity activity, LoginCallback loginCallback, ExitCallback exitCallback) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.callback = loginCallback;
        this.exitCallback = exitCallback;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        textUserName = findViewById(R.id.text_user_name);
        textUid = findViewById(R.id.text_uid);
        findViewById(R.id.text_sele).setOnClickListener(v -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure)) {
                exitCallback.exitSuccess("success");
                LogoWindow.getInstants(activity, callback, exitCallback).Stop();
                dismiss();
                //new XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).isDestroyOnDismiss(true).autoFocusEditText(false).asCustom(new LoginPopup(context, activity, callback)).show();
            }
        });
        textUserName.setText("账号: " + (String) SpUtil.get(activity, SpUtil.NAME, ""));
        textUid.setText("uid: " + (int) SpUtil.get(activity, SpUtil.UID, 0));
        findViewById(R.id.layout_bind).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure)) {
                dismiss();
                if ((!TextUtils.isEmpty((String) SpUtil.get(activity, SpUtil.MOBILE, "")))) {
                    new XPopup.Builder(context).asCustom(new ChangePhonePopup(context, activity)).show();
                } else {
                    new XPopup.Builder(context).asCustom(new BindPhonePopup(context, activity)).show();
                }
            }
        });
        findViewById(R.id.layout_real_name).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure)) {
                dismiss();
                if ((int) SpUtil.get(activity, SpUtil.INDULGE, 0) == 1) {
                    if ((int) SpUtil.get(activity, SpUtil.ISBINDID, 0) == 0) {
                        new XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).autoFocusEditText(false).asCustom(new RealNamePopup(context, activity, (int) SpUtil.get(activity, SpUtil.INDULGE, 0), callback, null)).show();
                    } else {
                        Toast.makeText(activity, "此账号已实名!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "不需要绑定");
                }
            }
        });
        findViewById(R.id.layout_change_passwd).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure)) {
                dismiss();
                new XPopup.Builder(context).autoFocusEditText(false).asCustom(new ChangePopup(context, activity)).show();
            }
        });
        findViewById(R.id.layout_service_center).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure)) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("web_url", GameSdk.serviceUrl);
                activity.startActivity(intent);
                dismiss();
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        if (GameSdk.appOrient == 1)
            return R.layout.popup_personal_center_land;
        return R.layout.popup_personal_center;
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
