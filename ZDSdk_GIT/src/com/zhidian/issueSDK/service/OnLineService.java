/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:07:46
 */
package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.zhidian.issueSDK.api.OnLineApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.util.PhoneInformation;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2014年12月15日
 */
public class OnLineService implements Runnable {

	private static final String TAG = "OnLineService";
	private Iplatform iplatform;
	private Context mContext;
	private Handler mHandler;
	private GameInfo model;

	/**
	 * @param context  
	 * @param mHandler
	 * @param iplateform
	 * @param model 
	 */
	public OnLineService(Context context, Handler mHandler, Iplatform iplateform, GameInfo model) {
		this.iplatform = iplateform;
		this.mContext = context;
		this.mHandler = mHandler;
		this.model = model;
	}

	public void onLine(GameInfo model) {
		PhoneInformation phoneInformation = new PhoneInformation(mContext);
		OnLineApi api = new OnLineApi();
		api.appId = SDKUtils.getAppId(mContext);
		api.platformId = iplatform.getPlatformId();
		api.uid = InitService.mUserInfoModel.id;
		api.zoneId = model.getZoneId();
		api.roleId = model.getRoleId();
		api.loginTime = LoginService.loginTime;
	api.deviceId = phoneInformation.getDeviceCode();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);
	}

	private JsonResponse jsonResponse = new JsonResponse() {
		@Override
		public void requestError(String string) {
			super.requestError(string);
		}

		@Override
		public void requestSuccess(JSONObject jsonObject) {
			int code = jsonObject.optInt("code");
			if (code == 0) {
				//online success
				SDKLog.e(TAG, jsonObject.toString());
			} else {
				//online fail
			}
		}
	};

	@Override
	public void run() {
		if (LoginService.isLogin) {
			onLine(model);
			mHandler.postDelayed(this, 60 * 1000);
		}
	}
}
