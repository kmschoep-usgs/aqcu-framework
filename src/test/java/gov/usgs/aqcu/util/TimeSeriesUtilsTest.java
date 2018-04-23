package gov.usgs.aqcu.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneOffset;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;

public class TimeSeriesUtilsTest {	
	@Test
	public void getZoneOffsetNullTest() {
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(null));
		assertEquals(ZoneOffset.UTC, TimeSeriesUtils.getZoneOffset(new TimeSeriesDescription()));
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
}
