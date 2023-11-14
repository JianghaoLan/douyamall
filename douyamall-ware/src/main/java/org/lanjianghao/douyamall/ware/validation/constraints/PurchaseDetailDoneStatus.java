package org.lanjianghao.douyamall.ware.validation.constraints;

import org.lanjianghao.douyamall.ware.validation.validators.PurchaseDetailDoneStatusConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { PurchaseDetailDoneStatusConstraintValidator.class })
public @interface PurchaseDetailDoneStatus {

    String message() default "{org.lanjianghao.douyamall.ware.validation.constraints.PurchaseDetailDoneStatus.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
