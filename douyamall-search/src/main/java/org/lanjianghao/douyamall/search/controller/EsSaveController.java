package org.lanjianghao.douyamall.search.controller;

import lombok.extern.slf4j.Slf4j;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.lanjianghao.douyamall.search.service.EsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.lanjianghao.common.exception.BizCodeEnum.PRODUCT_UP_EXCEPTION;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class EsSaveController {

    @Autowired
    EsProductService esProductService;

    /**
     * 保存商品
     */
    @PostMapping("/product/all")
    public R saveProducts(@RequestBody List<ProductEntity> productEntities) {
        boolean ret;
        try {
            ret = esProductService.saveProducts(productEntities);
        } catch (IOException e) {
            log.error("ESSaveController商品商家错误", e);
            return R.error(PRODUCT_UP_EXCEPTION.getCode(), PRODUCT_UP_EXCEPTION.getMessage());
        }

        if (ret) {
            return R.ok();
        }

        return R.error(PRODUCT_UP_EXCEPTION.getCode(), PRODUCT_UP_EXCEPTION.getMessage());
    }
}
