package org.lanjianghao.douyamall.search.validation.validators;

import org.apache.commons.lang.StringUtils;
import org.lanjianghao.douyamall.search.validation.constraints.AttrValuePair;
import org.lanjianghao.douyamall.search.validation.constraints.PriceRangeString;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AttrValuePairConstraintValidator
        implements ConstraintValidator<AttrValuePair, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String[] split = value.split("_", 2);
        return split.length == 2
                && split[0].length() > 0
                && StringUtils.isNumeric(split[0])
                && split[1].split(":").length > 0;
    }
}
