package com.nezha.gamelib.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.XPopupCallback;
import com.nezha.gamelib.activity.LandSplashActivity;
import com.nezha.gamelib.activity.SplashActivity;
import com.nezha.gamelib.activity.WebActivity;
import com.nezha.gamelib.bean.PayBean;
import com.nezha.gamelib.bean.SearchOrderBean;
import com.nezha.gamelib.callback.ExitCallback;
import com.nezha.gamelib.callback.LoginCallback;
import com.nezha.gamelib.callback.NZActivity;
import com.nezha.gamelib.callback.PayCallback;
import com.nezha.gamelib.callback.ReportCallback;
import com.nezha.gamelib.network.HttpUtils;
import com.nezha.gamelib.network.RequestListener;
import com.nezha.gamelib.network.Urls;
import com.nezha.gamelib.popup.ExitPopup;
import com.nezha.gamelib.popup.LoginPopup;
import com.nezha.gamelib.popup.PayPopup;
import com.nezha.gamelib.popup.PersonalCenterPopup;
import com.nezha.gamelib.popup.RealNamePopup;
import com.nezha.gamelib.popup.TipPopup;
import com.nezha.gamelib.sqlite.DBHelper;
import com.nezha.gamelib.utils.DeviceUtil;
import com.nezha.gamelib.utils.SpUtil;
import com.nezha.gamelib.view.LogoWindow;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by CH
 * on 2021/8/25 16:03
 * desc
 */
public class GameSdk implements NZActivity {

    private static final String TAG = GameSdk.class.getSimpleName();

    private static GameSdk instance;

    private Activity activity;

    public static String appId = "";
    public static String appkey = "";
    public static int appOrient = 0;
    public static String serviceUrl = "https://yzf.qq.com/xv/web/static/chat/index.html?sign=37ef9b97d57357c0744199e61fe2e03750a645b46ae6cd4fd62ff317e5cdda37c9e329c6a23e4405f291d70d4ab41baa2ffbc2ed";
    private static final String DB_NAME = "mydb";
    private boolean isShowPop = false;
    private final long inTime = System.currentTimeMillis() / 1000;
    private Timer timer;

    private String trade_id;
    private PayCallback payCallback;

    public GameSdk() {

    }

    public static GameSdk getInstance() {
        if (instance == null) {
            synchronized (GameSdk.class) {
                if (instance == null) {
                    instance = new GameSdk();
                }
            }
        }
        return instance;
    }

    public GameSdk init(Activity activity) {
        if (activity == null) {
            this.activity = activity;
        }
        return instance;
    }

    /**
     * 动画
     *
     * @return
     */
    public void anim(Activity activity) {
        if (GameSdk.appOrient == 1) {
            activity.startActivity(new Intent(activity, LandSplashActivity.class));
        } else {
            activity.startActivity(new Intent(activity, SplashActivity.class));
        }
    }

    public void exitLogin(Activity activity, ExitCallback exitCallback) {
        new XPopup.Builder(activity).asCustom(new ExitPopup(activity, exitCallback)).show();
    }

    public void pay(Activity activity, String amount, String attach, String product_name, String product_id, String role_id, String ext, PayCallback payCallback) {
        if ((int) SpUtil.get(activity, SpUtil.UID, 0) == 0) {
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
        } else {
            new XPopup.Builder(activity).dismissOnBackPressed(false).dismissOnTouchOutside(false).isDestroyOnDismiss(true).asCustom(new PayPopup(activity, activity, amount, product_name, attach, product_id, role_id, ext, payCallback)).show();
        }
    }

    /**
     * 登录
     */
    public void login(Activity activity, LoginCallback callback) {
        new XPopup.Builder(activity).dismissOnBackPressed(false).dismissOnTouchOutside(false).autoFocusEditText(false).asCustom(new LoginPopup(activity, activity, callback)).show();
    }


