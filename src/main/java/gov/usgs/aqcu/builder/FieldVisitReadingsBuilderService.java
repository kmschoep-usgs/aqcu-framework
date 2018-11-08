package gov.usgs.aqcu.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.FieldVisitDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Inspection;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionActivity;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.InspectionType;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Reading;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ReadingType;

import gov.usgs.aqcu.model.FieldVisitReading;

@Service
public class FieldVisitReadingsBuilderService {
	public static final String MON_METH_CREST_STAGE = "Crest stage";
	public static final String MON_METH_MAX_MIN_INDICATOR = "Max-min indicator";
	public static enum EmptyCsgReadings {
		NOMK("No mark"),
		NTRD("Not read"),
		OTOP("Over topped");
		
		private String readingDisplay;
		
		EmptyCsgReadings(String readingDisplay) {
			this.readingDisplay = readingDisplay;
		}
		
		public String getDisplay() {
			return readingDisplay;
		}
	}

	public List<FieldVisitReading> extractReadings(Instant visitTime, FieldVisitDataServiceResponse dataResponse, String parameter, List<String> includeInspectionTypes) {
		List<FieldVisitReading> result = new ArrayList<>();
		
		InspectionActivity activity = dataResponse.getInspectionActivity();
		
		if(activity != null && activity.getReadings() != null && !activity.getReadings().isEmpty()) {
			List<Reading> readings = activity.getReadings();
						
			// Optional filter to parameter
			if(parameter != null && !parameter.isEmpty()) {
				readings = filterToParameter(readings, parameter);
			}

			List<Inspection> inspections = activity.getInspections();

			//TODO - see AQCU-265, this is currently being left out of rendered reports until Aquarius adds the information
			String visitStatus = "TODO"; 
			
			Map<String,List<String>> inspectionCommentsBySerial = serialNumberToComment(filterInspections(inspections, includeInspectionTypes));

			for(Reading read : readings) {
				List<String> comments = new ArrayList<>();
				
				//comments attached to the inspection activity, linked by the reading's serial number 
				List<String> inspectionComments = inspectionCommentsBySerial.get(read.getSerialNumber());
				if(inspectionComments != null && !inspectionComments.isEmpty()) {
					comments.addAll(inspectionComments);
				}
				
				//comments already attached to the reading;
				String readingComments = read.getComments();
				if(readingComments != null){
					comments.add(readingComments);
				}
				
				result.add(new FieldVisitReading(visitTime, activity.getParty(), visitStatus, comments, read));
			}
			
			//add possible empty inspections (only if not filtering by parameter)
			if(parameter == null || parameter.isEmpty()) {
				result.addAll(extractEmptyCrestStageReadings(visitTime, inspections, activity.getParty()));
				result.addAll(extractEmptyMaxMinIndicatorReadings(visitTime, inspections, activity.getParty()));
				result.addAll(extractEmptyHighWaterMarkReadings(visitTime, inspections, activity.getParty()));
			}
		}

		return result;
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

	/**
	 * Returns comments stored in inspections linked by serial number. These comments are known as 
	 * "reference reading comments".
	 */
	protected Map<String, List<String>> serialNumberToComment(List<Inspection> inspections) {
		Map<String, List<String>> toRet = new HashMap<>();

		if(inspections != null && !inspections.isEmpty()) {
			for(Inspection inspection: inspections){
				if(StringUtils.isNotBlank(inspection.getComments())) {
					List<String> previous = toRet.get(inspection.getSerialNumber());
					if(previous == null || previous.isEmpty()){
						previous = new ArrayList<>();
						toRet.put(inspection.getSerialNumber(), previous);
					}
					previous.add(inspection.getComments());
				}
			}
		}

		return toRet;
	}

	protected List<Inspection> filterInspections(List<Inspection> inspections, List<String> allowedTypes) {
		if(inspections != null && !inspections.isEmpty() && allowedTypes != null && !allowedTypes.isEmpty()) {
			List<Inspection> filteredInspections = new ArrayList<>();
			for(Inspection inspection : inspections){
				if(allowedTypes.contains(inspection.getInspectionType().name())){
					filteredInspections.add(inspection);
				}
			}
			return filteredInspections;
		} else {
			return inspections;
		}
	}
	
	protected List<FieldVisitReading> extractEmptyCrestStageReadings(Instant visitTime, List<Inspection> inspections, String party) {
		List<FieldVisitReading> readings = new ArrayList<>();
		if(inspections != null && !inspections.isEmpty()) {
			for(Inspection ins : inspections) {
				if (ins.getInspectionType().equals(InspectionType.CrestStageGage)) {
					for(EmptyCsgReadings csg: EmptyCsgReadings.values()) {
						if(ins.getComments() != null && (ins.getComments().contains(csg.name()) || ins.getComments().contains(csg.readingDisplay))) {
							readings.add(new FieldVisitReading (
									visitTime, party, "TODO", 
									new ArrayList<>(Arrays.asList(ins.getComments())), null, MON_METH_CREST_STAGE,
									null, null, ins.getSubLocationIdentifier(), ReadingType.ExtremeMax
								)
							);
						}
					}
				}
			}
		}
		
		return readings;
	}
	
	protected List<FieldVisitReading> extractEmptyMaxMinIndicatorReadings(Instant visitTime, List<Inspection> inspections, String party) {
		List<FieldVisitReading> readings = new ArrayList<>();
		if(inspections != null && !inspections.isEmpty()) {
			for(Inspection ins : inspections) {
				if (ins.getInspectionType().equals(InspectionType.MaximumMinimumGage)) {
					if(StringUtils.isNotBlank(ins.getComments())){
						readings.add(new FieldVisitReading (
								visitTime, party, "TODO", 
								new ArrayList<>(Arrays.asList(ins.getComments())), null, MON_METH_MAX_MIN_INDICATOR,
								null, null, ins.getSubLocationIdentifier(), ReadingType.ExtremeMax
							)
						);
					}
				} 
			}
		}
		return readings;
	}
	
	protected List<FieldVisitReading> extractEmptyHighWaterMarkReadings(Instant visitTime, List<Inspection> inspections, String party) {
		//TODO when Aquarius adds HWM method
		List<FieldVisitReading> readings = new ArrayList<>();
		return readings;
	}
}
