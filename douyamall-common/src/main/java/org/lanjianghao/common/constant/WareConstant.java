package org.lanjianghao.common.constant;

public class WareConstant {

    public final static int STOCK_RELEASE_DELAY = 60000 * 35;     //ms

    public final static String MQ_STOCK_EVENT_EXCHANGE_NAME = "stock-event-exchange";
    public final static String MQ_STOCK_RELEASE_STOCK_QUEUE_NAME = "stock.release.stock.queue";
    public final static String MQ_STOCK_DELAY_QUEUE_NAME = "stock.delay.queue";
    public final static String MQ_STOCK_RELEASE_BINDING_ROUTING_KEY = "stock.release.#";
    public final static String MQ_STOCK_DELAY_DEAD_ROUTING_KEY = "stock.release";
    public final static String MQ_STOCK_LOCKED_BINDING_ROUTING_KEY = "stock.locked";

    public enum OrderTaskLockStatusEnum {
        LOCKED(1, "已锁定"), RELEASED(2, "已解锁"), DEDUCTED(3, "已扣除");

        private final int code;
        private final String msg;

        OrderTaskLockStatusEnum(int code, String msg) {
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
