package gov.usgs.aqcu.model;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;

import gov.usgs.aqcu.parameter.RequestParameters;

public class ReportMetadata {	
	private String requestingUser;
	private String timezone;
	private Instant startDate;
	private Instant endDate;
	private String title;
	private String reportType;
	private String primaryParameter;
	private String primaryTimeSeriesIdentifier;
	private String stationName;
	private String stationId;
	private RequestParameters requestParameters;
	private Map<String, GradeMetadata> gradeMetadata;
	private Map<String, QualifierMetadata> qualifierMetadata;
	
	public ReportMetadata() {
		gradeMetadata = new HashMap<>();
		qualifierMetadata = new HashMap<>();
	}

	public ReportMetadata(
		String reportType,
		String reportTitle,
		RequestParameters requestParameters,
		String requestingUser,
		String primaryParameter,
		Double utcOffset,
		String stationName,
		String stationId,
		Map<String,GradeMetadata> gradeMetadata,
		Map<String,QualifierMetadata> qualifierMetadata) {
		setRequestingUser(requestingUser);
		setTimezone(utcOffset);
		setRequestParameters(requestParameters);
		setStartDate(requestParameters.getStartInstant());
		setEndDate(requestParameters.getEndInstant());
		setTitle(reportTitle);
		setReportType(reportType);
		setPrimaryParameter(primaryParameter);
		setStationName(stationName);
		setStationId(stationId);
		setQualifierMetadata(qualifierMetadata);
		setGradeMetadata(gradeMetadata);
		setPrimaryTimeSeriesIdentifier(primaryTimeSeriesIdentifier);
	}

	public String getPrimaryTimeSeriesIdentifier() {
		return primaryTimeSeriesIdentifier;
	}
	
	public String getReportType() {
		return reportType;
	}

	public String getRequestingUser() {
		return requestingUser;
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
	
	public String getPrimaryParameter() {
		return primaryParameter;
	}
	
	public String getStationName() {
		return stationName;
	}
	
	public String getStationId() {
		return stationId;
	}
	
	public Map<String, GradeMetadata> getGradeMetadata() {
		return gradeMetadata;
	}
	
	public Map<String, QualifierMetadata> getQualifierMetadata() {
		return qualifierMetadata;
	}
	
	public RequestParameters getRequestParameters() {
		return requestParameters;
	}
	
	public void setPrimaryTimeSeriesIdentifier(String val) {
		primaryTimeSeriesIdentifier = val;
	}
	
	public void setReportType(String val) {
		reportType = val;
	}
	
	public void setRequestingUser(String val) {
		requestingUser = val;
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
	
	public void setPrimaryParameter(String val) {
		primaryParameter = val;
	}
	
	public void setStationName(String val) {
		stationName = val;
	}
	
	public void setStationId(String val) {
		stationId = val;
	}
	
	public void setGradeMetadata(Map<String, GradeMetadata> val) {
		gradeMetadata = val;
	}
	
	public void setQualifierMetadata(Map<String, QualifierMetadata> val) {
		qualifierMetadata = val;
	}
	
	public void setRequestParameters(RequestParameters val) {
		requestParameters = val;
	}
}
	
