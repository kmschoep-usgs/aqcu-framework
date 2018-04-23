package gov.usgs.aqcu.util;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.PeriodOfApplicability;

public class AqcuTimeUtilsTest {
	Instant start1 = Instant.parse("2017-01-01T00:00:00Z");
	Instant end1 = Instant.parse("2017-03-01T00:00:00Z");

	Instant start2 = Instant.parse("2017-01-01T00:00:00Z");
	Instant end2 = Instant.parse("2017-02-01T00:00:00Z");
	Instant start3 = Instant.parse("2017-02-01T00:00:00Z");
	Instant end3 = Instant.parse("2017-03-01T00:00:00Z");
	Instant start4 = Instant.parse("2017-02-01T10:00:00Z");
	Instant end4 = Instant.parse("2017-02-01T20:00:00Z");
	Instant start5 = Instant.parse("2017-03-01T00:00:00Z");
	Instant end5 = Instant.parse("2017-04-01T00:00:00Z");
	Instant start6 = Instant.parse("2016-12-01T00:00:00Z");
	Instant end6 = Instant.parse("2017-01-01T00:00:00Z");
	Instant start7 = Instant.parse("2016-12-31T00:00:00Z");
	Instant end7 = Instant.parse("2017-04-01T00:00:00Z");
	Instant start8 = Instant.parse("2017-01-01T00:00:00Z");
	Instant end8 = Instant.parse("2017-01-01T23:59:59Z");
	Instant open1 = Instant.parse("0001-01-01T00:00:00Z");
	Instant open2 = Instant.parse("9999-12-31T23:59:59.9999999Z");

	@Test
	public void doPeriodsOverlapTest() {
		PeriodOfApplicability p1 = new PeriodOfApplicability();
		p1.setStartTime(start1);
		p1.setEndTime(end1);
		PeriodOfApplicability p2 = new PeriodOfApplicability();
		p2.setStartTime(start2);
		p2.setEndTime(end2);
		PeriodOfApplicability p3 = new PeriodOfApplicability();
		p3.setStartTime(start3);
		p3.setEndTime(end3);
		PeriodOfApplicability p4 = new PeriodOfApplicability();
		p4.setStartTime(start4);
		p4.setEndTime(end4);
		PeriodOfApplicability p5 = new PeriodOfApplicability();
		p5.setStartTime(start5);
		p5.setEndTime(end5);
		PeriodOfApplicability p6 = new PeriodOfApplicability();
		p6.setStartTime(start6);
		p6.setEndTime(end6);
		PeriodOfApplicability p7 = new PeriodOfApplicability();
		p7.setStartTime(start7);
		p7.setEndTime(end7);
		PeriodOfApplicability p8 = new PeriodOfApplicability();
		p8.setStartTime(start8);
		p8.setEndTime(end8);
		PeriodOfApplicability p9 = new PeriodOfApplicability();
		p9.setStartTime(open1);
		p9.setEndTime(open2);

		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p2), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p3), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p4), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p5), false);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p6), false);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p7), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p8), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p9), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p2, p3), false);
	}

	@Test
	public void doesTimeRangeOverlapTest() {
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start2, end2), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start3, end3), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start4, end4), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start5, end5), false);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start2, end2, start3, end3), false);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start2, end2, start4, end4), false);
	}

	@Test
	public void isOpenEndedTimeTest() {
		assertEquals(AqcuTimeUtils.isOpenEndedTime(open1), true);
		assertEquals(AqcuTimeUtils.isOpenEndedTime(open2), true);
		assertEquals(AqcuTimeUtils.isOpenEndedTime(start1), false);
		assertEquals(AqcuTimeUtils.isOpenEndedTime(end1), false);
	}

	@Test
	public void toQueryDateTest() {
		assertEquals(AqcuTimeUtils.toQueryDate(start1).compareTo("2017-01-01"), 0);
		assertEquals(AqcuTimeUtils.toQueryDate(end1).compareTo("2017-03-01"), 0);
		assertEquals(AqcuTimeUtils.toQueryDate(open1).compareTo("0001-01-01"), 0);
		assertEquals(AqcuTimeUtils.toQueryDate(open2).compareTo("9999-12-31"), 0);
	}

	@Test
	public void getZoneOffsetNullTest() {
		assertEquals(ZoneOffset.UTC, AqcuTimeUtils.getZoneOffset(null));
	}

	@Test
	public void getZoneOffsetMinutesTest() {
		assertEquals(ZoneOffset.ofHoursMinutes(-5, -30), AqcuTimeUtils.getZoneOffset(-5.3));
	}

	@Test
	public void getZoneOffsetHoursTest() {
		assertEquals(ZoneOffset.ofHours(6), AqcuTimeUtils.getZoneOffset(6.0));
	}

	@Test
	public void getZoneOffsetEatExceptionTest() {
		assertEquals(ZoneOffset.UTC, AqcuTimeUtils.getZoneOffset(24.0));
	}
}
