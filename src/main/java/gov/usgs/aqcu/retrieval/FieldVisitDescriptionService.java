package gov.usgs.aqcu.retrieval;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDescriptionListServiceResponse;

import gov.usgs.aqcu.parameter.DateRangeRequestParameters;
import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class FieldVisitDescriptionService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public FieldVisitDescriptionService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	@LogExecutionTime
	public List<FieldVisitDescription> getDescriptions(String stationId, ZoneOffset zoneOffset, DateRangeRequestParameters requestParameters) {
		List<FieldVisitDescription> descriptions = new ArrayList<>();
		FieldVisitDescriptionListServiceResponse fieldVisitResponse = get(stationId,
				requestParameters.getStartInstant(zoneOffset),
				requestParameters.getEndInstant(zoneOffset));
		descriptions = fieldVisitResponse.getFieldVisitDescriptions();
		return descriptions;
	}

	protected FieldVisitDescriptionListServiceResponse get(String stationId, Instant startDate, Instant endDate) {
		FieldVisitDescriptionListServiceRequest request = new FieldVisitDescriptionListServiceRequest()
				.setLocationIdentifier(stationId)
				.setQueryFrom(startDate)
				.setQueryTo(endDate);
		FieldVisitDescriptionListServiceResponse fieldVisitResponse  = aquariusRetrievalService.executePublishApiRequest(request);
		return fieldVisitResponse;
	}

}
