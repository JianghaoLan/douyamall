package org.lanjianghao.common.constant;

public class WareConstant {
    public enum PurchaseEnum {
        PURCHASE_STATUS_CREATED(0, "新建"),
        PURCHASE_STATUS_ASSIGNED(1, "已分配"),
        PURCHASE_STATUS_RECEIVED(2, "已领取"),
        PURCHASE_STATUS_COMPLETED(3, "已完成"),
        PURCHASE_STATUS_HASERROR(4, "有异常");

        private final int code;
        private final String msg;

        PurchaseEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return this.code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum PurchaseDetailEnum {
        PURCHASE_DETAIL_STATUS_CREATED(0, "新建"),
        PURCHASE_DETAIL_STATUS_ASSIGNED(1, "已分配"),
        PURCHASE_DETAIL_STATUS_BUYING(2, "正在采购"),
        PURCHASE_DETAIL_STATUS_COMPLETED(3, "已完成"),
        PURCHASE_DETAIL_STATUS_FAILED(4, "采购失败");

        private final int code;
        private final String msg;

        PurchaseDetailEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return this.code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
