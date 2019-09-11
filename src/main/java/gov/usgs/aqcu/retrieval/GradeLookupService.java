package gov.usgs.aqcu.retrieval;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Grade;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeMetadata;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class GradeLookupService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public GradeLookupService(
		AquariusRetrievalService aquariusRetrievalService
	) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	@LogExecutionTime
	public Map<String, GradeMetadata> getByGradeList(List<Grade> includeGrades) {
		List<GradeMetadata> gradeList = new ArrayList<>();
		List<String> gradeIdentifiers = buildIdentifierList(includeGrades);
		GradeListServiceRequest request = new GradeListServiceRequest();
		GradeListServiceResponse gradeListResponse = aquariusRetrievalService.executePublishApiRequest(request);
		gradeList = gradeListResponse.getGrades();
		return filterList(gradeIdentifiers, gradeList);
	}

	protected List<String> buildIdentifierList(List<Grade> includeGrades) {
		return includeGrades.stream()
				.map(x -> x.getGradeCode())
				.collect(Collectors.toList());
	}

	protected Map<String, GradeMetadata> filterList(List<String> includeIdentifiers, List<GradeMetadata> gradeList) {
		Map<String, GradeMetadata> filtered = gradeList.stream()
				.filter(x -> includeIdentifiers.contains(x.getIdentifier()))
				.collect(Collectors.toMap(x -> x.getIdentifier(), x -> x));

		return filtered;
	}
}