package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lxj.xpopup.BuildConfig;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.nezha.gamelib.R;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.bean.ContactBean;
import com.nezha.gamelib.callback.PayCallback;
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
 * on 2021/8/26 09:23
 * desc
 */
public final class PayPopup extends CenterPopupView {
    private static final String TAG = PayPopup.class.getSimpleName();

    private final Context context;
    private RelativeLayout layout;

    private final boolean isVisible = false;

    private TextView textUserName;
    private TextView textUid;
    private final PayCallback payCallback;
    private final Activity activity;
    private LinearLayout aliPay;
    private LinearLayout wxPay;
    private TextView contactText;
    private TextView priceText;

    private final String amount;
    private final String product_name;
    private final String attach;
    private final String product_id;
    private final String role_id;
    private String did;
    private final String ext;

    private int age;

    private PayPopup payPopup;

    public PayPopup(@NonNull Context context, Activity activity, String amount, String product_name, String attach, String product_id, String role_id, String ext, PayCallback callback) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.payCallback = callback;
        this.amount = amount;
        this.product_name = product_name;
        this.attach = attach;
        this.product_id = product_id;
        this.role_id = role_id;
        this.did = did;
        this.ext = ext;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        payPopup = this;
        textUserName = findViewById(R.id.text_user_name);
        textUid = findViewById(R.id.text_uid);
        age = (int) SpUtil.get(context, SpUtil.AGE, 0);
        aliPay = findViewById(R.id.ali_pay);
        wxPay = findViewById(R.id.wx_pay);
        contactText = findViewById(R.id.text_contact);
        priceText = findViewById(R.id.popup_pay_price);
        priceText.setText("¥" + Integer.parseInt(amount) / 100);
        textUserName.setText("账号: " + (String) SpUtil.get(activity, SpUtil.NAME, ""));
        textUid.setText("uid: " + (int) SpUtil.get(activity, SpUtil.UID, 0) + "");
        aliPay.setOnClickListener(view -> {
            dismiss();
            GameSdk.getInstance().createWxOrder(context, activity, amount, product_name, attach, product_id, role_id, ext, payCallback, "1", payPopup);
        });
        wxPay.setOnClickListener(view -> {
            dismiss();
            GameSdk.getInstance().createWxOrder(context, activity, amount, product_name, attach, product_id, role_id, ext, payCallback, "2", payPopup);
        });
        findViewById(R.id.image_close).setOnClickListener(v -> {
            payCallback.payCancel("cancel");
            dismiss();
        });

    }


    @Override
    protected int getImplLayoutId() {
        int layout = 0;
        if (GameSdk.appOrient == 1) {
            layout = R.layout.popup_pay_land;
        } else {
            layout = R.layout.popup_pay;
        }
        return layout;
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

    public void getContact() {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign( parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("game_id", GameSdk.appId);
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().post(Urls.POST_CONTACT, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                Log.d("-----------", json);
                // {"code":1,"msg":"success","data":null}
//                try {
//                    JSONObject jsonObject = new JSONObject(json);
//                    int code = jsonObject.getInt("code");
//                    JSONObject data = jsonObject.getJSONObject("data");
//                    if (code == 1 && data != null) {
//                        ContactBean bean = new Gson().fromJson(json, ContactBean.class);
//                        contactText.setText(" 请选择支付方式,如支付失败, 请联系" + bean.getData().get(0).getKey() + " : " + bean.getData().get(0).getVal());
//                    } else {
//                        contactText.setText(" 请选择支付方式,如支付失败, 请联系");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(String msg) {
                Log.d("-----------", msg);
            }
        });

    }
}
