package org.lanjianghao.douyamall.order.constant;

public class OrderConstant {
    public final static String ORDER_TOKEN_PREFIX = "order:token:";
    public final static int ORDER_TOKEN_EXPIRE = 600;   //second

    public final static int ORDER_AUTO_CONFIRM_DAY = 14;

    public final static String MQ_ORDER_EVENT_EXCHANGE_NAME = "order-event-exchange";
    public final static String MQ_ORDER_RELEASE_ORDER_QUEUE_NAME = "order.release.order.queue";
    public final static String MQ_ORDER_DELAY_QUEUE_NAME = "order.delay.queue";
    public final static String MQ_ORDER_RELEASE_ORDER_BINDING_ROUTING_KEY = "order.release.order";
    public final static String MQ_ORDER_CREATE_ORDER_BINDING_ROUTING_KEY = "order.create.order";
    public final static String MQ_ORDER_DELAY_QUEUE_DEAD_ROUTING_KEY = "order.release.order";
    public final static String MQ_ORDER_RELEASE_OTHER_BINDING_ROUTING_KEY = "order.release.other.#";
    public final static int ORDER_RELEASE_DELAY = 30000;     //ms


    public static enum OrderDeleteStatusEnum {
        DELETED(1, "已删除"), NOT_DELETED(0, "未删除");

        private final int code;
        private final String msg;

        OrderDeleteStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() { return code; }
        public String getMsg() { return msg; }
    }
}
