package org.lanjianghao.common.exception;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *  11: 商品
 *  12: 库存
 *  13: 认证
 *  14: 会员
 *  15: 订单
 *  16: 购物车
 *  17: 物流
 */
public enum BizCodeEnum {
    /**
     * 10 通用
     */
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数校验不通过"),
    TOO_MANY_REQUEST(10003, "请求过于频繁"),

    /**
     * 11 商品
     */
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    SEC_KILL_SERVICE_ACCESS_FAILED_EXCEPTION(11001, "访问秒杀服务失败"),

    /**
     * 12 库存
     */
    INCORRECT_PURCHASE_ITEMS_EXCEPTION(12001, "采购需求有误"),
    INCORRECT_PURCHASE_STATUS_EXCEPTION(12002, "采购单状态有误"),
    GET_FARE_ERROR_EXCEPTION(12003, "运费查询失败"),
    NO_ENOUGH_STOCK_EXCEPTION(12004, "库存锁定失败"),

    /**
     * 13 认证
     */
    SMS_CODE_SEND_INTERVAL_EXCEPTION(13001, "验证码发送频率太高，请稍后再试"),
    SMS_SEND_CODE_EXCEPTION(13001, "验证码发送失败，请稍后再试"),

    /**
     * 14 会员
     */
    USERNAME_EXISTS_EXCEPTION(14001, "用户名已存在"),
    MOBILE_EXISTS_EXCEPTION(14002, "该手机号已经注册"),

    LOGIN_FAILED_EXCEPTION(14003, "账号或密码错误"),

    OAUTH2_USER_NOT_EXISTS_EXCEPTION(14004, "该第三方用户还未注册"),
    OAUTH2_LOGIN_FAILED_EXCEPTION(14002, "第三方用户登录失败，请稍后再试"),
    OAUTH2_REGISTER_FAILED_EXCEPTION(14003, "第三方用户注册失败，请稍后再试"),

    /**
     * 15 订单
     */
    ORDER_TOKEN_VERIFY_FAILED_EXCEPTION(15001, "订单Token验证失败"),
    ORDER_CREATE_FAILED_EXCEPTION(15002, "订单创建失败"),
    ORDER_PRICE_CHECK_FAILED_EXCEPTION(15003, "订单验价失败"),

    /**
     * 16 购物车
     */
    SKU_NOT_EXISTS_EXCEPTION(16001, "购物车不存在此商品")
    ;



    private final int code;
    private final String message;
    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
