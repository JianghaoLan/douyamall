package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.douyamall.product.entity.AttrAttrgroupRelationEntity;
import org.lanjianghao.douyamall.product.service.AttrAttrgroupRelationService;
import org.lanjianghao.douyamall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.ProductAttrValueDao;
import org.lanjianghao.douyamall.product.entity.ProductAttrValueEntity;
import org.lanjianghao.douyamall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    AttrAttrgroupRelationService relationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> listAttrValuesBySpuId(Long spuId) {
        return this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    }

    @Transactional
    @Override
    public void updateAttrValuesBySpuId(Long spuId, List<ProductAttrValueEntity> attrValues) {
        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        attrValues.forEach(attrValue -> attrValue.setSpuId(spuId));
        this.saveBatch(attrValues);
    }

}