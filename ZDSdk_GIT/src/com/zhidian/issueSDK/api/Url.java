package com.zhidian.issueSDK.api;

/**
 * Created by Administrator on 2014/12/11.
 */
public class Url {
//	public static String BASE_URL= "http://zdsdktest.zhidian3g.cn/" ;   //测试环境
	public static String BASE_URL= "http://rsservice.y6.cn/" ;          //正式环境
    /**
     * 初始化URL
     */
    public static String  INIT_URL = "account/initial" ;
    /**
     * 登录URL
     */
    public static final String LOGIN_URL = "account/login";
    /**
     * 用户心跳URL
     */
    public static final String HEARTBEAT_URL = "account/heartbeat";
    /**
     * 帐号注销URL
     */
    public static final String LOGOUT_URL = "account/logout";
    /**
     * 游戏退出URL
     */
    public static final String QUIT_URL = "account/quit";
    /**
     * 创建角色URL
     */
    public static final String ROLE_ESTABLISH_URL = "account/role/establish";
    /**
     * 生成订单URL
     */
    public static final String ORDER_GENERATE_URL = "account/order/generate";
    /**
     * 请求用户信息URL
     */
    public static final String GET_USERINFO_URL = "platform/ucgame/session";
}
