package com.cn.aes_rsa.encrypt.http;

import android.text.TextUtils;
import android.util.Log;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author LinDingQiang
 * @time 2020/8/3 11:29
 * @email dingqiang.l@verifone.cn
 */
public abstract class NetWorkCallBack implements Callback {

    /**
     * 成功回调
     *
     * @param response 成功信息
     */
    public abstract void onSuccess(String response);

    /**
     * 异常回调
     */
    public abstract void onError(String e);

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        Log.i("TAG", "response code =" + response.code() + "\nresponse message=" + response.message());
        String responseMsg = response.message();
        int code = response.code();
        if (TextUtils.isEmpty(responseMsg)) {
            responseMsg = "通讯异常";
        }
        if (!response.isSuccessful()) {
            onError(responseMsg);
            return;
        }
        ResponseBody responseBody = response.body();

        String result = responseBody.string();
        onSuccess(result);
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException throwable) {
        String detailMessage = "失败";
        Log.i("TAG", "response message=" + throwable.getMessage());
        if (throwable instanceof SocketTimeoutException) {
            detailMessage = "访问超时";
        } else if (throwable instanceof BindException) {
            detailMessage = "套接字绑定错误";
        } else if (throwable instanceof ProtocolException) {
            detailMessage = "协议错误";
        } else if (throwable instanceof ConnectException) {
            detailMessage = "网络连接失败";
        } else if (throwable instanceof HttpRetryException) {
            detailMessage = "请重试";
        } else if (throwable instanceof MalformedURLException) {
            detailMessage = "URL出错";
        } else if (throwable instanceof NoRouteToHostException) {
            detailMessage = "无法连接到主机";
        } else if (throwable instanceof PortUnreachableException) {
            detailMessage = "ICMP端口不可达";
        } else if (throwable instanceof SocketException) {
            detailMessage = "底层协议错误";
        } else if (throwable instanceof UnknownHostException) {
            detailMessage = "无法找到主机";
        } else if (throwable instanceof UnknownServiceException) {
            detailMessage = "未知服务异常";
        } else if (throwable instanceof SSLHandshakeException) {
            detailMessage = "SSL握手失败";
        } else if (detailMessage != null && detailMessage.length() > 0) {
            detailMessage = "未知通讯错误" + throwable.toString();
        }
        onError(detailMessage);
    }
}
