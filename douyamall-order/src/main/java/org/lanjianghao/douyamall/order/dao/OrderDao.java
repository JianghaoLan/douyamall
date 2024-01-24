package org.lanjianghao.douyamall.order.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Select("SELECT `status` FROM `oms_order` WHERE `order_sn` = #{orderSn}")
    Integer selectStatusByOrderSn(@Param("orderSn") String orderSn);

    @Update("UPDATE `oms_order` SET `status` = #{status} WHERE order_sn = #{orderSn}")
    void updateStatus(@Param("orderSn") String orderSn, @Param("status") Integer status);
}
