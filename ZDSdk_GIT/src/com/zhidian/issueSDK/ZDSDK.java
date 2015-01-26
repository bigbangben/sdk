package com.zhidian.issueSDK;

import android.app.Activity;

import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.InitInfo;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.platform.QihooPlatform;
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
public class ZDSDK {
	private static ZDSDK instance;
	private Activity mActivity;
	private Iplatform iplateform;

	private ZDSDK(Activity activity) {
		this.mActivity = activity;
		// 初始化 plateform
		iplateform = new QihooPlatform();
	}

	public static ZDSDK getInstance(Activity activity) {
		if (instance == null) {
			instance = new ZDSDK(activity);
		}
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param initInfo  初始化参数
	 * @param callback  回调
	 */
	public void sdkInit(ICallback callback) {
		new InitService(mActivity, iplateform).init(callback);
	};

	/**
	 * 显示登录界面
	 * 
	 * @param callback
	 */
	public void sdkLogin(Activity activity, ICallback callback) {
		new LoginService(activity, iplateform).login(callback);
	};

	/**
	 * 设置角色信息
	 * 
	 * @param gameInfo 角色信息
	 * @param showFloat 是否显示浮动工具栏
	 */
	public void setGameInfo(Activity activity,GameInfo gameInfo, boolean showFloat,
			ICallback callback) {
		new SetGameInfoService(activity, iplateform).setGameInfo(gameInfo,
				showFloat, callback);

	};

	/**
	 * 创建角色
	 * 
	 * @param gameInfo
	 */
	public void createRole(GameInfo gameInfo, ICallback callback) {
		new CreateRoleService(mActivity, iplateform).creatRole(gameInfo, callback);
	};

	/**
	 * 注销
	 * 
	 * @param gameInfo
	 * @param callback
	 */
	public void onSdkLogOut(Activity activity, GameInfo gameInfo, ICallback callback) {
		new LogOutService(activity, iplateform).logout(gameInfo, callback);
	};

	/**
	 * 退出
	 * 
	 * @param gameInfo
	 * @param callback
	 */
	public void onSdkExit(GameInfo gameInfo, ICallback callback) {
		new ExitService(mActivity, iplateform).exit(gameInfo, callback);
	};

	/**
	 * 支付接口
	 * 
	 * @param gameInfo
	 *            游戏用户信息
	 * @param money
	 *            充值金额
	 * @param cpOrderId
	 *            CP订单号
	 * @param extInfo
	 *            自定义参数
	 * @param notifyUrl
	 *            CP支付结果通知地址
	 * @param callback
	 *            回调
	 */
	public void doPay(GameInfo gameInfo, String money, String cpOrderId,
			String extInfo, String notifyUrl, ICallback callback) {
		new OrderGenerateService(mActivity, iplateform).dopay(gameInfo, money,
				cpOrderId, extInfo, notifyUrl, callback);
	};

	/**
	 * 暂停
	 */
	public void onSdkPause() {
		iplateform.onPause();
	}

	/**
	 * 销毁
	 */
	public void onSdkDestory() {
		iplateform.onDestory();
	}
}
