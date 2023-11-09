package org.lanjianghao.douyamall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.lanjianghao.common.to.MemberPriceTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.coupon.entity.MemberPriceEntity;
import org.lanjianghao.douyamall.coupon.service.MemberPriceService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 商品会员价格
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:11:38
 */
@RestController
@RequestMapping("coupon/memberprice")
public class MemberPriceController {
    @Autowired
    private MemberPriceService memberPriceService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberPriceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberPriceEntity memberPrice = memberPriceService.getById(id);

        return R.ok().put("memberPrice", memberPrice);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberPriceEntity memberPrice){
		memberPriceService.save(memberPrice);

        return R.ok();
    }

    /**
     * 批量保存
     */
    @PostMapping("/save/batch")
    public R saveBatch(@RequestBody List<MemberPriceTo> memberPriceTos){
        List<MemberPriceEntity> entities = memberPriceTos.stream().map(to -> {
            MemberPriceEntity ent = new MemberPriceEntity();
            ent.setSkuId(to.getSkuId());
            ent.setMemberLevelId(to.getId());
            ent.setMemberLevelName(to.getName());
            ent.setMemberPrice(to.getPrice());
            ent.setAddOther(1);
            return ent;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(entities);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberPriceEntity memberPrice){
		memberPriceService.updateById(memberPrice);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberPriceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