    @Override
    public void onCreate(Activity activity, LoginCallback loginCallback, ExitCallback exitCallback) {
        LogoWindow.getInstants(activity, loginCallback, exitCallback).start();
        GameSdk.getInstance().getInfo(activity);
        CrashReport.initCrashReport(activity.getApplicationContext(), "dfacea3c0b", false);
        dbHelper = new DBHelper(activity, DB_NAME, null, 1);
        db = dbHelper.getWritableDatabase();// 打开数据库
    }

    public void logoClick(Activity activity, LoginCallback loginCallback, ExitCallback exitCallback) {
        if ((int) SpUtil.get(activity, SpUtil.UID, 0) != 0) {
            if (!isShowPop)
                new XPopup.Builder(activity).setPopupCallback(new XPopupCallback() {
                    @Override
                    public void onCreated(BasePopupView popupView) {

                    }

                    @Override
                    public void beforeShow(BasePopupView popupView) {

                    }

                    @Override
                    public void onShow(BasePopupView popupView) {
                        isShowPop = true;
                    }

                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        isShowPop = false;
                    }

                    @Override
                    public void beforeDismiss(BasePopupView popupView) {

                    }

                    @Override
                    public boolean onBackPressed(BasePopupView popupView) {
                        return false;
                    }

                    @Override
                    public void onKeyBoardStateChanged(BasePopupView popupView, int height) {

                    }

                    @Override
                    public void onDrag(BasePopupView popupView, int value, float percent, boolean upOrLeft) {

                    }
                }).borderRadius(20).asCustom(new PersonalCenterPopup(activity, activity, loginCallback, exitCallback)).show();
        } else {
            Toast.makeText(activity, "请先登录!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart(Activity activity) {
    }

    @Override
    public void onStop(Activity activity) {
    }

    @Override
    public void onResume(Activity activity) {
        if (!TextUtils.isEmpty(trade_id)) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            searchOrder(activity, trade_id);
        } else {
            System.out.print("trade_id is null");
        }
    }

    @Override
    public void onPause(Activity paramActivity) {

    }

    @Override
    public void onRestart(Activity paramActivity) {

    }

    @Override
    public void onDestroy(Activity activity) {
        exit(activity);
        SpUtil.put(activity, SpUtil.UID, 0);
    }

