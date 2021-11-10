package com.nezha.gamelib.popup;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.util.XPopupUtils;
import com.nezha.gamelib.R;
import com.nezha.gamelib.adapter.CommonAdapter;
import com.nezha.gamelib.adapter.CommonViewHolder;
import com.nezha.gamelib.app.GameSdk;
import com.nezha.gamelib.bean.AutoBean;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.network.HttpUtils;
import com.nezha.gamelib.network.RequestListener;
import com.nezha.gamelib.network.Urls;
import com.nezha.gamelib.sqlite.DBHelper;
import com.nezha.gamelib.utils.ButtonUtils;
import com.nezha.gamelib.utils.DeviceUtil;
import com.nezha.gamelib.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.annotation.NonNull;

import static com.nezha.gamelib.utils.DeviceUtil.packageName;

/**
 * Created by CH
 * on 2021/8/25 15:04
 * desc 登录弹窗
 */
public class LoginPopup extends CenterPopupView {
    private final Context context;

    private boolean isPhone = false;
    private boolean isPasswd = false;
    private EditText editPhone;
    private ImageView ivPhone;
    private EditText editPasswd;
    private ImageView ivPasswd;
    private RelativeLayout hideLayout;
    private final LoginCallback loginCallback;
    private final Activity activity;
    private ListView phoneList;
    private static final String DB_NAME = "mydb";
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private String phoneStr;
    private String passwdStr;
    private final Map<String, String> map = new HashMap<>();
    private boolean isOnly = true;

    public LoginPopup(@NonNull Context context, Activity activity, LoginCallback callback) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.loginCallback = callback;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initView();
        addList();
    }

    @Override
    protected int getImplLayoutId() {
        if (GameSdk.appOrient == 1)
            return R.layout.popup_login_land;
        return R.layout.popup_login;
    }

    private void initView() {
        findViewById(R.id.text_forgot).setOnClickListener(view ->
                new XPopup.Builder(context).popupAnimation(PopupAnimation.TranslateFromLeft).autoFocusEditText(false).asCustom(new ForgotPopup(context, activity)).show());
        findViewById(R.id.text_new_user).setOnClickListener(view ->
                new XPopup.Builder(context).popupAnimation(PopupAnimation.TranslateFromRight).autoFocusEditText(false).asCustom(new NewUserPopup(context, activity)).show());
        editPhone = findViewById(R.id.edit_phone);
        ivPhone = findViewById(R.id.iv_phone);
        editPasswd = findViewById(R.id.edit_passwd);
        ivPasswd = findViewById(R.id.iv_passwd);
        hideLayout = findViewById(R.id.layout_hide);
        phoneList = findViewById(R.id.lv_hind);
        findViewById(R.id.btn_quick).setOnClickListener(v -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_quick)) {
                dismiss();
                new XPopup.Builder(activity).isRequestFocus(false).asCustom(new LoggingInPopup(activity, activity, loginCallback)).show();
            }
        });
        editPhone.setText((String) SpUtil.get(context, SpUtil.NAME, ""));
        editPhone.setSelection(((String) SpUtil.get(context, SpUtil.NAME, "")).length());
        editPasswd.setText((String) SpUtil.get(context, SpUtil.PASSWD, ""));
        ivPhone.setOnClickListener(view -> {
            if (isPhone) {
                ivPhone.setImageDrawable(getResources().getDrawable(R.mipmap.down));
                hideLayout.setVisibility(GONE);
            } else {
                ivPhone.setImageDrawable(getResources().getDrawable(R.mipmap.up));
                hideLayout.setVisibility(VISIBLE);
            }
            isPhone = !isPhone;
        });
        ivPasswd.setOnClickListener(view -> {
            if (isPasswd) {
                ivPasswd.setImageDrawable(getResources().getDrawable(R.mipmap.hind));
                editPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                ivPasswd.setImageDrawable(getResources().getDrawable(R.mipmap.display));
                editPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            isPasswd = !isPasswd;
        });

        findViewById(R.id.btn_login).setOnClickListener(view -> {
            if (!ButtonUtils.isFastDoubleClick(R.id.btn_login)) {
                autoLogin();
            }
        });
    }


    private void addList() {
        dbHelper = new DBHelper(activity, DB_NAME, null, 1);
        db = dbHelper.getWritableDatabase();// 打开数据库
        cursor = db.query(true, DBHelper.TB_NAME, null, null, null, "phone", null, null, null);
//        cursor = db.query(dbHelper.TB_NAME, null, null, null, null, null, "_id ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            phoneStr = cursor.getString(1);
            passwdStr = cursor.getString(2);
            cursor.moveToNext();
            map.put(phoneStr, passwdStr);
        }
        List<String> result = new ArrayList(map.keySet());

        CommonAdapter adapter = new CommonAdapter<String>(context, R.layout.item_phone, result) {
            @Override
            public void convert(CommonViewHolder holder, String itemData, int position) {
                RelativeLayout layout = holder.getView(R.id.layout);
                TextView text = holder.getView(R.id.text_phone);
                ImageView delImage = holder.getView(R.id.image_del);
                delImage.setOnClickListener(v -> {

                });
                layout.setOnClickListener(v -> {
                    hideLayout.setVisibility(GONE);
                    editPhone.setText(itemData);
                    editPasswd.setText(map.get(itemData));
                });
                text.setText(itemData);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }
        };
        phoneList.setAdapter(adapter);
    }


    /**
     * 账户密码登录
     */
    public void autoLogin() {
        if (TextUtils.isEmpty(editPhone.getText().toString())) {
            Toast.makeText(context, "请输入账号!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(editPasswd.getText().toString())) {
            Toast.makeText(context, "请输入密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        String device = "android";
        String idfa = DeviceUtil.getDeviceId(activity);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("loginname", editPhone.getText().toString());
        parameters.put("passwd", editPasswd.getText().toString());
        parameters.put("idfa", idfa);
        parameters.put("os_ver", packageName(activity));
        parameters.put("device", device);
        parameters.put("udid", idfa);
        parameters.put("t", t);

        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);

        JSONObject object = new JSONObject();
        try {
            object.put("idfa", idfa);
            object.put("device", device);
            object.put("os_ver", packageName(activity));
            object.put("udid", idfa);
            object.put("game_id", GameSdk.appId);
            object.put("loginname", editPhone.getText().toString());
            object.put("passwd", editPasswd.getText().toString());
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpUtils.getInstance().post(Urls.POST_LOGIN, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 1) {
                        AutoBean bean = new Gson().fromJson(json, AutoBean.class);
                        SpUtil.put(context, SpUtil.ISSELE, false);
                        SpUtil.put(activity, SpUtil.NAME, editPhone.getText().toString());
                        SpUtil.put(activity, SpUtil.PASSWD, editPasswd.getText().toString());
                        SpUtil.put(activity, SpUtil.MOBILE, bean.getData().getMobile());
                        SpUtil.put(activity, SpUtil.UID, bean.getData().getUid());
                        SpUtil.put(activity, SpUtil.ISBINDID, bean.getData().getIs_bind_idcard());
                        GameSdk.getInstance().addDb(editPhone.getText().toString().trim(), editPasswd.getText().toString().trim());
                        GameSdk.getInstance().timer(activity);
                        if (TextUtils.isEmpty(bean.getData().getFc_uid())) {
                            bean.getData().setFc_uid(String.valueOf(bean.getData().getUid()));
                        }
                        activity.runOnUiThread(() -> {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            dismiss();
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
                            }
                        } else {
                            activity.runOnUiThread(() -> {
                                loginCallback.loginSuccess(bean, msg);
                            });
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
