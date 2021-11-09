package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.bean.AutoBean;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.network.HttpUtils;
import com.nezha.gamelib.network.NetCallback;
import com.nezha.gamelib.network.RequestListener;
import com.nezha.gamelib.network.Urls;
import com.nezha.gamelib.utils.ButtonUtils;
import com.nezha.gamelib.utils.DeviceUtil;
import com.nezha.gamelib.utils.ImageUtil;
import com.nezha.gamelib.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static com.nezha.gamelib.utils.DeviceUtil.packageName;

/**
 * Created by CH
 * on 2021/8/26 10:28
 * desc
 */
public class LoggingInPopup extends BasePopupView {
    private static final String TAG = LoggingInPopup.class.getSimpleName();
    private final Context context;
    private RelativeLayout layout;
    private TextView userNameText;
    private Button selectorBtn;

    private final boolean isVisible = false;

    private final LoginCallback loginCallback;
    private final Activity activity;
    private boolean isOnly = true;

    public LoggingInPopup(@NonNull Context context, Activity activity, LoginCallback callback) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.loginCallback = callback;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        userNameText = findViewById(R.id.text_user_name);
        selectorBtn = findViewById(R.id.btn_selector);
        selectorBtn.setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_selector)) {
                dismiss();
                SpUtil.put(context, SpUtil.ISSELE, true);
                new XPopup.Builder(context).autoFocusEditText(false).asCustom(new LoginPopup(context, activity, loginCallback)).show();
            }
        });
        userNameText.setText((String) SpUtil.get(context, SpUtil.NAME, ""));
        autoRegister(activity, loginCallback);
    }

    @Override
    protected int getPopupLayoutId() {
        int layout = 0;
        if (GameSdk.appOrient == 1) {
            layout = R.layout.popup_logging_in_land;
        } else {
            layout = R.layout.popup_logging_in;
        }
        return layout;
    }

    /**
     * 自动注册
     */
    public void autoRegister(Activity activity, LoginCallback callback) {

        System.out.print("autoRegister");
        String device = "android";
        String idfa = DeviceUtil.getDeviceId(activity);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("idfa", idfa);
        parameters.put("device", device);
        parameters.put("os_ver", packageName(activity));
        parameters.put("udid", idfa);
        parameters.put("game_id", GameSdk.appId);
        parameters.put("has_idfa_auth", "1");
        parameters.put("t", t);

        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);

        JSONObject object = new JSONObject();
        try {
            object.put("idfa", idfa);
            object.put("device", device);
            object.put("os_ver", packageName(activity));
            object.put("udid", idfa);
            object.put("game_id", GameSdk.appId);
            object.put("has_idfa_auth", "1");
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_AUTO, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                System.out.print("autoRegister onSuccess");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 1) {
                        AutoBean bean = new Gson().fromJson(json, AutoBean.class);
                        SpUtil.put(activity, SpUtil.NAME, bean.getData().getUsername());
                        if (!TextUtils.isEmpty(bean.getData().getPasswd())) {
                            SpUtil.put(activity, SpUtil.PASSWD, bean.getData().getPasswd());
                            GameSdk.getInstance().addDb(bean.getData().getUsername(), bean.getData().getPasswd());
                        }
                        SpUtil.put(activity, SpUtil.UID, bean.getData().getUid());
                        SpUtil.put(activity, SpUtil.TOKEN, bean.getData().getToken());
                        SpUtil.put(activity, SpUtil.INDULGE, bean.getData().getIndulge());
                        SpUtil.put(activity, SpUtil.ISBINDID, bean.getData().getIs_bind_idcard());
                        SpUtil.put(activity, SpUtil.AGE, bean.getData().getAge());
                        SpUtil.saveAccount(activity, bean);
                        GameSdk.getInstance().timer(activity);
//                        if (!TextUtils.isEmpty(bean.getData().getPasswd()))
//                            addTextToJpg(bean.getData().getUsername(), bean.getData().getPasswd());

                        if (TextUtils.isEmpty(bean.getData().getFc_uid())) {
                            bean.getData().setFc_uid(String.valueOf(bean.getData().getUid()));
                        }
                        activity.runOnUiThread(() -> {
                            try {
                                Thread.sleep(3000);//休眠3秒
                                dismiss();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                }
                        });
                        if (bean.getData().getIndulge() == 1) {
                            SpUtil.put(activity, SpUtil.INDULGE, bean.getData().getIndulge());
                            if (bean.getData().getIs_bind_idcard() == 0) {
                                SpUtil.put(activity, SpUtil.ISBINDID, bean.getData().getIs_bind_idcard());
                                new XPopup.Builder(context).dismissOnBackPressed(false).dismissOnTouchOutside(false).autoFocusEditText(false).asCustom(new RealNamePopup(context, activity, bean.getData().getIndulge(), loginCallback, bean)).show();
                            } else {
                                activity.runOnUiThread(() -> {
                                    loginCallback.loginSuccess(bean, msg);
                                });
                                System.out.println("已绑定身份证");
                            }
                        } else {
                            activity.runOnUiThread(() -> {
                                loginCallback.loginSuccess(bean, msg);
                            });
                            System.out.println("不需要绑定");
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            loginCallback.loginFailed(msg);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String msg) {
                activity.runOnUiThread(() -> {
                    loginCallback.loginFailed(msg);
                });
                Log.d(TAG, msg);
                System.out.print("autoRegister onFailure");
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
        return 0;
    }


    protected int getPopupHeight() {
        return 0;
    }

    /**
     * 添加文本到图片
     */
    private void addTextToJpg(String name, String passwd) {
        //从文件中获取bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.passwd);
        Bitmap bit = ImageUtil.drawTextToBitmap(bitmap, name, passwd);
        //保存到本地文件
        ImageUtil.saveBitmapToSDCard(bit, "/sdcard/result.jpg");

    }
}
