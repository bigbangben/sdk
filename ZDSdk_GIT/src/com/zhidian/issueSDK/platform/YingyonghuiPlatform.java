package com.zhidian.issueSDK.platform;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import com.appchina.model.ErrorMsg;
import com.appchina.model.LoginErrorMsg;
import com.appchina.usersdk.AccountCenterListener;
import com.appchina.usersdk.AccountCenterOpenShopListener;
import com.appchina.usersdk.AccountManager;
import com.appchina.usersdk.CallBackListener;
import com.appchina.usersdk.SplashListener;
import com.appchina.usersdk.YYHToolBar;
import com.iapppay.mpay.ifmgr.IPayResultCallback;
import com.iapppay.mpay.ifmgr.SDKApi;
import com.iapppay.mpay.tools.PayRequest;
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

public class YingyonghuiPlatform implements Iplatform {

	private YYHToolBar mBar; // 工具条

	private AccountCenterListener acl = new AccountCenterListener() {

		@Override
		public void onLogout() {
			// TODO Auto-generated method
			// 注销账号的回调
			if (mBar != null) {
				mBar.hide();
			}
		}

		@Override
		public void onChangeAccount(com.appchina.usersdk.Account arg0,
				com.appchina.usersdk.Account arg1) {
			// TODO Auto-generated method stub

		}
	};

	public YingyonghuiPlatform() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getPlatformId() {
		return "1019";
	}

	@Override
	public void init(Activity mActivity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		int orientation = "0".equals(SDKUtils.getMeteData(mActivity,
				"screenOrientation")) ? SDKApi.LANDSCAPE_SLIM : SDKApi.PORTRAIT;
		String appid = SDKUtils.getMeteData(mActivity,
				"APPCHINA_ACCOUNT_APPKEY");
		SDKApi.init(mActivity, orientation, appid);
		AccountManager.initSetting(mActivity);
		AccountManager.openYYHSplash(mActivity,
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, 3000,
				new SplashListener() {

					@Override
					public void onAnimOver() {
						// TODO Auto-generated method stub
					}

				});
		gameInitListener.initSuccess(false, null);
	}

	@Override
	public void login(Activity activity,
			final GameLoginListener gameLoginListener) {
		if (AccountManager.isLogin(activity)) {
			// 可以通过isLogin来是否已经登陆了,
			// 登陆过可以不再登陆
			return;
		}
		// 打开登录界面，第二个参数orientation,也可写定横屏或竖屏
		int orientation = "0".equals(SDKUtils.getMeteData(activity,
				"screenOrientation")) ? SDKApi.LANDSCAPE_SLIM : SDKApi.PORTRAIT;
		AccountManager.openYYHLoginActivity(activity, orientation,
				new CallBackListener() {

					// 登录成功后的回调方法
					@Override
					public void onLoginSuccess(Activity activity,
							com.appchina.usersdk.Account account) {

						/*
						 * Log.i("yyhaccount", "" + account.userId); // 用户唯一标识
						 * Log.i("yyhaccount", account.userName); //
						 * 用户名（不推荐使用，如果用户使用Email帐号注册那么userName字段为"null"）
						 * Log.i("yyhaccount", account.openName); //
						 * 开放用户名（推荐使用，为用户注册时使用的标准名称可能是用户名、手机号码或者邮箱）
						 * Log.i("yyhaccount", account.accountType); //
						 * 账户类型（现只支持应用汇账户类型yyh_account） Log.i("yyhaccount",
						 * account.avatarUrl); // 用户头像地址 Log.i("yyhaccount",
						 * account.nickName); // 用户昵称（可能为空） Log.i("yyhaccount",
						 * account.ticket); // 用来获取用户详细信息的令牌 Log.i("yyhaccount",
						 * "" + account.actived); // 用户是否激活（绑定邮箱或手机为激活否则未激活）
						 * String loginTip = "用户：" + account.userName +
						 * " 登录成功, ID: " + account.userId;
						 * Toast.makeText(activity, loginTip,
						 * Toast.LENGTH_LONG).show();
						 */
						UserInfoModel model = new UserInfoModel();
						model.id = String.valueOf(account.userId);
						gameLoginListener.LoginSuccess(model);
						activity.finish(); // 登录成功后退出登录界面

					}

					@Override
					public void onLoginError(Activity activity,
							LoginErrorMsg error) {
						// 用户取消登录(用户按Back键取消登录)
						if (error.status == 100) {
						}
						// app_id为空
						if (error.status == 201) {
							// cp未传入app_id
						}
						// 202 app_key为空
						if (error.status == 202) {
							// cp未传入app_key
						}
						gameLoginListener.LoginFail("登录失败");
						activity.finish();
					}

					@Override
					public void onError(Activity activity, ErrorMsg error) {
						// sdk内部异常
						// 可不做任何处理
						// 如果出现异常请联系应用汇
						gameLoginListener.LoginFail("登录失败");
						activity.finish();
					}

				}, true);

	}

