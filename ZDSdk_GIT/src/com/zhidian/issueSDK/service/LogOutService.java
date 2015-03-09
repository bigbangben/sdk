/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:07:46
 */
package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;

import android.widget.Toast;
import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.LogoutApi;
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
public class LogOutService {

	private Iplatform iplatform;
	private Activity mActivity;
	private ICallback callback;
	private GameInfo gameInfo;

	public interface GameLogoutListener {
		public void logoutSuccess();

		public void logoutFail(String value);
	}

	public LogOutService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void logout(GameInfo gameInfo, ICallback callback) {
		this.callback = callback;
		this.gameInfo = gameInfo;
		iplatform.logOut(mActivity, listener);

	}

	private void logout(GameInfo model) {
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		LogoutApi api = new LogoutApi();
		api.appId = SDKUtils.getAppId(mActivity);
		api.platformId = iplatform.getPlatformId();
		api.uid = InitService.mUserInfoModel.id;
		api.zoneId = model.getZoneId();
		api.roleId = model.getRoleId();
		api.deviceId = phoneInformation.getDeviceCode();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);
	}

	private JsonResponse jsonResponse = new JsonResponse() {
		@Override
		public void requestError(String string) {
			super.requestError(string);
			if (callback != null) {
				SDKLog.e("msg", "Logout Failed >> " + string);
				callback.onError(ICallback.LOGOUT, "Logout Failed");
			}
		}

		@Override
		public void requestSuccess(JSONObject jsonObject) {
			int code = jsonObject.optInt("code");
			if (callback != null) {
				if (code == 0) {
					cleanCach();
					SDKLog.e("msg", "Logout Success");
					callback.logoutSuccess();
				} else {
					SDKLog.e("msg", "Logout Failed");
					callback.onError(ICallback.LOGOUT, "Logout Failed");
				}
			} else {
				Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	private GameLogoutListener listener = new GameLogoutListener() {

		@Override
		public void logoutSuccess() {
			logout(gameInfo);
		}

		@Override
		public void logoutFail(String value) {
			if (callback != null) {
				SDKLog.e("msg", "Logout Failed >> " + value);
				callback.onError(ICallback.LOGOUT, "Logout Failed");
			}
		}
	};

	private void cleanCach() {
		LoginService.loginTime = "";
		LoginService.isLogin = false;
		InitService.mUserInfoModel = null;
	}
}
