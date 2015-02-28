package com.zhidian.issueSDK.platform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.nd.commplatform.NdCommplatform;
import com.nd.commplatform.NdErrorCode;
import com.nd.commplatform.NdMiscCallbackListener;
import com.nd.commplatform.NdMiscCallbackListener.OnPayProcessListener;
import com.nd.commplatform.NdPageCallbackListener.OnExitCompleteListener;
import com.nd.commplatform.OnInitCompleteListener;
import com.nd.commplatform.entry.NdAppInfo;
import com.nd.commplatform.entry.NdBuyInfo;
import com.nd.commplatform.entry.NdLoginStatus;
import com.nd.commplatform.gc.widget.NdToolBar;
import com.nd.commplatform.gc.widget.NdToolBarPlace;
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
import com.zhidian.issueSDK.util.AppPreferences;
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2014年12月23日
 */
public class NdPlatform implements Iplatform {

	private NdToolBar toolBar;
	private Activity mActivity = null;

	@Override
	public String getPlatformId() {
		return "1";
	}

	@Override
	public void init(Activity activity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		this.mActivity  = activity;
		InitInfo initInfo = new InitInfo();
		initInfo = SDKUtils.getMeteData(activity);
		initSDK(initInfo, gameInitListener);
	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		accountLogin(activity, gameLoginListener);
	}

	@Override
	public void showFloat(Activity activity) {
		if (toolBar == null) {
			toolBar = NdToolBar.create(activity, NdToolBarPlace.NdToolBarRightMid);
		}
		toolBar.show();
	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		if (suportLogoutUI()) {
			NdCommplatform.getInstance().ndLogout(NdCommplatform.LOGOUT_TO_RESET_AUTO_LOGIN_CONFIG, activity);
			gameLogoutListener.logoutSuccess();
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
					// TODO logout
				}
			}).setCancelable(false).create().show();


			
		}
	}
	
	@Override
	public void exit(Activity mActivity, final GameExitListener listener) {
		NdCommplatform.getInstance().ndExit(
				new OnExitCompleteListener(mActivity){

					@Override
					public void onComplete() {
						listener.onSuccess();
					}
			
		});
	}

	@Override
	public void pay(Activity activity, String money, String order, GameInfo gameInfo, String notifyUrl, String exInfo, 
			final OrderGenerateListener listener) {
		NdBuyInfo info = new NdBuyInfo();
		info.setCount(1);
		info.setPayDescription("");
		info.setProductId("1");
		info.setProductName("");
		info.setProductOrginalPrice(Integer.valueOf(money));
		info.setProductPrice(Integer.valueOf(money));
		info.setSerial(order);
		NdCommplatform.getInstance().ndUniPay(info, activity, new OnPayProcessListener() {
			
			@Override
			public void finishPayProcess(int arg0) {
				listener.onSuccess();
			}
		});

	}

	@Override
	public void createRole(Activity activity, GameInfo gameInfo, CreateRoleListener listener) {
		 listener.onSuccess();
	}

	@Override
	public void setGameInfo(Activity activity, GameInfo gameInfo, SetGameInfoListener listener) {
		 listener.onSuccess();	
	}

	@Override
	public boolean suportLogoutUI() {
		return false;
	}


	@Override
	public void onDestory() {
		if(toolBar != null) {
			toolBar.recycle();
			toolBar = null;
		}
	}

	/**
	 * 初始化91SDK
	 */
	private void initSDK(InitInfo initInfo,
			final GameInitListener gameInitListener) {
		if (AppPreferences.isDebugMode(mActivity)) {
			NdCommplatform.getInstance().ndSetDebugMode(0);// 设置调试模式
		}

		NdCommplatform.getInstance().ndSetScreenOrientation(
				initInfo.getScreenOrientation());
		OnInitCompleteListener mOnInitCompleteListener = new OnInitCompleteListener() {

			@Override
			protected void onComplete(int ndFlag) {
				switch (ndFlag) {
				case OnInitCompleteListener.FLAG_NORMAL:
					gameInitListener.initSuccess(false, null);
					break;
					
				case OnInitCompleteListener.FLAG_FORCE_CLOSE:
					gameInitListener.initFail("初始化失败");
					break;
					
				default:
					// 如果还有别的Activity或资源要关闭的在这里处理
					gameInitListener.initFail("初始化失败");
					break;
				}
			}

		};

		NdAppInfo appInfo = new NdAppInfo();
		appInfo.setCtx(mActivity);
		appInfo.setAppId(Integer.parseInt(initInfo.getAppId()));// 应用ID
		appInfo.setAppKey(initInfo.getAppKey());// 应用Key
		/*
		 * NdVersionCheckLevelNormal 版本检查失败可以继续进行游戏 NdVersionCheckLevelStrict
		 * 版本检查失败则不能进入游戏 默认取值为NdVersionCheckLevelStrict
		 */
		appInfo.setNdVersionCheckStatus(NdAppInfo.ND_VERSION_CHECK_LEVEL_STRICT);

		// 初始化91SDK
		NdCommplatform.getInstance().ndInit(mActivity,
				appInfo, mOnInitCompleteListener);
	}

	/**
	 * 91帐号登录
	 * 
	 */
	private void accountLogin(Context ctx, final GameLoginListener gameLoginListener) {

		NdCommplatform.getInstance().ndLogin(ctx,
				new NdMiscCallbackListener.OnLoginProcessListener() {

					@Override
					public void finishLoginProcess(int code) {
						tipsLoginCode(code, gameLoginListener);

					}
				});
	}

	private void tipsLoginCode(int code, GameLoginListener gameLoginListener) {

		String tip = "";

		if (code == NdErrorCode.ND_COM_PLATFORM_SUCCESS) {

			if (NdCommplatform.getInstance().ndGetLoginStatus() == NdLoginStatus.AccountLogin) {// 账号登录
				// 账号登录成功，此时可用初始化玩家游戏数据
				UserInfoModel model = new UserInfoModel();
				model.id = String.valueOf(NdCommplatform.getInstance().getAppId());
				model.sessionId = NdCommplatform.getInstance().getSessionId();
				gameLoginListener.LoginSuccess(model);

			} else if (NdCommplatform.getInstance().ndGetLoginStatus() == NdLoginStatus.GuestLogin) {// 游客登录
				// 游客登录成功，此时可获取玩家的游客UIN做为保存游戏数据的标识，玩家游客账号转正后该UIN不变。

			}

		} else if (code == NdErrorCode.ND_COM_PLATFORM_ERROR_CANCEL) {
		} else if (code == NdErrorCode.ND_COM_GUEST_OFFICIAL_SUCCESS) {
		} else {
			tip = "登录失败，错误代码：" + code;
			gameLoginListener.LoginFail(tip);
			

		}

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
