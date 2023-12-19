package org.lanjianghao.douyamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lanjianghao.common.validation.groups.AddGroup;
import org.lanjianghao.common.validation.groups.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import org.lanjianghao.douyamall.product.service.CategoryService;
import org.lanjianghao.common.utils.R;



/**
 * 商品三级分类
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 17:01:58
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 树形分类
     */
    @RequestMapping("/list/tree")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryEntity> entities = categoryService.listWithTree();

        return R.ok().put("data", entities);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody CategoryEntity category){
//		categoryService.updateById(category);
		categoryService.updateCascadeById(category);

        return R.ok();
    }

    @RequestMapping("/update/batch")
    public R updateBatch(@Validated({UpdateGroup.class}) @RequestBody CategoryEntity[] categories) {
        categoryService.updateBatchById(Arrays.asList(categories));
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
//		categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }
}
