package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.SpuInfoEntity;
import org.lanjianghao.douyamall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpu(SpuSaveVo spuSaveVo);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

