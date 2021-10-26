package com.nezha.gamelib.network;


/**
 *  寻宝物语  tap v5  其它v1
 *  云中歌    v1
 */
public class Urls {

    public static final String BASE_URL = "https://api-ios.duobaoyu.fun";

    public static final String POST_HEART_BEAT = BASE_URL + "/v1/heartbeat/index";//心跳-2M
    public static final String POST_REPORT = BASE_URL + "/v1/role/report";//角色上报
    public static final String POST_UID = BASE_URL + "/v1/user/uid";//uid登录-2M
    public static final String POST_JIGUANG = BASE_URL + "/v1/user/jiguang";//极光登录-2M
    public static final String POST_REALNAME = BASE_URL + "/v1/user/realname";//实名认证-2M
    public static final String POST_MODIFY_PWD = BASE_URL + "/v1/user/modify-pwd";//修改密码-不带验证码
    public static final String POST_MOBILE_BIND = BASE_URL + "/v1/user/mobile-bind";//绑定手机
    public static final String POST_MOBILE_BIND_CHANGE = BASE_URL + "/v1/user/change-mobile-bind";//手机换绑
    public static final String POST_MOBILE_SMS = BASE_URL + "/v1/user/mobile-sms";//获取验证码
    public static final String POST_CONTACT = BASE_URL + "/v1/contact";//联系
    public static final String POST_CREATE = BASE_URL + "/v3/trade/create";//下订单
    public static final String POST_SEARCH = BASE_URL + "/v3/trade/search";//查询订单
    public static final String POST_LOGOUT = BASE_URL + "/v1/user/logout";//退出统计数据
    public static final String BASE_REFERER_URL = BASE_URL;

    //v1 v5
    public static final String POST_MOBILE = BASE_URL + "/v5/user/mobile";//手机验证码注册或登录或忘记密码-2M
    public static final String POST_LOGIN = BASE_URL + "/v5/user/login";//账户密码登录
    public static final String POST_AUTO = BASE_URL + "/v5/user/auto";//自动注册/登录 新增字段has_idfa_auth-2M
}
