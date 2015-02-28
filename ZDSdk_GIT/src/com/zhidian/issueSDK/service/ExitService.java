/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:33:51
 */
package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.ExitApi;
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
public class ExitService {
	private Iplatform iplatform;
	private Activity mActivity;
	private ICallback callback;
	private GameExitListener listener = new GameExitListener() {
		
		@Override
		public void onSuccess() {
			exit(gameInfo);
		}
		
		@Override
		public void onFail(String value) {
			
		}
	};
	private GameInfo gameInfo;
	
	public interface GameExitListener {
		public void onSuccess();

		public void onFail(String value);
	}
	
	

	public ExitService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void exit(GameInfo gameInfo, ICallback callback) {
		this.callback = callback;
		this.gameInfo = gameInfo;
		iplatform.exit(mActivity,listener);
		

	}

	private void exit(GameInfo model) {
		
			sendToSDKServer(model);
		}

	private void sendToSDKServer(GameInfo model) {
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		ExitApi api = new ExitApi();
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
			}

			@Override
			public void requestSuccess(JSONObject jsonObject) {
				int code = jsonObject.optInt("code");
			      if (callback != null) {
			    	  if (code == 0) {
							cleanCach();
							callback.exitSuccess();
							SDKLog.e("", "Exit Success");
						} else {
							callback.onError(ICallback.EXIT, "Exit failed");
						}
					}else {
						Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
						.show();
					}
		        }
		};
		
		private void cleanCach() {
			LoginService.loginTime = "";
			LoginService.isLogin = false;
			InitService.mUserInfoModel = null;
		}
		
	}


