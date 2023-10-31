package org.lanjianghao.common.validation.validators;

import org.apache.commons.lang.StringUtils;
import org.lanjianghao.common.validation.constraints.InValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 值必须为特定的几个值之一。null视为通过验证
 */
public class InValuesConstraintValidator implements ConstraintValidator<InValues, Integer> {
    Set<Integer> values = new HashSet<>();

    @Override
    public void initialize(InValues constraintAnnotation) {
        for (int v: constraintAnnotation.values()) {
            values.add(v);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || values.contains(value);
    }
}
