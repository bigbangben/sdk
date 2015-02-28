package com.zhidian.issueSDK.platform;

import android.app.Activity;

import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.service.CreateRoleService;
import com.zhidian.issueSDK.service.ExitService;
import com.zhidian.issueSDK.service.InitService;
import com.zhidian.issueSDK.service.LogOutService;
import com.zhidian.issueSDK.service.LoginService;
import com.zhidian.issueSDK.service.OrderGenerateService;
import com.zhidian.issueSDK.service.SetGameInfoService;

/**
 * Created by Administrator on 2014/12/11.
 */
public interface Iplatform {
	public String getPlatformId();

	/**
	 * @param mActivity
	 * @param gameInitListener
	 * @param gameLoginListener
	 */
	public void init(Activity mActivity,
			InitService.GameInitListener gameInitListener,
			LoginService.GameLoginListener gameLoginListener);

	/**
	 * @param activity
	 * @param gameLoginListener
	 */
	public void login(Activity activity,
			LoginService.GameLoginListener gameLoginListener);

	/**
	 * @param activity
	 * @param gameLoginListener
	 */
	public void showFloat(Activity activity);

	/**
	 * @param activity
	 * @param gameLogoutListener
	 */
	public void logOut(Activity activity,
			LogOutService.GameLogoutListener gameLogoutListener);

	/**
	 * @param mActivity
	 * @param listener
	 */
	public void exit(Activity mActivity, ExitService.GameExitListener listener);

	/**
	 * @param activity
	 * @param money
	 * @param order
	 * @param model
	 * @param listener
	 */
	public void pay(Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String extInfo,
			OrderGenerateService.OrderGenerateListener listener);

	/**
	 * @param mActivity 
	 * @param gameInfo
	 * @param listener
	 */
	public void createRole(Activity mActivity, GameInfo gameInfo,
			CreateRoleService.CreateRoleListener listener);

	/**
	 * @param mActivity 
	 * @param gameInfo
	 * @param listener
	 */
	public void setGameInfo(Activity mActivity, GameInfo gameInfo,
			SetGameInfoService.SetGameInfoListener listener);

	public boolean suportLogoutUI();

	public void onPause(Activity activity);

	public void onResume(Activity activity);

	public void onDestory();
}
