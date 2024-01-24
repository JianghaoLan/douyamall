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
    public final static int ORDER_RELEASE_DELAY = 60000 * 30;     //ms
    public final static String MQ_ORDER_SEC_KILL_ORDER_QUEUE_NAME = "order.seckill.order.queue";
    public final static String MQ_ORDER_SEC_KILL_ORDER_BINDING_ROUTING_KEY = "order.seckill.order";
    public final static String MQ_ORDER_SEC_KILL_DELAY_QUEUE_NAME = "order.seckill.delay.queue";
    public final static String MQ_ORDER_SEC_KILL_RELEASE_ORDER_BINDING_ROUTING_KEY = "order.seckill.release.order";
    public final static String MQ_ORDER_SEC_KILL_RELEASE_ORDER_QUEUE_NAME = "order.seckill.release.order.queue";
    public final static String MQ_ORDER_SEC_KILL_CREATE_ORDER_BINDING_ROUTING_KEY = "order.seckill.create.order";
    public final static String MQ_SEC_KILL_RELEASE_STOCK_BINDING_ROUTING_KEY = "seckill.release.stock";
    public final static String MQ_SEC_KILL_EVENT_EXCHANGE_NAME  = "seckill-event-exchange";
    public final static int ORDER_SEC_KILL_RELEASE_DELAY = 60000 * 2;       //2分钟后未付款自动取消订单


    /**
     * 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    public final static String ALIPAY_RETURN_URL = "http://member.douyamall.com/orderlist.html";
    /**
     * 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    public final static String ALIPAY_NOTIFY_URL = "http://frp-fly.top:51725/paid/alipay";

    public enum OrderDeleteStatusEnum {
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
