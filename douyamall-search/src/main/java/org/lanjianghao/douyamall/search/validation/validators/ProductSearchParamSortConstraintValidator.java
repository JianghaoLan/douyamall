package org.lanjianghao.douyamall.search.validation.validators;

import org.lanjianghao.douyamall.search.validation.constraints.ProductSearchParamSort;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ProductSearchParamSortConstraintValidator
        implements ConstraintValidator<ProductSearchParamSort, String> {

    private final static List<String> possibleFields = Arrays.asList("saleCount", "skuPrice", "hotScore");
    private final static List<String> possibleOrders = Arrays.asList("asc", "desc");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String[] split = value.split("_", 2);
        if (split.length < 2) {
            return false;
        }
        return possibleFields.contains(split[0]) && possibleOrders.contains(split[1]);
    }
}
