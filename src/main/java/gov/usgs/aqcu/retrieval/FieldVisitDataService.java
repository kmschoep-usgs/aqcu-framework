package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.model.FieldVisitMeasurement;
import gov.usgs.aqcu.model.MeasurementGrade;
import gov.usgs.aqcu.model.RatingModelErrorVector;
import gov.usgs.aqcu.util.DoubleWithDisplayUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ControlConditionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DischargeSummary;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;

@Repository
public class FieldVisitDataService {
	private static final Logger LOG = LoggerFactory.getLogger(FieldVisitDataService.class);

	private AquariusRetrievalService aquariusRetrievalService;
	private RatingModelInputValuesService ratingModelInputValuesService;

	@Autowired
	public FieldVisitDataService(
		AquariusRetrievalService aquariusRetrievalService, 
		RatingModelInputValuesService ratingModelInputValuesService) 
	{
		this.aquariusRetrievalService = aquariusRetrievalService;
		this.ratingModelInputValuesService = ratingModelInputValuesService;
	}

	public FieldVisitDataServiceResponse get(String fieldVisitIdentifier) {
		try {
			FieldVisitDataServiceRequest request = new FieldVisitDataServiceRequest()
					.setFieldVisitIdentifier(fieldVisitIdentifier)
					.setApplyRounding(true);
			FieldVisitDataServiceResponse fieldVisitResponse  = aquariusRetrievalService.executePublishApiRequest(request);
			return fieldVisitResponse;
		} catch (Exception e) {
			String msg = "An unexpected error occurred while attempting to fetch FieldVisitDataServiceRequest from Aquarius: ";
			LOG.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	public List<Reading> extractFieldVisitReadings(FieldVisitDataServiceResponse response, String filterToParameter) {
		return filterToParameter(response.getInspectionActivity().getReadings(), filterToParameter);
	}

	public List<Reading> extractFieldVisitReadings(FieldVisitDataServiceResponse response) {
		return response.getInspectionActivity().getReadings();
	}

	public List<FieldVisitMeasurement> extractFieldVisitMeasurements(FieldVisitDataServiceResponse response, String ratingModelIdentifier) {
		List<FieldVisitMeasurement> ret = new ArrayList<>();

		if (response.getDischargeActivities() != null) {
			ret = response.getDischargeActivities().stream()
				.filter(x -> x.getDischargeSummary() != null)
				.filter(y -> y.getDischargeSummary().getDischarge() != null).map(z -> {
					return createFieldVisitMeasurement(z.getDischargeSummary(), ratingModelIdentifier);
				}).collect(Collectors.toList());
		}

		return ret;
	}

	protected String extractControlCondition(FieldVisitDataServiceResponse fieldVisitDataServiceResponse) {
		ControlConditionType controlCondition = fieldVisitDataServiceResponse.getControlConditionActivity() != null
				? fieldVisitDataServiceResponse.getControlConditionActivity().getControlCondition()
				: null;

		return controlCondition != null ? controlCondition.toString() : null;
	}

	protected List<Reading> filterToParameter(List<Reading> readings, String parameter) {
		// Filter to parameter
		if(readings != null && !readings.isEmpty() && parameter != null && !parameter.isEmpty()) {
			return readings.stream()
				.filter(r -> r.getParameter().contentEquals(parameter))
				.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	protected FieldVisitMeasurement createFieldVisitMeasurement(DischargeSummary dischargeSummary, String ratingModelIdentifier) {
		MeasurementGrade grade = MeasurementGrade.fromMeasurementGradeType(dischargeSummary.getMeasurementGrade());
		FieldVisitMeasurement fieldVisitMeasurement = new FieldVisitMeasurement();
		BigDecimal dischargeValue = DoubleWithDisplayUtil.getRoundedValue(dischargeSummary.getDischarge());
		BigDecimal meanGageHeight = DoubleWithDisplayUtil.getRoundedValue(dischargeSummary.getMeanGageHeight());
		RatingModelErrorVector dischargeError = calculateDsichargeError(grade, dischargeValue);		
		
		fieldVisitMeasurement.setDischarge(dischargeValue);
		fieldVisitMeasurement.setMeanGageHeight(meanGageHeight);
		fieldVisitMeasurement.setPublish(dischargeSummary.isPublish());
		fieldVisitMeasurement.setMeasurementStartDate(dischargeSummary.getMeasurementStartTime());
		fieldVisitMeasurement.setMeasurementNumber(dischargeSummary.getMeasurementId());
		fieldVisitMeasurement.setErrorMaxDischarge(dischargeError.getMaxErrorValue());
		fieldVisitMeasurement.setErrorMinDischarge(dischargeError.getMinErrorValue());

		if(ratingModelIdentifier != null && !ratingModelIdentifier.isEmpty()) {
			RatingModelErrorVector gageHeightError = getGageHeightError(ratingModelIdentifier, dischargeSummary.getMeasurementStartTime(), dischargeError, meanGageHeight);
			fieldVisitMeasurement.setErrorMaxShiftInFeet(gageHeightError.getMaxErrorValue());
			fieldVisitMeasurement.setShiftInFeet(gageHeightError.getValue());
			fieldVisitMeasurement.setErrorMinShiftInFeet(gageHeightError.getMinErrorValue());
		}
		
		return fieldVisitMeasurement;
	}

	protected RatingModelErrorVector calculateDsichargeError(MeasurementGrade grade, BigDecimal dischargeValue) 
	{
		BigDecimal errorAmt = dischargeValue.multiply(grade.getPercentageOfError());
		return new RatingModelErrorVector(dischargeValue.add(errorAmt), dischargeValue, dischargeValue.subtract(errorAmt));
	}

	protected RatingModelErrorVector getGageHeightError(String ratingModelIdentifier, Instant effectiveTime, RatingModelErrorVector dischargeError, BigDecimal meanGageHeight) {
		List<BigDecimal> inputValues = ratingModelInputValuesService.get(
			ratingModelIdentifier, 
			effectiveTime, 
			dischargeError.getAsList()
		);
		RatingModelErrorVector ret = new RatingModelErrorVector();

		if (inputValues.size() > 0 && inputValues.get(0) != null) {
			ret.setMaxErrorValue(inputValues.get(0).subtract(meanGageHeight));
		}
		if (inputValues.size() > 1 && inputValues.get(1) != null) {
			ret.setValue(inputValues.get(1).subtract(meanGageHeight));
		}
		if (inputValues.size() > 2 && inputValues.get(2) != null) {
			ret.setMinErrorValue(inputValues.get(2).subtract(meanGageHeight));
		}

		return ret;
	}

}
