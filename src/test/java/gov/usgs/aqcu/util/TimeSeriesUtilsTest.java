package gov.usgs.aqcu.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.ZoneOffset;
import java.util.Arrays;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttribute;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;

public class TimeSeriesUtilsTest {	
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
}
