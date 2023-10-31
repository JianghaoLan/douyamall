package org.lanjianghao.common.validation.validators;

import org.apache.commons.lang.StringUtils;
import org.lanjianghao.common.validation.constraints.NullOrNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 值必须为null或者非空白字符串
 */
public class NullOrNotEmptyConstraintValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !StringUtils.isBlank(value);
    }
}
