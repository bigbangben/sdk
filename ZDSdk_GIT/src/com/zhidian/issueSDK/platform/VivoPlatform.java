package com.zhidian.issueSDK.platform;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bbkmobile.iqoo.payment.PaymentActivity;
import com.bbkmobile.iqoo.payment.payment.OnVivoPayResultListener;
import com.bbkmobile.iqoo.payment.payment.VivoPaymentManager;
import com.vivo.account.base.accounts.OnVivoAccountChangedListener;
import com.vivo.account.base.accounts.VivoAccountManager;
import com.vivo.account.base.activity.LoginActivity;
import com.zhidian.issueSDK.api.UserInfoApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.UserInfoModel;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

public class VivoPlatform implements Iplatform {

	public VivoPlatform() {
	}

	@Override
	public String getPlatformId() {
		return "1018";
	}

	@Override
	public void init(Activity mActivity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		gameInitListener.initSuccess(false, null);
	}

	@Override
	public void login(final Activity activity,
			final GameLoginListener gameLoginListener) {
		Intent loginIntent = new Intent(activity, LoginActivity.class);
		// loginIntent.putExtra(KEY_SHOW_TEMPLOGIN, false);
		activity.startActivity(loginIntent);
		VivoAccountManager mVivoAccountManager = VivoAccountManager
				.getInstance(activity);
		mVivoAccountManager.registeListener(new OnVivoAccountChangedListener() {
			@Override
			public void onAccountLogin(String name, String openid,
					String authtoken) {
				SDKLog.e("", "name=" + name + ", openid=" + openid
						+ ", authtoken=" + authtoken);
				// 向服务器请求用户信息
				UserInfoApi api = new UserInfoApi();
				api.authtoken = authtoken;
				api.appId = SDKUtils.getAppId(activity);
				api.platformId = getPlatformId();
				if (api.authtoken == null || api.appId == null
						|| api.platformId == null) {
					Toast.makeText(activity, "请求参数不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				api.setResponse(new JsonResponse() {

					@Override
					public void requestError(String string) {
						super.requestError(string);
					}

					@Override
					public void requestSuccess(JSONObject jsonObject) {
						if (jsonObject == null) {
							gameLoginListener.LoginFail("用户信息获取失败！");
							return;
						}
						JSONObject state = jsonObject.optJSONObject("state");
						String code = state.optString("code");
						if (code.equals("1")) {
							JSONObject data = jsonObject.optJSONObject("data");
							String accountId = data.optString("accountId");
							String creator = data.optString("creator");
							UserInfoModel model = new UserInfoModel();
							model.id = accountId;
							model.userName = creator;
							gameLoginListener.LoginSuccess(model);
						} else {
							gameLoginListener.LoginFail("用户信息获取失败！");
						}
					}

				});
				new NetTask().execute(api);
			}

			// 第三方游戏不需要使用此回调
			@Override
			public void onAccountRemove(boolean isRemoved) {
				// TODO Auto-generated method stub
				// if(isRemoved){
				// Log.d(TAG, "remove success");
				// }
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
			GameInfo model, String notifyUrl, String extInfo,
			final OrderGenerateListener listener) {
		        String productName = "金币";//商品名称
				String productDes = "";//商品描述
				String packageName = activity.getPackageName();//获取应用的包名
				Double price = Double.valueOf(money);
				Bundle localBundle = new Bundle();
				localBundle.putString("transNo", order);// 交易流水号，由订单推送接口返回
				localBundle.putString("signature", order);// 签名信息，由订单推送接口返回
				localBundle.putString("package", packageName); //在开发者平台创建应用时填写的包名，务必一致，否则SDK界面不会被唤起
				localBundle.putString("useMode", "00");//固定值
				localBundle.putString("productName", productName);//商品名称
				localBundle.putString("productDes", productDes);//商品描述
				localBundle.putDouble("price", price);//价格
				localBundle.putString("userId", "test");//vivo账户id，不允许为空
				Intent target = new Intent(activity, PaymentActivity.class);
				target.putExtra("payment_params", localBundle);
				VivoPaymentManager mVivoPaymentManager = VivoPaymentManager.getInstance(activity);
				mVivoPaymentManager.registeListener(new OnVivoPayResultListener() {
					
					@Override
					public void payResult(String arg0, boolean arg1, String arg2, String arg3) {
						listener.onSuccess();
					}
				});

	}

	@Override
	public void createRole(Activity activity, GameInfo gameInfo, CreateRoleListener listener) {
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
