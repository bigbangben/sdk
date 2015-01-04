package com.zhidian.issueSDK.platform;

import android.app.Activity;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.zhidian.issueSDK.R;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.InitInfo;
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
 * @time 2015年1月4日
 */
public class AnzhiPlatform implements Iplatform {

	@Override
	public String getPlatformId() {
		return null;
	}

	@Override
	public void init(InitInfo initInfo, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		final CPInfo info = new CPInfo();
		info.setOpenOfficialLogin(false);// 官方账号登录接口，默认关闭
		info.setAppKey(initInfo.getAppId());
		info.setSecret(initInfo.getAppKey());
		info.setChannel("AnZhi");// 传"AnZhi"
		info.setGameName(getResources().getString(R.string.app_name));
		mAnzhiCenter = AnzhiUserCenter.getInstance();
		mAnzhiCenter.isOpendTestLog = false;//测试log开关
		mAnzhiCenter.setCPInfo(info);
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

}
