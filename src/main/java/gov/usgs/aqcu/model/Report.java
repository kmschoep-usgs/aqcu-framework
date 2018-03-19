package gov.usgs.aqcu.model;

public abstract class Report {
	private ReportMetadata reportMetadata;
		
	public ReportMetadata getReportMetadata() {
		return reportMetadata;
	}
	
	public void setReportMetadata(ReportMetadata val) {
		reportMetadata = val;
	}
}
