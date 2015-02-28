package com.zhidian.issueSDK.api;

import java.util.HashMap;
import java.util.Map;

import com.zhidian.issueSDK.net.HttpEngine;
import com.zhidian.issueSDK.net.StringRequest;

/**
 * Created by Administrator on 2014/12/11.
 */
public class InitApi extends StringRequest {
    public String appId;
    public String platformId;
    public String deviceId;
    public final String  clientType = "1";
    public String manufacturer;
    public String model;
    public String systemVersion;
    public String platform;
    public String latitude;
    public String longitude;
    public String imsi;
    public String location;
    public String networkCountryIso;
    public String networkType;
    public String phonetype;
    public String simoperatorname;
    public String resolution;

    @Override
    public String getUrl() {
        return Url.BASE_URL + Url.INIT_URL;
    }

    @Override
    public HttpEngine.Method getMethod() {
        return HttpEngine.Method.POST;
    }

    @Override
    public Map<String, String> getParams() {
        Map paramMap = new HashMap();
        paramMap.put("appId",appId);
        paramMap.put("platformId",platformId);
        paramMap.put("clientType",clientType);
        paramMap.put("deviceId",deviceId);
        paramMap.put("imsi",imsi);
        paramMap.put("latitude",latitude);
        paramMap.put("longitude",longitude);
        paramMap.put("location",location);
        paramMap.put("manufacturer",manufacturer);
        paramMap.put("model",model);
        paramMap.put("networkCountryIso",networkCountryIso);
        paramMap.put("networkType",networkType);
        paramMap.put("phonetype",phonetype);
        paramMap.put("platform",platform);
        paramMap.put("resolution",resolution);
        paramMap.put("simoperatorname",simoperatorname);
        paramMap.put("systemVersion",systemVersion);
        return paramMap;
    }
}
