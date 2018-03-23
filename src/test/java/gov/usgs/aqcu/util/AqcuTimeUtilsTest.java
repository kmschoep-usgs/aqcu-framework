package gov.usgs.aqcu.util;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.PeriodOfApplicability;

import org.junit.Before;
import org.junit.Test;

public class AqcuTimeUtilsTest {
	Instant start1 = Instant.parse("2017-01-01T00:00:00Z");
	Instant end1 = Instant.parse("2017-03-01T00:00:00Z");
	Instant start2 = Instant.parse("2017-01-01T00:00:00Z");
	Instant end2 = Instant.parse("2017-02-01T00:00:00Z");
	Instant start3 = Instant.parse("2017-02-01T00:00:00Z");
	Instant end3 = Instant.parse("2017-03-01T00:00:00Z");
	Instant start4 = Instant.parse("2017-02-01T10:00:00Z");
	Instant end4 = Instant.parse("2017-02-01T20:00:00Z");
	Instant start5 = Instant.parse("2016-12-31T00:00:00Z");
	Instant end5 = Instant.parse("2017-04-01T00:00:00Z");
	Instant open1 = Instant.parse("0001-01-01T00:00:00Z");
	Instant open2 = Instant.parse("9999-12-31T23:59:59.9999999Z");

	@Before
	public void setup() {
		
	}

	@Test
	public void doPeriodsOverlapTest() {
		PeriodOfApplicability p1 = new PeriodOfApplicability();
		p1.setStartTime(start1);
		p1.setEndTime(end1);
		PeriodOfApplicability p2 = new PeriodOfApplicability();
		p2.setStartTime(start2);
		p2.setEndTime(end2);
		PeriodOfApplicability p3 = new PeriodOfApplicability();
		p3.setStartTime(start4);
		p3.setEndTime(end4);

		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p2), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p1, p3), true);
		assertEquals(AqcuTimeUtils.doPeriodsOverlap(p2, p3), false);
	}

	@Test
	public void doesTimeRangeOverlapTest() {
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start2, end2), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start3, end3), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start4, end4), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start1, end1, start5, end5), true);
		assertEquals(AqcuTimeUtils.doesTimeRangeOverlap(start2, end2, start3, end3), true);
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
}
