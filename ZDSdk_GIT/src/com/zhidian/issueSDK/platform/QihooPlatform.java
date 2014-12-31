package com.zhidian.issueSDK.platform;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.InitInfo;
import com.zhidian.issueSDK.model.QihooUserInfo;
import com.zhidian.issueSDK.model.TokenInfo;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;
import com.zhidian.issueSDK.util.ProgressUtil;
import com.zhidian.issueSDK.util.QihooUserInfoListener;
import com.zhidian.issueSDK.util.QihooUserInfoTask;
import com.zhidian.issueSDK.util.SDKLog;

public class QihooPlatform implements Iplatform {

	protected static final String TAG = "QihooPlatform";
	protected static int orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	protected static boolean isAccessTokenValid = true;
	// 登录返回的TokenInfo
	private TokenInfo mTokenInfo;
	private QihooUserInfoTask mUserInfoTask;
	private Activity mActivity;

	// 进度等待框
	private ProgressDialog mProgress;
	private GameLoginListener gameLoginListener;

	@Override
	public String getPlatformId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(InitInfo initInfo, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		orientation = initInfo.getScreenOrientation();
		gameInitListener.initSuccess(false, null);

	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		this.mActivity = activity;
		this.gameLoginListener = gameLoginListener;
		// 使用360SDK登录接口（横屏）
		doSdkLogin((orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ? true
				: false);
	}

	@Override
	public void showFloat(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, OrderGenerateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createRole(GameInfo gameInfo, CreateRoleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameInfo(GameInfo gameInfo, SetGameInfoListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean suportLogoutUI() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

	/**
	 * 使用360SDK的登录接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 * @param isBgTransparent
	 *            是否以透明背景显示登录界面
	 */
	protected void doSdkLogin(boolean isLandScape) {

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// *** 以下非界面相关参数 ***

		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
		bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

		// 必需参数，使用360SDK的登录模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_LOGIN);

		Intent intent = new Intent(mActivity, ContainerActivity.class);
		intent.putExtras(bundle);

		Matrix.invokeActivity(mActivity, intent, new IDispatcherCallback() {
			@Override
			public void onFinished(String data) {
				SDKLog.e(TAG, "mLoginCallback, data is " + data);
				procGotTokenInfoResult(data);
			}
		});
	}

	// ---------------------------------360SDK接口的回调-----------------------------------

	private void procGotTokenInfoResult(String data) {
		boolean isCallbackParseOk = false;

		if (!TextUtils.isEmpty(data)) {
			JSONObject jsonRes;

			try {
				jsonRes = new JSONObject(data);
				// error_code 状态码： 0 登录成功， -1 登录取消， 其他值：登录失败
				int errorCode = jsonRes.optInt("error_code");
				String dataString = jsonRes.optString("data");

				switch (errorCode) {
				case 0:
					TokenInfo tokenInfo = TokenInfo.parseJson(dataString);
					if (tokenInfo != null && tokenInfo.isValid()) {
						isCallbackParseOk = true;
						isAccessTokenValid = true;
						onGotTokenInfo(tokenInfo);
					}

					break;

				case -1:
					return;

				default:
					if (!TextUtils.isEmpty(dataString)) {
					}
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (!isCallbackParseOk) {
			// Toast.makeText(this, R.string.get_token_fail,
			// Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 通过此方法返回UserInfo
	 */
	public void onGotTokenInfo(TokenInfo tokenInfo) {
		if (tokenInfo != null && tokenInfo.isValid()) {
			mTokenInfo = tokenInfo;

			isAccessTokenValid = true;

			mUserInfoTask = QihooUserInfoTask.newInstance();

			// 提示用户进度
			/*
			 * mProgress = ProgressUtil.show(this, R.string.get_user_info_title,
			 * R.string.get_user_info_message, new OnCancelListener() {
			 * 
			 * @Override public void onCancel(DialogInterface dialog) { if
			 * (mUserInfoTask != null) { mUserInfoTask.doCancel(); } } });
			 */

			// 请求应用服务器，用AccessToken换取UserInfo
			mUserInfoTask.doRequest(mActivity, tokenInfo.getAccessToken(),
					Matrix.getAppKey(mActivity), new QihooUserInfoListener() {

						@Override
						public void onGotUserInfo(QihooUserInfo userInfo) {

							ProgressUtil.dismiss(mProgress);

							if (userInfo == null) {
								// Toast.makeText(this, R.string.get_user_fail,
								// Toast.LENGTH_LONG).show();
							} else {
								if (!userInfo.isValid()) {
									if (TextUtils.isEmpty(userInfo.getError())) {
										// Toast.makeText(this,
										// R.string.get_user_fail,
										// Toast.LENGTH_LONG).show();
									} else {
										// Toast.makeText(this,
										// userInfo.getError(),
										// Toast.LENGTH_LONG).show();
									}
								} else {
									//gameLoginListener.LoginSuccess(model);
									// startFlowTestPayActivity(mTokenInfo,
									// userInfo);
								}
							}

						}
					});
		} else {
			/*
			 * ProgressUtil.dismiss(mProgress); Toast.makeText(this,
			 * R.string.get_token_fail, Toast.LENGTH_LONG) .show();
			 */
		}
	}
}
