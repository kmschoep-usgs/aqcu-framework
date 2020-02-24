package gov.usgs.aqcu.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DataGapTest {

	@Test
	public void gapCalculationsTest() {
		DataGap testGap = new DataGap();
		assertEquals(testGap.getDurationInHours(), null);
		assertEquals(testGap.getGapExtent(), DataGapExtent.OVER_ALL);
		testGap.setStartTime(Instant.parse("2017-01-01T00:00:00Z"));
		assertEquals(testGap.getDurationInHours(), null);
		assertEquals(testGap.getGapExtent(), DataGapExtent.OVER_END);
		testGap.setEndTime(Instant.parse("2017-01-01T02:00:00Z"));
		assertEquals(testGap.getDurationInHours(), BigDecimal.valueOf(2.0));
		assertEquals(testGap.getGapExtent(), DataGapExtent.CONTAINED);
		testGap.setStartTime(null);
		assertEquals(testGap.getDurationInHours(), null);
		assertEquals(testGap.getGapExtent(), DataGapExtent.OVER_START);
	}
}
