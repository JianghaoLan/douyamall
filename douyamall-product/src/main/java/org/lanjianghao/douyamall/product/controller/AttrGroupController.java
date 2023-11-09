package org.lanjianghao.douyamall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lanjianghao.douyamall.product.entity.AttrAttrgroupRelationEntity;
import org.lanjianghao.douyamall.product.entity.AttrEntity;
import org.lanjianghao.douyamall.product.service.AttrAttrgroupRelationService;
import org.lanjianghao.douyamall.product.service.AttrService;
import org.lanjianghao.douyamall.product.service.CategoryService;
import org.lanjianghao.douyamall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.product.entity.AttrGroupEntity;
import org.lanjianghao.douyamall.product.service.AttrGroupService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 属性分组
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 17:01:58
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntities = attrService.listRelatedAttrs(attrgroupId);

        return R.ok().put("data", attrEntities);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params, @PathVariable("attrgroupId") Long attrgroupId) {
        PageUtils page = attrService.queryNoRelationAttrsPage(params, attrgroupId);

        return R.ok().put("page", page);
    }

    @RequestMapping("/attr/relation")
    public R saveAttrAttrGroupRelations(@RequestBody List<AttrAttrgroupRelationEntity> relationEntities) {
        relationService.saveBatch(relationEntities);

        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R listAttrGroupsWithAttrs(@PathVariable("catelogId") Long catId) {
        List<AttrGroupWithAttrsVo> vos = attrGroupService.listAttrGroupsWithAttrsByCatelogId(catId);
        return R.ok().put("data", vos);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        long categoryId = attrGroup.getCatelogId();
        Long[] catelogPath = categoryService.findCategoryPath(categoryId);
        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @RequestMapping("/attr/relation/delete")
    public R attrRelationDelete(@RequestBody AttrAttrgroupRelationEntity[] relationEntities){
        relationService.removeRelations(relationEntities);

        return R.ok();
    }

}
