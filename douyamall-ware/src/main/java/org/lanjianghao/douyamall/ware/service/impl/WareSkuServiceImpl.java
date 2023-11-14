package org.lanjianghao.douyamall.ware.service.impl;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.ware.feign.ProductFeignService;
import org.lanjianghao.douyamall.ware.vo.AddStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.ware.dao.WareSkuDao;
import org.lanjianghao.douyamall.ware.entity.WareSkuEntity;
import org.lanjianghao.douyamall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String wareId = (String)params.get("wareId");
        if (StringUtils.hasLength(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        String skuId = (String)params.get("skuId");
        if (StringUtils.hasLength(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    private String getSkuName(Long skuId) {
        if (skuId == null) {
            return null;
        }
        try {
            R r = productFeignService.getSkuInfo(skuId);
            if (r.getCode() == 0) {
                Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
                return (String) skuInfo.get("skuName");
            }
        } catch (Exception ignored) {}

        return null;
    }

    @Override
    public void addStocks(List<AddStockVo> addStockVos) {
        addStockVos.forEach(vo -> {
            boolean exists = this.baseMapper.exists(new QueryWrapper<WareSkuEntity>()
                    .eq("sku_id", vo.getSkuId())
                    .eq("ware_id", vo.getWareId()));
            if (exists) {
                //更新记录
                this.baseMapper.addStock(vo);
            } else {
                //插入新纪录
                WareSkuEntity wareSku = new WareSkuEntity();
                wareSku.setSkuId(vo.getSkuId());
                wareSku.setWareId(vo.getWareId());
                wareSku.setStock(vo.getStock());
                wareSku.setStockLocked(0);
                wareSku.setSkuName(getSkuName(vo.getSkuId()));
                this.save(wareSku);
            }
        });
    }

}