package com.nezha.gamelib.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nezha.gamelib.bean.AccountListBean;
import com.nezha.gamelib.bean.AutoBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by CH
 * on 2021/8/27 13:24
 * desc
 */
public class SpUtil {
    public static final String TOKEN = "token";
    public static final String IS_LOGIN = "isLogin";
    public static final String IMAGE = "image";
    public static final String NAME = "name";
    public static final String PASSWD = "passwd";
    public static final String MOBILE = "mobile";
    public static final String UID = "uid";
    public static final String QUICK_NAME = "quick_name";
    public static final String BANNER = "banner";//  	banner: 1是关闭 2是开启
    public static final String INFO_FLOW = "info_flow";//  	几条数据信息流
    public static final String IS_JIGUANG_LOGINED = "is_jiguang_logined";//
    public static final String IS_INIT = "is_init";
    public static final String ACCOUNT_LIST = "account_list";
    public static final String INDULGE = "indulge";
    public static final String AGE = "age";
    public static final String ISBINDID = "is_bind_idcard";
    public static final String ISSELE = "is_sele";
    public static final String PHONE = "his_phone";

    /**
     * SharedPreferences存储在sd卡中的文件名字
     */
    private static String getSpName() {
        return "game_sdk_sp";
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else {
            return null;
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE);
        return sp.getAll();
    }

    public static boolean saveAccount(Context context, AutoBean bean) {
        AccountListBean accountList = getAccountList(context);
        if (accountList != null) {
            List<AutoBean> list = accountList.getList();
            boolean needAdd = true;
            for (int i = 0; i < list.size(); i++) {
                AutoBean autoBean = list.get(i);
                if (autoBean.getData().getUid() == bean.getData().getUid()) {
                    needAdd = false;
                } else {

                }
            }
            if (needAdd) {
                list.add(bean);
                accountList.setList(list);
            }
        } else {
            accountList = new AccountListBean();
            List<AutoBean> autoBeans = new ArrayList<>();
            autoBeans.add(bean);
            accountList.setList(autoBeans);
        }

        final Gson gson = new Gson();
        final String jsonStr = gson.toJson(accountList);
        return context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE).edit().putString(SpUtil.ACCOUNT_LIST, jsonStr).commit();
    }

    public static AccountListBean getAccountList(Context context) {
        final Gson gson = new Gson();
        final String jsonStr = context.getSharedPreferences(getSpName(), Context.MODE_PRIVATE).getString(SpUtil.ACCOUNT_LIST, "");
        return gson.fromJson(jsonStr, new TypeToken<AccountListBean>() {
        }.getType());
    }
}