	@Override
	public void showFloat(Activity activity) {

		/*
		 * @param activity
		 * 
		 * @param place 悬浮框初始位置
		 * 
		 * @param color 悬浮框样式，现在固定为小鸟
		 * 
		 * @param orientation 方向,0横屏，1 竖屏，再问我就就把你吃掉
		 * 
		 * @param fullScreen 是否全屏
		 * 
		 * @param accountCenterListener 个人中心的Listener，包括注销和切换账号
		 * 
		 * @param autoUnfold 点击客服等自动关闭
		 * 
		 * @param openShopListener 粮饷打开商城（去充值）
		 * 
		 * @param isUnfold 默认状态{关闭 展开}
		 */
		int orientation = "0".equals(SDKUtils.getMeteData(activity,
				"screenOrientation")) ? SDKApi.LANDSCAPE_SLIM : SDKApi.PORTRAIT;
		mBar = YYHToolBar.getInstance(activity,
				YYHToolBar.YYH_TOOLBAR_MID_LEFT, YYHToolBar.YYH_TOOLBAR_BLUE,
				orientation, false, acl, true,
				new AccountCenterOpenShopListener() {

					@Override
					public boolean openShop(Activity arg0) {
						return false;
					}
				}, false);

		// 显示工具条 同样还有.hide()方法在需要的时候可以隐藏工具条
		if (mBar != null) {
			mBar.show();
		}
	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

	@Override
	public void pay(final Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String extInfo,
			final OrderGenerateListener listener) {
		String appid = SDKUtils.getMeteData(activity, "APPCHINA_ACCOUNT_APPID");
		final String appkey = SDKUtils.getMeteData(activity, "APPCHINA_ACCOUNT_APPKEY");
		PayRequest payRequest = new PayRequest();
		payRequest.addParam("notifyurl", notifyUrl);
		payRequest.addParam("appid", appid);
		payRequest.addParam("waresid", 7);
		payRequest.addParam("quantity", 1);
		payRequest.addParam("exorderno", order);
		payRequest.addParam("price", Integer.parseInt(money));
		payRequest.addParam("cpprivateinfo", extInfo);

		String paramUrl = payRequest.genSignedUrlParamString(appkey);
		if (AccountManager.getCurrentUser() == null) {
			// 支付前确保登陆
			Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
			return;
		}
		SDKApi.startPay(activity, paramUrl, new IPayResultCallback() {
			@Override
			public void onPayResult(int resultCode, String signValue,
					String resultInfo) {// resultInfo = 应用编号&商品编号&外部订单号
				if (SDKApi.PAY_SUCCESS == resultCode) {
					SDKLog.e("xx", "signValue = " + signValue);
					if (null == signValue) {
						// 没有签名值，默认采用finish()，请根据需要修改
						SDKLog.e("xx", "signValue is null ");
						Toast.makeText(activity, "没有签名值", Toast.LENGTH_SHORT)
								.show();
						listener.onSuccess();
					}
					boolean flag = PayRequest.isLegalSign(signValue, appkey);
					if (flag) {
						SDKLog.e("xx", "islegalsign: true");
						listener.onSuccess();
						// 合法签名值，支付成功，请添加支付成功后的业务逻辑
					} else {
						Toast.makeText(activity, "支付成功，但是验证签名失败",
								Toast.LENGTH_SHORT).show();
						listener.onSuccess();
						// 非法签名值，默认采用finish()，请根据需要修改
					}
				} else if (SDKApi.PAY_CANCEL == resultCode) {
					Toast.makeText(activity, "取消支付", Toast.LENGTH_SHORT)
							.show();
					// 取消支付处理，默认采用finish()，请根据需要修改
				} else {
					// 计费失败处理，默认采用finish()，请根据需要修改
					SDKLog.e("xx", "return Error");
					listener.onFail("支付失败");
				}

			}
		});
	

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
