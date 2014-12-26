package com.zhidian.issueSDK;

import com.zhidian.issueSDK.model.UserInfoModel;

/**
 * Created by Administrator on 2014/12/11.
 */
public interface ICallback {
    public static final int INIT = 1 ;
    public static final int LOGIN = 2 ;
    public static final int PAY = 3 ;
    public static final int CREATE_ROLE = 4 ;
    public static final int UPLOAD_GAME_INFO = 5 ;
    public static final int LOGOUT = 6 ;
    public static final int EXIT = 7 ;
    public void initSuccess() ;
    public void loginSuccess(UserInfoModel userInfoModle) ;
    public void setGameInfoSuccess(String loginTime);
    public void createRoleSuccess();
    public void logoutSuccess() ;
    public void paySuccess(String orderid) ;
    public void onError(int type,String message) ;
	public void exitSuccess();
}
