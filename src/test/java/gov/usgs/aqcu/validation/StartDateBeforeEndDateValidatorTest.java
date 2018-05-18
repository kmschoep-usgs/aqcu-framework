package gov.usgs.aqcu.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;
import gov.usgs.aqcu.validation.StartDateBeforeEndDateValidator;

public class StartDateBeforeEndDateValidatorTest {

	@Mock
	protected ConstraintValidatorContext context;

	protected StartDateBeforeEndDateValidator validator;
	protected DateRangeRequestParameters params;

	@Before
	public void setup() {
		validator = new StartDateBeforeEndDateValidator();
		params = new DateRangeRequestParameters();
	}

	@Test
	public void noValuesTest() {
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void onlyStartDateTest() {
		params.setStartDate(LocalDate.now());
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void onlyEndDateTest() {
		params.setEndDate(LocalDate.now());
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void startAfterEndDateTest() {
		LocalDate now = LocalDate.now();
		params.setStartDate(now);
		params.setEndDate(now.minusDays(1));
		assertFalse(validator.isValid(params, context));
	}

	@Test
	public void startBeforeEndDateTest() {
		LocalDate now = LocalDate.now();
		params.setStartDate(now.minusDays(1));
		params.setEndDate(now);
		assertTrue(validator.isValid(params, context));
	}

	@Test
	public void startEqualEndDateTest() {
		LocalDate now = LocalDate.now();
		params.setStartDate(now);
		params.setEndDate(now);
		assertTrue(validator.isValid(params, context));
	}

}
