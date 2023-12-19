package org.lanjianghao.douyamall.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.lanjianghao.common.constant.ProductConstant;
import org.lanjianghao.douyamall.product.dao.AttrAttrgroupRelationDao;
import org.lanjianghao.douyamall.product.dao.AttrGroupDao;
import org.lanjianghao.douyamall.product.dao.CategoryDao;
import org.lanjianghao.douyamall.product.entity.AttrAttrgroupRelationEntity;
import org.lanjianghao.douyamall.product.entity.AttrGroupEntity;
import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import org.lanjianghao.douyamall.product.service.CategoryService;
import org.lanjianghao.douyamall.product.vo.AttrNameVo;
import org.lanjianghao.douyamall.product.vo.AttrRespVo;
import org.lanjianghao.douyamall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.common.utils.Query;

import org.lanjianghao.douyamall.product.dao.AttrDao;
import org.lanjianghao.douyamall.product.entity.AttrEntity;
import org.lanjianghao.douyamall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public PageUtils queryPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", "base".equals(attrType) ?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());


        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        String key = (String)params.get("key");
        if (StringUtils.hasLength(key)) {
            queryWrapper.and((wrapper) -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> AttrRespVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            if ("base".equals(attrType)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrRespVo.getAttrId()));
                if (relationEntity != null) {
                    attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }


            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(AttrRespVos);

        return pageUtils;
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //保存属性-属性分组关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    @Override
    @Transactional
    public AttrRespVo getAttrInfoById(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrRespVo.getAttrId()));
            if (relationEntity != null) {
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        attrRespVo.setCatelogPath(categoryService.findCategoryPath(attrEntity.getCatelogId()));

//        System.out.println(attrRespVo.getGroupName());

        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateAttrById(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        this.updateById(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());

            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper =
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId());

            boolean exists = attrAttrgroupRelationDao.exists(queryWrapper);

            if (exists) { //修改属性-属性分组关系
                attrAttrgroupRelationDao.update(relationEntity, queryWrapper);
            } else {     //添加修改属性-属性分组关系
                attrAttrgroupRelationDao.insert(relationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> listRelatedAttrs(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities =
                attrAttrgroupRelationDao.selectList(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        if (relationEntities == null || relationEntities.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> attrIds = relationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        return this.baseMapper.selectBatchIds(attrIds);
    }

    @Override
    public PageUtils queryNoRelationAttrsPage(Map<String, Object> params, Long attrgroupId) {
        AttrGroupEntity attrGroup = attrGroupDao.selectById(attrgroupId);
        if (attrGroup == null) {
            return new PageUtils(new Page<>());
        }
        Long catelogId = attrGroup.getCatelogId();

        // 从属性分组表中得到当前分类下的所有属性分组id
        List<AttrGroupEntity> catelogAttrGroups = attrGroupDao.selectList(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> catelogAttrGroupIds = catelogAttrGroups.stream()
                .map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        // 从属性-属性分组关系表中得到当前分类下所有分组id关联的属性id
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", catelogAttrGroupIds));
        Collection<Long> relatedAttrIds = relations.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        // 从属性表中得到未关联分组的属性
        QueryWrapper<AttrEntity> attrQueryWrapper = new QueryWrapper<>();
        attrQueryWrapper.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        attrQueryWrapper.eq("catelog_id", catelogId);
        if (!relatedAttrIds.isEmpty()) {
            attrQueryWrapper.notIn("attr_id", relatedAttrIds);
        }
        String key = (String) params.get("key");
        if (StringUtils.hasLength(key)) {
            attrQueryWrapper.and((wrapper) -> wrapper.like("attr_name", key).or().eq("attr_id", key));
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrQueryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<AttrNameVo> listAttrNames(List<Long> attrIds) {
        if (CollectionUtils.isEmpty(attrIds)) {
            return Collections.emptyList();
        }
        return this.baseMapper.selectAttrNamesByIds(attrIds);
    }
}