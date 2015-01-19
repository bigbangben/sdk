package com.zhidian.issueSDK.util;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.zhidian.issueSDK.model.InitInfo;


public class SDKUtils {
	public static InitInfo getMeteData(Context context) {
		ApplicationInfo info;
		InitInfo model = new InitInfo();
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String appId = info.metaData.getString("appId");
			String appKey = info.metaData.getString("appKey");
			String screenOrientation = info.metaData.getString("screenOrientation");
			if (appId != null & appId.startsWith("appId:")) {
				model.setAppId(appId.split(":")[1]);
			}

			if (appKey != null & appKey.startsWith("appKey")) {
				model.setAppKey(appKey.split(":")[1]);
			}

			if (screenOrientation != null & screenOrientation.startsWith("screenOrientation")) {
				model.setScreenOrientation(Integer.valueOf(screenOrientation.split(":")[1]));
			}
			return model;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
	
    public static String getAppId(Context context) {
       ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String gameId = info.metaData.getString("gameId");
        	if (gameId.startsWith("gameId:")) {
        		gameId = gameId.split(":")[1];
        	}
            return gameId;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String getSKCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String mapToJsonArrayString(Map<String, Object> map) {
        if (map.isEmpty())
            return null;
        JSONArray jsonArray = new JSONArray();
        for (String key : map.keySet()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key", key);
                jsonObject.put("value", map.get(key) + "");
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }


    public static String mapToJson(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();
        for (String key : map.keySet()) {
            try {
                jsonObject.put(key, map.get(key));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        if (str.length() == 0) {
            return true;
        }
        return false;
    }

}
