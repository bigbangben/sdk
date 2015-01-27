package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.InitApi;
import com.zhidian.issueSDK.location.InitLBS;
import com.zhidian.issueSDK.model.InitInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.util.PhoneInformation;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * Created by Administrator on 2014/12/11.
 */
public class InitService {
	private static final String TAG = "InitService";
	private Iplatform iplatform;
	private Activity mActivity;
	private ICallback callback;
	private boolean logined = false;
	public static UserInfoModel mUserInfoModel;
	public static String location = "";
	public static String latitued = "";
	public static String lontitued = "";

	public interface GameInitListener {
		public void initSuccess(boolean hasAutoLogin, UserInfoModel model);

		public void initFail(String value);
	}

	public InitService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
		// 获取地理位置信息
		new InitLBS().initLBS(activity);
	}

	public void init(ICallback callback) {
		this.callback = callback;
		initPlatform();
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
				callback.initSuccess();
				if (logined) {
					LoginService.isLogin = true;
					callback.loginSuccess(mUserInfoModel);
				}
			} else {
				callback.onError(ICallback.INIT, jsonObject.toString());
			}
		}
	};

	private GameInitListener gameInitListener = new GameInitListener() {
		@Override
		public void initSuccess(boolean hasAutoLogin, UserInfoModel model) {
			logined = hasAutoLogin;
			if (hasAutoLogin) {
				SDKLog.e(TAG, "AutoLogin");
				mUserInfoModel = model;
			}
			initSDKServer();

		}

		@Override
		public void initFail(String value) {
			callback.onError(ICallback.INIT, value);
		}
	};
	

	private GameLoginListener gameLoginListener = new GameLoginListener() {

		@Override
		public void LoginSuccess(UserInfoModel model) {
			Log.e(TAG, model.sessionId + "--->" + model.id);
			LoginService.isLogin = true;
			InitService.mUserInfoModel = model;
			callback.loginSuccess(model);
		}

		@Override
		public void LoginFail(String value) {
			callback.onError(ICallback.LOGIN, value);
		}
	};

	private void initPlatform() {
		iplatform.init(mActivity,gameInitListener, gameLoginListener);
	}

	private void initSDKServer() {
		Log.e(TAG, "======= initSDKServer =======");// FIXME
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		InitApi api = new InitApi();
		api.appId = SDKUtils.getAppId(mActivity);
		api.deviceId = phoneInformation.getDeviceCode();
		api.imsi = phoneInformation.getImsi();
		api.latitude = latitued;
		api.longitude = lontitued;
		api.location = location;
		api.manufacturer = phoneInformation.getBrand();
		api.model = phoneInformation.getPhoneModel();
		api.networkCountryIso = phoneInformation.getNetworkCountryIso();
		api.phonetype = phoneInformation.getPhoneType();
		api.networkType = phoneInformation.getNetworkType();
		api.platform = phoneInformation.getReleaseVersion();
		api.resolution = phoneInformation.getResolution();
		api.platformId = iplatform.getPlatformId();
		api.simoperatorname = phoneInformation.getSimOperatorName();
		api.systemVersion = phoneInformation.getSdkVersion();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);
	}
}
