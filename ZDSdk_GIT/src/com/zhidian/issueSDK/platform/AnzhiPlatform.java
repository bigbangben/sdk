package com.zhidian.issueSDK.platform;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.KeybackCall;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.zhidian.issueSDK.R;
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
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2015年1月4日
 */
public class AnzhiPlatform implements Iplatform {

	private Activity mActivity;
	private AnzhiUserCenter mAnzhiCenter;
	private AnzhiCallback mCallback = new AnzhiCallback() {

		@Override
		public void onCallback(CPInfo cpInfo, String result) {
			SDKLog.e("anzhi", "result " + result);// result 为json内容根据具体回调的类型而确定
			try {
				JSONObject json = new JSONObject(result);
				String key = json.optString("callback_key");// 通过"callback_key"获得回调类型
															// 支付；key_pay
															// 登出：key_logout
															// 登入：key_login
				if ("key_pay".equals(key)) {
					int code = json.optInt("code"); // 支付成功标志位 ,200为成功
					String desc = json.optString("desc"); // 支付信息描述
					String orderId = json.optString("order_id"); // 订单号
					String price = json.optString("price"); // 支付金额
					String time = json.optString("time");
					if (code == 200) {
						// demo 代码请忽视；此处代表支付成功，游戏做简单轮轮询向服务器去查询是否支付成功
						orderGenerateListener.onSuccess();
					} else {
						orderGenerateListener.onFail(result);
					}
				} else if ("key_logout".equals(key)) {
					/*
					 * 安智账号退出以后，所对应的游戏角色不能继续游戏。 前端流程表现为，游戏必须回到游戏的登录界面或安智的登录界面。
					 */
					int code = json.optInt("code"); // 账号退出成功标志位 ,200为成功
					if (code == 200) {
						gameLogoutListener.logoutSuccess();
					} else {
						gameLogoutListener.logoutFail(result);
					}
				} else if ("key_login".equals(key)) {
					int code = json.optInt("code");// 登入成功标志位
					String desc = json.optString("code_desc");
					String uid = json.getString("uid");// uid是用户的唯一标示，用于标记安智用户
					String sid = json.getString("sid");
					String loginName = json.getString("login_name");
					if (code == 200) {
						UserInfoModel model = new UserInfoModel();
						model.sessionId = sid;
						model.id = uid;
						model.userName = loginName;
						gameLoginListener.LoginSuccess(model);
					} else {
						gameLoginListener.LoginFail(result);
					}
				}
			} catch (Exception e) {
				SDKLog.e("", e + "");
			}
		}
	};
	private KeybackCall mKeybackCall = new KeybackCall() {

		@Override
		public void KeybackCall(String st) {
			Log.e("", "st==========" + st);// 通过字符串来设置判断回调页面
		}
	};
	private GameLoginListener gameLoginListener;
	private GameLogoutListener gameLogoutListener;
	private OrderGenerateListener orderGenerateListener;

	public AnzhiPlatform() {
	}

	@Override
	public String getPlatformId() {
		return null;
	}

	@Override
	public void init(Activity activity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		this.mActivity = activity;
		InitInfo initInfo = new InitInfo();
		initInfo = SDKUtils.getMeteData(activity);
		final CPInfo info = new CPInfo();
		info.setOpenOfficialLogin(false);// 官方账号登录接口，默认关闭
		info.setAppKey(initInfo.getAppId());
		info.setSecret(initInfo.getAppKey());
		info.setChannel("AnZhi");// 传"AnZhi"
		info.setGameName(mActivity.getResources().getString(R.string.app_name));
		mAnzhiCenter = AnzhiUserCenter.getInstance();
		mAnzhiCenter.isOpendTestLog = false;// 测试log开关
		mAnzhiCenter.setCPInfo(info);
		mAnzhiCenter.setCallback(mCallback);// 设置登录、登出、支付回调；
		// mAnzhiCenter.setOfficialCallback(mOfficialCall);// 设置老帐户回调，未接入厂商忽略
		mAnzhiCenter.setActivityOrientation(initInfo.getScreenOrientation());// 设置SDK横竖屏，0横屏,1竖屏,4根据物理感应来选择方向
		mAnzhiCenter.setKeybackCall(mKeybackCall);// 设置通过back键回退到游戏的回调，涉及多个界面，详细请看回调
	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		/*
		 * 登入方法，入有自动登录的账号会自动登录，如果没有会跳至安智的登录界面，如果本地没有账号会跳至
		 * 注册界面，另外如果发下SDK无法记住账号则查看FAQ文档或技术论坛查找问题
		 */
		this.mActivity = activity;
		this.gameLoginListener = gameLoginListener;
		mAnzhiCenter.login(activity, true);// 第二个参数为预留参数，无实际意义
	}

	@Override
	public void showFloat(Activity activity) {
		this.mActivity = activity;
		mAnzhiCenter.createFloatView(activity);
	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		this.mActivity = activity;
		this.gameLogoutListener = gameLogoutListener;
		if (suportLogoutUI()) {
			mAnzhiCenter.logout(activity);
		}else {
			new AlertDialog.Builder(activity).setTitle("退出游戏")
			.setMessage("不多待一会吗？")
			.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mAnzhiCenter.logout(mActivity);
				}
			}).setCancelable(false).create().show();


			
		}

	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String exInfo, OrderGenerateListener orderGenerateListener) {
		this.mActivity = activity;
		this.orderGenerateListener = orderGenerateListener;
		mAnzhiCenter.pay(activity, 0, Float.valueOf(money), "", "");
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
	public void onDestory() {
		mAnzhiCenter.gameOver(mActivity);
	}

	@Override
	public void onPause(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume(Activity activity) {
		// TODO Auto-generated method stub
		
	}

}
