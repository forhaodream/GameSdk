package com.nezha.gamelib.network;

import android.util.Log;

import com.google.gson.Gson;
import com.lxj.xpopup.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by CH
 * on 2021/8/27 09:20
 * desc
 */
public class HttpUtils {
    private final OkHttpClient okHttpClient;


    private HttpUtils() {
        //token拦截器
        final Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("token", "werfdwsadfdsaxdfbg").build();
                return chain.proceed(request);
            }
        };
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
    }

    private static HttpUtils httpUtils = null;

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            synchronized (Object.class) {
                if (httpUtils == null) {
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }

    public void post(String url, String postBody, final RequestListener callBack) {
        MediaType MEDIA_TYPE_TEXT = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_TEXT, postBody))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure(e.getMessage());
                Log.d("HttpUtils", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callBack.onSuccess(response.body().string());
//                JSONObject jsonObject = null;
//                try {
//                    String resultStr = response.body().string();
//                    Log.d("接口返回-------", resultStr);
//                    jsonObject = new JSONObject(resultStr);
//                    int code = jsonObject.getInt("code");
//                    String msg = jsonObject.getString("msg");
//
//                    if (code == 1) {
//                        if (data != null) {
//                            Gson gson = new Gson();
//                            Object obj = gson.fromJson(resultStr, callBack.type);
//                            callBack.onSuccess(resultStr, obj);
//                        }
//                    } else {
//                        callBack.onFailure(msg);
//                    }
//                } catch (JSONException e) {
//                }

            }
        });
    }


}
