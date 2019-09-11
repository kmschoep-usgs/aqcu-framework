package gov.usgs.aqcu.retrieval;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class FieldVisitDataService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public FieldVisitDataService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}
	
	@LogExecutionTime
	public FieldVisitDataServiceResponse get(String fieldVisitIdentifier, Boolean includeInvalid, String discreteMeasurementActivity) {
		FieldVisitDataServiceRequest request = new FieldVisitDataServiceRequest()
				.setFieldVisitIdentifier(fieldVisitIdentifier)
				.setIncludeInvalidActivities(includeInvalid)
				.setDiscreteMeasurementActivity(discreteMeasurementActivity)
				.setApplyRounding(true);
		FieldVisitDataServiceResponse fieldVisitResponse  = aquariusRetrievalService.executePublishApiRequest(request);
		return fieldVisitResponse;
	}

	public FieldVisitDataServiceResponse get(String fieldVisitIdentifier) {
		return get(fieldVisitIdentifier, null, null);
	}
}
