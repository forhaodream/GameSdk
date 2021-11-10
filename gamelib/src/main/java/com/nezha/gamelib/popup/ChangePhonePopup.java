package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.network.HttpUtils;
import com.nezha.gamelib.network.RequestListener;
import com.nezha.gamelib.network.Urls;
import com.nezha.gamelib.utils.DeviceUtil;
import com.nezha.gamelib.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;

/**
 * Created by CH
 * on 2021/10/14 11:40
 * desc
 */
public class ChangePhonePopup extends CenterPopupView {

    private static final String TAG = ChangePhonePopup.class.getSimpleName();

    private final Context context;
    private EditText editPhone;
    private EditText editCode;
    private EditText editNewCode;
    private Button btnSure;
    private TextView sendCode, sendCodeNew;
    private final Activity activity;
    private TimeCount time;
    private TimeCountNew newTime;
    private TextView oldPhoneText;

    private String phone = "";
    private String maskNumber = "";

    public ChangePhonePopup(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected int getImplLayoutId() {
        int layout = 0;
        if (GameSdk.appOrient == 1) {
            layout = R.layout.popup_change_bind_land;
        } else {
            layout = R.layout.popup_change_bind;
        }
        return layout;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        editPhone = findViewById(R.id.edit_phone);
        editCode = findViewById(R.id.edit_code);
        editNewCode = findViewById(R.id.edit_code_new);
        btnSure = findViewById(R.id.btn_sure);
        sendCode = findViewById(R.id.text_send_sms);
        sendCodeNew = findViewById(R.id.text_send_sms_new);
        oldPhoneText = findViewById(R.id.old_phone);
        phone = (String) SpUtil.get(activity, SpUtil.MOBILE, "");
        maskNumber = phone.substring(0, 3) + "****" + phone.substring(7);
        oldPhoneText.setText("当前手机号: " + maskNumber);
        findViewById(R.id.image_back).setOnClickListener(view -> {
            dismiss();
        });
        sendCode.setOnClickListener(view -> {
            sendSms();
        });
        sendCodeNew.setOnClickListener(view -> {
            sendSmsNew();
        });
        btnSure.setOnClickListener(view -> {
//            if (!ButtonUtils.isFastDoubleClick(R.id.btn_sure))
            newUser();
        });
    }

    private void sendSms() {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("type", "4");
        parameters.put("mobile", phone);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", phone);
            object.put("type", "4");
            object.put("game_id", GameSdk.appId);
            object.put("sign", sign);
            object.put("t", t);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "game_id:" + GameSdk.appId);
        Log.d(TAG, "game_key:" + GameSdk.appkey);
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
                            time = new TimeCount(120000, 1000);
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

    private void sendSmsNew() {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("type", "4");
        parameters.put("mobile", editPhone.getText().toString().trim());
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", editPhone.getText().toString().trim());
            object.put("type", "4");
            object.put("game_id", GameSdk.appId);
            object.put("sign", sign);
            object.put("t", t);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "game_id:" + GameSdk.appId);
        Log.d(TAG, "game_key:" + GameSdk.appkey);
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
                            newTime = new TimeCountNew(120000, 1000);
                            newTime.start();
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
            Toast.makeText(context, "请输入新手机号!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editCode.getText().toString())) {
            Toast.makeText(context, "请输入验证码!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editNewCode.getText().toString())) {
            Toast.makeText(context, "请输入新验证码!", Toast.LENGTH_SHORT).show();
            return;
        }
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String phone = editPhone.getText().toString();
        String code = editCode.getText().toString();
        String newCode = editNewCode.getText().toString();
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("new_mobile", phone);
        parameters.put("code", code);
        parameters.put("new_code", newCode);
        parameters.put("uid", (int) SpUtil.get(context, SpUtil.UID, 0));
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);

        JSONObject object = new JSONObject();
        try {
            object.put("game_id", GameSdk.appId);
            object.put("new_mobile", phone);
            object.put("code", code);
            object.put("new_code", newCode);
            object.put("uid", (int) SpUtil.get(context, SpUtil.UID, 0));
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_MOBILE_BIND_CHANGE, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        if (code == 1) {
                            dismiss();
                            SpUtil.put(context, SpUtil.NAME, phone);
                            SpUtil.put(activity, SpUtil.MOBILE, phone);
                            GameSdk.getInstance().replacePhone(activity, phone);
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
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
            sendCode.setText("获取验证码");
            sendCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendCode.setClickable(false);
            sendCode.setText("验证码(" + millisUntilFinished / 1000 + "s)");
        }
    }
    class TimeCountNew extends CountDownTimer {
        public TimeCountNew(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            sendCodeNew.setText("获取验证码");
            sendCodeNew.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sendCodeNew.setClickable(false);
            sendCodeNew.setText("验证码(" + millisUntilFinished / 1000 + "s)");
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
