package com.zhidian.issueSDK.platform;

import android.app.Activity;

import com.nearme.gamecenter.open.api.ApiCallback;
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
		return "1";
	}

	@Override
	public void init(Activity activity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		SDKLog.e(TAG, "begin init");
		GameCenterSDK.setmCurrentContext(activity);
		String screenOrientation = SDKUtils.getMeteData(activity,
				"screenOrientation");
		GameCenterSettings.isOritationPort = screenOrientation.equals("0") ? false
				: true;
		GameCenterSettings.isDebugModel = true;
		GameCenterSettings.proInnerSwitcher = false;
		// 测试用的appkey和secret
		// TODO 这个里的为测试key和secret，请务必替换为正式的！
		GameCenterSettings gameCenterSettings = new GameCenterSettings(
				"c5217trjnrmU6gO5jG8VvUFU0", "e2eCa732422245E8891F6555e999878B") {

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

	}

	@Override
	public void login(final Activity activity, final GameLoginListener gameLoginListener) {
		GameCenterSDK.setmCurrentContext(activity);
		GameCenterSDK.getInstance().doLogin(new ApiCallback() {

			@Override
			public void onSuccess(String content, int code) {

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
