package gov.usgs.aqcu.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import gov.usgs.aqcu.model.Report;
import gov.usgs.aqcu.model.ReportMetadata;

import gov.usgs.aqcu.parameter.RequestParameters;

import gov.usgs.aqcu.retrieval.GradeLookupService;
import gov.usgs.aqcu.retrieval.QualifierLookupService;
import gov.usgs.aqcu.retrieval.LocationDescriptionListService;
import gov.usgs.aqcu.retrieval.TimeSeriesDescriptionListService;


@Component
public abstract class ReportBuilderService {	
	private static final Logger LOG = LoggerFactory.getLogger(ReportBuilderService.class);

	//Common Builder Services
	protected DataGapListBuilderService dataGapListBuilderService;
	protected ReportUrlBuilderService reportUrlBuilderService;

	//Common Lookup Services
	protected GradeLookupService gradeLookupService;
	protected QualifierLookupService qualifierLookupService;

	//Common Retrieval Services
	protected LocationDescriptionListService locationDescriptionListService;
	protected TimeSeriesDescriptionListService timeSeriesDescriptionListService;

	public ReportBuilderService(
			DataGapListBuilderService dataGapListBuilderService,
			ReportUrlBuilderService reportUrlBuilderService,
			GradeLookupService gradeLookupService, 
			QualifierLookupService qualifierLookupService,
			LocationDescriptionListService locationDescriptionListService,
			TimeSeriesDescriptionListService timeSeriesDescriptionListService) {
		this.dataGapListBuilderService = dataGapListBuilderService;
		this.reportUrlBuilderService = reportUrlBuilderService;
		this.qualifierLookupService = qualifierLookupService;
		this.gradeLookupService = gradeLookupService;
		this.locationDescriptionListService = locationDescriptionListService;
		this.timeSeriesDescriptionListService = timeSeriesDescriptionListService;
	}

	//Adds the basic report metadata we can get without doing any external calls
	protected <T extends Report> T addBasicReportMetadata(T report, RequestParameters requestParameters, String requestingUser) {
		report.setReportMetadata(new ReportMetadata(getReportType(), getReportTitle(), requestParameters, requestingUser, null, null, null, null, null, null));
		return report;
	}

	public abstract String getReportType();
	public abstract String getReportTitle();
}
	
