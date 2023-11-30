package org.lanjianghao.douyamall.search.service.impl;

import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.lanjianghao.douyamall.search.repository.ProductRepository;
import org.lanjianghao.douyamall.search.service.EsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EsProductServiceImpl implements EsProductService {
    @Autowired
    ProductRepository productRepository;

    @Override
    public boolean saveProducts(List<ProductEntity> productEntities) throws IOException {
        return productRepository.saveProducts(productEntities);
    }
}
