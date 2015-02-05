package com.zhidian.issueSDK.api;

import java.util.HashMap;
import java.util.Map;

import com.zhidian.issueSDK.net.HttpEngine;
import com.zhidian.issueSDK.net.StringRequest;
import com.zhidian.issueSDK.service.InitService;
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2015年1月30日
 */
public class UserInfoApi extends StringRequest {

	public String uid;
	public String session;
	public String appId;
	public String platformId;
	public String zdappId;
	
	
	@Override
	public String getUrl() {
		return Url.BASE_URL + Url.GET_USERINFO_URL;
	}

	@Override
	public HttpEngine.Method getMethod() {
		return HttpEngine.Method.POST;
	}
	
	@Override
	public Map<String, String> getParams() {
		Map paramMap = new HashMap();
		paramMap.put("uid", uid);
		paramMap.put("session", session);
		paramMap.put("appId", appId);
		paramMap.put("platformId", platformId);
		paramMap.put("zdappId", zdappId);
		return paramMap;
	}
}
