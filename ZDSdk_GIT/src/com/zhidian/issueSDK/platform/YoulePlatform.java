package com.zhidian.issueSDK.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.zhidian.gamesdk.listener.ILogOutListener;
import com.zhidian.gamesdk.listener.ILoginListener;
import com.zhidian.gamesdk.listener.InitListener;
import com.zhidian.gamesdk.manager.ZhiDianManager;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.InitInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;

/**
 * @Description
 * @author ZengQBo
 * @time 2014年12月22日
 */
public class YoulePlatform implements Iplatform {

	private static final String TAG = "YoulePlatform";

	private GameInitListener gameInitListener;

	private GameLoginListener gameLoginListener;

	/**
	 * 初始化监听器
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void initSuccess() {
			gameInitListener.initSuccess(false, null);
		}

		@Override
		public void initFail(String message) {
			gameInitListener.initFail(message);
		}
	};

	/**
	 * 登录监听器
	 */
	private ILoginListener loginListener = new ILoginListener() {

		@Override
		public void loginging() {

		}

		@Override
		public void loginSuccess(String sessionId, String uid) {
			UserInfoModel model = new UserInfoModel();
			model.sessionId = sessionId;
			model.id = uid;
			gameLoginListener.LoginSuccess(model);
		}

		@Override
		public void loginFail() {
			gameLoginListener.LoginFail("");
		}
	};

	private GameLogoutListener gameLogoutListener;

	private ILogOutListener iLogOutListener = new ILogOutListener() {

		@Override
		public void logouting() {

		}

		@Override
		public void logoutSuccess() {
			gameLogoutListener.logoutSuccess();
		}

		@Override
		public void logoutFail() {
		}
	};

	@Override
	public String getPlatformId() {
		return "1";
	}

	@Override
	public void init(InitInfo initInfo, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		this.gameInitListener = gameInitListener;
		this.gameLoginListener = gameLoginListener;
		ZhiDianManager.init((Activity) initInfo.getCtx(),
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, mInitListener,
				loginListener);
	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		this.gameLoginListener = gameLoginListener;
		ZhiDianManager.showLogin(activity, loginListener);

	}

	@Override
	public void logOut(final Activity activity, GameLogoutListener gameLogoutListener) {
		this.gameLogoutListener = gameLogoutListener;
		if (suportLogoutUI()) {
			ZhiDianManager.logout(activity, iLogOutListener);
		} else {
			new AlertDialog.Builder(activity).setTitle("退出游戏").setMessage("不多待一会吗？").setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ZhiDianManager.logout(activity, iLogOutListener);
				}
			}).create().show();
		}
		

	}

	@Override
	public void pay(Activity activity, String money, String order,GameInfo gameInfo,
			OrderGenerateListener listener) {
		if (money != null || Integer.parseInt(money) > 0) {
			ZhiDianManager.customPay(activity, Integer.parseInt(money), order);
		} else {
			ZhiDianManager.payNormal(activity, order);
		}

	}

	@Override
	public boolean suportLogoutUI() {
		return false;
	}

	@Override
	public void onPause() {

	}

	@Override
	public void onDestory() {

	}

	@Override
	public void showFloat(Activity activity) {
		ZhiDianManager.showFloadButton(activity);
	}

	@Override
	public void setGameInfo(GameInfo gameInfo, SetGameInfoListener listener) {
		listener.onSuccess();
	}

	@Override
	public void createRole(GameInfo gameInfo, CreateRoleListener listener) {
		listener.onSuccess();
	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

}