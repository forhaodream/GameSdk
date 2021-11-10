package com.nezha.gamelib.callback;

/**
 * Created by CH
 * on 2021/8/30 16:19
 * desc 角色上传回调
 */
public interface ReportCallback {

    void reportSuccess(String msg);

    void reportFailed(String msg);
}
