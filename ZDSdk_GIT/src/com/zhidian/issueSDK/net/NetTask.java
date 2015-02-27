package com.zhidian.issueSDK.net;


import android.os.AsyncTask;

/**
 * Created by Administrator on 2014/12/5.
 */
public class NetTask extends AsyncTask<Request,Integer,String> {
    private HttpEngine mHttpEngine ;
    private IHttpResponse mHttpResponse ;
    public NetTask() {
        mHttpEngine = new HttpEngine() ;
    }


    @Override
    protected String doInBackground(Request... params) {
        Request request = params[0] ;
        mHttpResponse = request.getHttpResponse() ;
        return mHttpEngine.excuteRquest(request.getUrl(),request.getParams(),request.getMethod()) ;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        if(TextUtils.isEmpty(s)){
//            Log.e("NetTask", "net error");
//            return  ;
//        }
        if(mHttpResponse!=null){
            mHttpResponse.response(s);
        }
    }
    
    

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
