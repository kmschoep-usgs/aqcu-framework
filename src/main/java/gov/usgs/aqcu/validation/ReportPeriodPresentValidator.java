package gov.usgs.aqcu.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.aqcu.parameter.RequestParameters;

public class ReportPeriodPresentValidator implements ConstraintValidator<ReportPeriodPresent, RequestParameters> {

	@Override
	public void initialize(ReportPeriodPresent constraintAnnotation) {
		// Nothing to see here.
	}

	@Override
	/** 
	 * This validation is based on a hierarchy - the first valid situation is accepted and used.
	 */
	public boolean isValid(RequestParameters value, ConstraintValidatorContext context) {
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
