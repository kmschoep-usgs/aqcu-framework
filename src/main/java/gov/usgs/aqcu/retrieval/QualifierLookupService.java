package gov.usgs.aqcu.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class QualifierLookupService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public QualifierLookupService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}
	
	@LogExecutionTime
	public Map<String, QualifierMetadata> getByQualifierList(List<Qualifier> includeQualifiers) {
		List<QualifierMetadata> qualifierList = new ArrayList<>();
		List<String> qualifierIdentifiers = buildIdentifierList(includeQualifiers);
		QualifierListServiceRequest request = new QualifierListServiceRequest();
		QualifierListServiceResponse qualifierListResponse = aquariusRetrievalService.executePublishApiRequest(request);
		qualifierList = qualifierListResponse.getQualifiers();
		return filterList(qualifierIdentifiers, qualifierList);
	}

	protected List<String> buildIdentifierList(List<Qualifier> includeQualifiers) {
		return includeQualifiers.stream()
				.map(x -> x.getIdentifier())
				.collect(Collectors.toList());
	}
	
	protected Map<String, QualifierMetadata> filterList(List<String> includeIdentifiers, List<QualifierMetadata> qualifierList) {
		Map<String, QualifierMetadata> filtered = qualifierList.stream()
				.filter(x -> includeIdentifiers.contains(x.getIdentifier()))
				.collect(Collectors.toMap(x -> x.getIdentifier(), x -> x));

		return filtered;
	}
}