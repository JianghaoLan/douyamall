package org.lanjianghao.douyamall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lanjianghao.common.to.SkuHasStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.ware.entity.WareSkuEntity;
import org.lanjianghao.douyamall.ware.service.WareSkuService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 商品库存
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 16:05:10
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 批量查询sku是否有库存
     */
    @PostMapping("/list/hasstock")
    public R listHasStocksBySkuIds(@RequestBody List<Long> skuIds) {
        List<SkuHasStockTo> vos = wareSkuService.listHasStocksBySkuIds(skuIds);

        return R.ok().put("data", vos);
    }
}
