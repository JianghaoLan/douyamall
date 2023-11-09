package org.lanjianghao.douyamall.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * 所述分类名字
     */
    private String catelogName;

    /**
     * 所属分组名字
     */
    private String groupName;

    /**
     * 分类路径
     */
    private Long[] catelogPath;
}
