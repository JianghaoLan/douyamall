package org.lanjianghao.douyamall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class SpuBaseAttrGroup {
    private String groupName;
    private List<SpuBaseAttr> attrs;
}
