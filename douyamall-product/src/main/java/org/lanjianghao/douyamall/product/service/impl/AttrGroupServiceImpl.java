package org.lanjianghao.douyamall.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.AttrGroupDao;
import org.lanjianghao.douyamall.product.entity.AttrGroupEntity;
import org.lanjianghao.douyamall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId == 0) {
            return queryPage(params);
        }

        //select * from pms_attr_group where catelog_id=? and (attr_group_id=? or attr_group_name=?)
        QueryWrapper<AttrGroupEntity> queryWrapper =
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);
        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper = queryWrapper.and((wrapper) ->
                    wrapper.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
}