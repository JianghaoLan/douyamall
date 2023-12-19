package org.lanjianghao.douyamall.search.vo;

import lombok.Data;
import org.lanjianghao.common.validation.constraints.NullOrNotBlank;
import org.lanjianghao.douyamall.search.validation.constraints.AttrValuePair;
import org.lanjianghao.douyamall.search.validation.constraints.PriceRangeString;
import org.lanjianghao.douyamall.search.validation.constraints.ProductSearchParamSort;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
public class ProductSearchParam {

    /**
     * 检索关键字
     */
    @NullOrNotBlank
    private String keyword;

    private Long catalogId;
    private List<Long> brandIds;

    /**
     * 可能取值：
     *      saleCount_asc, saleCount_desc
     *      skuPrice_asc, skuPrice_desc
     *      hotScore_asc, hotScore_desc
     */
    @ProductSearchParamSort
    private String sort;

    /**
     * 是否只显示有货[0 - 否, 1 - 是]
     */
    private Integer hasStock;

    /**
     * 商品价格区间
     * 格式：100_1000, _1000, 100_
     */
    @PriceRangeString
    private String skuPriceRange;

    /**
     * 属性
     * 前端路径格式：2_5寸:6寸&...
     */
    private List<@AttrValuePair String> attrs;

    /**
     * 页码，从1开始
     */
    @Positive
    private Integer pageNum;
}
