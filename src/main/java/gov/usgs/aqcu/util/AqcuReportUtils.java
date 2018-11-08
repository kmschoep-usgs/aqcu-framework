package gov.usgs.aqcu.util;

public abstract class AqcuReportUtils {
	public static String getSimsUrl(String stationId, String simsUrl) {
		String url = null;
		if (simsUrl != null && stationId != null) {
			url = simsUrl + "?site_no=" + stationId;
		}
		return url;
	}
}