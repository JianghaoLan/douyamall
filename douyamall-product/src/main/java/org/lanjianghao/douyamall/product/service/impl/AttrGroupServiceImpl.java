package org.lanjianghao.douyamall.product.service.impl;

import org.lanjianghao.douyamall.product.dao.AttrAttrgroupRelationDao;
import org.lanjianghao.douyamall.product.dao.AttrDao;
import org.lanjianghao.douyamall.product.service.AttrAttrgroupRelationService;
import org.lanjianghao.douyamall.product.service.AttrService;
import org.lanjianghao.douyamall.product.service.ProductAttrValueService;
import org.lanjianghao.douyamall.product.vo.AttrGroupWithAttrsVo;
import org.lanjianghao.douyamall.product.vo.SpuBaseAttrGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrDao attrDao;
    @Autowired
    AttrService attrService;
    @Autowired
    AttrAttrgroupRelationService relationService;

    @Autowired
    ProductAttrValueService productAttrValueService;

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

        //select * from pms_attr_group where catelog_id=? and (attr_group_id=? or attr_group_name=?)
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        if (catelogId != 0) {
            queryWrapper = queryWrapper.eq("catelog_id", catelogId);
        }
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

    @Override
    public List<AttrGroupWithAttrsVo> listAttrGroupsWithAttrsByCatelogId(Long catId) {
        List<AttrGroupEntity> groups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        return groups.stream().map((group) -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, vo);
            vo.setAttrs(attrService.listRelatedAttrs(group.getAttrGroupId()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SpuBaseAttrGroup> listAttrGroupBySpuId(Long spuId) {
        return this.baseMapper.selectAttrGroupBySpuId(spuId);
    }
}