    public void createWxOrder(Context context, Activity activity, String amount, String product_name
            , String attach, String product_id, String role_id, String ext
            , PayCallback payCallback, String payType, PayPopup payPopup) {
        this.payCallback = payCallback;
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String uid = String.valueOf((int) SpUtil.get(context, SpUtil.UID, 0));
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("uid", uid);
        parameters.put("product_name", product_name);
        parameters.put("product_id", product_id);
        parameters.put("amount", amount);
        parameters.put("attach", attach);//订单号
        parameters.put("is_debug", "0");
        parameters.put("pay_type", payType);
        parameters.put("did", DeviceUtil.getDeviceId(activity));
        parameters.put("ext", ext);
        parameters.put("role_id", role_id);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("game_id", GameSdk.appId);
            object.put("uid", uid);
            object.put("product_name", product_name);
            object.put("product_id", product_id);
            object.put("role_id", role_id);
            object.put("amount", amount);
            object.put("attach", attach);
            object.put("is_debug", "0");
            object.put("pay_type", payType);
            object.put("did", DeviceUtil.getDeviceId(activity));
            object.put("ext", ext);
            object.put("t", t);
            object.put("sign", sign);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_CREATE, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 1) {
                        PayBean bean = new Gson().fromJson(json, PayBean.class);
                        if ("1".equals(payType)) {
                            if (isAliPayInstalled(activity)) {
                                trade_id = bean.getData().getTrade_id();
                                Intent intent = new Intent(context, WebActivity.class);
                                intent.putExtra("web_url", bean.getData().getPay_url());
                                activity.startActivity(intent);
                            } else {
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(activity, "请先安装支付宝", Toast.LENGTH_SHORT).show();
                                    payCallback.payFailed(msg);
                                    payPopup.dismiss();
                                });
                            }
                        } else {
                            if (isWxInstalled(activity)) {
                                trade_id = bean.getData().getTrade_id();
                                Intent intent = new Intent(context, WebActivity.class);
                                intent.putExtra("web_url", bean.getData().getPay_url());
                                activity.startActivity(intent);
                            } else {
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(activity, "请先安装微信", Toast.LENGTH_SHORT).show();
                                    payCallback.payFailed(msg);
                                    payPopup.dismiss();
                                });
                            }
                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            payCallback.payFailed(msg);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(String msg) {
                if (msg.indexOf("实名") != -1) {
                    new XPopup.Builder(context).dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false).autoFocusEditText(false)
                            .asCustom(new RealNamePopup(context, activity, 0, null, null)).show();
                }
                activity.runOnUiThread(() -> {
                    payCallback.payFailed(msg);
                });
            }

        });
    }

    public void searchOrder(Activity activity, String tradeId) {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("trade_id", tradeId);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, GameSdk.appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("trade_id", tradeId);
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {

        }
        HttpUtils.getInstance().post(Urls.POST_SEARCH, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 1) {
                        SearchOrderBean bean = new Gson().fromJson(json, SearchOrderBean.class);
                        if (bean.getData().getPay_status() == 0) {
                            activity.runOnUiThread(() -> {
                                payCallback.payFailed(msg);
                            });
                            Log.d(TAG, "支付失败:" + msg);
                        } else {
                            activity.runOnUiThread(() -> {
                                payCallback.paySuccess(msg);
                            });
                            Log.d(TAG, "支付成功:" + msg);

                        }
                    } else {
                        activity.runOnUiThread(() -> {
                            payCallback.payFailed(msg);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                activity.runOnUiThread(() -> {
                    payCallback.payFailed(msg);
                });
            }
        });
    }

    public boolean isAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    private boolean isWxInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        if (packageInfo != null) {
            for (int i = 0; i < packageInfo.size(); i++) {
                String pn = packageInfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void report(String role_name, String role_id, String server_name, String server_id
            , String diamonds, String online, String power, String level, Activity activity, ReportCallback reportCallback) {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String uid = String.valueOf((int) SpUtil.get(activity, SpUtil.UID, 0));
        String idfa = DeviceUtil.getDeviceId(activity);
        SortedMap<Object, Object> parameters = new TreeMap<>();
        parameters.put("game_id", GameSdk.appId);
        parameters.put("uid", uid);
        parameters.put("role_id", role_id);// 角色id
        parameters.put("role_name", role_name);// 角色名称
        parameters.put("server_id", server_id);
        parameters.put("server_name", server_name);
        parameters.put("diamonds", diamonds);// 钻石
        parameters.put("online", online);// 在线时长
        parameters.put("power", power);// 战力
        parameters.put("level", level);// 级别
        parameters.put("idfa", idfa);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, appkey);

        JSONObject object = new JSONObject();
        try {
            object.put("game_id", GameSdk.appId);
            object.put("uid", uid);
            object.put("role_id", role_id);
            object.put("role_name", role_name);
            object.put("server_id", server_id);
            object.put("server_name", server_name);
            object.put("diamonds", diamonds);
            object.put("online", online);
            object.put("power", power);
            object.put("level", level);
            object.put("idfa", idfa);
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {

        }
        HttpUtils.getInstance().post(Urls.POST_REPORT, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                if (code(json) == 1) {
                    reportCallback.reportSuccess("上传成功");
                } else {
                    reportCallback.reportSuccess(msg(json) + "");
                }
            }

            @Override
            public void onFailure(String msg) {
                reportCallback.reportFailed("上传失败");
            }
        });
    }

    public void getOrientation(Context context, int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(context, "横屏", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "竖屏", Toast.LENGTH_SHORT).show();
        }
    }


    public String msg(String json) {
        String msg = "";
        try {
            JSONObject jsonObject = new JSONObject(json);
            msg = jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public int code(String json) {
        int code = 0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return code;
    }

    public void timer(Activity activity) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                heartbeat(activity);
            }
        }, 1000 * 60 * 5);
    }

    public void heartbeat(Activity activity) {
        String idfa = DeviceUtil.getDeviceId(activity);
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String uid = String.valueOf((int) SpUtil.get(activity, SpUtil.UID, 0));
        long day_total_time = System.currentTimeMillis() / 1000 - inTime;
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("uid", uid);
        parameters.put("idfa", idfa);
        parameters.put("udid", idfa);
        parameters.put("game_id", appId);
        parameters.put("day_total_time", day_total_time);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("game_id", appId);
            object.put("idfa", idfa);
            object.put("udid", idfa);
            object.put("uid", uid);
            object.put("day_total_time", String.valueOf(day_total_time));
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_HEART_BEAT, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
                Log.d(TAG, "onSuccess: heart");
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 0)
                        new XPopup.Builder(activity).dismissOnBackPressed(false).dismissOnTouchOutside(false).asCustom(new TipPopup(activity, msg)).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    public void showTip(Activity activity) {
        new XPopup.Builder(activity).dismissOnBackPressed(false).dismissOnTouchOutside(false).asCustom(new TipPopup(activity, "1111111")).show();
    }

    public void savePhone(Activity activity, String phone) {
        String historyPhone = (String) SpUtil.get(activity, SpUtil.PHONE, "");
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(phone);
        builder.append(",");
        builder.append(historyPhone);
        Log.d("ListDataSave", builder.toString());
        SpUtil.put(activity, SpUtil.PHONE, builder.toString());
    }

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public void addDb(String phone, String passwd) {
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("passwd", passwd);
        long rowid = db.insert(DBHelper.TB_NAME, null, values);
        if (rowid == -1)
            Log.i("myDbDemo", "数据插入失败！");
        else
            Log.i("myDbDemo", "数据插入成功!" + rowid);
    }

    public void replacePhone(Activity activity, String newPhone) {
        ContentValues values = new ContentValues();
        values.put("phone", newPhone);
        String nowPasswd = (String) SpUtil.get(activity, SpUtil.PASSWD, "");
        String[] whereArgs = {nowPasswd};
        db.update(DBHelper.TB_NAME, values, "passwd=?", whereArgs);
    }

    public void replacePasswd(Activity activity, String newPasswd) {
        ContentValues values = new ContentValues();
        values.put("passwd", newPasswd);
        String nowPhone = (String) SpUtil.get(activity, SpUtil.MOBILE, "");
        String[] whereArgs = {nowPhone};
        db.update(DBHelper.TB_NAME, values, "phone=?", whereArgs);
    }


    Map<String, String> map = new HashMap<>();

    //查询数据
    public void dbFindAll() {
        String phone = "";
        String passwd = "";
        cursor = db.query(true, DBHelper.TB_NAME, null, null, null, "phone", null, null, null);
//        cursor = db.query(dbHelper.TB_NAME, null, null, null, null, null, "_id ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0);
            phone = cursor.getString(1);
            passwd = cursor.getString(2);
            cursor.moveToNext();
            map.put(phone, passwd);
        }
    }

    public void getInfo(Activity activity) {
        ApplicationInfo info = null;
        try {
            info = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        GameSdk.appId = String.valueOf(info.metaData.getInt("app_id"));
        GameSdk.appkey = info.metaData.getString("app_key");
        GameSdk.appOrient = info.metaData.getInt("app_orient");
    }

    public void exit(Activity activity) {
        String t = String.valueOf(System.currentTimeMillis() / 1000);
        String uid = String.valueOf((int) SpUtil.get(activity, SpUtil.UID, 0));
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("uid", uid);
        parameters.put("game_id", appId);
        parameters.put("t", t);
        String sign = DeviceUtil.createSign(parameters, appkey);
        JSONObject object = new JSONObject();
        try {
            object.put("game_id", appId);
            object.put("uid", uid);
            object.put("t", t);
            object.put("sign", sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.getInstance().post(Urls.POST_LOGOUT, object.toString(), new RequestListener() {
            @Override
            public void onSuccess(String json) {
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    public void getAll(Activity activity) {
        Map<String, ?> map = SpUtil.getAll(activity);
        for (int i = 0; i < map.size(); i++) {
            Log.d(TAG, "getAll: " + map);
        }
    }
}
