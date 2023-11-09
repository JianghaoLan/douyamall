package org.lanjianghao.douyamall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lanjianghao.common.to.SkuLadderTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.coupon.entity.SkuLadderEntity;
import org.lanjianghao.douyamall.coupon.service.SkuLadderService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 商品阶梯价格
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:11:38
 */
@RestController
@RequestMapping("coupon/skuladder")
public class SkuLadderController {
    @Autowired
    private SkuLadderService skuLadderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuLadderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SkuLadderEntity skuLadder = skuLadderService.getById(id);

        return R.ok().put("skuLadder", skuLadder);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuLadderEntity skuLadder){
		skuLadderService.save(skuLadder);

        return R.ok();
    }

    @PostMapping("/save/batch")
    public R saveBatch(@RequestBody List<SkuLadderTo> skuLadderTos){
        List<SkuLadderEntity> SkuLadderEntities = skuLadderTos.stream().map(skuLadderTo -> {
            SkuLadderEntity skuLadderEnt = new SkuLadderEntity();
            BeanUtils.copyProperties(skuLadderTo, skuLadderEnt);
            skuLadderEnt.setAddOther(skuLadderTo.getCountStatus());
            return skuLadderEnt;
        }).collect(Collectors.toList());

        skuLadderService.saveBatch(SkuLadderEntities);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuLadderEntity skuLadder){
		skuLadderService.updateById(skuLadder);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		skuLadderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
