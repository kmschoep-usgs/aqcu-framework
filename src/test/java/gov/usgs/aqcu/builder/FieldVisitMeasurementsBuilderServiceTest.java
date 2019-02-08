package gov.usgs.aqcu.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.usgs.aqcu.model.FieldVisitMeasurement;
import gov.usgs.aqcu.model.MeasurementGrade;
import gov.usgs.aqcu.model.RatingModelErrorVector;
import gov.usgs.aqcu.retrieval.RatingModelInputValuesService;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.MeasurementGradeType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QuantityWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ControlConditionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeSummary;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;

@RunWith(SpringRunner.class)
public class FieldVisitMeasurementsBuilderServiceTest {
	@MockBean
	private RatingModelInputValuesService ratingInputService;

	private FieldVisitMeasurementsBuilderService service;

	@Before
	public void setup() throws Exception {
		service = new FieldVisitMeasurementsBuilderService(ratingInputService);
	}

	@Test
	public void calculateErrorTest() {
		BigDecimal dischargeValue = BigDecimal.valueOf(1.0D);
		RatingModelErrorVector result = service.calculateDsichargeError(MeasurementGrade.EXCELLENT, dischargeValue);
		assertEquals(result.getMaxErrorValue(), dischargeValue.add(dischargeValue.multiply(MeasurementGrade.EXCELLENT.getPercentageOfError())));
		assertEquals(result.getValue(), dischargeValue);
		assertEquals(result.getMinErrorValue(), dischargeValue.subtract(dischargeValue.multiply(MeasurementGrade.EXCELLENT.getPercentageOfError())));
	}

