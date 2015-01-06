package com.zhidian.issueSDK.platform;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.sogou.gamecenter.sdk.FloatMenu;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.SogouGameConfig;
import com.sogou.gamecenter.sdk.bean.UserInfo;
import com.sogou.gamecenter.sdk.listener.InitCallbackListener;
import com.sogou.gamecenter.sdk.listener.LoginCallbackListener;
import com.sogou.gamecenter.sdk.listener.OnExitListener;
import com.sogou.gamecenter.sdk.listener.PayCallbackListener;
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
import com.zhidian.issueSDK.util.SDKLog;

/**
 * @Description
 * @author ZengQBo
 * @time 2015年1月6日
 */
public class SogouPlatform implements Iplatform {

	private SogouGamePlatform mSogouGamePlatform;
	private FloatMenu mFloatMenu;

	@Override
	public String getPlatformId() {
		return "1";
	}

	public SogouPlatform() {
		mSogouGamePlatform = SogouGamePlatform.getInstance();

	}

	@Override
	public void init(InitInfo initInfo,
			final GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		// 配置游戏信息（gid、appKey由搜狗游戏平台统一分配）
		SogouGameConfig config = new SogouGameConfig();
		// 开发模式为true，false是正式环境
		// 先用开发模式进行联调，联调通过后请设置正式环境
		// 请注意，提交版本设置正式环境
		config.devMode = false;
		config.gid = Integer.parseInt(initInfo.getAppId());
		config.appKey = initInfo.getAppKey();
		config.gameName = "测试应用";

		// SDK准备初始化
		mSogouGamePlatform.prepare(initInfo.getCtx(), config);
		mSogouGamePlatform.init(initInfo.getCtx(), new InitCallbackListener() {

			@Override
			public void initSuccess() {
				gameInitListener.initSuccess(false, null);
			}

			@Override
			public void initFail(int arg0, String arg1) {
				gameInitListener.initFail(arg1);
			}
		});
	}

	@Override
	public void login(Activity activity,
			final GameLoginListener gameLoginListener) {
		mSogouGamePlatform.login(activity, new LoginCallbackListener() {

			@Override
			public void loginSuccess(int arg0, UserInfo arg1) {
				UserInfoModel model = new UserInfoModel();
				model.id = String.valueOf(arg1.getUserId());
				model.sessionId = arg1.getSessionKey();
				gameLoginListener.LoginSuccess(model);
			}

			@Override
			public void loginFail(int arg0, String arg1) {
				gameLoginListener.LoginFail(arg1);
			}
		});
	}

	@Override
	public void showFloat(Activity activity) {
		// 当前是全屏模式，isFullscreen为true
		mFloatMenu = mSogouGamePlatform.createFloatMenu(activity, true);
		mFloatMenu.show();
	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		mSogouGamePlatform.loginout(activity);
		gameLogoutListener.logoutSuccess();
	}

	@Override
	public void exit(Activity mActivity, final GameExitListener listener) {
		mSogouGamePlatform.exit(new OnExitListener(mActivity) {
			
			@Override
			public void onCompleted() {
				listener.onSuccess();
			}
		});
	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, final OrderGenerateListener listener) {
		Map<String, Object> data = new HashMap<String, Object>();
		// 游戏中货币名字
		data.put("currency", "符石");
		// 人民币兑换比例
		data.put("rate", 1);
		// 支付金额单位是元，在手游中数据类型为整型
		data.put("amount", 1);
		// 购买商品名字
		data.put("product_name", "倚天剑");
		// 透传参数,游戏方自行定义
		data.put("app_data", "appdata_demo");
		// 可选参数:隐藏支付渠道,支付渠道之间用冒号分割，隐藏2:3:4支付渠道,如：隐藏充值卡渠道
		// data.put("hide_channel", "4");

		mSogouGamePlatform.pay(activity, data, new PayCallbackListener() {

			// 支付成功回调,游戏方可以做后续逻辑处理
			// 收到该回调说明提交订单成功，但成功与否要以服务器回调通知为准
			@Override
			public void paySuccess(String orderId, String appData) {
				// orderId是订单号，appData是游戏方自己传的透传消息
				SDKLog.d("", "paySuccess orderId:" + orderId + " appData:"
						+ appData);
				listener.onSuccess();
			}

			@Override
			public void payFail(int code, String orderId, String appData) {
				// 支付失败情况下,orderId可能为空
				if (orderId != null) {
					SDKLog.d("", "payFail code:" + code + "orderId:" + orderId
							+ " appData:" + appData);
					listener.onFail(appData);
				} else {
					SDKLog.d("", "payFail code:" + code + " appData:" + appData);
					listener.onFail(appData);
				}
			}

		}, true);

	}

	@Override
	public void createRole(GameInfo gameInfo, CreateRoleListener listener) {
		listener.onSuccess();
	}

	@Override
	public void setGameInfo(GameInfo gameInfo, SetGameInfoListener listener) {
		listener.onSuccess();
	}

	@Override
	public boolean suportLogoutUI() {
		return false;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestory() {
		// 防止内存泄露，清理相关数据务必调用SDK结束接口
		mSogouGamePlatform.onTerminate();
	}

}
