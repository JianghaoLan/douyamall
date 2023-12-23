package org.lanjianghao.douyamall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.AttrAttrgroupRelationDao;
import org.lanjianghao.douyamall.product.entity.AttrAttrgroupRelationEntity;
import org.lanjianghao.douyamall.product.service.AttrAttrgroupRelationService;
import org.springframework.util.CollectionUtils;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void removeRelations(AttrAttrgroupRelationEntity[] relationEntities) {
        this.baseMapper.deleteBatchRelations(relationEntities);
    }

    @Override
    public List<AttrAttrgroupRelationEntity> listByAttrIds(List<Long> attrIds) {
        if (CollectionUtils.isEmpty(attrIds)) {
            return Collections.emptyList();
        }
        return this.list(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_id", attrIds));
    }
}