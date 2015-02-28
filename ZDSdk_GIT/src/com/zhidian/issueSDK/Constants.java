package com.zhidian.issueSDK;


public class Constants {

    // 应用id,需要接入方申请,开发者重点关注
    // 生产环境
    public static String API_KEY = "7EBF116B7DC847C4A109F51C858320E4";
    // 测试环境.开发者不需要关注
    // public static String API_KEY = "C182B2152A414E8E8BA0CC8434AA2D33";

    // 测试私钥,需要接入方申请(接入支付的时候需要，直接入账号不需要用到)开发者重点关注
    // 为安全考虑，这个可以放到服务器去，由服务管理。
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN33eE2nD7fcBF/W"
            + "NxTukvffy9NTXeWduFjfsyzVXPLXbxysQDsOJpoXIYhwU0Dif1bpT9BHY74Jnymw"
            + "3+/D2bTM1mc+r0G84hSQ67wjL4fr3gY9UP5GgUCEX2t2lp9CLv0RU68elISSCE7O"
            + "r+jN0kXLxhC1ZlxEmskNc8y7o87jAgMBAAECgYEAoWRi0PN79k+/zn9PpaSisCDF"
            + "b27agy5e8CAXg63P27LRU6PbQBVV9AyFkVM69Z66wFL8eZCu8WrFk+bLrOZW0Ei2"
            + "v8MHru1aYkX1Oa0hprob8O0hlr8Wxri1VHxSXOHq3MTD/NM9bAB2Kb6coqpR4T2P"
            + "oajtk5zXyNZMiDeiPYECQQDzRm6RlXaKorHRbAhYXfktQ/0o+hZSidzYaDDKlUij"
            + "ZFF2CmczK93/na0HRwoIEUTyucLdL2BVipyu5cu7rb6xAkEA6ZO1O9WkJRxWxtnO"
            + "5h0HNsEsH1mSRa5sjK1i2QJ4h1OxLJz4+P/UrXAvj1/sgnfxUG/eDh0WOTmz6V37"
            + "0fCT0wJAGzwyWrgZ6lFmiOSIVqRGpiurZvAAmcL3Z37an4Nw+2HawNVPUmpB00Eq"
            + "wtrQI7ETP/1N9Ic+SLVY7zeoxF0iMQJBAKSQ+xmjFjlHVCRaBRm/zftX8pxL4XDS"
            + "yYv8BS7cPMsrviKungPhS5i+9+NONDZgB1ci2hKbj7LV4tpC608o7x0CQDY8BmLv"
            + "63BNxS9/1s7X6thmVzP6co2fKdpWr5gw3E5bBXr2VJAInc5CfuDjaX2iRiYjvYLp" + "C8QOWfxqTBLokY0=";

    public final static int LOGIN_REQUEST_CODE = 111;
    public final static int UPGRADE_USING_ACCOUNT_CODE = 222;

    public static class ResultCode {
        public static final int LOGIN_SUCCESS = 1001;
        public static final int UPGRADE_USING_ACCOUNT_SUCCESS = 1007;
    }

    public static final String RE_LOGION = "reLogion";
    public static final String ACCOUNT_STATUS = "accountStatus";
    public static final String ASSOCIATE_STRING = "as";
    public static final String TELEPHONE_NUMBER = "tn";

    public static final String PLAYER_ID = "pid";
    public static final String NAME = "na";

}
