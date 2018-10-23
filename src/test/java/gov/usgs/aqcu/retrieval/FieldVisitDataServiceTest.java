package gov.usgs.aqcu.retrieval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.ObjectCompare;
import gov.usgs.aqcu.model.FieldVisitMeasurement;
import gov.usgs.aqcu.model.MeasurementGrade;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.MeasurementGradeType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QuantityWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ControlConditionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ControlConditionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeSummary;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;

import net.servicestack.client.IReturn;

@RunWith(SpringRunner.class)
public class FieldVisitDataServiceTest {

	@MockBean
	private AquariusRetrievalService aquariusService;

	private FieldVisitDataService service;
	private FieldVisitDataServiceResponse expected = new FieldVisitDataServiceResponse();

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws Exception {
		service = new FieldVisitDataService(aquariusService);
		given(aquariusService.executePublishApiRequest(any(IReturn.class))).willReturn(expected);
	}

	@Test
	public void get_happyTest() {
		FieldVisitDataServiceResponse actual = service.get("a");
		assertEquals(expected, actual);
	}

	@Test
	public void calculateErrorTest() {
		FieldVisitMeasurement expected = new FieldVisitMeasurement("20.0090", new BigDecimal("20.0090"),
				new BigDecimal("21.00945000"), new BigDecimal("19.00855000"), Instant.parse("2017-01-01T00:00:00Z"), false);
		FieldVisitMeasurement actual = service.calculateError(MeasurementGrade.GOOD, "20.0090",
				new BigDecimal("20.0090"), Instant.parse("2017-01-01T00:00:00Z"), false);
		ObjectCompare.compare(expected, actual);
	}

	@Test
	public void filterToParameterTest() {
		List<Reading> result = service.filterToParameter(getReadings(), "param1");
		assertEquals(result.size(), 1);
		result = service.filterToParameter(getReadings(), "invalidparam");
		assertEquals(result.size(), 0);
		result = service.filterToParameter(getReadings(), "");
		assertEquals(result.size(), 0);
		result = service.filterToParameter(getReadings(), null);
		assertEquals(result.size(), 0);
		result = service.filterToParameter(new ArrayList<>(), "param1");
		assertEquals(result.size(), 0);
		result = service.filterToParameter(new ArrayList<>(), "");
		assertEquals(result.size(), 0);
		result = service.filterToParameter(new ArrayList<>(), null);
		assertEquals(result.size(), 0);
		result = service.filterToParameter(null, "param1");
		assertEquals(result.size(), 0);
		result = service.filterToParameter(null, "");
		assertEquals(result.size(), 0);
		result = service.filterToParameter(null, null);
		assertEquals(result.size(), 0);
	}

