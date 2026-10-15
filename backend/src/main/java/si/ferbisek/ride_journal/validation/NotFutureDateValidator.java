package si.ferbisek.ride_journal.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class NotFutureDateValidator implements ConstraintValidator<NotFutureDate, LocalDate> {

    private static final ZoneOffset FURTHEST_AHEAD_ZONE = ZoneOffset.ofHours(14);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isAfter(LocalDate.now(FURTHEST_AHEAD_ZONE));
    }
}
