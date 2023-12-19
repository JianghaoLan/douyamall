package org.lanjianghao.douyamall.search.validation.constraints;

import org.lanjianghao.douyamall.search.validation.validators.PriceRangeStringConstraintValidator;

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
@Constraint(validatedBy = { PriceRangeStringConstraintValidator.class })
public @interface PriceRangeString {

    String message() default "{org.lanjianghao.douyamall.search.validation.constraints.PriceRangeString.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
