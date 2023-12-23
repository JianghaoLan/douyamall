package org.lanjianghao.douyamall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Md5Tests {
    @Test
    public void testMd5() {
        String s = DigestUtils.md5Hex("13254325");
        System.out.println(s);

        String s1 = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println(s1);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
    }
}
