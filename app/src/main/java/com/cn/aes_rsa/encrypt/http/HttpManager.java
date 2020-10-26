package com.cn.aes_rsa.encrypt.http;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author LinDingQiang
 * @time 2020/8/3 10:15
 * @email dingqiang.l@verifone.cn
 */
public class HttpManager {

    private static class SingletonHolder {
        private static final HttpManager INSTANCE = new HttpManager();
    }

    private HttpManager() {

    }

    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 初始化并且获取OkHttp
     *
     * @return OkHttpClient
     */
    private OkHttpClient getOkHttp() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * request 构建
     *
     * @return Request
     */
    private Request postRequest(String json) {
        Request.Builder builder = new Request.Builder()
                .url("http://192.168.43.209:8080/encrypt/upp0013")
                .post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), json));
        return builder.build();
    }

    /**
     * POST请求，以json串的形式进行传递
     *
     */
    public void postJson( String json, Callback callBack) {
        getOkHttp().newCall(postRequest(json)).enqueue(callBack);
    }


}
