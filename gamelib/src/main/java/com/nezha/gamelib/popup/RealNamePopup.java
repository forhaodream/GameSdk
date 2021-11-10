package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.bean.AutoBean;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.network.HttpUtils;
import com.nezha.gamelib.network.RequestListener;
import com.nezha.gamelib.network.Urls;
import com.nezha.gamelib.utils.ButtonUtils;
import com.nezha.gamelib.utils.DeviceUtil;
import com.nezha.gamelib.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;

/**
 * Created by CH
 * on 2021/8/26 10:28
 * desc
 */
public class RealNamePopup extends CenterPopupView {

    private final Context context;
    private ImageView imageClose;
    private EditText editName;
    private EditText editNumber;
    private Button btnSure;
    private final Activity activity;
    private final int isShow;
    private final LoginCallback loginCallback;
    private final AutoBean autoBean;
    private ProgressBar bar;

    public RealNamePopup(@NonNull Context context, Activity activity, int isShow, LoginCallback loginCallback, AutoBean bean) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.isShow = isShow;
        this.loginCallback = loginCallback;
        this.autoBean = bean;

    }

    @Override
    protected int getImplLayoutId() {
        int layout = 0;
        if (GameSdk.appOrient == 1) {
            layout = R.layout.popup_real_land;
        } else {
            layout = R.layout.popup_real;
        }
        return layout;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        imageClose = findViewById(R.id.image_close);
        editName = findViewById(R.id.edit_name);
        editNumber = findViewById(R.id.edit_number);
        bar = findViewById(R.id.progress_bar);
        if (isShow == 1) {
            imageClose.setVisibility(GONE);
        } else {
            imageClose.setVisibility(VISIBLE);
            imageClose.setOnClickListener(view -> {
                dismiss();
            });
        }
        findViewById(R.id.btn_sure).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure))
                realName();
        });
    }

    private void realName() {
        if (TextUtils.isEmpty(editName.getText().toString())) {
            Toast.makeText(context, "请输入真实姓名!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editNumber.getText().toString())) {
            Toast.makeText(context, "请输入身份证号码!", Toast.LENGTH_SHORT).show();
            return;
        }
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String name = editName.getText().toString();
        String number = editNumber.getText().toString();

        parameters.put("game_id", GameSdk.appId);
        parameters.put("uid", (int) SpUtil.get(context, SpUtil.UID, 0));
        parameters.put("realname", name);
        parameters.put("idcard", number);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);

        JSONObject object = new JSONObject();
        try {
            object.put("game_id", GameSdk.appId);
            object.put("uid", (int) SpUtil.get(context, SpUtil.UID, 0));
            object.put("realname", name);
            object.put("idcard", number);
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bar.setVisibility(VISIBLE);
        HttpUtils.getInstance().post(Urls.POST_REALNAME, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 1) {
                        AutoBean result = new Gson().fromJson(json, AutoBean.class);
                        SpUtil.put(activity, SpUtil.INDULGE, result.getData().getIndulge());
                        SpUtil.put(activity, SpUtil.AGE, result.getData().getAge());
                        SpUtil.put(activity, SpUtil.ISBINDID, result.getData().getIs_bind_idcard());
                        activity.runOnUiThread(() -> {
                            bar.setVisibility(GONE);
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            dismiss();
                            GameSdk.getInstance().heartbeat(activity);
                            loginCallback.loginSuccess(autoBean, msg);
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
                });
            }
        });
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

}
