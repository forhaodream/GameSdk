package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.network.HttpUtils;
import com.nezha.gamelib.network.RequestListener;
import com.nezha.gamelib.network.Urls;
import com.nezha.gamelib.utils.ButtonUtils;
import com.nezha.gamelib.utils.DeviceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static com.nezha.gamelib.utils.DeviceUtil.packageName;

/**
 * Created by CH
 * on 2021/8/26 17:24
 * desc
 */
public class NewUserPopup extends CenterPopupView {
    private static final String TAG = NewUserPopup.class.getSimpleName();

    private final Context context;
    private RelativeLayout layout;

    private EditText editPhone;
    private EditText editCode;
    private TextView btnSendCode;
    private EditText editPasswd;
    private Button btnSure;
    private ImageView backImage;

    private final LoginCallback loginCallback;
    private final Activity activity;
    private TimeCount time;

    public NewUserPopup(@NonNull Context context, Activity activity, LoginCallback callback) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.loginCallback = callback;
    }

    @Override
    protected int getImplLayoutId() {
        int layout = 0;
        if (GameSdk.appOrient == 1) {
            layout = R.layout.popup_new_user_land;
        } else {
            layout = R.layout.popup_new_user;
        }
        return layout;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        editPhone = findViewById(R.id.edit_phone);
        editCode = findViewById(R.id.edit_code);
        btnSendCode = findViewById(R.id.btn_send_code);
        editPasswd = findViewById(R.id.edit_passwd);
        btnSure = findViewById(R.id.btn_sure);
        backImage = findViewById(R.id.image_back);
        backImage.setOnClickListener(view -> {
            dismiss();
        });
        btnSendCode.setOnClickListener(view -> {
            sendSms();
        });
        btnSure.setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure)) {
                newUser();

            }
        });
    }

    private void sendSms() {
        if (TextUtils.isEmpty(editPhone.getText().toString())) {
            Toast.makeText(context, "请输入手机号!", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = editPhone.getText().toString();
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("type", "1");
        parameters.put("mobile", phone);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", phone);
            object.put("type", "1");
            object.put("game_id", GameSdk.appId);
            object.put("sign", sign);
            object.put("t", t);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_MOBILE_SMS, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 1) {
                        activity.runOnUiThread(() -> {
                            time = new TimeCount(60000, 1000);
                            time.start();
                        });
                    } else {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String s) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, s + "", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void newUser() {
        if (TextUtils.isEmpty(editPhone.getText().toString())) {
            Toast.makeText(context, "请输入手机号!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editCode.getText().toString())) {
            Toast.makeText(context, "请输入验证码!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editPasswd.getText().toString())) {
            Toast.makeText(context, "请输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        String device = "android";
        String idfa = DeviceUtil.getDeviceId(activity);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String phone = editPhone.getText().toString();
        String code = editCode.getText().toString();
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("idfa", idfa);
        parameters.put("device", device);
        parameters.put("os_ver", DeviceUtil.getVersionName(activity));
        parameters.put("udid", idfa);
        parameters.put("game_id", GameSdk.appId);
        parameters.put("mobile", phone);
        parameters.put("code", code);
        parameters.put("passwd", editPasswd.getText().toString() + "");
        parameters.put("t", t);

        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);

        JSONObject object = new JSONObject();
        try {
            object.put("idfa", idfa);
            object.put("device", device);
            object.put("os_ver", packageName(activity));
            object.put("udid", idfa);
            object.put("game_id", GameSdk.appId);
            object.put("mobile", phone);
            object.put("code", code);
            object.put("passwd", editPasswd.getText().toString() + "");
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().post(Urls.POST_MOBILE, object.toString(), new RequestListener() {
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


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnSendCode.setText("获取验证码");
            btnSendCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnSendCode.setClickable(false);
            btnSendCode.setText("验证码(" + millisUntilFinished / 1000 + "s)");
        }
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
