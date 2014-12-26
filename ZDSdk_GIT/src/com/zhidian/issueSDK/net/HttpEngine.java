package com.zhidian.issueSDK.net;

import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2014/12/5.
 */
public class HttpEngine {
    private String mUrl;
    private String mParam;
    private HttpURLConnection mConnection ;
    private static  final int TIME_OUT= 20000 ;
    public String excuteRquest(String url, Map<String,String> param, Method method) {
        mUrl = url;
        if(param!=null&&!param.isEmpty()){
            paramMapToString(param);
        }
        if (method == Method.POST) {
            return doPostRequest();
        } else {
            return doGetRequest();
        }
    }

    private void paramMapToString(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        String value = "";
        try {
			for (String key : param.keySet()) {
				Log.e("", key.trim() + " ======= " + param.get(key));// FIXME
				value = param.get(key);
				if (value != null) {
					value = URLEncoder.encode(value, "UTF-8");
				}
				sb.append(key.trim()).append("=").append(value).append("&");
			}
        	mParam = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String doGetRequest() {
        try {
            Log.e("HttpEngine","GET"+mUrl+"?"+mParam) ;
            URL url = new URL(mUrl+"?"+mParam) ;
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestMethod("GET");
            mConnection.setReadTimeout(TIME_OUT);
            mConnection.setConnectTimeout(TIME_OUT);
            mConnection.setRequestProperty("Accept-Encoding", "identity");
            mConnection.setDoInput(true);
            mConnection.connect();
            int responseCode = mConnection.getResponseCode() ;
            if(responseCode==200){//请求成功
                String encoding = mConnection.getContentEncoding() ;
                if(encoding!=null&&encoding.contains("gzip")) {
                    InputStream inputStream = new GZIPInputStream(mConnection.getInputStream());
                    return readInputStreamToString(inputStream) ;
                }else {
                    return readInputStreamToString(mConnection.getInputStream()) ;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return e.getMessage() ;
        } catch (IOException e) {
            e.printStackTrace();
            return  e.getMessage() ;
        }finally {
            mConnection.disconnect();
        }
        return null;
    }

    private String doPostRequest() {
        try {
            Log.e("HttpEngine","POST >> "+mUrl+"?"+mParam) ;
            URL url = new URL(mUrl+"?"+mParam) ;
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestMethod("POST");
            mConnection.setReadTimeout(TIME_OUT);
            mConnection.setConnectTimeout(TIME_OUT);
            mConnection.setRequestProperty("Accept-Encoding", "identity");
            mConnection.setDoInput(true);
            mConnection.setDoOutput(true);
            mConnection.setUseCaches(false);
            OutputStream os = mConnection.getOutputStream() ;
            os.write(mParam.getBytes());
            os.flush();
            os.close();
            mConnection.connect();
            int responseCode = mConnection.getResponseCode() ;
            if(responseCode==200){//请求成功
                String encoding = mConnection.getContentEncoding() ;
                if(encoding!=null&&encoding.contains("gzip")) {
                    InputStream inputStream = new GZIPInputStream(mConnection.getInputStream());
                    return readInputStreamToString(inputStream) ;
                }else {
                    return readInputStreamToString(mConnection.getInputStream()) ;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mConnection.disconnect();
        }
        return null;
    }

    private String readInputStreamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024] ;
        int len = 0 ;
        while ((len = stream.read(buffer))!=-1){
            outputStream.write(buffer,0,len);
        }
        return  new String(outputStream.toByteArray(),"UTF-8") ;
    }

    public enum Method {
        GET, POST;
    }
}
