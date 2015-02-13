package com.zhidian.issueSDK.platform;

import java.util.Random;

import android.app.Activity;

import com.nearme.gamecenter.open.api.ApiCallback;
import com.nearme.gamecenter.open.api.FixedPayInfo;
import com.nearme.gamecenter.open.api.GameCenterSDK;
import com.nearme.gamecenter.open.api.GameCenterSettings;
import com.nearme.oauth.model.UserInfo;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

public class OppoPlatform implements Iplatform {

	private static final String TAG = "OppoPlatform";

	public OppoPlatform() {
	}

	@Override
	public String getPlatformId() {
		return "1017";
	}

	@Override
	public void init(Activity activity, final GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		SDKLog.e(TAG, "begin init");
		GameCenterSDK.setmCurrentContext(activity);
		String appKey =  SDKUtils.getMeteData(activity,
				"appKey");
		String appSecret =  SDKUtils.getMeteData(activity,
				"appSecret");
		String screenOrientation = SDKUtils.getMeteData(activity,
				"screenOrientation");
		GameCenterSettings.isOritationPort = screenOrientation.equals("0") ? false
				: true;
		GameCenterSettings.isDebugModel = true;
		GameCenterSettings.proInnerSwitcher = false;
		// 测试用的appkey和secret
		// TODO 这个里的为测试key和secret，请务必替换为正式的！
		GameCenterSettings gameCenterSettings = new GameCenterSettings(
				appKey, appSecret) {

			@Override
			public void onForceReLogin() {
				// sdk由于某些原因登出,此方法通知cp,cp需要在此处清理当前的登录状态并重新请求登录.
				// 可以发广播通知页面重新登录
			}

			@Override
			public void onForceUpgradeCancel() {
				// 游戏自升级，后台有设置为强制升级，用户点击取消时的回调函数。
				// 若开启强制升级模式 ， 一般要求不更新则强制退出游戏并杀掉进程。
				// System.exit(0) or kill this process
			}
		};
		// TODO for test old
		// AccountAgent.useNewApi = true;
		GameCenterSDK.init(gameCenterSettings, activity);
		gameInitListener.initSuccess(false, null);

	}

	@Override
	public void login(final Activity activity, final GameLoginListener gameLoginListener) {
		GameCenterSDK.setmCurrentContext(activity);
		GameCenterSDK.getInstance().doLogin(new ApiCallback() {

			@Override
			public void onSuccess(String content, int code) {
				//获取用户信息
				GameCenterSDK.getInstance().doGetUserInfo(new ApiCallback() {

					@Override
					public void onSuccess(String content, int code) {
						try {
							UserInfo userInfo = new UserInfo(content);
							UserInfoModel model = new UserInfoModel();
							model.id = userInfo.id;
							model.userName = userInfo.username;
							
							gameLoginListener.LoginSuccess(model);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(String content, int code) {
						gameLoginListener.LoginFail(content);
					}
				}, activity);
				
			}

			@Override
			public void onFailure(String content, int code) {
				gameLoginListener.LoginFail(content);
			}
		}, activity);

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
			GameInfo model, String notifyUrl, String extInfo,
			OrderGenerateListener listener) {
		final FixedPayInfo payInfo = new FixedPayInfo(
				System.currentTimeMillis() + new Random().nextInt(1000) + "",
				"自定义字段", amount);
		payInfo.setProductDesc("商品描述");
		payInfo.setProductName("符石");
		payInfo.setCallbackUrl("http://gamecenter.wanyol.com:8080/gamecenter/callback_test_url");
		payInfo.setGoodsCount(300);
		GameCenterSDK.getInstance().doFixedKebiPayment(kebiPayment, payInfo,
				this);
	
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
	public void onPause(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

}
