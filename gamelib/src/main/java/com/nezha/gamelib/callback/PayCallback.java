package com.nezha.gamelib.callback;

/**
 * Created by CH
 * on 2021/8/26 16:53
 * desc
 */
public interface PayCallback {

    void paySuccess(String s);

    void payFailed(String s);

    void payCancel(String s);
}
