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
 *  13: 订单
 *  14: 购物车
 *  15: 物流
 */
public enum BizCodeEnum {
    /**
     * 10 通用
     */
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数校验不通过"),

    /**
     * 12 库存
     */
    INCORRECT_PURCHASE_ITEMS_EXCEPTION(12001, "采购需求有误"),
    INCORRECT_PURCHASE_STATUS_EXCEPTION(12002, "采购单状态有误")

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
