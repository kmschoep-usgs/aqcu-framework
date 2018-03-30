package gov.usgs.aqcu.parameter;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class RequestParametersTest {

	RequestParameters params;
	Instant last3MonthsBegin = Instant.parse(LocalDate.now().minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01T00:00:00.000000000Z");
	Instant last3MonthsEnd = Instant.parse(LocalDate.now().toString() + "T23:59:59.999999999Z");
	Instant waterYear2018Begin = Instant.parse("2017-10-01T00:00:00.000000000Z");
	Instant waterYear2018End = Instant.parse("2018-09-30T23:59:59.999999999Z");
	public static final Instant REPORT_END_INSTANT = Instant.parse("2018-03-16T23:59:59.999999999Z");
	public static final Instant REPORT_START_INSTANT = Instant.parse("2018-03-16T00:00:00.000000000Z");
	public static final LocalDate REPORT_END_DATE = LocalDate.of(2018, 03, 16);
	public static final LocalDate REPORT_START_DATE = LocalDate.of(2018, 03, 16);

	@Before
	public void setup() {
		params = new RequestParameters();
	}

	@Test
	public void dateToReportEndTimeTest() {
		assertEquals(REPORT_END_INSTANT, params.dateToReportEndTime(REPORT_END_DATE));
	}

	@Test
	public void dateToReportStartTimeTest() {
		assertEquals(REPORT_START_INSTANT, params.dateToReportStartTime(REPORT_START_DATE));
	}

	@Test
	public void lastMonthsToReportPeriodTest() {
		Pair<Instant,Instant> reportPeriod = params.lastMonthsToReportPeriod(3);
		assertEquals(last3MonthsBegin, reportPeriod.getLeft());
		assertEquals(last3MonthsEnd, reportPeriod.getRight());
	}

	@Test
	public void waterYearToReportPeriodTest() {
		Pair<Instant,Instant> reportPeriod = params.waterYearToReportPeriod(2018);
		assertEquals(waterYear2018Begin, reportPeriod.getLeft());
		assertEquals(waterYear2018End, reportPeriod.getRight());
	}

	@Test
	public void determineReportPeriod_lastmonthsTest() {
		params.setLastMonths(3);
		params.determineReportPeriod();
		assertEquals(last3MonthsBegin, params.getStartInstant());
		assertEquals(last3MonthsEnd, params.getEndInstant());
	}

	@Test
	public void determineReportPeriod_waterYearTest() {
		params.setWaterYear(2018);
		params.determineReportPeriod();
		assertEquals(waterYear2018Begin, params.getStartInstant());
		assertEquals(waterYear2018End, params.getEndInstant());
	}

	@Test
	public void determineReportPeriod_fromDatesTest() {
		params.setEndDate(REPORT_END_DATE);
		params.setStartDate(REPORT_START_DATE);
		params.determineReportPeriod();
		assertEquals(REPORT_START_INSTANT, params.getStartInstant());
		assertEquals(REPORT_END_INSTANT, params.getEndInstant());
	}


	@Test
	public void determineReportPeriod_waterYearOverridesDatesTest() {
		params.setWaterYear(2018);
		params.setEndDate(REPORT_END_DATE);
		params.setStartDate(REPORT_START_DATE);
		params.determineReportPeriod();
		assertEquals(waterYear2018Begin, params.getStartInstant());
		assertEquals(waterYear2018End, params.getEndInstant());
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesDatesTest() {
		params.setLastMonths(3);
		params.setEndDate(REPORT_END_DATE);
		params.setStartDate(REPORT_START_DATE);
		params.determineReportPeriod();
		assertEquals(last3MonthsBegin, params.getStartInstant());
		assertEquals(last3MonthsEnd, params.getEndInstant());
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesWaterYearTest() {
		params.setLastMonths(3);
		params.setWaterYear(2018);
		params.determineReportPeriod();
		assertEquals(last3MonthsBegin, params.getStartInstant());
		assertEquals(last3MonthsEnd, params.getEndInstant());
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesAllTest() {
		params.setLastMonths(3);
		params.setWaterYear(2018);
		params.setEndDate(REPORT_END_DATE);
		params.setStartDate(REPORT_START_DATE);
		params.determineReportPeriod();
		assertEquals(last3MonthsBegin, params.getStartInstant());
		assertEquals(last3MonthsEnd, params.getEndInstant());
	}

	@Test
	public void getStartInstant_nullTest() {
		params.setLastMonths(3);
		assertEquals(last3MonthsBegin, params.getStartInstant());
	}

	@Test
	public void getEndInstant_nullTest() {
		params.setLastMonths(3);
		assertEquals(last3MonthsEnd, params.getEndInstant());
	}

}
