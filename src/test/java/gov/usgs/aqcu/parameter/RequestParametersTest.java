package gov.usgs.aqcu.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class RequestParametersTest {

	RequestParameters params;
	Instant last3MonthsBegin = Instant.parse(LocalDate.now().minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01T00:00:00.00Z");
	Instant last3MonthsEnd = Instant.parse(LocalDate.now().toString() + "T23:59:59.999999999Z");
	Instant waterYear2018Begin = Instant.parse("2017-10-01T00:00:00.00Z");
	Instant waterYear2018End = Instant.parse("2018-09-30T23:59:59.999999999Z");
	Instant reportEndInstant = Instant.parse("2018-03-16T23:59:59.999999999Z");
	Instant reportStartInstant = Instant.parse("2018-03-16T00:00:00.00Z");
	LocalDate reportEndDate = LocalDate.of(2018, 03, 16);
	LocalDate reportStartDate = LocalDate.of(2018, 03, 16);
	String primaryIdentifier = "test-identifier";

	@Before
	public void setup() {
		params = new RequestParameters();
	}

	@Test
	public void dateToReportEndTimeTest() {
		assertEquals(reportEndInstant, params.dateToReportEndTime(reportEndDate));
	}

	@Test
	public void dateToReportStartTimeTest() {
		assertEquals(reportStartInstant, params.dateToReportStartTime(reportStartDate));
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
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineReportPeriod();
		assertEquals(reportStartInstant, params.getStartInstant());
		assertEquals(reportEndInstant, params.getEndInstant());
	}


	@Test
	public void determineReportPeriod_waterYearOverridesDatesTest() {
		params.setWaterYear(2018);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineReportPeriod();
		assertEquals(waterYear2018Begin, params.getStartInstant());
		assertEquals(waterYear2018End, params.getEndInstant());
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesDatesTest() {
		params.setLastMonths(3);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
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
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
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

	@Test
	public void getAsQueryStringAbsoluteBasicTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + reportStartDate.toString().substring(0, 10) + 
						"&endDate=" + reportStartDate.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteWYTest() {
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + waterYear2018Begin.toString().substring(0, 10) + 
						"&endDate=" + waterYear2018End.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteLastMonthsTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + last3MonthsBegin.toString().substring(0, 10) + 
						"&endDate=" + last3MonthsEnd.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringBasicTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + reportStartDate.toString().substring(0, 10) + 
						"&endDate=" + reportStartDate.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringWYTest() {
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "waterYear=2018&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringLastMonthsTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "lastMonths=3&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteWYOverDatesTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + waterYear2018Begin.toString().substring(0, 10) + 
						"&endDate=" + waterYear2018End.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringWYOverDatesTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "waterYear=2018&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteAllTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + last3MonthsBegin.toString().substring(0, 10) + 
						"&endDate=" + last3MonthsEnd.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAllTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "lastMonths=3&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringOverrideTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "lastMonths=3&primaryTimeseriesIdentifier=test-override";
		assertEquals(0, params.getAsQueryString("test-override", false).compareTo(expected));
	}
}
