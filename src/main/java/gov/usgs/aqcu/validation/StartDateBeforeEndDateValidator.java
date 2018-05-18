package gov.usgs.aqcu.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;

public class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDate, DateRangeRequestParameters> {

	@Override
	public void initialize(StartDateBeforeEndDate constraintAnnotation) {
		// Nothing to see here.
	}

	@Override
	/** 
	 * This validation should only apply if BOTH values are present.
	 */
	public boolean isValid(DateRangeRequestParameters value, ConstraintValidatorContext context) {
		if (value.getStartDate() == null
				|| value.getEndDate() == null) {
			return true;
		} else if (value.getStartDate().compareTo(value.getEndDate()) < 1) {
			return true;
		} else {
			return false;
		}
	}

}
