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

public class SDKUtils {

	public static String getMeteData(Context context, String tag) {
		ApplicationInfo info;
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String rex = info.metaData.getString(tag);
			if (rex == null) {
				return null;
			}
			if (rex.startsWith(tag + ":")) {
				return rex.split(":")[1];
			} else {
				return null;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMeteDataNoTag(Context context, String tag) {
		ApplicationInfo info;
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String rex = info.metaData.getString(tag);
			if (rex == null) {
				return null;
			}
			return rex;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAppId(Context context) {
		ApplicationInfo info;
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			String appId = info.metaData.getString("ZD_APPID");
			if (appId == null) {
				return null;
			}
			if (appId.startsWith("ZD_APPID:")) {

				return appId.split(":")[1];
			} else {
				return null;
			}
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