	@Test
	public void extractFieldVisitReadingsTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(null, getInspectionActivity());
		List<Reading> result = service.extractFieldVisitReadings(resp);
		assertEquals(result.size(), 2);
		result = service.extractFieldVisitReadings(resp, "param1");
		assertEquals(result.size(), 1);
		result = service.extractFieldVisitReadings(resp, "invalidparam");
		assertEquals(result.size(), 0);
	}

	@Test
	public void extractControlCondition_nullActivityTest() {
		FieldVisitDataServiceResponse resp = new FieldVisitDataServiceResponse();
		assertNull(service.extractControlCondition(resp));
	}

	@Test
	public void getControlCondition_nullConditionTest() {
		FieldVisitDataServiceResponse resp = new FieldVisitDataServiceResponse()
				.setControlConditionActivity(new ControlConditionActivity());
		assertNull(service.extractControlCondition(resp));
	}

	@Test
	public void getControlCondition_happyTest() {
		FieldVisitDataServiceResponse resp = new FieldVisitDataServiceResponse().setControlConditionActivity(
				new ControlConditionActivity().setControlCondition(ControlConditionType.DebrisHeavy));
		assertEquals("DebrisHeavy", service.extractControlCondition(resp));
	}

	@Test
	public void extractFieldVisitMeasurement_noDischargeActivitiesTest() {
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(new FieldVisitDataServiceResponse());
		assertTrue(actual.isEmpty());
	}

	@Test
	public void extractFieldVisitMeasurement_noDischargeSummaryTest() {
		ArrayList<DischargeActivity> activities = Stream.of(new DischargeActivity(), new DischargeActivity())
				.collect(Collectors.toCollection(ArrayList::new));
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(getFieldVisitDataServiceResponse(activities, null));
		assertTrue(actual.isEmpty());
	}

	@Test
	public void extractFieldVisitMeasurement_noDischargeTest() {
		ArrayList<DischargeActivity> activities = Stream
				.of(new DischargeActivity().setDischargeSummary(new DischargeSummary()),
						new DischargeActivity().setDischargeSummary(new DischargeSummary()))
				.collect(Collectors.toCollection(ArrayList::new));
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(getFieldVisitDataServiceResponse(activities, null));
		assertTrue(actual.isEmpty());
	}

	@Test
	public void extractFieldVisitMeasurement_happyTest() {
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(getFieldVisitDataServiceResponse(getDischargeActivities(), null));
		assertEquals(2, actual.size());
	}

	@Test
	public void createFieldVisitMeasurementTest() {
		FieldVisitMeasurement actual = service.createFieldVisitMeasurement(new DischargeSummary()
				.setMeasurementGrade(MeasurementGradeType.Good).setMeasurementId("20.0090")
				.setDischarge((QuantityWithDisplay) new QuantityWithDisplay().setUnit("dischargeUnits")
						.setDisplay("20.0090").setNumeric(Double.valueOf("20.0090")))
				.setMeanGageHeight((QuantityWithDisplay) new QuantityWithDisplay().setUnit("meanGageHeightUnits")
						.setDisplay("2.0090").setNumeric(Double.valueOf("2.0090")))
				.setMeasurementStartTime(Instant.parse("2017-01-01T00:00:00Z"))
				.setPublish(false));
		FieldVisitMeasurement expected = new FieldVisitMeasurement("20.0090", new BigDecimal("20.0090"),
				new BigDecimal("21.00945000"), new BigDecimal("19.00855000"), Instant.parse("2017-01-01T00:00:00Z"), false);
		ObjectCompare.compare(expected, actual);
	}

	protected FieldVisitDataServiceResponse getFieldVisitDataServiceResponse(ArrayList<DischargeActivity> dischargeActivities, InspectionActivity inspectionActivity) {
		return new FieldVisitDataServiceResponse().setDischargeActivities(dischargeActivities).setInspectionActivity(inspectionActivity);
	}

	protected InspectionActivity getInspectionActivity() {
		InspectionActivity ret = new InspectionActivity();
		ret.setReadings(getReadings());
		return ret;
	}

	protected ArrayList<Reading> getReadings() {
		ArrayList<Reading> readings = Stream
			.of(new Reading()
				.setIsValid(true)
				.setParameter("param1")
				.setValue(new DoubleWithDisplay().setDisplay("1").setNumeric(1.0D)),
				new Reading()
				.setIsValid(false)
				.setParameter("param2")
				.setValue(new DoubleWithDisplay().setDisplay("2").setNumeric(2.0D))
			)
			.collect(Collectors.toCollection(ArrayList::new));
		return readings;
	}

	protected ArrayList<DischargeActivity> getDischargeActivities() {
		ArrayList<DischargeActivity> activities = Stream
			.of(new DischargeActivity()
					.setDischargeSummary(
							new DischargeSummary().setMeasurementGrade(MeasurementGradeType.Good)
									.setMeanGageHeight((QuantityWithDisplay) new QuantityWithDisplay()
											.setUnit("meanGageHeightUnits").setDisplay("2.0090")
											.setNumeric(Double.valueOf("2.0090")))
									.setDischarge((QuantityWithDisplay) new QuantityWithDisplay()
											.setUnit("dischargeUnits").setDisplay("20.0090")
											.setNumeric(Double.valueOf("20.0090")))
									.setPublish(false)),
				new DischargeActivity()
					.setDischargeSummary(
							new DischargeSummary().setMeasurementGrade(MeasurementGradeType.Excellent)
									.setMeanGageHeight((QuantityWithDisplay) new QuantityWithDisplay()
											.setUnit("meanGageHeightUnits").setDisplay("2.0090")
											.setNumeric(Double.valueOf("2.0090")))
									.setDischarge((QuantityWithDisplay) new QuantityWithDisplay()
											.setUnit("dischargeUnits").setDisplay("20.0090")
											.setNumeric(Double.valueOf("20.0090")))
									.setPublish(false)))
			.collect(Collectors.toCollection(ArrayList::new));
		return activities;
	}

	protected List<FieldVisitMeasurement> getFieldVisitMeasurements() {
		return Stream.of(
				new FieldVisitMeasurement(null, BigDecimal.valueOf(20.009), BigDecimal.valueOf(21.00945),
						BigDecimal.valueOf(19.00855), null, false),
				new FieldVisitMeasurement(null, BigDecimal.valueOf(20.009), BigDecimal.valueOf(20.40918),
						BigDecimal.valueOf(19.60882), null, false),
				new FieldVisitMeasurement(null, BigDecimal.valueOf(20.009), BigDecimal.valueOf(21.00945),
						BigDecimal.valueOf(19.00855), null, false),
				new FieldVisitMeasurement(null, BigDecimal.valueOf(20.009), BigDecimal.valueOf(20.40918),
						BigDecimal.valueOf(19.60882), null, false))
			.collect(Collectors.toList());
	}
}
