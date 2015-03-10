package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.LoginApi;
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
 * @time 2014年12月25日
 */
public class SetGameInfoService {
	private Activity mActivity;
	private Iplatform iplatform;
	private ICallback callback;
	private GameInfo gameInfo;
	private boolean showFloat;

	public interface SetGameInfoListener {
		public void onSuccess();

		public void onFail(String value);
	}

	public SetGameInfoService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void setGameInfo(GameInfo gameInfo, boolean showFloat,
			ICallback callback) {
		this.callback = callback;
		this.gameInfo = gameInfo;
		this.showFloat = showFloat;
		iplatform.setGameInfo(mActivity, gameInfo, listener);

	}

	private SetGameInfoListener listener = new SetGameInfoListener() {

		@Override
		public void onSuccess() {
			loginSDKServer(gameInfo);
		}

		@Override
		public void onFail(String value) {
			if (callback != null) {
				SDKLog.e("msg", "Set Game Information Failed >> " + value);
				callback.onError(ICallback.UPLOAD_GAME_INFO, "Set Game Information Failed");
			}
		}
	};

	private void loginSDKServer(GameInfo model) {
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		LoginApi api = new LoginApi();
		// api.appId = SDKUtils.getAppId(mActivity);
		api.appId = SDKUtils.getAppId(mActivity);
		api.platformId = iplatform.getPlatformId();
		api.uid = InitService.mUserInfoModel.id;
		api.zoneId = model.getZoneId();
		api.zoneName = model.getZoneName();
		api.roleId = model.getRoleId();
		api.roleName = model.getRoleName();
		api.roleLevel = model.getRoleLevel();
		api.deviceId = phoneInformation.getDeviceCode();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);

	}

	private JsonResponse jsonResponse = new JsonResponse() {
		@Override
		public void requestError(String string) {
			super.requestError(string);
			if (callback != null) {
				SDKLog.e("msg", "Set Game Information Failed >> " + string);
				callback.onError(ICallback.UPLOAD_GAME_INFO, "Set Game Information Failed");
			}
		}

		@Override
		public void requestSuccess(JSONObject jsonObject) {
			int code = jsonObject.optInt("code");
		    if (callback != null) {
		    	if (code == 0) {
					LoginService.loginTime = jsonObject.optString("loginTime");
					// 启动心跳
					Handler h = new Handler();
					SDKLog.e("msg", "Set Game Information Success");
					callback.setGameInfoSuccess(LoginService.loginTime);
					h.post(new OnLineService(mActivity, h, iplatform, gameInfo));
					if (showFloat) {
						// 显示浮动工具栏
						iplatform.showFloat(mActivity);
					}
				} else {
					SDKLog.e("msg", "Set Game Information Failed >> " + jsonObject.toString());
					callback.onError(ICallback.UPLOAD_GAME_INFO,
							"Set Game Information Failed");
				}
			}else {
				Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
				.show();
			}
		
		}
	};

}
