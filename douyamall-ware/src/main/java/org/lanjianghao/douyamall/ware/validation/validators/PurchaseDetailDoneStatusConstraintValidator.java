package org.lanjianghao.douyamall.ware.validation.validators;

import org.lanjianghao.common.constant.WareConstant;
import org.lanjianghao.douyamall.ware.validation.constraints.PurchaseDetailDoneStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PurchaseDetailDoneStatusConstraintValidator
        implements ConstraintValidator<PurchaseDetailDoneStatus, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null
                || value.equals(WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_COMPLETED.getCode())
                || value.equals(WareConstant.PurchaseDetailEnum.PURCHASE_DETAIL_STATUS_FAILED.getCode());
    }
}
