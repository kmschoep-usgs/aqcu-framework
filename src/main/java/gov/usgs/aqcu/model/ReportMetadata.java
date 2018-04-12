package gov.usgs.aqcu.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;

public class ReportMetadata {
	private String timezone;
	private Instant startDate;
	private Instant endDate;
	private String title;
	private String stationName;
	private String stationId;
	private Map<String, QualifierMetadata> qualifierMetadata;

	public ReportMetadata() {
		qualifierMetadata = new HashMap<>();
	}

	public ReportMetadata(
		String reportTitle,
		Double utcOffset,
		String stationName,
		String stationId,
		Map<String,QualifierMetadata> qualifierMetadata) {
		setTitle(reportTitle);
		setTimezone(utcOffset);
		setStationName(stationName);
		setStationId(stationId);
		setQualifierMetadata(qualifierMetadata);
	}

	public String getTimezone() {
		return timezone;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public String getTitle() {
		return title;
	}

	public String getStationName() {
		return stationName;
	}

	public String getStationId() {
		return stationId;
	}

	public Map<String, QualifierMetadata> getQualifierMetadata() {
		return qualifierMetadata;
	}

	public void setTimezone(String val) {
		timezone = val;
	}

	public void setTimezone(Double utcOffset) {
		timezone = (utcOffset != null) ? ("Etc/GMT+" + (int)(-1 * utcOffset)) : null;
	}

	public void setStartDate(Instant val) {
		startDate = val;
	}

	public void setEndDate(Instant val) {
		endDate = val;
	}

	public void setTitle(String val) {
		title = val;
	}

	public void setStationName(String val) {
		stationName = val;
	}

	public void setStationId(String val) {
		stationId = val;
	}

	public void setQualifierMetadata(Map<String, QualifierMetadata> val) {
		qualifierMetadata = val;
	}
}
