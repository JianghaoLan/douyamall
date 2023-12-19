package org.lanjianghao.douyamall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.lanjianghao.douyamall.product.entity.BrandEntity;
import org.lanjianghao.douyamall.product.service.BrandService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.sql.SQLOutput;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class DouyamallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    void testRedissonClient() {
        System.out.println(redissonClient);
    }

    @Test
    void testStringRedisTemplate() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world_" + UUID.randomUUID());

        String value = ops.get("hello");
        System.out.println("Value: " + value);
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("xiaomi");
//        brandService.save(brandEntity);
//        System.out.println("保存成功！");

//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("小米是一家年轻的公司");
//        brandService.updateById(brandEntity);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach(System.out::println);

    }

}
