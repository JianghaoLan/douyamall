package org.lanjianghao.douyamall.search.vo;

import lombok.Data;
import org.lanjianghao.douyamall.search.entity.ProductEntity;

import java.util.List;

@Data
public class ProductSearchResult {
    private List<ProductEntity> products;

    /**
     * 当前页码
     */
    private Integer pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页码数
     */
    private Long totalPages;

    /**
     * 页码导航，便于前端显示
     */
    private List<Integer> pageNavs;

    /**
     * 当前查询结果关联的所有品牌
     */
    private List<Brand> brands;

    /**
     * 当前查询结果关联的所有属性
     */
    private List<Attr> attrs;

    /**
     * 当前查询结果关联的所有分类
     */
    private List<Catalog> catalogs;

    /**
     * 面包屑导航，便于前端展示
     */
    private List<Nav> navs;

    @Data
    public static class Nav {
//        private Long attrId;
        private String name;
        private String value;
        private String paramKey;
        private String paramValue;
    }

    @Data
    public static class Brand {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class Attr {
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }

    @Data
    public static class Catalog {
        private Long catalogId;
        private String catalogName;
    }
}
