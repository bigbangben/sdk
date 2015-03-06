package com.zhidian.issueSDK.platform;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.widget.Toast;
import cn.uc.gamesdk.UCCallbackListener;
import cn.uc.gamesdk.UCCallbackListenerNullException;
import cn.uc.gamesdk.UCFloatButtonCreateException;
import cn.uc.gamesdk.UCGameSDK;
import cn.uc.gamesdk.UCGameSDKStatusCode;
import cn.uc.gamesdk.UCLogLevel;
import cn.uc.gamesdk.UCLoginFaceType;
import cn.uc.gamesdk.UCOrientation;
import cn.uc.gamesdk.info.FeatureSwitch;
import cn.uc.gamesdk.info.GameParamInfo;
import cn.uc.gamesdk.info.OrderInfo;
import cn.uc.gamesdk.info.PaymentInfo;

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

public class UcPlatform implements Iplatform {

	// 值为true时，为调试环境模式，当值为false时，是生产环境模式，验收及对外发布时，要求必须使用生产环境模式
	public static final String TAG = "UcPlatform";
	public static boolean debugMode = false;
	private GameLogoutListener gameLogoutListener;

	@Override
	public String getPlatformId() {
	return "1001"; 
	}

	@Override
	public void init(final Activity activity,
			final GameInitListener gameInitListener,
			final GameLoginListener gameLoginListener) {
		// 监听用户注销登录消息
				// 九游社区-退出当前账号功能执行时会触发此监听
		try {
			UCGameSDK.defaultSDK().setLogoutNotifyListener(
					new UCCallbackListener<String>() {
						@Override
						public void callback(int statuscode, String msg) {
							// TODO 此处需要游戏客户端注销当前已经登录的游戏角色信息
							String s = "游戏接收到用户退出通知。" + msg + statuscode;
							Log.e("UCGameSDK", s);
							// 未成功初始化
							if (statuscode == UCGameSDKStatusCode.NO_INIT) {
								// 调用SDK初始化接口
								//init(activity,gameInitListener,gameLoginListener);
							}
							// 未登录成功
							if (statuscode == UCGameSDKStatusCode.NO_LOGIN) {
								// 调用SDK登录接口
								//login(activity, gameLoginListener);
							}
							// 退出账号成功
							if (statuscode == UCGameSDKStatusCode.SUCCESS) {
								// 执行销毁悬浮按钮接口
								ucSdkDestoryFloatButton(activity);
								gameLogoutListener.logoutSuccess();
								// 调用SDK登录接口
								//ucSdkLogin();
							}
							// 退出账号失败
							if (statuscode == UCGameSDKStatusCode.FAIL) {
								// 调用SDK退出当前账号接口
								//ucSdkLogout();
								gameLogoutListener.logoutFail(msg);
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			// 处理异常
		}
		GameParamInfo gpi = new GameParamInfo();// 下面的值仅供参考
		String cpId = SDKUtils.getMeteData(activity, "cpId");
		String gameId = SDKUtils.getMeteData(activity, "gameId");
		String screenOrientation = SDKUtils.getMeteData(activity, "screenOrientation");
		if (cpId == null || gameId == null || screenOrientation == null) {
			Toast.makeText(activity, "MetaData设置出错，请检查!!!", Toast.LENGTH_SHORT).show();
			return;
		}
		gpi.setCpId(Integer.parseInt(cpId));
		gpi.setGameId(Integer.parseInt(gameId));
		gpi.setServerId(0); // 服务器ID可根据游戏自身定义设置，或传入0
		// gpi.setChannelId(2); // 渠道号统一处理，已不需设置，此参数已废弃，服务端此参数请设置值为2

		// 在九游社区设置显示查询充值历史和显示切换账号按钮，
		// 在不设置的情况下，默认情况情况下，生产环境显示查询充值历史记录按钮，不显示切换账户按钮
		// 测试环境设置无效
		gpi.setFeatureSwitch(new FeatureSwitch(true, false));

		// 设置SDK登录界面为横屏，个人中心及充值页面默认为强制竖屏，无法修改
		// UCGameSDK.defaultSDK().setOrientation(UCOrientation.LANDSCAPE);

		// 设置SDK登录界面为竖屏

		UCGameSDK
				.defaultSDK()
				.setOrientation(
						(Integer.parseInt(screenOrientation) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) ? UCOrientation.LANDSCAPE
								: UCOrientation.PORTRAIT);

		// 设置登录界面：
		// USE_WIDGET - 简版登录界面
		// USE_STANDARD - 标准版登录界面
		UCGameSDK.defaultSDK().setLoginUISwitch(UCLoginFaceType.USE_WIDGET);

		try {
			UCGameSDK.defaultSDK().initSDK(activity,
					UCLogLevel.DEBUG, debugMode, gpi,
					new UCCallbackListener<String>() {
						@Override
						public void callback(int code, String msg) {
							switch (code) {
							// 初始化成功,可以执行后续的登录充值操作
							case UCGameSDKStatusCode.SUCCESS:
								gameInitListener.initSuccess(false, null);
								break;
							case UCGameSDKStatusCode.FAIL:
								gameInitListener.initFail(msg);
							default:
								break;
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void login(Activity activity,
			final GameLoginListener gameLoginListener) {
		ucSdkLogin(activity, gameLoginListener);
	}

	private void ucSdkLogin(final Activity activity,
			final GameLoginListener gameLoginListener) {
		try {
			UCGameSDK.defaultSDK().login(activity,
					new UCCallbackListener<String>() {

						@Override
						public void callback(int code, String msg) {
							switch (code) {
							case UCGameSDKStatusCode.SUCCESS:
								//向服务器请求用户信息
								UserInfoApi api = new UserInfoApi();
								Log.e(TAG, "sid  ==== " + UCGameSDK.defaultSDK().getSid());
								api.sid = UCGameSDK.defaultSDK().getSid();
								api.gameId = SDKUtils.getMeteData(activity, "gameId");
								api.appId = SDKUtils.getAppId(activity);
								api.platformId = getPlatformId();
								if (api.sid ==null || api.gameId ==null || api.appId ==null || api.platformId ==null) {
									Toast.makeText(activity, "请求参数不能为空", Toast.LENGTH_SHORT).show();
									return;
								}
								api.setResponse(new JsonResponse(){

									@Override
									public void requestError(String string) {
										super.requestError(string);
										gameLoginListener.LoginFail(string);
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
											model.sessionId = UCGameSDK.defaultSDK().getSid();
											gameLoginListener.LoginSuccess(model);
										} else {
											gameLoginListener.LoginFail("用户信息获取失败！");
										}
									}
								
								});
								new NetTask().execute(api);
								break;

							// 登录失败。应该先执行初始化成功后再进行登录调用。
							case UCGameSDKStatusCode.NO_INIT:
								// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
								break;

							// 登录退出。该回调会在登录界面退出时执行。
							case UCGameSDKStatusCode.LOGIN_EXIT:
								// 登录界面关闭，游戏需判断此时是否已登录成功进行相应操作
								break;

							default:
								break;
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showFloat(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					// 创建悬浮按钮。该悬浮按钮将悬浮显示在GameActivity页面上，点击时将会展开悬浮菜单，菜单中含有
					// SDK 一些功能的操作入口。
					// 创建完成后，并不自动显示，需要调用showFloatButton(Activity,
					// double, double, boolean)方法进行显示或隐藏。
					UCGameSDK.defaultSDK().createFloatButton(activity,
							new UCCallbackListener<String>() {

								@Override
								public void callback(int statuscode, String data) {
									Log.d("``````floatButton Callback",
											"statusCode == " + statuscode
													+ "  data == " + data);
								}
							});
					UCGameSDK.defaultSDK().showFloatButton(activity, 100, 50, true);
				} catch (UCCallbackListenerNullException e) {
					e.printStackTrace();
				} catch (UCFloatButtonCreateException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void logOut(Activity activity, GameLogoutListener gameLogoutListener) {
		/**
		 * 选接功能<br>
		 * 游戏可通过调用下面方法，退出当前登录的账号<br>
		 * 通过退出账号侦听器（此侦听器在初始化前经由setLogoutNotifyListener 方法设置）<br>
		 * 把退出消息返回给游戏，游戏可根据状态码进行相应的处理。<br>
		 */
		this.gameLogoutListener = gameLogoutListener;
	
			try {
				UCGameSDK.defaultSDK().logout();
			} catch (UCCallbackListenerNullException e) {
				// 未设置退出侦听器
			}
		
	}

	@Override
	public void exit(Activity mActivity, final GameExitListener listener) {
		UCGameSDK.defaultSDK().exitSDK(mActivity, new UCCallbackListener<String>() {
            @Override
            public void callback(int code, String msg) {
                    if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
                            // 此加入继续游戏的代码

                    } else if (UCGameSDKStatusCode.SDK_EXIT == code) {
                            // 在此加入退出游戏的代码
                            Log.e(TAG, "退出SDK");
                            listener.onSuccess();
                    }
            }
    });
		
	}

	@Override
	public void pay(Activity activity, String money, String order,GameInfo gameInfo, String notifyUrl, String exInfo, 
 OrderGenerateListener listener) {
		ucSdkPay(activity, money, order, notifyUrl, exInfo, gameInfo, listener);
	}

	@Override
	public void createRole(Activity activity, GameInfo gameInfo, CreateRoleListener listener) {
		listener.onSuccess();
	}

	@Override
	public void setGameInfo(Activity activity, GameInfo gameInfo, SetGameInfoListener listener) {
		UCGameSDK.defaultSDK().notifyZone(gameInfo.getZoneName(),
				gameInfo.getRoleId(), gameInfo.getRoleName());
		try {
			JSONObject jsonExData = new JSONObject();
			jsonExData.put("roleId", gameInfo.getRoleId());
			jsonExData.put("roleName", gameInfo.getRoleName());
			jsonExData.put("roleLevel", gameInfo.getRoleLevel());
			jsonExData.put("zoneId", gameInfo.getZoneId());
			jsonExData.put("zoneName", gameInfo.getZoneName());
			UCGameSDK.defaultSDK().submitExtendData("loginGameRole", jsonExData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		listener.onSuccess();
	}

	@Override
	public boolean suportLogoutUI() {
		return true;
	}


	@Override
	public void onDestory() {

	}
	
	/**
	 * 必接功能<br>
	 * 悬浮按钮销毁<br>
	 * 悬浮按钮销毁需要在UI线程中调用<br>
	 */
	private void ucSdkDestoryFloatButton(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				// 悬浮按钮销毁功能
				UCGameSDK.defaultSDK().destoryFloatButton(activity);
			}
		});
	}
	
	/**
	 * 必接功能<br>
	 * 当游戏退出前必须调用该方法，进行清理工作。建议在游戏退出事件中进行调用，必须在游戏退出前执行<br>
	 * 如果游戏直接退出，而不调用该方法，可能会出现未知错误，导致程序崩溃<br>
	 */
	private void ucSdkExit(final Activity activity) {
		UCGameSDK.defaultSDK().exitSDK(activity, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
				if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
					// 此加入继续游戏的代码

				} else if (UCGameSDKStatusCode.SDK_EXIT == code) {
					// 在此加入退出游戏的代码
					ucSdkDestoryFloatButton(activity);
					System.exit(0);
				}
			}
		});
	}
	
	/**
	 * 必接功能<br>
	 * SDK支付功能<br>
	 * 调用SDK支付功能 如你在调用支付页面时，没有显示正确的支付页面，请检查以下几点：<br>
	 * 1、是否已经提交对应环境的支付回调地址给技术接口人，是否配置到对应的环境中<br>
	 * 2、检查pInfo.setServerId()传入的值是否正确。<br>
	 * 在联调环境中进行支付，可使用无效卡进行支付，只需符合卡号卡密长度位数即可<br>
	 * 当卡号卡密全部输入为1时，是支付失败的订单，服务器将会收到订单状态为F的订单信息<br>
	 * @param listener 
	 * @param order 
	 * @param notifyUrl 
	 * @param exInfo 
	 * @param money 
	 * @param activity 
	 * @param gameInfo 
	 */
	private void ucSdkPay(Activity activity, String money, String order, String notifyUrl, String exInfo,GameInfo gameInfo, final OrderGenerateListener listener) {
		PaymentInfo pInfo = new PaymentInfo(); // 创建Payment对象，用于传递充值信息

	    // 设置充值自定义参数，此参数不作任何处理，
		// 在充值完成后，sdk服务器通知游戏服务器充值结果时原封不动传给游戏服务器传值，字段为服务端回调的callbackInfo字段
		pInfo.setCustomInfo(exInfo);
		
		// 非必选参数，可不设置，此参数已废弃,默认传入0即可。
		// 如无法支付，请在开放平台检查是否已经配置了对应环境的支付回调地址，如无请配置，如有但仍无法支付请联系UC技术接口人。
		pInfo.setServerId(0);
		
		pInfo.setRoleId(gameInfo.getRoleId()); // 设置用户的游戏角色的ID，此为必选参数，请根据实际业务数据传入真实数据
		pInfo.setRoleName(gameInfo.getRoleName()); // 设置用户的游戏角色名字，此为必选参数，请根据实际业务数据传入真实数据
		pInfo.setGrade(gameInfo.getRoleLevel()); // 设置用户的游戏角色等级，此为可选参数
		
		// 非必填参数，设置游戏在支付完成后的游戏接收订单结果回调地址，必须为带有http头的URL形式。
		pInfo.setNotifyUrl(notifyUrl);
		pInfo.setTransactionNumCP(order);
		
		// 当传入一个amount作为金额值进行调用支付功能时，SDK会根据此amount可用的支付方式显示充值渠道
		// 如你传入6元，则不显示充值卡选项，因为市面上暂时没有6元的充值卡，建议使用可以显示充值卡方式的金额
		pInfo.setAmount(Integer.parseInt(money)/100.f);// 设置充值金额，此为可选参数

		try {
			UCGameSDK.defaultSDK().pay(activity, pInfo,
					new UCCallbackListener<OrderInfo>() {
						@Override
						public void callback(int statudcode, OrderInfo orderInfo) {
							if (statudcode == UCGameSDKStatusCode.NO_INIT) {
								// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
							}
							if (statudcode == UCGameSDKStatusCode.SUCCESS) {
								// 成功充值
								listener.onSuccess();
							}
							if (statudcode == UCGameSDKStatusCode.PAY_USER_EXIT) {
								// 用户退出充值界面。
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			// 异常处理
		}

	}

	@Override
	public void onPause(Activity activity) {
	}

	@Override
	public void onResume(Activity activity) {
		// TODO Auto-generated method stub
		
	}
	

/*	private UCCallbackListener<OrderInfo> payResultListener = new UCCallbackListener<OrderInfo>() {
		@Override
		public void callback(int statudcode, OrderInfo orderInfo) {
			if (statudcode == UCGameSDKStatusCode.NO_INIT) {
				// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
			}
			if (statudcode == UCGameSDKStatusCode.SUCCESS) {
				// 成功充值
				if (orderInfo != null) {
					String ordereId = orderInfo.getOrderId();// 获取订单号
					float orderAmount = orderInfo.getOrderAmount();// 获取订单金额
					int payWay = orderInfo.getPayWay();
					String payWayName = orderInfo.getPayWayName();
					System.out.print(ordereId + "," + orderAmount + ","
							+ payWay + "," + payWayName);
				}
			}
			if (statudcode == UCGameSDKStatusCode.PAY_USER_EXIT) {
				// 用户退出充值界面。
			}
		}

	};*/


}
