package gov.usgs.aqcu.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AqcuReportUtilsTest {
    @Test
	public void getSimsUrlNullTest() {
		assertNull(AqcuReportUtils.getSimsUrl(null, null));
		assertNull(AqcuReportUtils.getSimsUrl("stationid", null));
		assertNull(AqcuReportUtils.getSimsUrl(null, "www.test.org"));
	}

	@Test
	public void getSimsUrlTest() {
		assertEquals("www.hi.org?site_no=stationid", AqcuReportUtils.getSimsUrl("stationid", "www.hi.org"));
	}
}