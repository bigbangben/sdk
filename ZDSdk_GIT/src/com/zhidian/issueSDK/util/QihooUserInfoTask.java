
package com.zhidian.issueSDK.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.zhidian.issueSDK.api.Url;
import com.zhidian.issueSDK.model.QihooUserInfo;

/***
 * 此类使用Access Token，请求您的应用服务器，获取QihooUserInfo。
 * （注：应用服务器由360SDK使用方自行搭建，用于和360服务器进行安全交互，具体协议请查看文档中，服务器端接口）。
 */
public class QihooUserInfoTask {

    private static final String TAG = "QihooUserInfoTask";

    private SdkHttpTask sSdkHttpTask;

    public static QihooUserInfoTask newInstance(){
        return new QihooUserInfoTask();
     }

    public void doRequest(Context context, Map<String, String> param,
            final QihooUserInfoListener listener) {
    	  String mParam = paramMapToString(param);
        // DEMO使用的应用服务器url仅限DEMO示范使用，禁止正式上线游戏把DEMO应用服务器当做正式应用服务器使用，请使用方自己搭建自己的应用服务器。
        String url = Url.BASE_URL + Url.GET_USERINFO_URL + "?" + mParam;

        // 如果存在，取消上一次请求
        if (sSdkHttpTask != null) {
            sSdkHttpTask.cancel(true);
        }

        // 新请求
        sSdkHttpTask = new SdkHttpTask(context);
        sSdkHttpTask.doGet(new SdkHttpListener() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse=" + response);
                QihooUserInfo userInfo = QihooUserInfo.parseJson(response);
                listener.onGotUserInfo(userInfo);
                sSdkHttpTask = null;
            }

            @Override
            public void onCancelled() {
                listener.onGotUserInfo(null);
                sSdkHttpTask = null;
            }

        }, url);

        Log.d(TAG, "url=" + url);
    }

    public boolean doCancel() {
        return (sSdkHttpTask != null) ? sSdkHttpTask.cancel(true) : false;
    }
    
    private String paramMapToString(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        String value = "";
        try {
			for (String key : param.keySet()) {
				Log.e("", key.trim() + " ======= " + param.get(key));// FIXME
				value = param.get(key);
				if (value != null) {
					value = URLEncoder.encode(value, "UTF-8");
				}
				sb.append(key.trim()).append("=").append(value).append("&");
			}
        	return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
