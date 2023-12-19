package org.lanjianghao.douyamall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lanjianghao.common.utils.PageUtils;
import org.lanjianghao.douyamall.product.entity.CategoryEntity;
import org.lanjianghao.douyamall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] findCategoryPath(long categoryId);

    boolean updateCascadeById(CategoryEntity category);

    List<CategoryEntity> listTopLevelCategories();

    Map<String, List<Catalog2Vo>> getCatalogJson();
}
