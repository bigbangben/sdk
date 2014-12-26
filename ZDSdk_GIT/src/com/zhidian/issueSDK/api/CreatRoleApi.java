package com.zhidian.issueSDK.api;

import java.util.HashMap;
import java.util.Map;

import com.zhidian.issueSDK.net.HttpEngine;
import com.zhidian.issueSDK.net.StringRequest;

/**
 * @Description
 * @author ZingQBo
 * @time 2014年12月15日
 */
public class CreatRoleApi extends StringRequest {
	public String appId;
	public String platformId;
	public String uid;
	public String zoneId;
	public String zoneName;
	public String roleId;
	public String roleName;
	public String roleLevel;
	public String deviceId;
	public final String clientType = "1";

	@Override
	public String getUrl() {
		return Url.BASE_URL + Url.ROLE_ESTABLISH_URL;
	}

	@Override
	public HttpEngine.Method getMethod() {
		return HttpEngine.Method.POST;
	}

	@Override
	public Map<String, String> getParams() {
		Map paramMap = new HashMap();
		paramMap.put("appId", appId);
		paramMap.put("platformId", platformId);
		paramMap.put("uid", uid);
		paramMap.put("zoneId", zoneId);
		paramMap.put("zoneName", zoneName);
		paramMap.put("roleId", roleId);
		paramMap.put("roleName", roleName);
		paramMap.put("roleLevel", roleLevel);
		paramMap.put("deviceId", deviceId);
		paramMap.put("clientType", clientType);
		return paramMap;
	}

}
