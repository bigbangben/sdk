package com.zhidian.issueSDK.platform;

import android.app.Activity;

import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.zhidian.issueSDK.ZhidianIssueApplication;
import com.zhidian.issueSDK.model.GameInfo;
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
 * @time 2015年3月2日
 */
public class WdjPlatform implements Iplatform {

	public WdjPlatform() {
	}

	@Override
	public String getPlatformId() {
		return "1016";
	}

	@Override
	public void init(Activity mActivity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		WandouGamesApi wandouGamesApi = ZhidianIssueApplication.getWandouGamesApi();
		  wandouGamesApi.init(mActivity);
	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		// TODO Auto-generated method stub

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
	public void createRole(Activity mActivity, GameInfo gameInfo,
			CreateRoleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameInfo(Activity mActivity, GameInfo gameInfo,
			SetGameInfoListener listener) {
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
