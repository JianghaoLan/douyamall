package org.lanjianghao.douyamall.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.lanjianghao.common.utils.R;
import org.lanjianghao.douyamall.search.entity.ProductEntity;
import org.lanjianghao.douyamall.search.feign.ProductFeignService;
import org.lanjianghao.douyamall.search.repository.ProductRepository;
import org.lanjianghao.douyamall.search.service.EsProductService;
import org.lanjianghao.douyamall.search.vo.AttrNameVo;
import org.lanjianghao.douyamall.search.vo.BrandNameVo;
import org.lanjianghao.douyamall.search.vo.ProductSearchParam;
import org.lanjianghao.douyamall.search.vo.ProductSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EsProductServiceImpl implements EsProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public boolean saveProducts(List<ProductEntity> productEntities) throws IOException {
        return productRepository.saveProducts(productEntities);
    }

    private static List<Integer> makePageNavs(Integer pageNum, Long totalPages) {
        List<Integer> navs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            navs.add(i);
        }
        return navs;
    }

    private List<Long> parseAttrIds(List<String> attrs) {
        if (CollectionUtils.isEmpty(attrs)) {
            return Collections.emptyList();
        }
        return attrs.stream().map(
                attr -> Long.parseLong(attr.split("_", 2)[0])
        ).collect(Collectors.toList());
    }

    private List<ProductSearchResult.Nav> makeNavs(ProductSearchParam param) {

        //品牌面包屑
        List<Long> brandIds = param.getBrandIds() == null ? Collections.emptyList() : param.getBrandIds();
        List<BrandNameVo> brandNames = null;
        if (!CollectionUtils.isEmpty(brandIds)) {
            try {
                R getBrandNamesR = productFeignService.getBrandNames(brandIds);
                if (getBrandNamesR.getCode() == 0) {
                    brandNames = getBrandNamesR.get("data", new TypeReference<List<BrandNameVo>>() {});
                }
            } catch (Exception ignored) {}
        }
        Map<Long, String> brandNameMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(brandNames)) {
            brandNameMap = brandNames.stream().collect(Collectors.toMap(
                    BrandNameVo::getBrandId, BrandNameVo::getBrandName
            ));
        }
        Map<Long, String> finalBrandNameMap = brandNameMap;
        List<ProductSearchResult.Nav> navs = brandIds.stream().map(id -> {
            ProductSearchResult.Nav nav = new ProductSearchResult.Nav();
            nav.setName("分类");
            nav.setValue(finalBrandNameMap.getOrDefault(id, id.toString()));
            nav.setParamKey("brandIds");
            nav.setParamValue(id.toString());
            return nav;
        }).collect(Collectors.toList());

        //属性面包屑
        List<String> attrs = param.getAttrs() == null ? Collections.emptyList() : param.getAttrs();
        List<Long> attrIds = parseAttrIds(attrs);
        List<AttrNameVo> attrNames = null;
        if (!CollectionUtils.isEmpty(attrIds)) {
            try {
                R getAttrNamesR = productFeignService.getAttrNames(attrIds);
                if ((Integer) getAttrNamesR.get("code") == 0) {
                    attrNames = getAttrNamesR.get("data", new TypeReference<List<AttrNameVo>>() {});
                }
            } catch (Exception ignored) {}
        }
        Map<Long, String> attrNameMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(attrNames)) {
            attrNameMap = attrNames.stream().collect(Collectors.toMap(
                    AttrNameVo::getAttrId, AttrNameVo::getAttrName)
            );
        }
        Map<Long, String> finalAttrNameMap = attrNameMap;
        navs.addAll(attrs.stream().map(attr -> {
            String[] split = attr.split("_", 2);
            String attrId = split[0];
            String attrValue = split[1];
            ProductSearchResult.Nav nav = new ProductSearchResult.Nav();
            nav.setName(finalAttrNameMap.getOrDefault(Long.parseLong(attrId), attrId));
            nav.setValue(attrValue);
            nav.setParamKey("attrs");
            nav.setParamValue(attr);
            return nav;
        }).collect(Collectors.toList()));

        return navs;
    }

    @Override
    public ProductSearchResult search(ProductSearchParam searchParam) {
        ProductSearchResult result = productRepository.search(searchParam);

        //set pageNavs
        result.setPageNavs(makePageNavs(result.getPageNum(), result.getTotalPages()));

        //set nav
        result.setNavs(makeNavs(searchParam));

        return result;
    }
}
