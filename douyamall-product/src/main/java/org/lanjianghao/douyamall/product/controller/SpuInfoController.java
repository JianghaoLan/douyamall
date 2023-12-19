package org.lanjianghao.douyamall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.lanjianghao.douyamall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.product.entity.SpuInfoEntity;
import org.lanjianghao.douyamall.product.service.SpuInfoService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * spu信息
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 17:01:58
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = spuInfoService.queryPage(params);
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo spuSaveVo){
//		spuInfoService.save(spuInfo);
        spuInfoService.saveSpu(spuSaveVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 商品上架
     * @param spuId 商品id
     */
    @PostMapping("/{spuId}/up")
    public R upSpu(@PathVariable("spuId") Long spuId) {
        spuInfoService.upSpu(spuId);
        return R.ok();
    }
}
