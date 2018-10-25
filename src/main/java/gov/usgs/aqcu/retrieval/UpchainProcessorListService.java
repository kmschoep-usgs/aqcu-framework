package gov.usgs.aqcu.retrieval;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UpchainProcessorListByTimeSeriesServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ProcessorListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;

@Repository
public class UpchainProcessorListService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public UpchainProcessorListService(
		AquariusRetrievalService aquariusRetrievalService
	) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}
	
	public ProcessorListServiceResponse getRawResponse(String primaryTimeseriesIdentifier, Instant startDate, Instant endDate) {
				UpchainProcessorListByTimeSeriesServiceRequest request = new UpchainProcessorListByTimeSeriesServiceRequest()
				.setTimeSeriesUniqueId(primaryTimeseriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate);
		ProcessorListServiceResponse processorsResponse = aquariusRetrievalService.executePublishApiRequest(request);
		return processorsResponse;
	}

	public List<String> getInputTimeSeriesUniqueIdList(List<Processor> processors) {
		Set<String> uniqueIds = new HashSet<>();

		for(Processor proc : processors) {
			if(proc != null && proc.getInputTimeSeriesUniqueIds() != null && !proc.getInputTimeSeriesUniqueIds().isEmpty()) {
				uniqueIds.addAll(proc.getInputTimeSeriesUniqueIds());
			}
		}

		return new ArrayList<>(uniqueIds);
	}

	public List<String> getRatingModelUniqueIdList(List<Processor> processors) {
		Set<String> uniqueIds = new HashSet<>();

		for(Processor proc : processors) {
			if(proc != null && proc.getInputRatingModelIdentifier() != null && !proc.getInputRatingModelIdentifier().isEmpty()) {
				uniqueIds.add(proc.getInputRatingModelIdentifier());
			}
		}

		return new ArrayList<>(uniqueIds);
	}
}
