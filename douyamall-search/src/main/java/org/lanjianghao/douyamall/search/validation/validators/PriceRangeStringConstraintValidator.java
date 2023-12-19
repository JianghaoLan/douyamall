package org.lanjianghao.douyamall.search.validation.validators;

import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.douyamall.search.validation.constraints.PriceRangeString;
import org.springframework.util.NumberUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class PriceRangeStringConstraintValidator
        implements ConstraintValidator<PriceRangeString, String> {

    private static boolean isDecimal(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String[] range = value.split("_", 2);
        if (range.length < 2) {
            return false;
        }

        return (range[0].length() > 0 || range[1].length() > 0)   //至少有一边有值
                && (range[0].length() == 0 || isDecimal(range[0]))
                && (range[1].length() == 0 || isDecimal(range[1]));
    }
}
