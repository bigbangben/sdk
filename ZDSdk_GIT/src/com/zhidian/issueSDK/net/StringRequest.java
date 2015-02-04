package com.zhidian.issueSDK.net;

import java.util.Map;

/**
 * Created by Administrator on 2014/12/8.
 */
public abstract class StringRequest implements Request {
    HttpEngine.Method method  = HttpEngine.Method.GET;
    private IHttpResponse mResponse ;
    @Override
    public abstract String getUrl();

    @Override
    public HttpEngine.Method getMethod() {
        return method;                       
    }
    public void setResponse(IHttpResponse response){
       this.mResponse = response ;
    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public IHttpResponse getHttpResponse() {
        return mResponse;
    }
}
