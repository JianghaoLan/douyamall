package org.lanjianghao.douyamall.order.dao;

import org.lanjianghao.douyamall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:45:24
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
