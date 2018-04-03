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

	private RequestParameters params;
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
		params = new RequestParameters();
	}

	@Test
	public void datesToReportPeriodTest() {
		Pair<LocalDate,LocalDate> reportPeriod = params.datesToReportPeriod(reportStartDate, reportEndDate);
		assertEquals(reportStartDate, reportPeriod.getLeft());
		assertEquals(reportEndDate, reportPeriod.getRight());
	}

	@Test
	public void lastMonthsToReportPeriodTest() {
		Pair<LocalDate,LocalDate> reportPeriod = params.lastMonthsToReportPeriod(3);
		assertEquals(last3MonthsBeginDate, reportPeriod.getLeft());
		assertEquals(last3MonthsEndDate, reportPeriod.getRight());
	}

	@Test
	public void waterYearToReportPeriodTest() {
		Pair<LocalDate,LocalDate> reportPeriod = params.waterYearToReportPeriod(2018);
		assertEquals(waterYear2018BeginDate, reportPeriod.getLeft());
		assertEquals(waterYear2018EndDate, reportPeriod.getRight());
	}

	@Test
	public void determineReportPeriod_lastmonthsTest() {
		params.setLastMonths(3);
		params.determineReportPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineReportPeriod_waterYearTest() {
		params.setWaterYear(2018);
		params.determineReportPeriod();
		assertEquals(waterYear2018BeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(waterYear2018EndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineReportPeriod_fromDatesTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineReportPeriod();
		assertEquals(reportStartInstantUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(reportEndInstantUtc, params.getEndInstant(ZoneOffset.UTC));
	}


	@Test
	public void determineReportPeriod_waterYearOverridesDatesTest() {
		params.setWaterYear(2018);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineReportPeriod();
		assertEquals(waterYear2018BeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(waterYear2018EndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesDatesTest() {
		params.setLastMonths(3);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineReportPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesWaterYearTest() {
		params.setLastMonths(3);
		params.setWaterYear(2018);
		params.determineReportPeriod();
		assertEquals(last3MonthsBeginUtc, params.getStartInstant(ZoneOffset.UTC));
		assertEquals(last3MonthsEndUtc, params.getEndInstant(ZoneOffset.UTC));
	}

	@Test
	public void determineReportPeriod_lastmonthsOverridesAllTest() {
		params.setLastMonths(3);
		params.setWaterYear(2018);
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.determineReportPeriod();
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
		String expected = "startDate=" + waterYear2018BeginUtc.toString().substring(0, 10) + 
						"&endDate=" + waterYear2018EndUtc.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringAbsoluteLastMonthsTest() {
		params.setLastMonths(3);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + last3MonthsBeginUtc.toString().substring(0, 10) + 
						"&endDate=" + last3MonthsEndUtc.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
		assertEquals(0, params.getAsQueryString(null, true).compareTo(expected));
	}

	@Test
	public void getAsQueryStringBasicTest() {
		params.setEndDate(reportEndDate);
		params.setStartDate(reportStartDate);
		params.setPrimaryTimeseriesIdentifier(primaryIdentifier);
		params.determineReportPeriod();
		String expected = "startDate=" + reportStartDate.toString().substring(0, 10) + 
						"&endDate=" + reportEndDate.toString().substring(0, 10) + "&primaryTimeseriesIdentifier=test-identifier";
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

	@Test
	public void getAsQueryStringEmptyTest() {
		assertEquals("", params.getAsQueryString(null, true));
		assertEquals("", params.getAsQueryString(null, false));
	}
}
