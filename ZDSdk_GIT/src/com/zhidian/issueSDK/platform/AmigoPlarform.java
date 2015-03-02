package com.zhidian.issueSDK.platform;

import android.app.Activity;
import android.widget.Button;
import android.widget.Toast;

import com.gionee.gamesdk.AccountInfo;
import com.gionee.gamesdk.GamePayer;
import com.gionee.gamesdk.GamePlatform;
import com.gionee.gamesdk.GamePlatform.LoginListener;
import com.gionee.gsp.GnEFloatingBoxPositionModel;
import com.zhidian.issueSDK.Constants;
import com.zhidian.issueSDK.R;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;

public class AmigoPlarform implements Iplatform {

	public AmigoPlarform() {
	}

	@Override
	public String getPlatformId() {
		return "1021";
	}

	@Override
	public void init(Activity mActivity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		GamePlatform mGamePlatform = GamePlatform.getInstance(mActivity);
		// 测试用，实际接入可以去掉。
		Constants.API_KEY = GamePayer.readApiKey(mActivity, Constants.API_KEY);
		if ("".equals(Constants.API_KEY)) {
			Toast.makeText(mActivity, "请先申请APP ID", Toast.LENGTH_LONG).show();
		}

		// 设置悬浮窗的默认位置(如果不设置，则默认左下角)，最好放在init方法前调用，否则可能无效
		mGamePlatform
				.setFloatingBoxOriginPosition(GnEFloatingBoxPositionModel.LEFT_TOP);

		// 初始化依赖的组件
		mGamePlatform.init(Constants.API_KEY);
	}

	@Override
	public void login(Activity activity,
			final GameLoginListener gameLoginListener) {
		GamePlatform mGamePlatform = GamePlatform.getInstance(activity);
		mGamePlatform.loginAccount(Constants.LOGIN_REQUEST_CODE, true,
				new LoginListener() {

					@Override
					public void onSuccess(AccountInfo accountInfo) {
						// 登录成功，处理自己的业务。

						// 获取playerId
						String playerId = accountInfo.mPlayerId;

						// 获取amigoToken
						String amigoToken = accountInfo.mToken;
						UserInfoModel model = new UserInfoModel();
						model.id = playerId;
						gameLoginListener.LoginSuccess(model);

					}

					@Override
					public void onError(Exception e) {
						/*
						 * Toast.makeText(mActivity, "登录失败:" + e,
						 * Toast.LENGTH_SHORT).show();
						 */
						gameLoginListener.LoginFail("登录失败");
					}

					@Override
					public void onCancel() {
					}
				});

	}

	@Override
	public void showFloat(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		gameLogoutListener.logoutSuccess();
	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String extInfo,
			OrderGenerateListener listener) {

	}

	@Override
	public void createRole(Activity mActivity, GameInfo gameInfo,
			CreateRoleListener listener) {
		listener.onSuccess();
	}

	@Override
	public void setGameInfo(Activity mActivity, GameInfo gameInfo,
			SetGameInfoListener listener) {
		listener.onSuccess();
	}

	@Override
	public boolean suportLogoutUI() {
		return false;
	}

	@Override
	public void onPause(Activity activity) {

	}

	@Override
	public void onResume(Activity activity) {

	}

	@Override
	public void onDestory() {

	}

}
