package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
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
 * on 2021/9/22 14:25
 * desc
 */
public class ChangePopup extends CenterPopupView {
    private static final String TAG = ChangePopup.class.getSimpleName();
    private final Context context;
    private EditText psdEdit;
    private EditText psdNewEdit;
    private final Activity activity;


    public ChangePopup(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.image_back).setOnClickListener(view -> {
            dismiss();
        });
        psdEdit = findViewById(R.id.edit_passwd);
        psdNewEdit = findViewById(R.id.edit_passwd_new);
        findViewById(R.id.btn_sure).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure))
                changePsd();
        });
    }

    @Override
    protected int getImplLayoutId() {
        if (GameSdk.appOrient == 1)
            return R.layout.popup_change_land;
        return R.layout.popup_change;
    }


    private void changePsd() {
        if (TextUtils.isEmpty(psdEdit.getText().toString())) {
            Toast.makeText(context, "请输入原密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(psdNewEdit.getText().toString())) {
            Toast.makeText(context, "请确认新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String uid = String.valueOf((int) SpUtil.get(activity, SpUtil.UID, 0));
        String psd = (String) SpUtil.get(activity, SpUtil.PASSWD, "");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("uid", uid);
        parameters.put("passwd", psd);
        parameters.put("passwd_new", psdNewEdit.getText().toString().trim());
        parameters.put("game_id", GameSdk.appId);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("uid", uid);
            object.put("passwd", psd);
            object.put("passwd_new", psdNewEdit.getText().toString().trim());
            object.put("game_id", GameSdk.appId);
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_MODIFY_PWD, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        if (code == 1)
                            dismiss();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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


    protected int getPopupWidth() {
        return (int) XPopupUtils.getWindowWidth(context);
    }


    protected int getPopupHeight() {
        return 0;
    }

}
