package org.lanjianghao.douyamall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.lanjianghao.common.validation.constraints.InValues;
import org.lanjianghao.common.validation.constraints.NullOrNotBlank;
import org.lanjianghao.common.validation.groups.AddGroup;
import org.lanjianghao.common.validation.groups.UpdateGroup;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * 商品三级分类
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Data
@TableName("pms_category")
//TODO 完成字段检查条件
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@TableId
	@Null(message = "分类id必须为空", groups = {AddGroup.class})
	@NotNull(message = "分类id不能为空", groups = {UpdateGroup.class})
	private Long catId;
	/**
	 * 分类名称
	 */
	@NotBlank(message = "分类名不能为空", groups = {AddGroup.class})
	@NullOrNotBlank(message = "分类名必须为空或者非空白字符串", groups = UpdateGroup.class)
	private String name;
	/**
	 * 父分类id
	 */
	@NotNull(message = "父分类id不能为空", groups = {AddGroup.class})
	private Long parentCid;
	/**
	 * 层级
	 */
	@NotNull(message = "分类层级不能为空", groups = {AddGroup.class})
	private Integer catLevel;
	/**
	 * 是否显示[0-不显示，1显示]
	 */
	@TableLogic(value = "1", delval = "0")
	@NotNull(message = "显示状态不能为空", groups = {AddGroup.class})
	@InValues(values = {0, 1}, message = "显示状态必须为0（不显示）或者1（显示）", groups = {AddGroup.class, UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空", groups = {AddGroup.class})
	@Min(value = 0, message = "排序必须为非负数", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;
	/**
	 * 图标地址
	 */
	private String icon;
	/**
	 * 计量单位
	 */
	private String productUnit;
	/**
	 * 商品数量
	 */
	private Integer productCount;

	/**
	 * 子分类
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@TableField(exist = false)
	private List<CategoryEntity> children;

}
