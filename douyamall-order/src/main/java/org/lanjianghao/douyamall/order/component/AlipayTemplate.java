package org.lanjianghao.douyamall.order.component;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import org.lanjianghao.douyamall.order.config.AlipayConfigProperties;
import org.lanjianghao.douyamall.order.constant.OrderConstant;
import org.lanjianghao.douyamall.order.exception.AlipayCloseFailedException;
import org.lanjianghao.douyamall.order.vo.AlipayPayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AlipayTemplate {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfigProperties properties;

    public String pay(AlipayPayVo payVo) throws AlipayApiException {
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(OrderConstant.ALIPAY_RETURN_URL);
        alipayRequest.setNotifyUrl(OrderConstant.ALIPAY_NOTIFY_URL);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = payVo.getOutTradeNo();
        //付款金额，必填
        String total_amount = payVo.getTotalAmount();
        //订单名称，必填
        String subject = payVo.getSubject();
        //商品描述，可空
        String body = payVo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        return alipayClient.pageExecute(alipayRequest).getBody();
    }

    public boolean checkSignature(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                    params,
                    properties.getAlipayPublicKey(),
                    properties.getCharset(),
                    properties.getSignType()); //调用SDK验证签名
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeByOrderSn(String orderSn) throws AlipayCloseFailedException {
        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderSn);

        alipayRequest.setBizContent(bizContent.toString());

        //请求
        AlipayTradeCloseResponse response;
        try {
            response = alipayClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            throw new AlipayCloseFailedException(e);
        }
        if (!response.isSuccess()) {
            String subCode = response.getSubCode();
            final List<String> acceptableCodes =
                    Arrays.asList("ACQ.REASON_ILLEGAL_STATUS",
                            "ACQ.REASON_TRADE_STATUS_INVALID",
                            "ACQ.TRADE_NOT_EXIST",
                            "ACQ.TRADE_STATUS_ERROR");
            if (acceptableCodes.contains(subCode)) {
                return;
            }
            throw new AlipayCloseFailedException();
        }
    }
}