	@Test
	public void getGageHeightErrorTest() {
		BigDecimal maxError1 = BigDecimal.valueOf(3.0D);
		BigDecimal value1 = BigDecimal.valueOf(2.0D);
		BigDecimal minError1 = BigDecimal.valueOf(1.0D);
		BigDecimal meanGageHeight = BigDecimal.valueOf(0.0D);
		RatingModelErrorVector outputValues1 = new RatingModelErrorVector();
		outputValues1.setMaxErrorValue(maxError1);
		outputValues1.setValue(value1);
		outputValues1.setMinErrorValue(minError1);
		RatingModelErrorVector outputValues2 = new RatingModelErrorVector();
		outputValues2.setMaxErrorValue(maxError1);
		outputValues2.setValue(maxError1);
		outputValues2.setMinErrorValue(maxError1);
		RatingModelErrorVector outputValues3 = new RatingModelErrorVector();
		outputValues3.setMaxErrorValue(value1);
		outputValues3.setValue(value1);
		outputValues3.setMinErrorValue(value1);
		RatingModelErrorVector outputValues4 = new RatingModelErrorVector();
		outputValues4.setMaxErrorValue(minError1);
		outputValues4.setValue(minError1);
		outputValues4.setMinErrorValue(minError1);

		given(ratingInputService.get(any(String.class), any(Instant.class), eq(outputValues1.getAsList()))).willReturn(
			new ArrayList<BigDecimal>(Arrays.asList(maxError1, value1, minError1))
		);
		given(ratingInputService.get(any(String.class), any(Instant.class), eq(outputValues2.getAsList()))).willReturn(
			new ArrayList<BigDecimal>(Arrays.asList(maxError1, value1))
		);
		given(ratingInputService.get(any(String.class), any(Instant.class), eq(outputValues3.getAsList()))).willReturn(
			new ArrayList<BigDecimal>(Arrays.asList(maxError1))
		);
		given(ratingInputService.get(any(String.class), any(Instant.class), eq(outputValues4.getAsList()))).willReturn(
			new ArrayList<BigDecimal>(Arrays.asList())
		);

		RatingModelErrorVector result = service.getGageHeightError("Test", Instant.parse("2018-01-01T00:00:00Z"), outputValues1, meanGageHeight);
		assertEquals(result.getMaxErrorValue(), maxError1.subtract(meanGageHeight));
		assertEquals(result.getValue(), value1.subtract(meanGageHeight));
		assertEquals(result.getMinErrorValue(), minError1.subtract(meanGageHeight));
		result = service.getGageHeightError("Test", Instant.parse("2018-01-01T00:00:00Z"), outputValues2, meanGageHeight);
		assertEquals(result.getMaxErrorValue(), maxError1.subtract(meanGageHeight));
		assertEquals(result.getValue(), value1.subtract(meanGageHeight));
		assertEquals(result.getMinErrorValue(), null);
		result = service.getGageHeightError("Test", Instant.parse("2018-01-01T00:00:00Z"), outputValues3, meanGageHeight);
		assertEquals(result.getMaxErrorValue(), maxError1.subtract(meanGageHeight));
		assertEquals(result.getValue(), null);
		assertEquals(result.getMinErrorValue(), null);
		result = service.getGageHeightError("Test", Instant.parse("2018-01-01T00:00:00Z"), outputValues4, meanGageHeight);
		assertEquals(result.getMaxErrorValue(), null);
		assertEquals(result.getValue(), null);
		assertEquals(result.getMinErrorValue(), null);
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
				new ControlConditionActivity().setControlCondition("DebrisHeavy"));
		assertEquals("DebrisHeavy", service.extractControlCondition(resp));
	}

	@Test
	public void extractFieldVisitMeasurement_noDischargeActivitiesTest() {
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(new FieldVisitDataServiceResponse(), null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void extractFieldVisitMeasurement_noDischargeSummaryTest() {
		ArrayList<DischargeActivity> activities = Stream.of(new DischargeActivity(), new DischargeActivity())
				.collect(Collectors.toCollection(ArrayList::new));
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(getFieldVisitDataServiceResponse(activities, null), null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void extractFieldVisitMeasurement_noDischargeTest() {
		ArrayList<DischargeActivity> activities = Stream
				.of(new DischargeActivity().setDischargeSummary(new DischargeSummary()),
						new DischargeActivity().setDischargeSummary(new DischargeSummary()))
				.collect(Collectors.toCollection(ArrayList::new));
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(getFieldVisitDataServiceResponse(activities, null), null);
		assertTrue(actual.isEmpty());
	}

	@Test
	public void extractFieldVisitMeasurement_happyTest() {
		List<FieldVisitMeasurement> actual = service.extractFieldVisitMeasurements(getFieldVisitDataServiceResponse(getDischargeActivities(), null), null);
		assertEquals(2, actual.size());
	}

	@Test
	public void createFieldVisitMeasurementNoRatingTest() {
		DischargeSummary summary = new DischargeSummary()
			.setMeasurementGrade(MeasurementGradeType.Good).setMeasurementId("20.0090")
			.setDischarge((QuantityWithDisplay) new QuantityWithDisplay().setUnit("dischargeUnits")
				.setDisplay("20.0090").setNumeric(Double.valueOf("20.0090"))
			)
			.setMeanGageHeight((QuantityWithDisplay) new QuantityWithDisplay().setUnit("meanGageHeightUnits")
					.setDisplay("2.0090").setNumeric(Double.valueOf("2.0090"))
			)
			.setMeasurementStartTime(Instant.parse("2017-01-01T00:00:00Z"))
			.setPublish(false);
		BigDecimal errorAmt = DoubleWithDisplayUtil.getRoundedValue(summary.getDischarge()).multiply(MeasurementGrade.GOOD.getPercentageOfError());
		FieldVisitMeasurement result = service.createFieldVisitMeasurement(summary, null);

		assertNotNull(result.getDischarge());
		assertNotNull(result.getErrorMaxDischarge());
		assertNotNull(result.getErrorMinDischarge());
		assertNotNull(result.getMeanGageHeight());
		assertNotNull(result.getMeasurementNumber());
		assertNotNull(result.getMeasurementStartDate());
		assertNotNull(result.isPublish());
		assertNull(result.getErrorMaxShiftInFeet());
		assertNull(result.getErrorMinShiftInFeet());
		assertNull(result.getShiftInFeet());
		assertNull(result.getShiftNumber());
		assertNull(result.isHistoric());
		assertEquals(result.getDischarge(), DoubleWithDisplayUtil.getRoundedValue(summary.getDischarge()));
		assertEquals(result.getErrorMaxDischarge(), DoubleWithDisplayUtil.getRoundedValue(summary.getDischarge()).add(errorAmt));
		assertEquals(result.getErrorMinDischarge(), DoubleWithDisplayUtil.getRoundedValue(summary.getDischarge()).subtract(errorAmt));
		assertEquals(result.getMeanGageHeight(), DoubleWithDisplayUtil.getRoundedValue(summary.getMeanGageHeight()));
		assertEquals(result.getMeasurementNumber(), "20.0090");
		assertEquals(result.getMeasurementStartDate(), Instant.parse("2017-01-01T00:00:00Z"));
		assertEquals(result.isPublish(), false);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createFieldVisitMeasurementWithRatingTest() {
		DischargeSummary summary = new DischargeSummary()
			.setMeasurementGrade(MeasurementGradeType.Good).setMeasurementId("20.0090")
			.setDischarge((QuantityWithDisplay) new QuantityWithDisplay().setUnit("dischargeUnits")
				.setDisplay("20.0090").setNumeric(Double.valueOf("20.0090"))
			)
			.setMeanGageHeight((QuantityWithDisplay) new QuantityWithDisplay().setUnit("meanGageHeightUnits")
					.setDisplay("2.0090").setNumeric(Double.valueOf("2.0090"))
			)
			.setMeasurementStartTime(Instant.parse("2017-01-01T00:00:00Z"))
			.setPublish(false);
		BigDecimal maxErrorResp = BigDecimal.valueOf(3.0D);
		BigDecimal valueResp = BigDecimal.valueOf(2.0D);
		BigDecimal minErrorResp = BigDecimal.valueOf(1.0D);

		given(ratingInputService.get(any(String.class), any(Instant.class), any(List.class))).willReturn(
			new ArrayList<BigDecimal>(Arrays.asList(maxErrorResp, valueResp, minErrorResp))
		);

		BigDecimal dischargeValue = DoubleWithDisplayUtil.getRoundedValue(summary.getDischarge());
		BigDecimal meanGageHeight = DoubleWithDisplayUtil.getRoundedValue(summary.getMeanGageHeight());
		BigDecimal errorAmt = dischargeValue.multiply(MeasurementGrade.GOOD.getPercentageOfError());
		FieldVisitMeasurement result = service.createFieldVisitMeasurement(summary, "test-rating");

		assertNotNull(result.getDischarge());
		assertNotNull(result.getErrorMaxDischarge());
		assertNotNull(result.getErrorMinDischarge());
		assertNotNull(result.getMeanGageHeight());
		assertNotNull(result.getMeasurementNumber());
		assertNotNull(result.getMeasurementStartDate());
		assertNotNull(result.isPublish());
		assertNotNull(result.getErrorMaxShiftInFeet());
		assertNotNull(result.getErrorMinShiftInFeet());
		assertNotNull(result.getShiftInFeet());
		assertNull(result.getShiftNumber());
		assertNull(result.isHistoric());
		assertEquals(result.getDischarge(), dischargeValue);
		assertEquals(result.getErrorMaxDischarge(), dischargeValue.add(errorAmt));
		assertEquals(result.getErrorMinDischarge(), dischargeValue.subtract(errorAmt));
		assertEquals(result.getMeanGageHeight(), meanGageHeight);
		assertEquals(result.getShiftInFeet(), valueResp.subtract(meanGageHeight));
		assertEquals(result.getErrorMaxShiftInFeet(), maxErrorResp.subtract(meanGageHeight));
		assertEquals(result.getErrorMinShiftInFeet(), minErrorResp.subtract(meanGageHeight));
		assertEquals(result.getMeasurementNumber(), "20.0090");
		assertEquals(result.getMeasurementStartDate(), Instant.parse("2017-01-01T00:00:00Z"));
		assertEquals(result.isPublish(), false);
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
}
