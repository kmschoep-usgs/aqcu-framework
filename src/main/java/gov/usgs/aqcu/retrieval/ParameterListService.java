package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class ParameterListService {
	public static final String VOLUMETRIC_FLOW_UNIT_GROUP_VALUE = "Volumetric Flow";

	private static final Logger LOG = LoggerFactory.getLogger(ParameterListService.class);
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public ParameterListService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}
	
	@LogExecutionTime
	public Map<String, ParameterMetadata> getParameterMetadata() {
		List<ParameterMetadata> metadataList = new ArrayList<>();

		try {
			metadataList = get();
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch ParameterMetadata from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}

		return buildMap(metadataList);
	}

	public Boolean isVolumetricFlow(Map<String, ParameterMetadata> parameterMetadata, String parameter) {
		return parameterMetadata != null && parameterMetadata.containsKey(parameter) && VOLUMETRIC_FLOW_UNIT_GROUP_VALUE
				.equalsIgnoreCase(parameterMetadata.get(parameter).getUnitGroupIdentifier());
	}

	protected List<ParameterMetadata> get() throws Exception {
		ParameterListServiceRequest request = new ParameterListServiceRequest();
		ParameterListServiceResponse response = aquariusRetrievalService.executePublishApiRequest(request);
		return response.getParameters();
	}

	protected Map<String, ParameterMetadata> buildMap(List<ParameterMetadata> metadataList) {
		Map<String, ParameterMetadata> map = metadataList.parallelStream()
				.collect(Collectors.toMap(x -> x.getIdentifier(), x -> x));

		return map;
	}
}
