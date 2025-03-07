package org.lanjianghao.douyamall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lanjianghao.common.vo.MemberVo;
import org.lanjianghao.douyamall.member.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.member.entity.MemberReceiveAddressEntity;
import org.lanjianghao.douyamall.member.service.MemberReceiveAddressService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 会员收货地址
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:30:22
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);

        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.updateById(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

//    @GetMapping("/list/{memberId}")
//    public List<MemberReceiveAddressEntity> listByMember(@PathVariable("memberId") Long memberId) {
//        return memberReceiveAddressService.listByMemberId(memberId);
//    }

    @GetMapping("/list")
    public List<MemberReceiveAddressEntity> list() {
        MemberVo memberVo = AuthInterceptor.loginUser.get();
        if (memberVo == null || memberVo.getId() == null) {
            return null;
        }

        return memberReceiveAddressService.listByMemberId(memberVo.getId());
    }
}
