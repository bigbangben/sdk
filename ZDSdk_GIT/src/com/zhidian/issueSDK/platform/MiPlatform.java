package com.zhidian.issueSDK.platform;

import java.util.UUID;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.xiaomi.gamecenter.sdk.GameInfoField;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;
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
 * @time 2015年1月7日
 */
public class MiPlatform implements Iplatform {

	@Override
	public String getPlatformId() {
		return "1";
	}

	@Override
	public void init(InitInfo initInfo, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		/** SDK初始化 */
		MiAppInfo appInfo = new MiAppInfo();
		appInfo.setAppId(initInfo.getAppId());
		appInfo.setAppKey(initInfo.getAppKey());
		appInfo.setOrientation(initInfo.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ScreenOrientation.horizontal
				: ScreenOrientation.vertical); // 横竖屏
		MiCommplatform.Init(initInfo.getCtx(), appInfo);
	}

	@Override
	public void login(Activity activity,
			final GameLoginListener gameLoginListener) {
		MiCommplatform.getInstance().miLogin(activity,
				new OnLoginProcessListener() {
					@Override
					public void finishLoginProcess(int code, MiAccountInfo arg1) {
						switch (code) {
						case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
							// 登陆成功
							// 获取用户的登陆后的UID（即用户唯一标识）

							/** 以下为获取session并校验流程，如果是网络游戏必须校验，如果是单机游戏或应用可选 **/
							// 获取用户的登陆的Session（请参考5.3.3流程校验Session有效性）
							UserInfoModel model = new UserInfoModel();
							model.id = String.valueOf(arg1.getUid());
							model.sessionId = arg1.getSessionId();
							gameLoginListener.LoginSuccess(model);
							// 请开发者完成将uid和session提交给开发者自己服务器进行session验证

							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_LOGIN_FAIL:
							gameLoginListener.LoginFail(code + "");
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_CANCEL:
							// 取消登录
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
							// 登录操作正在进行中
							break;
						default:
							// 登录失败
							break;
						}
					}
				});
	}

	@Override
	public void showFloat(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		gameLogoutListener.logoutSuccess();
	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		listener.onSuccess();
	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String exInfo, OrderGenerateListener listener) {
		Bundle mBundle = new Bundle();
		mBundle.putString(GameInfoField.GAME_USER_BALANCE, ""); // 用户余额
		mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, ""); // vip等级
		mBundle.putString(GameInfoField.GAME_USER_LV, model.getRoleLevel()); // 角色等级
		mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, "猎人"); // 工会，帮派
		mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME, model.getRoleName()); // 角色名称
		mBundle.putString(GameInfoField.GAME_USER_ROLEID, model.getRoleId()); // 角色id
		mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME, model.getServerId()); // 所在服务器
		MiBuyInfo miBuyInfo = new MiBuyInfo();
		miBuyInfo.setExtraInfo(mBundle); // 设置用户信息
		miBuyInfo.setCpOrderId(UUID.randomUUID().toString());//订单号唯一（不为空）
		miBuyInfo.setCpUserInfo( "cpUserInfo" ); //此参数在用户支付成功后会透传给CP的服务器
		miBuyInfo.setAmount(Integer.parseInt(money)); //必须是大于1的整数，10代表10米币，即10元人民币（不为空

		MiCommplatform.getInstance().miUniPay(activity, miBuyInfo,
				new OnPayProcessListener() {
					@Override
					public void finishPayProcess(int code) {
						switch (code) {
						case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
							// 购买成功
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
							// 取消购买
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
							// 购买失败
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
							// 操作正在进行中
							break;
						default:
							// 购买失败
							break;
						}
					}
				});
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
