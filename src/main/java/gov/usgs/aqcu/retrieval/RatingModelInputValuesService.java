package gov.usgs.aqcu.retrieval;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingModelInputValuesServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.RatingModelInputValuesServiceResponse;

@Repository
public class RatingModelInputValuesService {
	private AquariusRetrievalService aquariusRetrievalService;

	@Autowired
	public RatingModelInputValuesService(AquariusRetrievalService aquariusRetrievalService) {
		this.aquariusRetrievalService = aquariusRetrievalService;
	}

	public List<BigDecimal> get(String ratingModelIdentifier, Instant effectiveTime, List<BigDecimal> outputValues) {
        ArrayList<Double> outputValueDoubles = outputValues.stream()
            .map(v -> v.doubleValue())
            .collect(Collectors.toCollection(ArrayList::new));
        RatingModelInputValuesServiceRequest request = new RatingModelInputValuesServiceRequest()
            .setRatingModelIdentifier(ratingModelIdentifier)
            .setEffectiveTime(effectiveTime)
            .setOutputValues(outputValueDoubles);
		RatingModelInputValuesServiceResponse response = aquariusRetrievalService.executePublishApiRequest(request);
        return response.getInputValues().stream()
            .map(v -> BigDecimal.valueOf(v))
            .collect(Collectors.toList());
	}
}
