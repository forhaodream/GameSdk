package com.nezha.gamelib.callback;

import com.nezha.gamelib.bean.AutoBean;

/**
 * Created by CH
 * on 2021/8/26 16:49
 * desc
 */
public interface LoginCallback {

    void loginSuccess(AutoBean bean, String msg);

    void loginFailed(String msg);

    void logOut(String s);


}
