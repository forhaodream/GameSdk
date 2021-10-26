package com.nezha.gamelib.network;

/**
 * Created by CH
 * on 2021/9/2 14:00
 * desc
 */
public interface RequestListener {

    void onSuccess( String json);

    void onFailure(String msg);
}
