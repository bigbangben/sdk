package com.zhidian.issueSDK.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2014/12/8.
 */
public class JsonResponse implements IHttpResponse {
    @Override
    public final void response(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str) ;
           requestSuccess(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            requestError(str);
        }
    }

    public void requestError(String string){

    }

    public void requestSuccess(JSONObject jsonObject){

    }
}
