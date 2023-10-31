package org.lanjianghao.douyamall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.lanjianghao.common.validation.constraints.InValues;
import org.lanjianghao.common.validation.constraints.NullOrNotBlank;
import org.lanjianghao.common.validation.groups.AddGroup;
import org.lanjianghao.common.validation.groups.UpdateGroup;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author lanjianghao
 * @email 528601933@qq.com
 * @date 2023-04-12 15:43:09
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "Id必须为空", groups = {AddGroup.class})
	@NotNull(message = "Id不能为空", groups = {UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */

	@NotBlank(message = "品牌名不能为空", groups = {AddGroup.class})
	@NullOrNotBlank(message = "品牌名必须为空或非空白字符串", groups = {UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "URL不能为空", groups = {AddGroup.class})
	@URL(message = "logo必须为URL地址", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "显示状态不能为空", groups = {AddGroup.class})
	@InValues(values = {0, 1}, message = "显示状态必须为0（不显示）或1（显示）", groups = {UpdateGroup.class, AddGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "检索首字母不能为空", groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须为单个英文字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序值不能为空", groups = {AddGroup.class})
	@Min(value=0, message = "排序值必须为非负整数", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
