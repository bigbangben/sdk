package com.zhidian.issueSDK.location;

import com.zhidian.issueSDK.service.InitService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InterruptedIOException;

public class RequestLBS extends Thread{
	private String url;
	public RequestLBS(String url) {
		this.url = url;
		this.start();
	}
	@Override
	public void run() {
		super.run();
		getLBSInfo(url);
	}
	
	
	public  void getLBSInfo(String url){
		try {
			DefaultHttpClient defaultHttpClient =new DefaultHttpClient();
			HttpGet httpGet=new HttpGet(url);
			HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
			String result = null;
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);
				String results = result;
				if(results!=null || !"".equals(results)){
					JSONObject resultJson = new JSONObject(results);
					String string = resultJson.getString("status");
					if("OK".equals(string)){
						JSONArray resultJsonArray = resultJson.optJSONArray("results");
						if(resultJsonArray!=null){
							if(resultJsonArray.length()>0){
								JSONObject jo =resultJsonArray.optJSONObject(0);
										String optString = jo.optString("formatted_address");
										if(optString !=null || !"".equals(optString)){
                                            InitService.location= optString ;
										}
							}
						}
					}
				}
			}

		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedIOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
