package gov.usgs.aqcu.builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import gov.usgs.aqcu.model.DataGap;
import gov.usgs.aqcu.model.DataGapExtent;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class DataGapListBuilderTest {
	List<TimeSeriesPoint> pointList;
	DataGapListBuilderService service;

	final DoubleWithDisplay GAP_MARKER_VALUE = new DoubleWithDisplay().setDisplay("EMPTY").setNumeric(null);
	final DoubleWithDisplay POINT_VALUE = new DoubleWithDisplay().setDisplay("1").setNumeric(1.0);
	final TimeSeriesPoint GAP_MARKER_OVER_START = new TimeSeriesPoint()
		.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-01T00:00:00Z")))
		.setValue(GAP_MARKER_VALUE);
	final TimeSeriesPoint GAP_MARKER_CONTAINED = new TimeSeriesPoint()
		.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-02T12:00:00Z")))
		.setValue(GAP_MARKER_VALUE);
	final TimeSeriesPoint GAP_MARKER_OVER_END = new TimeSeriesPoint()
		.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-05T00:00:00Z")))
		.setValue(GAP_MARKER_VALUE);
	final TimeSeriesPoint POINT_1 = new TimeSeriesPoint()
		.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-02T00:00:00Z")))
		.setValue(POINT_VALUE);
	final TimeSeriesPoint POINT_2 = new TimeSeriesPoint()
		.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-03T00:00:00Z")))
		.setValue(POINT_VALUE);
	final TimeSeriesPoint POINT_3 = new TimeSeriesPoint()
		.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(Instant.parse("2017-01-04T00:00:00Z")))
		.setValue(POINT_VALUE);

	@Before
	public void setup() {
		service = new DataGapListBuilderService();
		pointList = new ArrayList<>();
		pointList.add(POINT_1);
		pointList.add(POINT_2);
		pointList.add(POINT_3);
	}

	@Test
	public void noPointsTest() {
		List<DataGap> result = service.buildGapList(new ArrayList<>());
		assertTrue(result.isEmpty());
	}

	@Test
	public void nullPointsTest() {
		try {
			service.buildGapList(null);
		} catch (NullPointerException e) {
			return;
		} catch (Exception e) {
			fail("Expected NullPointerException but got " + e.toString());
		}
		fail("Expected NullPointerException but got no exception.");
	}

	@Test 
	public void noGapsTest() {
		List<DataGap> result = service.buildGapList(pointList);
		assertTrue(result.isEmpty());
	}

	@Test
	public void containedGapTest() {
		pointList.add(1, GAP_MARKER_CONTAINED);
		List<DataGap> result = service.buildGapList(pointList);
		assertEquals(1, result.size());
		assertEquals(POINT_1.getTimestamp().getDateTimeOffset(), result.get(0).getStartTime());
		assertEquals(POINT_2.getTimestamp().getDateTimeOffset(), result.get(0).getEndTime());
		assertEquals(BigDecimal.valueOf(24.0), result.get(0).getDurationInHours());
		assertEquals(DataGapExtent.CONTAINED, result.get(0).getGapExtent());
	}

	@Test
	public void overStartGapTest() {
		pointList.add(0, GAP_MARKER_OVER_START);
		List<DataGap> result = service.buildGapList(pointList);
		assertEquals(1, result.size());
		assertEquals(null, result.get(0).getStartTime());
		assertEquals(POINT_1.getTimestamp().getDateTimeOffset(), result.get(0).getEndTime());
		assertEquals(null, result.get(0).getDurationInHours());
		assertEquals(DataGapExtent.OVER_START, result.get(0).getGapExtent());
	}

	@Test
	public void overEndGapTest() {
		pointList.add(3, GAP_MARKER_OVER_END);
		List<DataGap> result = service.buildGapList(pointList);
		assertEquals(1, result.size());
		assertEquals(POINT_3.getTimestamp().getDateTimeOffset(), result.get(0).getStartTime());
		assertEquals(null, result.get(0).getEndTime());
		assertEquals(null, result.get(0).getDurationInHours());
		assertEquals(DataGapExtent.OVER_END, result.get(0).getGapExtent());
	}

	@Test
	public void overAllGapTest() {
		List<TimeSeriesPoint> singleMarkerList = new ArrayList<>();
		singleMarkerList.add(GAP_MARKER_CONTAINED);
		List<DataGap> result = service.buildGapList(singleMarkerList);
		assertEquals(1, result.size());
		assertEquals(null, result.get(0).getStartTime());
		assertEquals(null, result.get(0).getEndTime());
		assertEquals(null, result.get(0).getDurationInHours());
		assertEquals(DataGapExtent.OVER_ALL, result.get(0).getGapExtent());
	}

	@Test
	public void multipleGapsTest() {
		pointList.add(0, GAP_MARKER_OVER_START);
		pointList.add(2, GAP_MARKER_CONTAINED);
		pointList.add(5, GAP_MARKER_OVER_END);
		List<DataGap> result = service.buildGapList(pointList);
		assertEquals(3, result.size());
		assertEquals(null, result.get(0).getStartTime());
		assertEquals(POINT_1.getTimestamp().getDateTimeOffset(), result.get(0).getEndTime());
		assertEquals(null, result.get(0).getDurationInHours());
		assertEquals(DataGapExtent.OVER_START, result.get(0).getGapExtent());
		assertEquals(POINT_1.getTimestamp().getDateTimeOffset(), result.get(1).getStartTime());
		assertEquals(POINT_2.getTimestamp().getDateTimeOffset(), result.get(1).getEndTime());
		assertEquals(BigDecimal.valueOf(24.0), result.get(1).getDurationInHours());
		assertEquals(DataGapExtent.CONTAINED, result.get(1).getGapExtent());
		assertEquals(POINT_3.getTimestamp().getDateTimeOffset(), result.get(2).getStartTime());
		assertEquals(null, result.get(2).getEndTime());
		assertEquals(null, result.get(2).getDurationInHours());
		assertEquals(DataGapExtent.OVER_END, result.get(2).getGapExtent());
	}
}
