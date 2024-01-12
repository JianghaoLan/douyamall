package org.lanjianghao.douyamall.ware.service.impl;

import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.ware.exception.GetFareErrorException;
import org.lanjianghao.douyamall.ware.feign.MemberFeignService;
import org.lanjianghao.douyamall.ware.vo.FareVo;
import org.lanjianghao.douyamall.ware.vo.MemberReceiveAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.ware.dao.WareInfoDao;
import org.lanjianghao.douyamall.ware.entity.WareInfoEntity;
import org.lanjianghao.douyamall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String)params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFareByAddrId(Long addrId) {
        FareVo vo = new FareVo();

        R getReceiveAddressR = memberFeignService.getReceiveAddressById(addrId);
        if (getReceiveAddressR.getCode() == 0) {
            MemberReceiveAddressVo address = getReceiveAddressR.get("memberReceiveAddress", MemberReceiveAddressVo.class);
            vo.setAddress(address);

            //TODO 计算真正的运费
            if (address != null) {
                String phone = address.getPhone();
                String fareStr = phone.substring(phone.length() - 1);
                vo.setFare(new BigDecimal(fareStr));
                return vo;
            }
            throw new GetFareErrorException("计算运费失败：无法查询到收货地址信息");
        }

        throw new GetFareErrorException("计算运费失败：无法查询到收货地址信息");
    }

}