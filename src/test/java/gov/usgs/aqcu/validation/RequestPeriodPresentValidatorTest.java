package gov.usgs.aqcu.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;

public class RequestPeriodPresentValidatorTest {

	@Mock
	protected ConstraintValidatorContext context;

	protected RequestPeriodPresentValidator validator;
	protected DateRangeRequestParameters params;

	@Before
	public void setup() {
		validator = new RequestPeriodPresentValidator();
		params = new DateRangeRequestParameters();
	}

	@Test
	public void noValuesTest() {
		assertFalse(validator.isValid(params, context));
	}

	@Test
	public void onlyStartDateTest() {
		params.setStartDate(LocalDate.now());
		assertFalse(validator.isValid(params, context));
	}

	@Test
	public void onlyEndDateTest() {
		params.setEndDate(LocalDate.now());
		assertFalse(validator.isValid(params, context));
	}

	@Test
	public void lastMonthsTest() {
		params.setLastMonths(12);
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void waterYearTest() {
		params.setWaterYear(1999);
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void startEndDateTest() {
		LocalDate now = LocalDate.now();
		params.setStartDate(now);
		params.setEndDate(now);
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void allTest() {
		params.setLastMonths(12);
		params.setWaterYear(1999);
		LocalDate now = LocalDate.now();
		params.setStartDate(now);
		params.setEndDate(now.minusDays(1));
		assertTrue(validator.isValid(params, context));
	}

}
