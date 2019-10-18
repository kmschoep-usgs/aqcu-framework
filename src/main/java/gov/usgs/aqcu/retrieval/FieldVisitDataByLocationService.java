package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.List;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ActivityType;
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
	public FieldVisitDataByLocationServiceResponse get(String locationIdentifier, Boolean includeInvalid, List<ActivityType> activities) {
		FieldVisitDataByLocationServiceRequest request = new FieldVisitDataByLocationServiceRequest()
                .setLocationIdentifier(locationIdentifier)
                .setActivities(new ArrayList<>(activities))
                .setIncludeInvalidActivities(includeInvalid)
                .setApplyRounding(true);

		FieldVisitDataByLocationServiceResponse fieldVisitResponse  = aquariusRetrievalService.executePublishApiRequest(request);
		return fieldVisitResponse;
	}
}
