/**
 * @author ZingQBo
 * @time 2014年12月15日下午5:33:51
 */
package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.OrderGenerateApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.util.PhoneInformation;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

/**
 * @Description
 * @author ZengQBo
 * @time 2014年12月15日
 */
public class OrderGenerateService {
	private Iplatform iplatform;
	private Activity mActivity;
	private ICallback callback;
	private String money;
	private String notifyUrl;
	private GameInfo model;
	private String extInfo;

	public interface OrderGenerateListener {
		public void onSuccess();

		public void onFail(String value);
	}

	public OrderGenerateService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void dopay(GameInfo model, String money, String cpOrderId,
			String extInfo, String notifyUrl, ICallback callback) {
		this.callback = callback;
		this.money = money;
		this.model = model;
		this.extInfo = extInfo;
		// 生成订单
		orderGenerate(model, money, cpOrderId, extInfo, notifyUrl);
	}

	private OrderGenerateListener listener = new OrderGenerateListener() {

		@Override
		public void onSuccess() {
			if (callback != null) {
				SDKLog.e("msg", "Pay Success");
				callback.paySuccess(notifyUrl);
			}
		}

		@Override
		public void onFail(String value) {
			if (callback != null) {
				SDKLog.e("msg", "Pay Failed >> " + value);
				callback.onError(ICallback.PAY, value);
			}

		}
	};

	private void orderGenerate(GameInfo model, String money, String cpOrderId,
			String extInfo, String notifyUrl) {
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		OrderGenerateApi api = new OrderGenerateApi();
		String fixed = "0";
		if (money != null && Integer.parseInt(money) > 0) {
			fixed = "1";
		}
		api.appId = SDKUtils.getAppId(mActivity);
		api.platformId = iplatform.getPlatformId();
		api.uid = InitService.mUserInfoModel.id;
		api.zoneId = model.getZoneId();
		api.roleId = model.getRoleId();
		api.cpOrderId = cpOrderId;
		api.extInfo = extInfo;
		api.amount = money;
		api.notifyUrl = notifyUrl;
		api.fixed = fixed;
		api.loginTime = LoginService.loginTime;
		api.deviceId = phoneInformation.getDeviceCode();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);
	}

	private JsonResponse jsonResponse = new JsonResponse() {

		@Override
		public void requestError(String string) {
			super.requestError(string);
			SDKLog.e("msg", "Pay Failed >> " + string);
			callback.onError(ICallback.PAY, "Pay Failed");
		}

		@Override
		public void requestSuccess(JSONObject jsonObject) {
			int code = jsonObject.optInt("code");
			if (callback != null) {
				if (code == 0) {
					String orderId = jsonObject.optString("orderId");
					notifyUrl = jsonObject.optString("notifyUrl");
					iplatform.pay(mActivity, money, orderId, model, notifyUrl,
							extInfo, listener);
				} else {
					SDKLog.e("msg", "Pay Failed >> " + jsonObject.toString());
					callback.onError(ICallback.PAY, "Pay Failed");
				}
			} else {
				Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

}
