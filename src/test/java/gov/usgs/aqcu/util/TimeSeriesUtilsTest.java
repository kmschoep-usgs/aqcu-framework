package gov.usgs.aqcu.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.Matchers.contains;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import gov.usgs.aqcu.model.MinMaxData;
import gov.usgs.aqcu.model.MinMaxPoint;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesPoint;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DoubleWithDisplay;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.StatisticalDateTimeOffset;

public class TimeSeriesUtilsTest {	
	private Instant nowInstant;
	private LocalDate nowLocalDate;

	@Before
	public void setup() {
		nowInstant = Instant.now();
		nowLocalDate = LocalDate.now();
	}

	@Test
	public void getZoneOffsetNullTest() {
		TimeSeriesDescription nullTsDesc = null;
		LocationDataServiceResponse nullLocData = null;
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(nullTsDesc));
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(nullLocData));
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(new TimeSeriesDescription()));
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(new LocationDataServiceResponse()));
	}

	@Test
	public void getZoneOffsetMinutesTest() {
		assertEquals(ZoneOffset.ofHoursMinutes(-5, -30), TimeSeriesUtils.getZoneOffset(new TimeSeriesDescription().setUtcOffset(-5.3)));
	}

	@Test
	public void getZoneOffsetHoursTest() {
		assertEquals(ZoneOffset.ofHours(6), TimeSeriesUtils.getZoneOffset(new TimeSeriesDescription().setUtcOffset(6.0)));
	}

	@Test
	public void getZoneOffsetEatExceptionTest() {
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(new TimeSeriesDescription().setUtcOffset(24.0)));
	}

	@Test
	public void isDailyTimeSeriesNullTest() {
		assertFalse(TimeSeriesUtils.isDailyTimeSeries(null));
		assertFalse(TimeSeriesUtils.isDailyTimeSeries(new TimeSeriesDescription()));
	}

	@Test
	public void isDailyTimeSeriesTrueTest() {
		assertTrue(TimeSeriesUtils.isDailyTimeSeries(new TimeSeriesDescription().setComputationPeriodIdentifier("DaiLY")));
	}

	@Test
	public void isDailyTimeSeriesFalseTest() {
		assertFalse(TimeSeriesUtils.isDailyTimeSeries(new TimeSeriesDescription().setComputationPeriodIdentifier("NEVER")));
	}

	@Test
	public void isPrimaryTimeSeriesNullTest() {
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(null));
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription()));
		ExtendedAttribute notPrimaryAttr = new ExtendedAttribute();
		notPrimaryAttr.setName(AquariusRetrievalUtils.getPrimaryFilter().getFilterName());
		notPrimaryAttr.setValue(null);
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(notPrimaryAttr))));
		notPrimaryAttr = new ExtendedAttribute();
		notPrimaryAttr.setName(null);
		notPrimaryAttr.setValue(AquariusRetrievalUtils.getPrimaryFilter().getFilterValue());
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(notPrimaryAttr))));
		notPrimaryAttr = new ExtendedAttribute();
		notPrimaryAttr.setName(null);
		notPrimaryAttr.setValue(null);
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(notPrimaryAttr))));
	}

	@Test
	public void isPrimaryTimeSeriesTrueTest() {
		ExtendedAttribute primaryAttr = new ExtendedAttribute();
		primaryAttr.setName(AquariusRetrievalUtils.getPrimaryFilter().getFilterName());
		primaryAttr.setValue(AquariusRetrievalUtils.getPrimaryFilter().getFilterValue());
		assertTrue(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(primaryAttr))));
	}

	@Test
	public void isPrimarySeriesFalseTest() {
		ExtendedAttribute notPrimaryAttr = new ExtendedAttribute();
		notPrimaryAttr.setName("NotPrimary");
		notPrimaryAttr.setValue("NotPrimary");
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(notPrimaryAttr))));
		notPrimaryAttr = new ExtendedAttribute();
		notPrimaryAttr.setName(AquariusRetrievalUtils.getPrimaryFilter().getFilterName());
		notPrimaryAttr.setValue("NotPrimary");
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(notPrimaryAttr))));
		notPrimaryAttr = new ExtendedAttribute();
		notPrimaryAttr.setName("NotPrimary");
		notPrimaryAttr.setValue(AquariusRetrievalUtils.getPrimaryFilter().getFilterValue());
		assertFalse(TimeSeriesUtils.isPrimaryTimeSeries(new TimeSeriesDescription().setExtendedAttributes(Arrays.asList(notPrimaryAttr))));
	}

	@Test
	public void getMinMaxDataEmptyListTest() {
		MinMaxData minMaxData = TimeSeriesUtils.getMinMaxData(new ArrayList<TimeSeriesPoint>());
		assertNotNull(minMaxData);
		assertNotNull(minMaxData.getMin());
		assertTrue(minMaxData.getMin().isEmpty());
		assertNotNull(minMaxData.getMax());
		assertTrue(minMaxData.getMax().isEmpty());
	}

	@Test
	public void getMinMaxDataDvTest() {
		boolean endOfPeriod = true;
		ZoneOffset zoneOffset = ZoneOffset.of("-6");
		MinMaxData minMaxData = TimeSeriesUtils.getMinMaxData(getTimeSeriesPoints(endOfPeriod, zoneOffset));
		assertNotNull(minMaxData);
		assertNotNull(minMaxData.getMin());
		assertEquals(3, minMaxData.getMin().size());
		assertThat(minMaxData.getMin(),
				contains(samePropertyValuesAs(getMinMaxPoint5(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint4(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint2(endOfPeriod, zoneOffset))));
		assertNotNull(minMaxData.getMax());
		assertEquals(1, minMaxData.getMax().size());
		assertThat(minMaxData.getMax(), contains(samePropertyValuesAs(getMinMaxPoint3(endOfPeriod, zoneOffset))));
	}

	@Test
	public void getMinMaxDataTsTest() {
		boolean endOfPeriod = false;
		ZoneOffset zoneOffset = ZoneOffset.UTC;
		MinMaxData minMaxData = TimeSeriesUtils.getMinMaxData(getTimeSeriesPoints(endOfPeriod, zoneOffset));
		assertNotNull(minMaxData);
		assertNotNull(minMaxData.getMin());
		assertEquals(3, minMaxData.getMin().size());
		assertThat(minMaxData.getMin(),
				contains(samePropertyValuesAs(getMinMaxPoint5(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint4(endOfPeriod, zoneOffset)),
						samePropertyValuesAs(getMinMaxPoint2(endOfPeriod, zoneOffset))));
		assertNotNull(minMaxData.getMax());
		assertEquals(1, minMaxData.getMax().size());
		assertThat(minMaxData.getMax(), contains(samePropertyValuesAs(getMinMaxPoint3(endOfPeriod, zoneOffset))));
	}

	protected Instant getTestInstant(boolean endOfPeriod, ZoneOffset zoneOffset, long days) {
		if (endOfPeriod) {
			//In the world of Aquarius, Daily Values are at 24:00 of the day of measurement, which is actually
			//00:00 of the next day in (most) all other realities.
			//For testing, this means we need to back up one day from what would be expected.
			return nowLocalDate.atTime(0, 0, 0).toInstant(zoneOffset).minus(Duration.ofDays(days-1));
		} else {
			return nowInstant.minus(Duration.ofDays(days));
		}
	}
	protected ArrayList<TimeSeriesPoint> getTimeSeriesPoints(boolean endOfPeriod, ZoneOffset zoneOffset) {
		ArrayList<TimeSeriesPoint> timeSeriesPoints = Stream
				.of(getTsPoint1(endOfPeriod, zoneOffset),
					getTsPoint2(endOfPeriod, zoneOffset),
					getTsPoint3(endOfPeriod, zoneOffset),
					getTsPoint4(endOfPeriod, zoneOffset),
					getTsPoint5(endOfPeriod, zoneOffset),
					getTsPoint6(endOfPeriod, zoneOffset))
				.collect(Collectors.toCollection(ArrayList::new));
		return timeSeriesPoints;
	}
	protected TimeSeriesPoint getTsPoint1(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("654.321").setNumeric(Double.valueOf("123.456")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 6))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint2(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("321.987").setNumeric(Double.valueOf("789.123")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 2))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint3(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("987.654").setNumeric(Double.valueOf("456.789")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 0))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint4(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("321.987").setNumeric(Double.valueOf("789.123")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 4))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint5(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("321.987").setNumeric(Double.valueOf("789.123")))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 5))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected TimeSeriesPoint getTsPoint6(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new TimeSeriesPoint()
				.setValue(new DoubleWithDisplay().setDisplay("EMPTY").setNumeric(null))
				.setTimestamp(new StatisticalDateTimeOffset().setDateTimeOffset(getTestInstant(endOfPeriod, zoneOffset, 12))
						.setRepresentsEndOfTimePeriod(endOfPeriod));
	}
	protected MinMaxPoint getMinMaxPoint1(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 6), new BigDecimal("654.321"));
	}
	protected MinMaxPoint getMinMaxPoint2(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 2), new BigDecimal("321.987"));
	}
	protected MinMaxPoint getMinMaxPoint3(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 0), new BigDecimal("987.654"));
	}
	protected MinMaxPoint getMinMaxPoint4(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 4), new BigDecimal("321.987"));
	}
	protected MinMaxPoint getMinMaxPoint5(boolean endOfPeriod, ZoneOffset zoneOffset) {
		return new MinMaxPoint(getTestInstant(endOfPeriod, zoneOffset, 5), new BigDecimal("321.987"));
	}
}
