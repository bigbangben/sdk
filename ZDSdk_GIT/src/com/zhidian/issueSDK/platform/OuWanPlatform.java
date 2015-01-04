package com.zhidian.issueSDK.platform;

import net.umipay.android.GameParamInfo;
import net.umipay.android.GameRolerInfo;
import net.umipay.android.GameUserInfo;
import net.umipay.android.UmiPaySDKManager;
import net.umipay.android.UmiPaymentInfo;
import net.umipay.android.UmipaySDKStatusCode;
import net.umipay.android.interfaces.AccountCallbackListener;
import net.umipay.android.interfaces.InitCallbackListener;
import android.app.Activity;

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

public class OuWanPlatform implements Iplatform {

	private Activity mActivity;

	public OuWanPlatform(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public String getPlatformId() {
		// TODO 添加platformId
		return "1";
	}

	@Override
	public void init(InitInfo initInfo,
			final GameInitListener gameInitListener,
			final GameLoginListener gameLoginListener) {
		GameParamInfo gameParamInfo = new GameParamInfo();
		gameParamInfo.setAppId(String.valueOf(initInfo.getAppId()));// 设置AppID
		gameParamInfo.setAppSecret(initInfo.getAppKey());// 设置AppSecret
		// gameParamInfo.setTestMode(false); //【可选】设置测试模式，默认为false
		// gameParamInfo.setChannel("0","0"); //【可选】设置渠道及子渠道id
		// 调用初始化接口
		UmiPaySDKManager.initSDK(initInfo.getCtx(), gameParamInfo,
				new InitCallbackListener() {
					@Override
					public void onSdkInitFinished(int code, String message) {
						if (code == UmipaySDKStatusCode.SUCCESS) {
							// Toast.makeText(context, "初始化成功",
							// Toast.LENGTH_SHORT).show();
							// 初始化成功,可以执行后续的登录充值操作
							gameInitListener.initSuccess(false, null);
						} else {
							// Toast.makeText(context, "初始化失败:" + message,
							// Toast.LENGTH_SHORT).show();
							// 初始化失败,不能进行后续操作
							gameInitListener.initFail(message);
						}
					}
				}, new AccountCallbackListener() {
					@Override
					public void onLogin(int code, GameUserInfo userInfo) {
						if (code == UmipaySDKStatusCode.SUCCESS
								&& userInfo != null) {
							// 登录成功后,sdk返回一个GameUserInfo结构,里面包含平台用户唯一标志Uid,用户签名Sign及utc秒数。
							// Toast.makeText(context,
							// "登录成功,用户id:"+userInfo.getUid()+
							// " 用户sign:"+userInfo.getSign()+
							// " timestamp:"+userInfo.getTimestamp_s(),
							// Toast.LENGTH_SHORT).show();
							UserInfoModel model = new UserInfoModel();
							model.id = userInfo.getUid();
							model.sessionId = "";
							gameLoginListener.LoginSuccess(model);
						} else {
							// 用户按返回键,退出了sdk登录界面,需要进行相关的操作,比如弹出对话框提醒用户重新登录。
						}
					}

					@Override
					public void onLogout(int code) {
						if (code == UmipaySDKStatusCode.SUCCESS) {
							// 客户端成功登出游戏账户
						}
					}
				});
	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		UmiPaySDKManager.showLoginView(activity);
	}

	@Override
	public void showFloat(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		UmiPaySDKManager.logoutAccount(activity);// 登出账户接口
	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, OrderGenerateListener listener) {
		UmiPaymentInfo paymentInfo = new UmiPaymentInfo();
		// 业务类型，SERVICE_TYPE_QUOTA(固定额度模式，充值金额在支付页面不可修改)，SERVICE_TYPE_RATE(汇率模式，充值金额在支付页面可修改）
		paymentInfo.setServiceType(UmiPaymentInfo.SERVICE_TYPE_QUOTA);
		// 定额支付金额，单位RMB
		paymentInfo.setPayMoney(Integer.valueOf(money));
		// 订单描述
		paymentInfo.setDesc("100元宝");
		// 【可选】外部订单号
		paymentInfo.setTradeno(order);
		paymentInfo.setRoleGrade(model.getRoleLevel()); // 【必填】设置用户的游戏角色等级
		paymentInfo.setRoleId(model.getRoleId());// 【必填】设置用户的游戏角色的ID
		paymentInfo.setRoleName(model.getRoleName());// 【必填】设置用户的游戏角色名字
		paymentInfo.setServerId(model.getServerId());// 【必填】设置用户所在的服务器ID
		// paymentInfo.setCustomInfo("");//
		// 【可选】游戏开发商自定义数据。该值将在用户充值成功后，在充值回调接口通知给游戏开发商时携带该数据
		UmiPaySDKManager.showPayView(activity, paymentInfo);// 调用充值接口
	}

	@Override
	public void createRole(GameInfo gameInfo, CreateRoleListener listener) {
		listener.onSuccess();
	}

	@Override
	public void setGameInfo(GameInfo gameInfo, SetGameInfoListener listener) {
        GameRolerInfo gameRolerInfo = new GameRolerInfo();
        gameRolerInfo.setServerId(gameInfo.getServerId());
        gameRolerInfo.setServerName("");
        gameRolerInfo.setRoleId(gameInfo.getRoleId());
        gameRolerInfo.setRoleName(gameInfo.getRoleName());
        gameRolerInfo.setRoleLevel(gameInfo.getRoleLevel());
        UmiPaySDKManager.setGameRolerInfo(mActivity,gameRolerInfo);//调用上报角色信息接口
		listener.onSuccess();
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

}
