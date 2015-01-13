package com.zhidian.issueSDK.platform;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.InitInfo;
import com.zhidian.issueSDK.model.QihooPayInfo;
import com.zhidian.issueSDK.model.QihooUserInfo;
import com.zhidian.issueSDK.model.TokenInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
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
	protected Activity mActivity = null;
	// 登录返回的TokenInfo
	private TokenInfo mTokenInfo;
	private QihooUserInfoTask mUserInfoTask;

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
			GameInfo model, String notifyUri, OrderGenerateListener listener) {
		this.mActivity = activity;
		String name = activity.getApplicationInfo().packageName;
		String moneyAmount = String.valueOf(Integer.parseInt(money) * 100);
		QihooPayInfo pay = new QihooPayInfo();
		pay.setAccessToken(mTokenInfo.getAccessToken());
		pay.setQihooUserId(model.getRoleId());
		pay.setMoneyAmount(moneyAmount);
		pay.setExchangeRate("1");
		pay.setProductName("商品");
		pay.setProductId("1111");
		pay.setNotifyUri(order);
		
		doSdkPay((orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ? true
				: false,true,pay);

	}

	@Override
	public void createRole(GameInfo gameInfo, CreateRoleListener listener) {
		listener.onSuccess();
	}

	@Override
	public void setGameInfo(GameInfo gameInfo, SetGameInfoListener listener) {
		listener.onSuccess();
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

			mProgress = ProgressUtil.show(mActivity, "获取Qihoo UserInfo",
					"正在请求应用服务器，请稍候……", new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							if (mUserInfoTask != null) {
								mUserInfoTask.doCancel();
							}
						}
					});

			// 请求应用服务器，用AccessToken换取UserInfo
			mUserInfoTask.doRequest(mActivity, tokenInfo.getAccessToken(),
					Matrix.getAppKey(mActivity), new QihooUserInfoListener() {

						@Override
						public void onGotUserInfo(QihooUserInfo userInfo) {

							ProgressUtil.dismiss(mProgress);

							if (userInfo == null) {
								Toast.makeText(mActivity, "未获取到Qihoo UserInfo",
										Toast.LENGTH_LONG).show();
							} else {
								if (!userInfo.isValid()) {
									if (TextUtils.isEmpty(userInfo.getError())) {
										Toast.makeText(mActivity,
												"未获取到Qihoo UserInfo",
												Toast.LENGTH_LONG).show();
									} else {
										Toast.makeText(mActivity,
												userInfo.getError(),
												Toast.LENGTH_LONG).show();
									}
								} else {
									UserInfoModel model = new UserInfoModel();
									gameLoginListener.LoginSuccess(model);

								}
							}

						}
					});
		} else {
			ProgressUtil.dismiss(mProgress);
			Toast.makeText(mActivity, "未获取到Access Token", Toast.LENGTH_LONG)
					.show();

		}
	}

	/**
	 * 使用360SDK的支付接口
	 * 
	 * @param isLandScape
	 *            是否横屏显示支付界面
	 * @param isFixed
	 *            是否定额支付
	 */
	protected void doSdkPay(final boolean isLandScape, final boolean isFixed, QihooPayInfo pay) {

		if (!isAccessTokenValid) {
			Toast.makeText(mActivity, "AccessToken已失效，请重新登录",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 支付基础参数
		Intent intent = getPayIntent(isLandScape, isFixed, pay);

		// 必需参数，使用360SDK的支付模块。
		intent.putExtra(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_PAY);

		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
		intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

		Matrix.invokeActivity(mActivity, intent, mPayCallback);
	}

	// -----------------------------------参数Intent-------------------------------------

	/***
	 * 生成调用360SDK支付接口基础参数的Intent
	 * 
	 * @param isLandScape
	 * @param pay
	 * @return Intent
	 */
	protected Intent getPayIntent(boolean isLandScape, boolean isFixed, QihooPayInfo pay) {

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// *** 以下非界面相关参数 ***

		// 设置QihooPay中的参数。
		// 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
		bundle.putString(ProtocolKeys.ACCESS_TOKEN, pay.getAccessToken());

		// 必需参数，360账号id，整数。
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

		// 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
		bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

		// 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
		bundle.putString(ProtocolKeys.RATE, pay.getExchangeRate());

		// 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
		bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

		// 必需参数，购买商品的商品id，应用指定，最大16字符。
		bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

		// 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
		bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

		// 必需参数，游戏或应用名称，最大16中文字。
		bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

		// 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
		// 充到统一的用户账户，各区服角色均可使用）。
		bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

		// 必需参数，应用内的用户id。
		// 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
		bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

		// 可选参数，应用扩展信息1，原样返回，最大255字符。
		bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

		// 可选参数，应用扩展信息2，原样返回，最大255字符。
		bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

		// 必选参数，应用订单号，应用内必须唯一，最大32字符。
		bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

		Intent intent = new Intent(mActivity, ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}


	// 支付的回调
	protected IDispatcherCallback mPayCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			Log.d(TAG, "mPayCallback, data is " + data);
			if (TextUtils.isEmpty(data)) {
				return;
			}

			boolean isCallbackParseOk = false;
			JSONObject jsonRes;
			try {
				jsonRes = new JSONObject(data);
				// error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中。
				// error_msg 状态描述
				int errorCode = jsonRes.optInt("error_code");
				isCallbackParseOk = true;
				switch (errorCode) {
				case 0:
				case 1:
				case -1:
				case -2: {
					isAccessTokenValid = true;
					String errorMsg = jsonRes.optString("error_msg");
					String text = "状态码: " + errorCode + ", 状态描述：" + errorMsg; 
					Toast.makeText(mActivity, text,
							Toast.LENGTH_SHORT).show();

				}
					break;
				case 4010201:
					isAccessTokenValid = false;
					Toast.makeText(mActivity,
							"AccessToken已失效，请重新登录", Toast.LENGTH_SHORT)
							.show();
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// 用于测试数据格式是否异常。
			if (!isCallbackParseOk) {
				Toast.makeText(mActivity,
						"严重错误！！接口返回数据格式错误！！",
						Toast.LENGTH_LONG).show();
			}
		}
	};
}
