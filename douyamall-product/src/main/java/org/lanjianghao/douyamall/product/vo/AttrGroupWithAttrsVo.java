package org.lanjianghao.douyamall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.lanjianghao.douyamall.product.entity.AttrEntity;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 属性列表
     */
    private List<AttrEntity> attrs;
}
