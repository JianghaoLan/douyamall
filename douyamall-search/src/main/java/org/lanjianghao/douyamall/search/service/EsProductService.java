package org.lanjianghao.douyamall.search.service;

import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.lanjianghao.douyamall.search.vo.ProductSearchParam;
import org.lanjianghao.douyamall.search.vo.ProductSearchResult;

import java.io.IOException;
import java.util.List;

public interface EsProductService {
    boolean saveProducts(List<ProductEntity> productEntities) throws IOException;

    /**
     * 商品检索
     *
     * @param searchParam 检索参数
     * @return 检索结果
     */
    ProductSearchResult search(ProductSearchParam searchParam);
}
