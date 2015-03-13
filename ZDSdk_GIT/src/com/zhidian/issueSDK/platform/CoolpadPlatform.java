package com.zhidian.issueSDK.platform;

import android.app.Activity;

import com.coolcloud.uac.android.api.Coolcloud;
import com.iapppay.sdk.main.CoolPadPay;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.service.CreateRoleService.CreateRoleListener;
import com.zhidian.issueSDK.service.ExitService.GameExitListener;
import com.zhidian.issueSDK.service.InitService.GameInitListener;
import com.zhidian.issueSDK.service.LogOutService.GameLogoutListener;
import com.zhidian.issueSDK.service.LoginService.GameLoginListener;
import com.zhidian.issueSDK.service.OrderGenerateService.OrderGenerateListener;
import com.zhidian.issueSDK.service.SetGameInfoService.SetGameInfoListener;
import com.zhidian.issueSDK.util.SDKUtils;

public class CoolpadPlatform implements Iplatform {

	private Coolcloud mCoolcloud;

	public CoolpadPlatform() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getPlatformId() {
		return "1020";
	}

	@Override
	public void init(Activity activity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		
		/**
		 * SDK初始化，完成SDK的初始化
		 */
		String screenOrientation = SDKUtils.getMeteData(activity, "screenOrientation");
		boolean isLandscape = "0" == screenOrientation ? true : false;
        String appId = SDKUtils.getMeteData(activity, "appId");
		String appKey = SDKUtils.getMeteData(activity, "appKey");
		   mCoolcloud = Coolcloud.createInstance(activity, appId, null);
		   mCoolcloud.getConfiguration().setLandscape(true);
        gameInitListener.initSuccess(false, null);

	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		
		   mCoolcloud.getAuthCode(activity, "", null, new BasicAuthHandler("获取授权码") {
		        @Override
		        public void onDone(Object arg0) {
			    String authCode = (String)arg0;
			   // sendPrompt(handler, "授权成功，授权码: " + authCode, null);
		                                        
		            // 下面的步骤就是服务器的流程了，这里是示例
		            // 根据获取的临时授权码获取授权令牌
			    // getAccessToken(authCode);
		        }
		});
		   /*mCoolcloud.login(activity, "/user/getuserinfo", new OnAuthListener() {

				@Override
				public void onDone(Object arg0) {
					data = (Bundle)arg0;
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(GoodsActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				@Override
				public void onCancel() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(GoodsActivity.this, "登陆取消", Toast.LENGTH_SHORT).show();
						}
					});
				}

				@Override
				public void onError(final ErrInfo arg0) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(GoodsActivity.this, "登陆失败 "+arg0.getDetail(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
	   */
	}

	@Override
	public void showFloat(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(Activity mActivity, GameExitListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pay(Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String extInfo,
			OrderGenerateListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createRole(Activity mActivity, GameInfo gameInfo,
			CreateRoleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameInfo(Activity mActivity, GameInfo gameInfo,
			SetGameInfoListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean suportLogoutUI() {
		return true;
	}

	@Override
	public void onPause(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
