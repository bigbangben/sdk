/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:21:09
 */
package com.zhidian.issueSDK.api;

import java.util.HashMap;
import java.util.Map;

import com.zhidian.issueSDK.net.HttpEngine;
import com.zhidian.issueSDK.net.StringRequest;

/**
 * @Description 
 * @author ZengQBo
 * @time 2014年12月15日
 */
public class OnLineApi extends StringRequest {
	
	public String appId;
	public String platformId;
	public String uid;
	public String zoneId;
	public String roleId;
	public String loginTime;
	public String deviceId;
	public final String clientType = "1";


	/* (non-Javadoc)
	 * @see com.zhidian.issueSDK.net.StringRequest#getUrl()
	 */
	@Override
	public String getUrl() {
		return Url.BASE_URL + Url.HEARTBEAT_URL;
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
		paramMap.put("roleId", roleId);
		paramMap.put("loginTime", loginTime);
		paramMap.put("deviceId", deviceId);
		paramMap.put("clientType", clientType);
		return paramMap;
	}

}
