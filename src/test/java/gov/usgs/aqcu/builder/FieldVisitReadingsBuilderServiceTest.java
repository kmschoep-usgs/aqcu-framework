package gov.usgs.aqcu.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Inspection;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.MeasurementGradeType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QuantityWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeSummary;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.model.FieldVisitReading;

@RunWith(SpringRunner.class)
public class FieldVisitReadingsBuilderServiceTest {

	private FieldVisitReadingsBuilderService service;

	@Before
	public void setup() throws Exception {
		service = new FieldVisitReadingsBuilderService();
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
	public void extractFieldVisitReadingsNullDataTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(null, null);
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertTrue(readings.isEmpty());
	}

	@Test
	public void extractFieldVisitReadingsNullInspectionTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), null);
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertTrue(readings.isEmpty());
	}

	@Test
	public void extractFieldVisitReadingsNullInspectionInspectionsTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(getReadings(), null));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertEquals(2, readings.size());
	}

	@Test
	public void extractFieldVisitReadingsNullInspectionReadingsTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(null, getInspections()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertTrue(readings.isEmpty());
	}

	@Test
	public void extractFieldVisitReadingsEmptyInspectionInspectionsTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(getReadings(), new ArrayList<>()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertEquals(2, readings.size());
	}

	@Test
	public void extractFieldVisitReadingsEmptyInspectionReadingsTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(new ArrayList<>(), getInspections()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertTrue(readings.isEmpty());
	}

	@Test
	public void extractFieldVisitReadingsEmptyInspectionDataTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(new ArrayList<>(), new ArrayList<>()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertTrue(readings.isEmpty());
	}

	@Test
	public void extractFieldVisitReadingsFilterToParamNullTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(getReadings(), getInspections()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, null, null);
		assertNotNull(readings);
		assertEquals(4, readings.size());
	}

	@Test
	public void extractFieldVisitReadingsFilterToParamEmptyTest() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(getReadings(), getInspections()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, "", null);
		assertNotNull(readings);
		assertEquals(4, readings.size());
	}

	@Test
	public void extractFieldVisitReadingsFilterToParamTest1() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(getReadings(), getInspections()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, "param1", null);
		assertNotNull(readings);
		assertEquals(1, readings.size());
	}

	@Test
	public void extractFieldVisitReadingsFilterToParamTest2() {
		FieldVisitDataServiceResponse resp = getFieldVisitDataServiceResponse(getDischargeActivities(), getInspectionActivity(getReadings(), getInspections()));
		
		List<FieldVisitReading> readings = service.extractReadings(null, resp, "param3", null);
		assertNotNull(readings);
		assertTrue(readings.isEmpty());
	}

	@Test
	public void extractFieldVisitReadingsFullTest1() {
		List<FieldVisitReading> result = service.extractReadings(
			Instant.parse("2018-01-01T00:00:00Z"), getFieldVisitDataServiceResponse(getDischargeActivities(), 
			getInspectionActivity(getReadings(), getInspections())), 
			null, null
		);

		assertNotNull(result);
		assertEquals(result.size(), 4);
		assertTrue(result.get(0).getAssociatedIvQualifiers().isEmpty());
		assertNull(result.get(0).getAssociatedIvTime());
		assertNull(result.get(0).getAssociatedIvValue());
		assertEquals(result.get(0).getComments().size(), 3);
		assertEquals(result.get(0).getMonitoringMethod(), "method1");
		assertEquals(result.get(0).getParty(), "test-party");
		assertEquals(result.get(0).getReadingType(), ReadingType.ExtremeMax);
		assertEquals(result.get(0).getSublocation(), "subloc1");
		assertEquals(result.get(0).getTime(), Instant.parse("2018-02-01T00:00:00Z"));
		assertEquals(result.get(0).getUncertainty(), null);
		assertEquals(result.get(0).getValue(), "1");
		assertEquals(result.get(0).getVisitStatus(), "TODO");
		assertEquals(result.get(0).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
		assertTrue(result.get(1).getAssociatedIvQualifiers().isEmpty());
		assertNull(result.get(1).getAssociatedIvTime());
		assertNull(result.get(1).getAssociatedIvValue());
		assertEquals(result.get(1).getComments().size(), 0);
		assertEquals(result.get(1).getMonitoringMethod(), "method2");
		assertEquals(result.get(1).getParty(), "test-party");
		assertEquals(result.get(1).getReadingType(), ReadingType.Reference);
		assertEquals(result.get(1).getSublocation(), "subloc2");
		assertEquals(result.get(1).getTime(), Instant.parse("2018-02-02T00:00:00Z"));
		assertEquals(result.get(1).getUncertainty(), null);
		assertEquals(result.get(1).getValue(), "2");
		assertEquals(result.get(1).getVisitStatus(), "TODO");
		assertEquals(result.get(1).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
		assertTrue(result.get(2).getAssociatedIvQualifiers().isEmpty());
		assertNull(result.get(2).getAssociatedIvTime());
		assertNull(result.get(2).getAssociatedIvValue());
		assertEquals(result.get(2).getComments().size(), 1);
		assertEquals(result.get(2).getMonitoringMethod(), FieldVisitReadingsBuilderService.MON_METH_CREST_STAGE);
		assertEquals(result.get(2).getParty(), "test-party");
		assertEquals(result.get(2).getReadingType(), ReadingType.ExtremeMax);
		assertEquals(result.get(2).getSublocation(), "testsubloc");
		assertEquals(result.get(2).getTime(), null);
		assertEquals(result.get(2).getUncertainty(), null);
		assertEquals(result.get(2).getValue(), null);
		assertEquals(result.get(2).getVisitStatus(), "TODO");
		assertEquals(result.get(2).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
		assertTrue(result.get(3).getAssociatedIvQualifiers().isEmpty());
		assertNull(result.get(3).getAssociatedIvTime());
		assertNull(result.get(3).getAssociatedIvValue());
		assertEquals(result.get(3).getComments().size(), 1);
		assertEquals(result.get(3).getMonitoringMethod(), FieldVisitReadingsBuilderService.MON_METH_MAX_MIN_INDICATOR);
		assertEquals(result.get(3).getParty(), "test-party");
		assertEquals(result.get(3).getReadingType(), ReadingType.ExtremeMax);
		assertEquals(result.get(3).getSublocation(), "testsubloc");
		assertEquals(result.get(3).getTime(), null);
		assertEquals(result.get(3).getUncertainty(), null);
		assertEquals(result.get(3).getValue(), null);
		assertEquals(result.get(3).getVisitStatus(), "TODO");
		assertEquals(result.get(3).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
	}

	@Test
	public void extractFieldVisitReadingsFullTest2() {
		List<FieldVisitReading> result = service.extractReadings(
			Instant.parse("2018-01-01T00:00:00Z"), getFieldVisitDataServiceResponse(getDischargeActivities(), 
			getInspectionActivity(getReadings(), getInspections())), 
			"param1", null
		);

		assertNotNull(result);
		assertEquals(result.size(), 1);
		assertTrue(result.get(0).getAssociatedIvQualifiers().isEmpty());
		assertNull(result.get(0).getAssociatedIvTime());
		assertNull(result.get(0).getAssociatedIvValue());
		assertEquals(result.get(0).getComments().size(), 3);
		assertEquals(result.get(0).getMonitoringMethod(), "method1");
		assertEquals(result.get(0).getParty(), "test-party");
		assertEquals(result.get(0).getReadingType(), ReadingType.ExtremeMax);
		assertEquals(result.get(0).getSublocation(), "subloc1");
		assertEquals(result.get(0).getTime(), Instant.parse("2018-02-01T00:00:00Z"));
		assertEquals(result.get(0).getUncertainty(), null);
		assertEquals(result.get(0).getValue(), "1");
		assertEquals(result.get(0).getVisitStatus(), "TODO");
		assertEquals(result.get(0).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
	}

	@Test
	public void extractFieldVisitReadingsFullTest3() {
		List<FieldVisitReading> result = service.extractReadings(
			Instant.parse("2018-01-01T00:00:00Z"), getFieldVisitDataServiceResponse(getDischargeActivities(), 
			getInspectionActivity(getReadings(), getInspections())), 
			"", Arrays.asList(InspectionType.FieldMeter.name())
		);

		assertNotNull(result);
		assertEquals(result.size(), 4);
		assertEquals(result.get(0).getComments().size(), 1);
		assertEquals(result.get(1).getComments().size(), 0);
		assertEquals(result.get(2).getComments().size(), 1);
		assertEquals(result.get(3).getComments().size(), 1);
	}

	@Test
	public void serialNumberToCommentNullTest() {
		Map<String, List<String>> result = service.serialNumberToComment(null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void serialNumberToCommentEmptyTest() {
		Map<String, List<String>> result = service.serialNumberToComment(Arrays.asList());
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void serialNumberToCommentTest() {
		List<Inspection> inspections = Arrays.asList(
			new Inspection()
				.setComments("test1")
				.setSerialNumber("1"),
			new Inspection()
				.setComments("test2")
				.setSerialNumber("1"),
			new Inspection()
				.setComments("test3")
				.setSerialNumber("2"),
			new Inspection()
				.setComments("test4")
				.setSerialNumber("1"),
			new Inspection()
				.setComments("test5")
				.setSerialNumber("3"),
			new Inspection()
				.setComments("test6")
				.setSerialNumber("3")
		);
		Map<String, List<String>> result = service.serialNumberToComment(inspections);
		assertNotNull(result);
		assertEquals(result.size(), 3);
		assertThat(result.get("1"), containsInAnyOrder("test1", "test2", "test4"));
		assertThat(result.get("2"), containsInAnyOrder("test3"));
		assertThat(result.get("3"), containsInAnyOrder("test5", "test6"));
	}

	@Test
	public void filterInspectionsNullTest() {
		List<Inspection> result = service.filterInspections(null, null);
		assertNull(result);
		result = service.filterInspections(getInspections(), null);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(4, result.size());
		result = service.filterInspections(null, Arrays.asList("filter"));
		assertNull(result);
	}

	@Test
	public void filterInspectionsEmptyTest() {
		List<Inspection> result = service.filterInspections(Arrays.asList(), Arrays.asList());
		assertNotNull(result);
		assertTrue(result.isEmpty());
		result = service.filterInspections(Arrays.asList(), Arrays.asList("filter"));
		assertNotNull(result);
		assertTrue(result.isEmpty());
		result = service.filterInspections(getInspections(), Arrays.asList());
		assertNotNull(result);
		assertEquals(4, result.size());
	}

	@Test
	public void filterInspectionsTest() {
		List<Inspection> result = service.filterInspections(getInspections(), Arrays.asList(InspectionType.CrestStageGage.name()));
		assertNotNull(result);
		assertEquals(result.size(), 2);
		result = service.filterInspections(getInspections(), Arrays.asList(InspectionType.MaximumMinimumGage.name()));
		assertNotNull(result);
		assertEquals(result.size(), 2);
		result = service.filterInspections(getInspections(), Arrays.asList(InspectionType.FieldMeter.name()));
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyCrestStageReadingsNullTest() {
		List<FieldVisitReading> result = service.extractEmptyCrestStageReadings(null, null, null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyCrestStageReadingsEmptyTest() {
		List<FieldVisitReading> result = service.extractEmptyCrestStageReadings(null, Arrays.asList(), null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyCrestStageReadingsTest() {
		List<FieldVisitReading> result = service.extractEmptyCrestStageReadings(Instant.parse("2018-01-01T00:00:00Z"), getInspections(), "testparty");
		assertNotNull(result);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getAssociatedIvQualifiers().size(), 0);
		assertEquals(result.get(0).getAssociatedIvTime(), null);
		assertEquals(result.get(0).getAssociatedIvValue(), null);
		assertEquals(result.get(0).getComments().size(), 1);
		assertEquals(result.get(0).getComments().get(0), "No mark");
		assertEquals(result.get(0).getLastVisitPrior(), null);
		assertEquals(result.get(0).getMonitoringMethod(), FieldVisitReadingsBuilderService.MON_METH_CREST_STAGE);
		assertEquals(result.get(0).getParty(), "testparty");
		assertEquals(result.get(0).getReadingType(), ReadingType.ExtremeMax);
		assertEquals(result.get(0).getSublocation(), "testsubloc");
		assertEquals(result.get(0).getTime(), null);
		assertEquals(result.get(0).getUncertainty(), null);
		assertEquals(result.get(0).getValue(), null);
		assertEquals(result.get(0).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
		assertEquals(result.get(0).getVisitStatus(), "TODO");
	}

	@Test
	public void extractEmptyMaxMinIndicatorReadingsNullTest() {
		List<FieldVisitReading> result = service.extractEmptyMaxMinIndicatorReadings(null, null, null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyMaxMinIndicatorReadingsEmptyTest() {
		List<FieldVisitReading> result = service.extractEmptyMaxMinIndicatorReadings(null, Arrays.asList(), null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyMaxMinIndicatorReadingsTest() {
		List<FieldVisitReading> result = service.extractEmptyMaxMinIndicatorReadings(Instant.parse("2018-01-01T00:00:00Z"), getInspections(), "testparty");
		assertNotNull(result);
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getAssociatedIvQualifiers().size(), 0);
		assertEquals(result.get(0).getAssociatedIvTime(), null);
		assertEquals(result.get(0).getAssociatedIvValue(), null);
		assertEquals(result.get(0).getComments().size(), 1);
		assertEquals(result.get(0).getComments().get(0), "inspection1");
		assertEquals(result.get(0).getLastVisitPrior(), null);
		assertEquals(result.get(0).getMonitoringMethod(), FieldVisitReadingsBuilderService.MON_METH_MAX_MIN_INDICATOR);
		assertEquals(result.get(0).getParty(), "testparty");
		assertEquals(result.get(0).getReadingType(), ReadingType.ExtremeMax);
		assertEquals(result.get(0).getSublocation(), "testsubloc");
		assertEquals(result.get(0).getTime(), null);
		assertEquals(result.get(0).getUncertainty(), null);
		assertEquals(result.get(0).getValue(), null);
		assertEquals(result.get(0).getVisitTime(), Instant.parse("2018-01-01T00:00:00Z"));
		assertEquals(result.get(0).getVisitStatus(), "TODO");
	}

	@Test
	public void extractEmptyHighWaterMarkReadingsNullTest() {
		List<FieldVisitReading> result = service.extractEmptyHighWaterMarkReadings(null, null, null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyHighWaterMarkReadingsEmptyTest() {
		List<FieldVisitReading> result = service.extractEmptyHighWaterMarkReadings(null, Arrays.asList(), null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void extractEmptyHighWaterMarkReadingsTest() {
		List<FieldVisitReading> result = service.extractEmptyHighWaterMarkReadings(Instant.parse("2018-01-01T00:00:00Z"), getInspections(), "testparty");
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	protected FieldVisitDataServiceResponse getFieldVisitDataServiceResponse(ArrayList<DischargeActivity> dischargeActivities, InspectionActivity inspectionActivity) {
		return new FieldVisitDataServiceResponse().setDischargeActivities(dischargeActivities).setInspectionActivity(inspectionActivity);
	}

	protected InspectionActivity getInspectionActivity(ArrayList<Reading> readings, ArrayList<Inspection> inspections) {
		InspectionActivity ret = new InspectionActivity();
		ret.setReadings(readings);
		ret.setInspections(inspections);
		ret.setParty("test-party");
		return ret;
	}

	protected ArrayList<Inspection> getInspections() {
		ArrayList<Inspection> inspections = Stream
			.of(new Inspection()
					.setComments("inspection1")
					.setSerialNumber("1")
					.setSubLocationIdentifier("testsubloc")
					.setInspectionType(InspectionType.MaximumMinimumGage),
				new Inspection()
					.setSerialNumber("2")
					.setSubLocationIdentifier("testsubloc")
					.setInspectionType(InspectionType.MaximumMinimumGage),
				new Inspection()
					.setComments("inspection2")
					.setSerialNumber("1")
					.setSubLocationIdentifier("testsubloc")
					.setInspectionType(InspectionType.CrestStageGage),
				new Inspection()
					.setComments("No mark")
					.setSerialNumber("3")
					.setSubLocationIdentifier("testsubloc")
					.setInspectionType(InspectionType.CrestStageGage)
			)
			.collect(Collectors.toCollection(ArrayList::new));
		return inspections;
	}

	protected ArrayList<Reading> getReadings() {
		ArrayList<Reading> readings = Stream
			.of(new Reading()
				.setIsValid(true)
				.setReadingType(ReadingType.ExtremeMax)
				.setComments("comments1")
				.setParameter("param1")
				.setSerialNumber("1")
				.setMonitoringMethod("method1")
				.setSubLocationIdentifier("subloc1")
				.setTime(Instant.parse("2018-02-01T00:00:00Z"))
				.setValue(new DoubleWithDisplay().setDisplay("1").setNumeric(1.0D)),
				new Reading()
				.setIsValid(false)
				.setReadingType(ReadingType.Reference)
				.setParameter("param2")
				.setSerialNumber("4")
				.setMonitoringMethod("method2")
				.setSubLocationIdentifier("subloc2")
				.setTime(Instant.parse("2018-02-02T00:00:00Z"))
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
