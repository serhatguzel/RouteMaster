package com.routemaster.RouteMaster.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class OperationDaysValidator implements ConstraintValidator<ValidOperationDays, Set<Integer>> {

    @Override
    public boolean isValid(Set<Integer> days, ConstraintValidatorContext context) {
        if (days == null || days.isEmpty())
            return true;
        return days.stream().allMatch(day -> day >= 1 && day <= 7);
    }
}
