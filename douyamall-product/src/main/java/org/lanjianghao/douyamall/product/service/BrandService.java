package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.BrandEntity;
import org.lanjianghao.douyamall.product.vo.BrandNameVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean updateCascadeById(BrandEntity brand);

    List<BrandNameVo> listBrandNames(List<Long> brandIds);
}

