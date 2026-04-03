package com.routemaster.RouteMaster.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OperationDaysValidator.class)
public @interface ValidOperationDays {
    String message() default "Operating days must be between 1 and 7";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
