package org.lanjianghao.common.constant;

public class AuthConstant {
    public static final String SMS_REDIS_KEY_PREFIX = "sms:code:";
    public static final int SMS_EXPIRE = 10;    //minutes
    public static final int SMS_MIN_SEND_INTERVAL = 1;
    public static final String LOGIN_USER_SESSION_KEY = "loginUser";

    public enum OAuth2PlatformEnum {
        WEIBO(1, "微博");

        private final int code;
        private final String msg;

        OAuth2PlatformEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static class WeiboOpenConstant {
        public static final String OAUTH2_URL = "https://api.weibo.com/oauth2/access_token";
        public static final String OAUTH2_REDIRECT_URI = "http://auth.douyamall.com/oauth2/weibo/success";
        public static final String OAUTH2_CLIENT_ID = "208483731";
        public static final String OAUTH2_CLIENT_SECRET = "273c21abfc5ea3bb0032a8c08313536e";

        public static final String USER_URL = "https://api.weibo.com/2/users/show.json";
    }
}
