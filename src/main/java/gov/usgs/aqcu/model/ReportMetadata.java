package gov.usgs.aqcu.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;

import gov.usgs.aqcu.util.AqcuTimeUtils;

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

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public void setTimezone(Double offset) {
		this.timezone = AqcuTimeUtils.getTimezone(offset);
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public void setQualifierMetadata(Map<String, QualifierMetadata> qualifierMetadata) {
		this.qualifierMetadata = qualifierMetadata;
	}
}
