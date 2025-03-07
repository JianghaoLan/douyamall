package org.lanjianghao.douyamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lanjianghao.douyamall.product.entity.BrandEntity;
import org.lanjianghao.douyamall.product.vo.BriefBrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.product.entity.CategoryBrandRelationEntity;
import org.lanjianghao.douyamall.product.service.CategoryBrandRelationService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 17:01:58
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取指定品牌关联的所有分类
     * @param brandId
     * @return
     */
    @GetMapping("/catelog/list")
    public R listCatelog(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.listCategory(brandId);

        return R.ok().put("data", data);
    }

    @GetMapping("/brands/list")
    public R listBrands(@RequestParam("catId") Long catId){
        List<BrandEntity> brandEntities = categoryBrandRelationService.listBrands(catId);
        List<BriefBrandVo> briefBrandVos = brandEntities.stream().map((entity) -> {
            BriefBrandVo briefBrandVo = new BriefBrandVo();
            briefBrandVo.setBrandId(entity.getBrandId());
            briefBrandVo.setBrandName(entity.getName());
            return briefBrandVo;
        }).collect(Collectors.toList());

        return R.ok().put("data", briefBrandVos);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
