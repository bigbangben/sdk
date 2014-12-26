package com.zhidian.issueSDK.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 当前应用
 * @author Administrator
 *
 */
public class AppPreferences {
	 

	private static final int MODE = Context.MODE_APPEND;//_PRIVATE;
	private static final String fileName = "app";

	private static SharedPreferences readPreferences(Context ctx) {
		return ctx.getSharedPreferences(fileName , MODE);
	}

	private static SharedPreferences.Editor editPreferences(Context ctx) {
		SharedPreferences settings = readPreferences(ctx);
		return settings.edit();
	}
	
	/**
	 * 保存需要初始化的应用类型
	 * @param ctx
	 * @param md5Code
	 * @return
	 */
	public static boolean setAppType(Context ctx,int app) {
		try {
			SharedPreferences.Editor editor = editPreferences(ctx);
			editor.putInt("app", app);
			editor.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 
	 * 获取需要初始化的应用类型
	 * @param ctx 
	 * @return
	 */
	public static int getAppType(Context ctx) {
		return readPreferences(ctx).getInt("app",0); 
	}
	
	
	/**
	 * 保存DEBUG模式类型
	 * @param ctx
	 * @param md5Code
	 * @return
	 */
	public static boolean setDebugMode(Context ctx,boolean debugMode) {
		try {
			SharedPreferences.Editor editor = editPreferences(ctx);
			editor.putBoolean("debugMode", debugMode);
			editor.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 
	 * 获取需要初始化的应用类型
	 * @param ctx 
	 * @return
	 */
	public static boolean isDebugMode(Context ctx) {
		return readPreferences(ctx).getBoolean("debugMode",false); 
	}

	
}
