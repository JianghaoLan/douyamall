package org.lanjianghao.common.validation.constraints;

import org.lanjianghao.common.validation.validators.InValuesConstraintValidator;

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
@Constraint(validatedBy = { InValuesConstraintValidator.class })
public @interface InValues {

    int[] values() default { };

    String message() default "{org.lanjianghao.common.validation.constraints.InValues.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
