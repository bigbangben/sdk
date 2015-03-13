package com.zhidian.issueSDK.platform;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.zhidian.issueSDK.api.UserInfoApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.model.QihooPayInfo;
import com.zhidian.issueSDK.model.QihooUserInfo;
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

public class QihooPlatform implements Iplatform {
	
	protected static final String TAG = "QihooPlatform";

	private Activity mActivity;
	private GameLoginListener gameLoginListener;
	private String mAccessToken;
	 /**
     * AccessToken是否有效
     */
    protected static boolean isAccessTokenValid = true;
    /**
     * QT是否有效
     */
    protected static boolean isQTValid = true;
	protected QihooUserInfo mUserInfo;

	/**
	 * 是否横屏显示
	 */
	private boolean isLandScape = true;

	@Override
	public String getPlatformId() {
		return "1003";
	}

	@Override
	public void init(Activity mActivity, GameInitListener gameInitListener,
			GameLoginListener gameLoginListener) {
		int orientation = Integer.parseInt(SDKUtils.getMeteData(mActivity,
				"screenOrientation"));
		isLandScape = (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ? true
				: false;
		gameInitListener.initSuccess(false, null);
	}

	@Override
	public void login(Activity activity, GameLoginListener gameLoginListener) {
		this.mActivity = activity;
		this.gameLoginListener = gameLoginListener;
		Matrix.init(mActivity);
		// 使用360SDK登录接口（横屏）
		doSdkLogin(gameLoginListener);
		
	}

	@Override
	public void showFloat(Activity activity) {
		
	}

	@Override
	public void logOut(Activity activity, final GameLogoutListener gameLogoutListener) {
		Matrix.init(activity);
        if(!checkLoginInfo(mUserInfo)) {
            return;
        }
        //function_code : 必须参数，表示调用SDK接口执行的功能
        Intent intent = new Intent();
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGOUT);
        Matrix.execute(activity, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
            }
        });
        mUserInfo = null;
        gameLogoutListener.logoutSuccess();
	}

	@Override
	public void exit(Activity mActivity, final GameExitListener listener) {
		Matrix.init(mActivity);
        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 必需参数，使用360SDK的退出模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_QUIT);

        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
       // bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

        Intent intent = new Intent(mActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        Matrix.invokeActivity(mActivity, intent, new IDispatcherCallback() {

            @Override
            public void onFinished(String data) {
                JSONObject json;
                try {
                    json = new JSONObject(data);
                    int which = json.optInt("which", -1);
                    String label = json.optString("label");
                    switch (which) {
                        case 0: // 用户关闭退出界面
                            return;
                        default:// 退出游戏
                        	listener.onSuccess();
                            return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    
	}

	@Override
	public void pay(final Activity activity, String money, String order,
			GameInfo model, String notifyUrl, String extInfo,
			final OrderGenerateListener listener) {
        if (!checkLoginInfo(mUserInfo)) {
            return;
        }
        if(!isAccessTokenValid) {
            Toast.makeText(activity, "AccessToken已失效，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isQTValid) {
            Toast.makeText(activity, "QT已失效，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 支付基础参数
        String qihooUserId = (mUserInfo != null) ? mUserInfo.getId() : null;
        PackageManager pm = activity.getPackageManager();
		String name = (String) pm.getApplicationLabel(activity
				.getApplicationInfo());
        // 创建QihooPay
        QihooPayInfo qihooPay = new QihooPayInfo();
        qihooPay.setQihooUserId(qihooUserId);
        qihooPay.setMoneyAmount(money);
        qihooPay.setExchangeRate("1");

        qihooPay.setProductName("游戏支付");
        qihooPay.setProductId("0001");

        qihooPay.setNotifyUri(notifyUrl);

        qihooPay.setAppName(name);
        qihooPay.setAppUserName(model.getRoleName());
        qihooPay.setAppUserId(model.getRoleId());

        // 可选参数
        qihooPay.setAppExt1(extInfo);
       // qihooPay.setAppExt2(getString(R.string.demo_pay_app_ext2));
        qihooPay.setAppOrderId(order);
        Intent intent = getPayIntent(qihooPay);

        // 必需参数，使用360SDK的支付模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

        // 启动接口
        Matrix.invokeActivity(activity, intent, new IDispatcherCallback() {

            @Override
            public void onFinished(String data) {
//                Log.d(TAG, "mPayCallback, data is " + data);
                if(TextUtils.isEmpty(data)) {
                    return;
                }

                JSONObject jsonRes;
                try {
                    jsonRes = new JSONObject(data);
                    // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中, 4010201和4009911 登录状态已失效，引导用户重新登录
                    // error_msg 状态描述
                    int errorCode = jsonRes.optInt("error_code");
                    switch (errorCode) {
                        case 0:
                        	listener.onSuccess();
                        	break;
                        	
                        case 1:
                        	break;
                        	
                        case -1:
                        	listener.onFail(jsonRes.optString("error_msg"));
                        	break;
                        	
                        case -2: {
                            isAccessTokenValid = true;
                            isQTValid = true;
                           break;
                        }
                        
                        case 4010201:
                            //acess_token失效
                            isAccessTokenValid = false;
                            Toast.makeText(activity, "AccessToken已失效，请重新登录", Toast.LENGTH_SHORT).show();
                            break;
                        case 4009911:
                            //QT失效
                            isQTValid = false;
                            Toast.makeText(activity, "QT已失效，请重新登录", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
		this.mActivity = mActivity;
		Matrix.init(mActivity);
		listener.onSuccess();
	}

	@Override
	public boolean suportLogoutUI() {
		return true;
	}

	@Override
	public void onPause(Activity activity) {
		
	}

	@Override
	public void onStop(Activity activity) {
		
	}

	@Override
	public void onResume(Activity activity) {
		
	}

	@Override
	public void onDestory() {
		Matrix.destroy(mActivity);
	}
	
	
	/**
	 * 使用360SDK的登录接口
	 * 
	 */
	protected void doSdkLogin(final GameLoginListener gameLoginListener) {
		
		   Intent intent = getLoginIntent();
	       Matrix.execute(mActivity, intent, new IDispatcherCallback() {
				@Override
				public void onFinished(String data) {
					SDKLog.e(TAG, "mLoginCallback, data is " + data);
					//procGotTokenInfoResult(data);
					// press back
		            if (isCancelLogin(data)) {
		            	gameLoginListener.LoginFail(data);
		            }
		     
		         // 解析access_token
		            mAccessToken = parseAccessTokenFromLoginResult(data);
		          if (!TextUtils.isEmpty(mAccessToken)) {
		                // 登录结果直接返回的userinfo中没有qid，需要去应用的服务器获取用access_token获取一下带qid的用户信息
		        	  getUserInfo();
		            } else {
		                Toast.makeText(mActivity, "get access_token failed!", Toast.LENGTH_LONG).show();
		            }
				}
			});
}
	
	  /**
     * 生成调用360SDK登录接口的Intent
     * @return intent
     */
    private Intent getLoginIntent() {

        Intent intent = new Intent(mActivity, ContainerActivity.class);

        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);

        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);

        // 可选参数，是否支持离线模式，默认值为false
       // intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, getCheckBoxBoolean(R.id.isSupportOffline));

        // 可选参数，是否在自动登录的过程中显示切换账号按钮
        intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH, true);

        // 可选参数，是否隐藏欢迎界面
       // intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, getCheckBoxBoolean(R.id.isHideWellcome));

        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
        //intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, getUiBackgroundPicPath());
        // 可选参数，指定assets中的图片路径，作为背景图
      //  intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS, getUiBackgroundPathInAssets());

        //-- 以下参数仅仅针对自动登录过程的控制
        // 可选参数，自动登录过程中是否不展示任何UI，默认展示。
       // intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI, getCheckBoxBoolean(R.id.isAutoLoginHideUI));

        // 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
        intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN, true);
        // 测试参数，发布时要去掉
       // intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, getCheckBoxBoolean(R.id.isDebugSocialShare));

        return intent;
    }
    
    private boolean isCancelLogin(String data) {
        try {
            JSONObject joData = new JSONObject(data);
            int errno = joData.optInt("errno", -1);
            if (-1 == errno) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }
    
    private QihooUserInfo parseUserInfoFromLoginResult(String loginRes) {
        try {
            JSONObject joRes = new JSONObject(loginRes);
            JSONObject joData = joRes.getJSONObject("data");
            JSONObject joUserLogin = joData.getJSONObject("user_login_res");
            JSONObject joUserLoginData = joUserLogin.getJSONObject("data");
            JSONObject joAccessInfo = joUserLoginData.getJSONObject("accessinfo");
            JSONObject joUserMe = joAccessInfo.getJSONObject("user_me");
            return QihooUserInfo.parseUserInfo(joUserMe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String parseAccessTokenFromLoginResult(String loginRes) {
        try {

            JSONObject joRes = new JSONObject(loginRes);
            JSONObject joData = joRes.getJSONObject("data");
            return joData.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
    
    /**
	 * 通过此方法返回UserInfo
	 */
	public void getUserInfo() {
		UserInfoApi api = new UserInfoApi();
		    isAccessTokenValid = true;
		    isQTValid = true;
			// 请求应用服务器，用AccessToken换取UserInfo
			api.zdappId = SDKUtils.getAppId(mActivity);
			api.access_token = mAccessToken;
			api.platformId = getPlatformId();
			if (api.zdappId == null || api.access_token == null
					|| api.platformId == null) {
				Toast.makeText(mActivity, "请求参数不能为空", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			api.setResponse(new JsonResponse() {

				@Override
				public void requestSuccess(JSONObject jsonObject) {
					super.requestSuccess(jsonObject);
					if (jsonObject == null) {
						gameLoginListener.LoginFail("从应用服务器获取用户信息失败");
						return;
					}
					SDKLog.e("jsonObject", "jsonObject >> " + jsonObject);//FIXME
					mUserInfo = new QihooUserInfo();
					mUserInfo = parseJson(jsonObject);
					if (!mUserInfo.isValid()) {
							Toast.makeText(mActivity, "未获取到Qihoo UserInfo",
									Toast.LENGTH_LONG).show();
							gameLoginListener.LoginFail("未获取到Qihoo UserInfo");
						
					} else {
						UserInfoModel model = new UserInfoModel();
						model.id = mUserInfo.getId();
						model.userName = mUserInfo.getName();
						gameLoginListener.LoginSuccess(model);
					}

				}

				@Override
				public void requestError(String string) {
					super.requestError(string);
					gameLoginListener.LoginFail(string);
				}
			});
			new NetTask().execute(api);
		
	}
	
	   public  QihooUserInfo parseJson(JSONObject jsonObj) {
	        QihooUserInfo userInfo = null;
	            try {
	                userInfo = new QihooUserInfo();
	                    String id = jsonObj.getString("id");
	                    String name = jsonObj.getString("name");
	                    String avatar = jsonObj.getString("avatar");

	                    userInfo.setId(id);
	                    userInfo.setName(name);
	                    userInfo.setAvatar(avatar);

	                    // 非必返回项
	                    if (jsonObj.has("sex")) {
	                        String sex = jsonObj.getString("sex");
	                        userInfo.setSex(sex);
	                    }

	                    if (jsonObj.has("area")) {
	                        String area = jsonObj.getString("area");

	                        userInfo.setArea(area);
	                    }

	                    if (jsonObj.has("nick")) {
	                        String nick = jsonObj.getString("nick");
	                        userInfo.setNick(nick);
	                    }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	        
	        return userInfo;
	    }
	   
	   private boolean checkLoginInfo(QihooUserInfo info){
	        if(null == info || !info.isValid()){
	            Toast.makeText(mActivity, "需要登录才能执行此操作", Toast.LENGTH_SHORT).show();
	            return false;
	        }
	        return true;
	    }
	   
	   /***
	     * 生成调用360SDK支付接口的Intent
	     *
	     * @param isLandScape
	     * @param pay
	     * @return Intent
	     */
	    protected Intent getPayIntent(QihooPayInfo pay) {
	        Bundle bundle = new Bundle();

	        // 界面相关参数，360SDK界面是否以横屏显示。
	        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

	        // *** 以下非界面相关参数 ***

	        // 设置QihooPay中的参数。

	        // 必需参数，360账号id，整数。
	        bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

	        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
	        bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

	        // 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
	        bundle.putString(ProtocolKeys.RATE, pay.getExchangeRate());

	        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
	        bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

	        // 必需参数，购买商品的商品id，应用指定，最大16字符。
	        bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

	        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
	        bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

	        // 必需参数，游戏或应用名称，最大16中文字。
	        bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

	        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
	        // 充到统一的用户账户，各区服角色均可使用）。
	        bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

	        // 必需参数，应用内的用户id。
	        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
	        bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

	        // 可选参数，应用扩展信息1，原样返回，最大255字符。
	        bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

	        // 可选参数，应用扩展信息2，原样返回，最大255字符。
	        bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

	        // 可选参数，应用订单号，应用内必须唯一，最大32字符。
	        bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

	        // 必需参数，使用360SDK的支付模块。
	        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

	        Intent intent = new Intent(mActivity, ContainerActivity.class);
	        intent.putExtras(bundle);

	        return intent;
	    }
}
