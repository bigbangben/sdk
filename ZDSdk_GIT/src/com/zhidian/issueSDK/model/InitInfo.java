package com.zhidian.issueSDK.model;

import android.content.Context;

/**
 * @Description 初始化参数
 * @author ZengQBo
 * @time 2014年12月23日
 * 
 */
public class InitInfo {
	private int appId;
	private String appKey;
	private int screenOrientation;
	private Context ctx;
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public int getScreenOrientation() {
		return screenOrientation;
	}
	public void setScreenOrientation(int screenOrientation) {
		this.screenOrientation = screenOrientation;
	}
	public Context getCtx() {
		return ctx;
	}
	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	

}
