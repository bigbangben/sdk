package com.zhidian.issueSDK.platform;

import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xiaomi.gamecenter.sdk.GameInfoField;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;
import com.zhidian.issueSDK.api.UserInfoApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2015年1月7日
 */
public class MiPlatform implements Iplatform {

	protected static final String TAG = "MiPlatform";

	@Override
	public String getPlatformId() {
		return "1002";
	}

	@Override
	public void init(Activity activity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		String appId = SDKUtils.getMeteData(activity, "appId");
		String appKey = SDKUtils.getMeteData(activity, "appKey");
		if (appId == null || appKey == null) {
			Toast.makeText(activity, "MetaData配置出错！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		SDKLog.e(TAG, "appId =====" + appId);
		SDKLog.e(TAG, "appKey =====" + appKey);
		String screenOrientation = SDKUtils.getMeteData(activity,
				"screenOrientation");
		/** SDK初始化 */
		MiAppInfo appInfo = new MiAppInfo();
		appInfo.setAppId(appId);
		appInfo.setAppKey(appKey);
		appInfo.setOrientation(Integer.parseInt(screenOrientation) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ScreenOrientation.horizontal
				: ScreenOrientation.vertical); // 横竖屏
		MiCommplatform.Init(activity, appInfo);
		gameInitListener.initSuccess(false, null);
	}

	@Override
	public void login(final Activity activity,
			final GameLoginListener gameLoginListener) {
		MiCommplatform.getInstance().miLogin(activity,
				new OnLoginProcessListener() {
					@Override
					public void finishLoginProcess(int code,
							final MiAccountInfo arg1) {
						switch (code) {
						case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
							// 登陆成功
							// 获取用户的登陆后的UID（即用户唯一标识）
							final long uid = arg1.getUid();
							SDKLog.e(TAG, "uid  ==== " + uid);
							/** 以下为获取session并校验流程，如果是网络游戏必须校验，如果是单机游戏或应用可选 **/
							// 获取用户的登陆的Session（请参考5.3.3流程校验Session有效性）
							final String session = arg1.getSessionId();
							// 请开发者完成将uid和session提交给开发者自己服务器进行session验证
							// 向服务器请求用户信息
							UserInfoApi api = new UserInfoApi();
							api.uid = String.valueOf(uid);
							api.session = session;
							api.appId = SDKUtils.getMeteData(activity, "appId");
							api.zdappId = SDKUtils.getAppId(activity);
							api.platformId = getPlatformId();
							if (api.uid == null || api.session == null
									|| api.appId == null
									|| api.platformId == null) {
								Toast.makeText(activity, "请求参数不能为空",
										Toast.LENGTH_SHORT).show();
								return;
							}
							api.setResponse(new JsonResponse() {

								@Override
								public void requestError(String string) {
									super.requestError(string);
									gameLoginListener
									.LoginFail("用户信息获取失败！");
								}

								@Override
								public void requestSuccess(JSONObject jsonObject) {
									if (jsonObject == null) {
										gameLoginListener
												.LoginFail("用户信息获取失败！");
										return;
									}
									SDKLog.e(TAG, "jsonObject  ==== "
											+ jsonObject.toString());
									int errcode = jsonObject.optInt("errcode");
									if (errcode == 200) {
										UserInfoModel model = new UserInfoModel();
										model.id = String.valueOf(uid);
										model.sessionId = session;
										model.userName = arg1.getNikename();
										gameLoginListener.LoginSuccess(model);
									} else if (errcode == 1515) {
										gameLoginListener.LoginFail("appId错误！");
									} else if (errcode == 1516) {
										gameLoginListener.LoginFail("uid错误！");
									} else if (errcode == 1520) {
										gameLoginListener
												.LoginFail("session错误！");
									} else if (errcode == 1525) {
										gameLoginListener
												.LoginFail("signature错误！");
									} else {
										gameLoginListener
												.LoginFail("用户信息获取失败！");
									}
								}

							});
							new NetTask().execute(api);
							/*
							 * UserInfoModel model = new UserInfoModel();
							 * model.id = String.valueOf(uid); model.sessionId =
							 * arg1.getSessionId();
							 * gameLoginListener.LoginSuccess(model);
							 */

							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_LOGIN_FAIL:
							SDKLog.e(TAG, "login fail");
							gameLoginListener.LoginFail(code + "");
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_CANCEL:
							// 取消登录
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
							// 登录操作正在进行中
							break;
						default:
							// 登录失败
							break;
						}
					}
				});
	}

	@Override
	public void showFloat(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOut(Activity activity,
			final GameLogoutListener gameLogoutListener) {
		if (suportLogoutUI()) {
			gameLogoutListener.logoutSuccess();
		} else {
			new AlertDialog.Builder(activity).setTitle("退出游戏")
					.setMessage("不多待一会吗？")
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							gameLogoutListener.logoutSuccess();
						}
					}).setCancelable(false).create().show();

		}
	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String exInfo,
			final OrderGenerateListener listener) {
		Bundle mBundle = new Bundle();
		mBundle.putString(GameInfoField.GAME_USER_BALANCE, ""); // 用户余额
		mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, ""); // vip等级
		mBundle.putString(GameInfoField.GAME_USER_LV, model.getRoleLevel()); // 角色等级
		mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, "猎人"); // 工会，帮派
		mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME,
				model.getRoleName()); // 角色名称
		mBundle.putString(GameInfoField.GAME_USER_ROLEID, model.getRoleId()); // 角色id
		mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME,
				model.getServerId()); // 所在服务器
		MiBuyInfo miBuyInfo = new MiBuyInfo();
		miBuyInfo.setExtraInfo(mBundle); // 设置用户信息
		miBuyInfo.setCpOrderId(order);// 订单号唯一（不为空）
		miBuyInfo.setCpUserInfo(exInfo); // 此参数在用户支付成功后会透传给CP的服务器
		if (money == null || money.equals("") || money.equals("0")) {
			Toast.makeText(activity, "金额不能为空！！", Toast.LENGTH_SHORT).show();
			return;
		}
		int amount = Integer.parseInt(money);
		if (amount < 100) {
			Toast.makeText(activity, "金额不合法，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		miBuyInfo.setAmount(amount/100); // 必须是大于1的整数，10代表10米币，即10元人民币（不为空

		MiCommplatform.getInstance().miUniPay(activity, miBuyInfo,
				new OnPayProcessListener() {
					@Override
					public void finishPayProcess(int code) {
						switch (code) {
						case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
							// 购买成功
							listener.onSuccess();
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
							// 取消购买
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
							// 购买失败
							listener.onFail(code + "");
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
							// 操作正在进行中
							break;
						default:
							// 购买失败
							break;
						}
					}
				});
	}

	@Override
	public void createRole(Activity activity, GameInfo gameInfo, CreateRoleListener listener) {
           listener.onSuccess();
	}

	@Override
	public void setGameInfo(Activity activity, GameInfo gameInfo, SetGameInfoListener listener) {
		listener.onSuccess();
	}

	@Override
	public boolean suportLogoutUI() {
		return false;
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume(Activity activity) {
		// TODO Auto-generated method stub

	}

}
