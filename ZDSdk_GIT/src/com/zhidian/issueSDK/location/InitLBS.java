package com.zhidian.issueSDK.location;


import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.zhidian.issueSDK.service.InitService;

public class InitLBS {
	
	public void initLBS(Context context){
		// 获取到LocationManager对象
				LocationManager locationManager = (LocationManager) context.getSystemService(Activity.LOCATION_SERVICE);
				// 创建一个Criteria对象
				Criteria criteria = new Criteria();
				// 设置粗略精确度
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				// 设置是否需要返回海拔信息
				criteria.setAltitudeRequired(false);
				// 设置是否需要返回方位信息
				criteria.setBearingRequired(false);
				// 设置是否允许付费服务
				criteria.setCostAllowed(true);
				// 设置电量消耗等级
				criteria.setPowerRequirement(Criteria.POWER_HIGH);
				// 设置是否需要返回速度信息
				criteria.setSpeedRequired(false);

				// 根据设置的Criteria对象，获取最符合此标准的provider对象
				String currentProvider = locationManager
						.getBestProvider(criteria, true);
				Log.d("Location", "currentProvider: " + currentProvider);
				// 根据当前provider对象获取最后一次位置信息
				if(currentProvider==null) return ;
				Location currentLocation = locationManager
						.getLastKnownLocation(currentProvider);
				// 如果位置信息为null，则请求更新位置信息
				/*if (currentLocation == null) {
					locationManager.requestLocationUpdates(currentProvider, 0, 0,
							locationListener);
				}*/
				// 直到获得最后一次位置信息为止，如果未获得最后一次位置信息，则显示默认经纬度
				// 每隔10秒获取一次位置信息
					currentLocation = locationManager
							.getLastKnownLocation(currentProvider);
					if (currentLocation != null) {
						Log.e("Location", "Latitude: " + currentLocation.getLatitude());
						Log.e("Location", "location: " + currentLocation.getLongitude());
                        InitService.latitued =currentLocation.getLatitude()+"" ;
                        InitService.lontitued  = currentLocation.getLongitude()+"" ;
						//http://maps.google.com/maps/api/geocode/json?latlng=23.910093,113.403945&language=zh-CN&sensor=false
						//String url = "http://maps.google.com/maps/api/geocode/json?latlng="+23.910093+","+113.403945+"&language=zh-CN&sensor=false";
						String url = "http://maps.google.com/maps/api/geocode/json?latlng="+currentLocation.getLatitude()+","+currentLocation.getLongitude()+"&language=zh-CN&sensor=false";
						new RequestLBS(url);
					} 
	}

}
