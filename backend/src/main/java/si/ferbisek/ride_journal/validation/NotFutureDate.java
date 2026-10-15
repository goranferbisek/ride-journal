package si.ferbisek.ride_journal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rejects calendar dates that are in the future anywhere on Earth.
 * <p>
 * Event dates are calendar days chosen by the user in their own local timezone, so the server
 * cannot compare them against its own "today" (e.g. {@code @PastOrPresent}): a client ahead of the
 * server's timezone would legitimately send tomorrow's date and be rejected. Instead we accept
 * anything up to the current date in UTC+14, the furthest-ahead timezone. This loosens strict
 * future-date validation by at most one day, but never rejects a date that is valid locally.
 */
@Documented
@Constraint(validatedBy = NotFutureDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFutureDate {

    String message() default "Date must not be in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
