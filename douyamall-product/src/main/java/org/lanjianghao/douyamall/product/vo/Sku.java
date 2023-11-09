/**
 * Copyright 2023 bejson.com
 */
package org.lanjianghao.douyamall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2023-11-07 21:39:6
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Sku {

    private String skuName;
    private String skuTitle;
    private String skuSubtitle;
    private BigDecimal price;
    private List<Image> images;
    private List<String> descar;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
    private List<Attr> attr;

}