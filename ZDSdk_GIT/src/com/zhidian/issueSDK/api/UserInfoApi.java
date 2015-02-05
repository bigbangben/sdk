package com.zhidian.issueSDK.api;

import java.util.HashMap;
import java.util.Map;

import com.zhidian.issueSDK.net.HttpEngine;
import com.zhidian.issueSDK.net.StringRequest;

/**
 * @Description
 * @author ZengQBo
 * @time 2015年1月30日
 */
public class UserInfoApi extends StringRequest {

	public String sid;
	public String gameId;
	public String appId;
	public String platformId;
	
	
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
		paramMap.put("sid", sid);
		paramMap.put("gameId", gameId);
		paramMap.put("appId", appId);
		paramMap.put("platformId", platformId);
		return paramMap;
	}
}
