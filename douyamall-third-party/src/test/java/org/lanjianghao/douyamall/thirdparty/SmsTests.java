package org.lanjianghao.douyamall.thirdparty;

import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lanjianghao.douyamall.thirdparty.component.SmsComponent;
import org.lanjianghao.douyamall.thirdparty.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTests {
    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSendSms() {
        smsComponent.sendSmsCode("17638344482", "1234", 3);
    }

}
