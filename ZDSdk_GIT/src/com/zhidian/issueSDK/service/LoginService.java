package com.zhidian.issueSDK.service;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.util.SDKLog;

/**
 * @Description
 * @author ZengQBo
 * @time 2014年12月15日
 */
public class LoginService {
	private static final String TAG = "LoginService";
	private Iplatform iplatform;
	private Activity mActivity;
	private ICallback callback;
	protected GameInfo gameInfo;
	public static boolean isLogin = false;
	public static String loginTime = "";

	public interface GameLoginListener {
		public void LoginSuccess(UserInfoModel model);

		public void LoginFail(String value);
	}

	public LoginService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void login(ICallback callback) {
		this.callback = callback;
		iplatform.login(mActivity, listener);
	}

	private GameLoginListener listener = new GameLoginListener() {

		@Override
		public void LoginSuccess(UserInfoModel model) {
			//model.id = iplatform.getPlatformId() + "_" + model.id;
			Log.e(TAG, model.sessionId + "--->" + model.id);
			LoginService.isLogin = true;
			InitService.mUserInfoModel = model;
			if (callback != null) {
				SDKLog.e("msg", "Login Success");
				callback.loginSuccess(model);
			}else {
				Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
				.show();
			}
		}

		@Override
		public void LoginFail(String value) {
			if (callback != null) {
				SDKLog.e("msg", "Login Failed >> " + value);
				callback.onError(ICallback.LOGIN, "Login Failed");
			}else {
				Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
				.show();
			}
		}
	};

}
