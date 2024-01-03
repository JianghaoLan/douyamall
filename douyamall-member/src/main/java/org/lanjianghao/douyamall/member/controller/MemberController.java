package org.lanjianghao.douyamall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.lanjianghao.common.exception.BizCodeEnum;
import org.lanjianghao.douyamall.member.feign.CouponFeignService;
import org.lanjianghao.douyamall.member.vo.MemberLoginVo;
import org.lanjianghao.douyamall.member.vo.MemberRegisterVo;
import org.lanjianghao.douyamall.member.vo.OAuth2LoginVo;
import org.lanjianghao.douyamall.member.vo.OAuth2RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.lanjianghao.douyamall.member.entity.MemberEntity;
import org.lanjianghao.douyamall.member.service.MemberService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.R;



/**
 * 会员
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-17 15:30:22
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R memberCoupons = couponFeignService.memberCoupons();
        return R.ok().put("member", memberEntity).put("coupon", memberCoupons.get("coupon"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody @Validated MemberRegisterVo vo) {

        memberService.register(vo);

        return R.ok();
    }

    /**
     * 登录
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody @Validated MemberLoginVo vo) {
        MemberEntity member = memberService.login(vo);

        if (member == null) {
            return R.error(BizCodeEnum.LOGIN_FAILED_EXCEPTION.getCode(),
                    BizCodeEnum.LOGIN_FAILED_EXCEPTION.getMessage());
        }

        return R.ok().put("data", member);
    }

    @PostMapping("/oauth2/login")
    public R oAuth2Login(@RequestBody @Validated OAuth2LoginVo vo) {
        MemberEntity member = memberService.oAuth2Login(vo);

        if (member == null) {
            return R.error(BizCodeEnum.OAUTH2_USER_NOT_EXISTS_EXCEPTION.getCode(),
                    BizCodeEnum.OAUTH2_USER_NOT_EXISTS_EXCEPTION.getMessage());
        }

        return R.ok().put("data", member);
    }

    @PostMapping("/oauth2/register")
    public R oAuth2Register(@RequestBody @Validated OAuth2RegisterVo vo) {
        memberService.oAuth2Register(vo);

        return R.ok();
    }
}
