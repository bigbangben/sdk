package com.zhidian.issueSDK.platform;

import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.ResultCode;
import com.baidu.platformsdk.PayOrderInfo;
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
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2015年1月5日
 */
public class BaiduPlatform implements Iplatform {

	@Override
	public String getPlatformId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Activity activity,
			final GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {// 初始化游戏SDK
		InitInfo initInfo = new InitInfo();
		initInfo = SDKUtils.getMeteData(activity);
		BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
		mBDGameSDKSetting.setAppID(Integer.parseInt(initInfo.getAppId()));// APPID设置
		mBDGameSDKSetting.setAppKey(initInfo.getAppKey());// APPKEY设置
		mBDGameSDKSetting.setDomain(Domain.DEBUG);// 设置为正式模式
		mBDGameSDKSetting
				.setOrientation((initInfo.getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ? BDGameSDKSetting.Orientation.LANDSCAPE
						: BDGameSDKSetting.Orientation.PORTRAIT);

		BDGameSDK.init(activity, mBDGameSDKSetting,
				new IResponse<Void>() {

					@Override
					public void onResponse(int resultCode, String resultDesc,
							Void extraData) {
						switch (resultCode) {
						case ResultCode.INIT_SUCCESS:
							// 初始化成功
							gameInitListener.initSuccess(false, null);
							break;

						case ResultCode.INIT_FAIL:
							gameInitListener.initFail(resultDesc);
							break;
						default:
							// 初始化失败
						}

					}

				});

	}

	@Override
	public void login(Activity activity,
			final GameLoginListener gameLoginListener) {// 登录
		BDGameSDK.login(new IResponse<Void>() {

			@Override
			public void onResponse(int resultCode, String resultDesc,
					Void extraData) {
				switch (resultCode) {
				case ResultCode.LOGIN_SUCCESS:
					UserInfoModel model = new UserInfoModel();
					model.sessionId = BDGameSDK.getLoginUid();
					gameLoginListener.LoginSuccess(model);
					break;
				case ResultCode.LOGIN_CANCEL:
					break;
				case ResultCode.LOGIN_FAIL:
					gameLoginListener.LoginFail(resultDesc);
				default:
				}
			}
		});
	}

	@Override
	public void showFloat(Activity activity) {
	}

	@Override
	public void logOut(Activity activity, final GameLogoutListener gameLogoutListener) {
		if (suportLogoutUI()) {
			BDGameSDK.logout();
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
					BDGameSDK.logout();
					gameLogoutListener.logoutSuccess();
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
			GameInfo model, String notifyUrl, String exInfo, final OrderGenerateListener listener) {

		PayOrderInfo payOrderInfo = buildOrderInfo(money,order,model);
		if (payOrderInfo == null) {
			return;
		}

		BDGameSDK.pay(payOrderInfo, null, new IResponse<PayOrderInfo>() {

			@Override
			public void onResponse(int resultCode, String resultDesc,
					PayOrderInfo extraData) {
				switch (resultCode) {
				case ResultCode.PAY_SUCCESS:// 支付成功
					listener.onSuccess();
					break;
				case ResultCode.PAY_CANCEL:// 订单支付取消
					break;
				case ResultCode.PAY_FAIL:// 订单支付失败
					listener.onFail(resultDesc);
					break;
				case ResultCode.PAY_SUBMIT_ORDER:// 订单已经提交，支付结果未知（比如：已经请求了，但是查询超时）
					break;
				}

			}

		});

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
		BDGameSDK.destroy();
	}

	/**
	 * 构建订单信息
	 * @param model 
	 * @param order 
	 * @param money 
	 */
	private PayOrderInfo buildOrderInfo(String money, String order, GameInfo model) {
		String cpOrderId = order;// CP订单号
		String goodsName = "金币";
		String totalAmount = money;// 支付总金额 （以分为单位）
		int ratio = 1;// 该参数为非定额支付时生效 (支付金额为0时为非定额支付,具体参见使用手册)
		String extInfo = "第X号服务器，Y游戏分区充值";// 扩展字段，该信息在支付成功后原样返回给CP

		if (TextUtils.isEmpty(totalAmount)) {
			totalAmount = "0";
		}

		PayOrderInfo payOrderInfo = new PayOrderInfo();
		payOrderInfo.setCooperatorOrderSerial(cpOrderId);
		payOrderInfo.setProductName(goodsName);
		long p = Long.parseLong(totalAmount);
		payOrderInfo.setTotalPriceCent(p);// 以分为单位
		payOrderInfo.setRatio(ratio);
		payOrderInfo.setExtInfo(extInfo);// 该字段将会在支付成功后原样返回给CP(不超过500个字符)

		return payOrderInfo;
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
