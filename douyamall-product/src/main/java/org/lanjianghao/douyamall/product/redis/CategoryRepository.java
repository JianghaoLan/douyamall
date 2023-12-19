package org.lanjianghao.douyamall.product.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lanjianghao.douyamall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Repository
public class CategoryRepository {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public Map<String, List<Catalog2Vo>> getIndexCatalog() throws JsonProcessingException {
        String indexCatalog = redisTemplate.opsForValue().get("indexCatalog");
        if (indexCatalog == null) {
            return null;
        }
        return objectMapper.readValue(indexCatalog, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
    }

    public void setIndexCatalog(Map<String, List<Catalog2Vo>> catalog) throws JsonProcessingException {
        // 加入随机过期时间，防止缓存雪崩问题
        redisTemplate.opsForValue().set("indexCatalog", objectMapper.writeValueAsString(catalog),
                86400 + new Random().nextInt(7200), TimeUnit.SECONDS);
    }
}
