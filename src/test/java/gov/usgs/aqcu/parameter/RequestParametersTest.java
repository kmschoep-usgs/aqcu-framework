package gov.usgs.aqcu.parameter;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class RequestParametersTest {

	private ReportRequestParameters params;
	private LocalDate last3MonthsBeginDate = LocalDate.parse(LocalDate.now().minusMonths(3).format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01");
	private LocalDate last3MonthsEndDate = LocalDate.now();
	private LocalDate waterYear2018BeginDate = LocalDate.parse("2017-10-01");
	private LocalDate waterYear2018EndDate = LocalDate.parse("2018-09-30");
	private Instant last3MonthsBeginUtc = last3MonthsBeginDate.atStartOfDay().toInstant(ZoneOffset.UTC);
	private Instant last3MonthsEndUtc = Instant.parse(last3MonthsEndDate.toString() + "T23:59:59.999999999Z");
	private Instant waterYear2018BeginUtc = Instant.parse("2017-10-01T00:00:00.000000000Z");
	private Instant waterYear2018EndUtc = Instant.parse("2018-09-30T23:59:59.999999999Z");
	private Instant reportEndInstantUtc = Instant.parse("2018-03-16T23:59:59.999999999Z");
	private Instant reportStartInstantUtc = Instant.parse("2018-03-16T00:00:00.000000000Z");
	private LocalDate reportEndDate = LocalDate.of(2018, 03, 16);
	private LocalDate reportStartDate = LocalDate.of(2018, 03, 16);
	private String primaryIdentifier = "test-identifier";

	@Before
	public void setup() {
		params = new ReportRequestParameters();
	}

	@Test
	public void datesToRequestPeriodTest() {
		Pair<LocalDate,LocalDate> requestPeriod = params.datesToRequestPeriod(reportStartDate, reportEndDate);
		assertEquals(reportStartDate, requestPeriod.getLeft());
		assertEquals(reportEndDate, requestPeriod.getRight());
	}

	@Test
	public void lastMonthsToRequestPeriodTest() {
		Pair<LocalDate,LocalDate> requestPeriod = params.lastMonthsToRequestPeriod(3);
		assertEquals(last3MonthsBeginDate, requestPeriod.getLeft());
		assertEquals(last3MonthsEndDate, requestPeriod.getRight());
	}

	@Test
	public void waterYearToRequestPeriodTest() {
		Pair<LocalDate,LocalDate> requestPeriod = params.waterYearToRequestPeriod(2018);
		assertEquals(waterYear2018BeginDate, requestPeriod.getLeft());
		assertEquals(waterYear2018EndDate, requestPeriod.getRight());
	}

	@Test
	public void determineRequestPeriod_lastmonthsTest() {
		params.setLastMonths(3);
		params.determineRequestPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineRequestPeriod_waterYearTest() {
		params.setWaterYear(2018);
		params.determineRequestPeriod();
		assertEquals(waterYear2018BeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(waterYear2018EndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineRequestPeriod_fromDatesTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineRequestPeriod();
		assertEquals(reportStartInstantUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(reportEndInstantUtc, params.getEndInstant(ZoneOffset.UTC));
	}


	@Test
	public void determineRequestPeriod_waterYearOverridesDatesTest() {
		params.setWaterYear(2018);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineRequestPeriod();
		assertEquals(waterYear2018BeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(waterYear2018EndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineRequestPeriod_lastmonthsOverridesDatesTest() {
		params.setLastMonths(3);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineRequestPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineRequestPeriod_lastmonthsOverridesWaterYearTest() {
		params.setLastMonths(3);
		params.setWaterYear(2018);
		params.determineRequestPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineRequestPeriod_lastmonthsOverridesAllTest() {
		params.setLastMonths(3);
		params.setWaterYear(2018);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineRequestPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void getStartInstant_nullTest() {
		params.setLastMonths(3);
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
	}

	@Test
	public void getEndInstant_nullTest() {
		params.setLastMonths(3);
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void getAsQueryStringAbsoluteBasicTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "startDate=" + reportStartDate.toString().substring(0, 10) + 
						"&endDate=" + reportStartDate.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteWYTest() {
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "startDate=" + waterYear2018BeginUtc.toString().substring(0, 10) + 
						"&endDate=" + waterYear2018EndUtc.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteLastMonthsTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "startDate=" + last3MonthsBeginUtc.toString().substring(0, 10) + 
						"&endDate=" + last3MonthsEndUtc.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringBasicTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "startDate=" + reportStartDate.toString().substring(0, 10) + 
						"&endDate=" + reportEndDate.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringWYTest() {
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "waterYear=2018&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringLastMonthsTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "lastMonths=3&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteWYOverDatesTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "startDate=" + waterYear2018BeginUtc.toString().substring(0, 10) + 
						"&endDate=" + waterYear2018EndUtc.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringWYOverDatesTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
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
		params.determineRequestPeriod();
		String expected = "startDate=" + last3MonthsBeginUtc.toString().substring(0, 10) + 
						"&endDate=" + last3MonthsEndUtc.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAllTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setWaterYear(2018);
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "lastMonths=3&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, false).compareTo(expected));
	}

	@Test
	public void getAsQueryStringOverrideTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineRequestPeriod();
		String expected = "lastMonths=3&primaryTimeseriesIdentifier=test-override";
		assertEquals(0, params.getAsQueryString("test-override", false).compareTo(expected));
	}
}
