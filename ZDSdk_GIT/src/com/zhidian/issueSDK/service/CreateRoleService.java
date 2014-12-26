package com.zhidian.issueSDK.service;

import org.json.JSONObject;

import android.app.Activity;

import com.zhidian.issueSDK.ICallback;
import com.zhidian.issueSDK.api.CreatRoleApi;
import com.zhidian.issueSDK.model.GameInfo;
import com.zhidian.issueSDK.net.JsonResponse;
import com.zhidian.issueSDK.net.NetTask;
import com.zhidian.issueSDK.platform.Iplatform;
import com.zhidian.issueSDK.util.PhoneInformation;
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
		iplatform.createRole(model, listener);
		

	}
	
	private CreateRoleListener listener = new CreateRoleListener() {
		
		@Override
		public void onSuccess() {
			sendToSDKServer(model);
		}
		
		@Override
		public void onFail(String value) {
			
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
        }

        @Override
        public void requestSuccess(JSONObject jsonObject) {
            int code = jsonObject.optInt("code") ;
            if(code == 0) {
            	callback.createRoleSuccess();
            }else {
            	callback.onError(ICallback.CREATE_ROLE, jsonObject.toString());
            }
        }
    };



}
