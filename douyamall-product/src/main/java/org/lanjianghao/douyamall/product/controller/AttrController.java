package org.lanjianghao.douyamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.product.entity.ProductAttrValueEntity;
import org.lanjianghao.douyamall.product.service.ProductAttrValueService;
import org.lanjianghao.douyamall.product.vo.AttrNameVo;
import org.lanjianghao.douyamall.product.vo.AttrRespVo;
import org.lanjianghao.douyamall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.product.entity.AttrEntity;
import org.lanjianghao.douyamall.product.service.AttrService;
import org.lanjianghao.common.utils.PageUtils;



/**
 * 商品属性
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 17:01:58
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取特定分类的规格参数
     * @param params
     * @return
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId,
                  @PathVariable("attrType") String attrType){
//        PageUtils page = attrService.queryPage(params);
        PageUtils page = attrService.queryPage(params, catelogId, attrType);

        return R.ok().put("page", page);
    }

    @PostMapping("/list/attrNames")
    public R attrNames(@RequestBody List<Long> attrIds) {
        List<AttrNameVo> attrNames = attrService.listAttrNames(attrIds);
        return R.ok().put("data", attrNames);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo attr = attrService.getAttrInfoById(attrId);
//        System.out.println(attr.getGroupName());

        return R.ok().put("attr", attr);
    }

    /**
     * 获取spu规格
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R listAttrValuesBySpuId(@PathVariable("spuId") Long spuId){
//        AttrRespVo attr = attrService.getAttrInfoById(attrId);
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.listAttrValuesBySpuId(spuId);
//        System.out.println(attr.getGroupName());

        return R.ok().put("data", attrValueEntities);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
//		attrService.save(attr);
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
//		attrService.updateById(attr);
        attrService.updateAttrById(attr);

        return R.ok();
    }

    /**
     * 修改商品规格
     */
    @PostMapping("/update/{spuId}")
    public R updateAttrValuesBySpuId(@PathVariable("spuId") Long spuId,
                                     @RequestBody List<ProductAttrValueEntity> attrValues){
//		attrService.updateById(attr);
        productAttrValueService.updateAttrValuesBySpuId(spuId, attrValues);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
