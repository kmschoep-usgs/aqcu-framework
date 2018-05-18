package gov.usgs.aqcu.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;

public class RequestPeriodPresentValidator implements ConstraintValidator<RequestPeriodPresent, DateRangeRequestParameters> {

	@Override
	public void initialize(RequestPeriodPresent constraintAnnotation) {
		// Nothing to see here.
	}

	@Override
	/** 
	 * This validation is based on a hierarchy - the first valid situation is accepted and used.
	 */
	public boolean isValid(DateRangeRequestParameters value, ConstraintValidatorContext context) {
		if (value.getLastMonths() != null) {
			return true;
		} else if (value.getWaterYear() != null) {
			return true;
		} else if (value.getStartDate() != null
				&& value.getEndDate() != null) {
			return true;
		} else {
			return false;
		}
	}

}
