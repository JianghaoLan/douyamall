package org.lanjianghao.douyamall.search.service;

import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public interface EsProductService {
    boolean saveProducts(List<ProductEntity> productEntities) throws IOException;
}
