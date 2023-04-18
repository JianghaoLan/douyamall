package org.lanjianghao.douyamall.coupon.dao;

import org.lanjianghao.douyamall.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:11:38
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
