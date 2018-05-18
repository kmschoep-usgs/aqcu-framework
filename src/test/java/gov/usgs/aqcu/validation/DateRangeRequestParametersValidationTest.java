package gov.usgs.aqcu.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;

public class DateRangeRequestParametersValidationTest {

	protected static ValidatorFactory validatorFactory;
	protected static Validator validator;
	protected DateRangeRequestParameters params;

	public static final int BASE_EMPTY_PARAMETER_ERROR_COUNT = 1;

	@BeforeClass
	public static void createValidator() {
		validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}

	@Before
	public void setup() {
		params = new DateRangeRequestParameters();
	}

	@AfterClass
	public static void destroyValidator() {
		validatorFactory.close();
	}

	@Test
	public void emptyRequestParameters() {
		Set<ConstraintViolation<DateRangeRequestParameters>> validationErrors = validator.validate(params);
		assertEquals(BASE_EMPTY_PARAMETER_ERROR_COUNT, validationErrors.size());

		assertValidationResults(validationErrors,
				":Missing information required to build request time period. Must include at least one of: [lastMonths, waterYear, {startDate, endDate}].");
	}

	@Test
	public void tooSmallParameters() {
		params.setWaterYear(1);
		params.setLastMonths(0);
		Set<ConstraintViolation<DateRangeRequestParameters>> validationErrors = validator.validate(params);
		assertEquals(2, validationErrors.size());

		assertValidationResults(validationErrors,
				"waterYear:must be greater than or equal to 2",
				"lastMonths:must be greater than or equal to 1");
	}

	@Test
	public void tooBigParameters() {
		params.setWaterYear(10000);
		params.setLastMonths(13);
		Set<ConstraintViolation<DateRangeRequestParameters>> validationErrors = validator.validate(params);
		assertEquals(2, validationErrors.size());

		assertValidationResults(validationErrors,
				"waterYear:must be less than or equal to 9999",
				"lastMonths:must be less than or equal to 12");
	}

	@Test
	public void funkyDates() {
		LocalDate now = LocalDate.now();
		params.setStartDate(now);
		params.setEndDate(now.minusDays(1));
		Set<ConstraintViolation<DateRangeRequestParameters>> validationErrors = validator.validate(params);
		assertEquals(1, validationErrors.size());

		assertValidationResults(validationErrors,
				":The report period start date must be before or equal to the report period end date.");
	}

	public void assertValidationResults(Set<ConstraintViolation<DateRangeRequestParameters>> actual, String... expected) {
		List<String> actualStrings = actual
				.stream()
				.map(x -> String.join(":", x.getPropertyPath().toString(), x.getMessage()))
				.collect(Collectors.toList());
		assertThat(actualStrings, containsInAnyOrder(expected));
	}

}
