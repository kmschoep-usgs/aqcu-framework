package gov.usgs.aqcu.retrieval;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Approval;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GapTolerance;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Grade;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InterpolationType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Method;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Note;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalTimeRange;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;

import gov.usgs.aqcu.parameter.ReportRequestParameters;
import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class TimeSeriesDataServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private TimeSeriesDataService service;
	private ReportRequestParameters parameters;

	private static Qualifier qualifierA = new Qualifier().setIdentifier("a");
	private static Qualifier qualifierB = new Qualifier().setIdentifier("b");
	private static Qualifier qualifierC = new Qualifier().setIdentifier("c");

	private int secondsInDay = 60 * 60 * 24;

	private static final ArrayList<Approval> approvals = new ArrayList<>(Arrays.asList(
		new Approval()
			.setApprovalLevel(1)
			.setComment("test-1")
			.setDateAppliedUtc(Instant.parse("2017-01-01T00:00:00Z"))
			.setLevelDescription("desc-1")
			.setUser("user-1"),
		new Approval()
			.setApprovalLevel(2)
			.setComment("test-2")
			.setDateAppliedUtc(Instant.parse("2017-01-01T00:00:00Z"))
			.setLevelDescription("desc-2")
			.setUser("user-2")
	));

	private static final ArrayList<GapTolerance> gapTolerances = new ArrayList<>(Arrays.asList(
		new GapTolerance()
			.setToleranceInMinutes(2.0)
	));

	private static final ArrayList<Grade> grades = new ArrayList<>(Arrays.asList(
		new Grade()
			.setGradeCode("1.0")
	));

	private static final ArrayList<InterpolationType> interps = new ArrayList<>(Arrays.asList(
		new InterpolationType()
			.setType("type")
	));

	private static final ArrayList<Method> methods = new ArrayList<>(Arrays.asList(
		new Method()
			.setMethodCode("1.0")
	));

	private static final ArrayList<Note> notes = new ArrayList<>(Arrays.asList(
		new Note()
			.setNoteText("note text")
	));

	private static final ArrayList<TimeSeriesPoint> points = new ArrayList<>(Arrays.asList(
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-01T00:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("1").setNumeric(1.0)),
		new TimeSeriesPoint()
			.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-02T00:00:00Z")))
			.setValue(new DoubleWithDisplay().setDisplay("1").setNumeric(2.0))
	));

	private TimeSeriesDataServiceResponse TS_DATA_RESPONSE;
	
	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new TimeSeriesDataService(aquariusService);
		parameters = new ReportRequestParameters();
		TS_DATA_RESPONSE = buildData();
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(TS_DATA_RESPONSE);
	}

	@Test
	public void getQualifiersTest1() throws Exception {
		TimeSeriesDataServiceResponse actual = service.getData("", null, null, null, null, null, null);
		assertEquals(3, actual.getQualifiers().size());
		assertThat(actual.getQualifiers(), containsInAnyOrder(qualifierA, qualifierB, qualifierC));
	}

	@Test
	public void getQualifiersTest2() throws Exception {
		parameters.setStartDate(LocalDate.parse("2018-01-01"));
		parameters.setEndDate(LocalDate.parse("2018-01-02"));
		TimeSeriesDataServiceResponse actual = service.get(parameters.getPrimaryTimeseriesIdentifier(), parameters, ZoneOffset.UTC, true, false, null, null);
		assertEquals(3, actual.getQualifiers().size());
		assertThat(actual.getQualifiers(), containsInAnyOrder(qualifierA, qualifierB, qualifierC));
	}

	@Test
	public void adjustIfDVTest () {
		Instant now = Instant.now();
		Instant tomorrow = now.plusSeconds(secondsInDay);
		assertEquals(now, service.adjustIfDv(now, false));
		assertEquals(tomorrow, service.adjustIfDv(now, true));
	}

	@Test
	public void getUVFullTest() {
		parameters.setStartDate(LocalDate.parse("2018-01-01"));
		parameters.setEndDate(LocalDate.parse("2018-01-02"));
		TimeSeriesDataServiceResponse result = service.get(parameters.getPrimaryTimeseriesIdentifier(), parameters, ZoneOffset.UTC, false, false, null, null);
		assertEquals(result, TS_DATA_RESPONSE);
		assertEquals(result.getPoints().size(), 2);
	}

	@Test
	public void getUVNoRoundFullTest() {
		parameters.setStartDate(LocalDate.parse("2018-01-01"));
		parameters.setEndDate(LocalDate.parse("2018-01-02"));
		TimeSeriesDataServiceResponse result = service.get(parameters.getPrimaryTimeseriesIdentifier(), parameters, ZoneOffset.UTC, false, false, false, false, null);
		assertEquals(result, TS_DATA_RESPONSE);
		assertEquals(result.getPoints().size(), 2);
	}

	@Test
	public void getDVFullTest() {
		parameters.setStartDate(LocalDate.parse("2018-01-01"));
		parameters.setEndDate(LocalDate.parse("2018-01-02"));
		TimeSeriesDataServiceResponse result = service.get(parameters.getPrimaryTimeseriesIdentifier(), parameters, ZoneOffset.UTC, true, false, null, null);
		assertEquals(result.getApprovals(), TS_DATA_RESPONSE.getApprovals());
		assertEquals(result.getGapTolerances(), TS_DATA_RESPONSE.getGapTolerances());
		assertEquals(result.getGrades(), TS_DATA_RESPONSE.getGrades());
		assertEquals(result.getTimeRange(), TS_DATA_RESPONSE.getTimeRange());
		assertEquals(result.getQualifiers(), TS_DATA_RESPONSE.getQualifiers());
		assertEquals(result.getPoints().size(), 1);
	}

	public static TimeSeriesDataServiceResponse buildData() {
		return new TimeSeriesDataServiceResponse()
			.setApprovals(approvals)
			.setGapTolerances(gapTolerances)
			.setGrades(grades)
			.setInterpolationTypes(interps)
			.setLabel("label")
			.setLocationIdentifier("loc-id")
			.setMethods(methods)
			.setNotes(notes)
			.setNumPoints(new Long("1"))
			.setParameter("param")
			.setPoints(points)
			.setQualifiers(new ArrayList<Qualifier>(Arrays.asList(qualifierA, qualifierB, qualifierC)))
			.setTimeRange(new StatisticalTimeRange()
				.setStartTime(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-01T00:00:00Z")))
				.setEndTime(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-03-01T00:00:00Z"))))
			.setUniqueId("uuid")
			.setUnit("unit");
	}
}
