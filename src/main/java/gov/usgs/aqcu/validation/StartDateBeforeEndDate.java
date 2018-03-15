package gov.usgs.aqcu.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { StartDateBeforeEndDateValidator.class })
public @interface StartDateBeforeEndDate {

	String message() default "The report period start date must be before or equal to the report period end date.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


}
