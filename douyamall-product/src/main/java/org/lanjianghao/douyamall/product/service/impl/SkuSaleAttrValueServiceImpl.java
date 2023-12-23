package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.douyamall.product.vo.Attr;
import org.lanjianghao.douyamall.product.vo.SkuSaleAttr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.SkuSaleAttrValueDao;
import org.lanjianghao.douyamall.product.entity.SkuSaleAttrValueEntity;
import org.lanjianghao.douyamall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuSaleAttrValueEntity> listBySkuId(Long skuId) {
        return this.list(new QueryWrapper<SkuSaleAttrValueEntity>().eq("sku_id", skuId));
    }

    @Override
    public List<SkuSaleAttr> listSaleAttrBySpuId(Long spuId) {

        return this.baseMapper.selectSaleAttrsBySpuId(spuId);
    }
}