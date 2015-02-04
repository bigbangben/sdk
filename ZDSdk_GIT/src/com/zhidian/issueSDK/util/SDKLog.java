package com.zhidian.issueSDK.util;

import android.util.Log;


public class SDKLog {
	private static  boolean DEBUG= true;
	public static void v(String tag,Object o){
		if(DEBUG){
			Log.v(tag, o.toString()) ;
		}
	}
	public static void i(String tag,Object o){
		if(DEBUG){
			Log.i(tag, o.toString()) ;
		}
	}
	
	public static void d(String tag,Object o){
		if(DEBUG){
			Log.d(tag, o.toString()) ;
		}
	}
	public static void e(String tag,Object o){
		if(DEBUG){
			Log.e(tag, o.toString()) ;
		}
	}
}
