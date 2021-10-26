package com.nezha.gamelib.network;

import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CH
 * on 2021/8/27 15:18
 * desc
 */
public abstract class NetCallback<T> {

    public Type type;


    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public NetCallback() {
        type = getSuperclassTypeParameter(getClass());
    }

    //    public abstract void onSuccess(String json, T result);
    public abstract void onSuccess(String json);

    public abstract void onFailure(String msg);

}
