package com.zhidian.issueSDK.net;

import java.util.Map;

/**
 * Created by Administrator on 2014/12/8.
 */
public interface Request {
    public HttpEngine.Method getMethod() ;
    public String getUrl() ;
    public Map<String,String> getParams() ;
    public IHttpResponse getHttpResponse() ;
}
