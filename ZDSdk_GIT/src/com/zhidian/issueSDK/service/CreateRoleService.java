package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.CreatRoleApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.util.PhoneInformation;
import com.zhidian.issueSDK.util.SDKLog;
import com.zhidian.issueSDK.util.SDKUtils;

public class CreateRoleService {
	private Iplatform iplatform;
	private Activity mActivity;
	private GameInfo model;
	private ICallback callback;
	
	public interface CreateRoleListener {
		public void onSuccess();

		public void onFail(String value);
	}

	public CreateRoleService() {
	}
	
	public CreateRoleService(Activity activity, Iplatform iplateform) {
		this.mActivity = activity;
		this.iplatform = iplateform;
	}

	public void creatRole(GameInfo model, ICallback callback) {
		this.callback = callback;
		this.model = model;
		iplatform.createRole(mActivity, model, listener);
		

	}
	
	private CreateRoleListener listener = new CreateRoleListener() {
		
		@Override
		public void onSuccess() {
			sendToSDKServer(model);
		}
		
		@Override
		public void onFail(String value) {
			if (callback != null) {
				SDKLog.e("msg", "Create Role Failed >>  " + value);
				callback.onError(ICallback.CREATE_ROLE, "创建角色失败");
			}
		}
	};

	private void sendToSDKServer(GameInfo model) {
		PhoneInformation phoneInformation = new PhoneInformation(mActivity);
		CreatRoleApi api = new CreatRoleApi();
		api.appId = SDKUtils.getAppId(mActivity);
		api.platformId = iplatform.getPlatformId();
		api.uid = InitService.mUserInfoModel.id;
		api.zoneId = model.getZoneId();
		api.zoneName = model.getZoneName();
		api.roleId = model.getRoleId();
		api.roleName = model.getRoleName();
		api.roleLevel = model.getRoleLevel();
		api.deviceId = phoneInformation.getDeviceCode();
		api.setResponse(jsonResponse);
		new NetTask().execute(api);
	}
	
    private JsonResponse jsonResponse = new JsonResponse() {
        @Override
        public void requestError(String string) {
            super.requestError(string);
            if (callback != null) {
            	SDKLog.e("msg", "Create Role Failed");
        		callback.onError(ICallback.CREATE_ROLE, "create role failed");
            }
        }

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            int code = jsonObject.optInt("code") ;
            if (callback != null) {
            	if(code == 0) {
            		SDKLog.e("msg", "Create Role Success");
            		callback.createRoleSuccess();
            	}else {
            		SDKLog.e("msg", "Create Role Failed");
            		callback.onError(ICallback.CREATE_ROLE, "create role failed");
            	}
			}else {
				Toast.makeText(mActivity, "Callback不能为空！", Toast.LENGTH_SHORT)
				.show();
			}
        }
    };



}
