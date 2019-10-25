package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ActivityType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisit;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataByLocationServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataByLocationServiceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class FieldVisitDataByLocationService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public FieldVisitDataByLocationService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}
	
	@LogExecutionTime
	public FieldVisitDataByLocationServiceResponse get(String locationIdentifier, Boolean includeInvalid, List<ActivityType> includeActivities, List<String> includeParameters, String targetDatum) {
		FieldVisitDataByLocationServiceRequest request = new FieldVisitDataByLocationServiceRequest()
			.setLocationIdentifier(locationIdentifier)
			.setIncludeInvalidActivities(includeInvalid)
			.setActivities(includeActivities != null ? new ArrayList<>(includeActivities) : null)
			.setParameters(includeParameters != null ? new ArrayList<>(includeParameters) : null)
			.setConvertToStandardReferenceDatum(targetDatum)
			.setApplyRounding(true);

		FieldVisitDataByLocationServiceResponse fieldVisitResponse  = aquariusRetrievalService.executePublishApiRequest(request);
		
		return fieldVisitResponse;
	}

	public List<FieldVisit> getWithinDateRange(String locationIdentifier, Boolean includeInvalid, List<ActivityType> includeActivities, List<String> includeParameters, String targetDatum, Instant startDate, Instant endDate) {
		return get(locationIdentifier, includeInvalid, includeActivities, includeParameters, targetDatum).getFieldVisitData().stream()
			.filter(f -> isVisitWithinTimeRange(f, startDate, endDate))
			.collect(Collectors.toList());
	}

	private Boolean isVisitWithinTimeRange(FieldVisit visit, Instant startDate, Instant endDate) {
		return visit.getEndTime().isAfter(startDate) && visit.getStartTime().isBefore(endDate);
	}
}
