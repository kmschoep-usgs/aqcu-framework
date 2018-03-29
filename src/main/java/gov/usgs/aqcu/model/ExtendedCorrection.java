package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionType;

public class ExtendedCorrection extends Correction {	
	private ExtendedCorrectionType aqcuExtendedCorrectionType;
	private String dominantType;

	public ExtendedCorrection() {}
	
	public ExtendedCorrection(Correction source) {
		//Construct Base Correction
		super.setType(source.getType());
		super.setStartTime(source.getStartTime());
		super.setEndTime(source.getEndTime());
		super.setAppliedTimeUtc(source.getAppliedTimeUtc());
		super.setComment(source.getComment());
		super.setUser(source.getUser());
		super.setParameters(source.getParameters());
		super.setProcessingOrder(source.getProcessingOrder());
		
		//Construct Extended Correction
		setAqcuExtendedCorrectionType(ExtendedCorrectionType.fromCorrection(source));
	}

	public ExtendedCorrectionType getAqcuExtendedCorrectionType() {
		return aqcuExtendedCorrectionType;
	}

	public String getDominantType() {
		return dominantType;
	}

	@Override
	public ExtendedCorrection setType(CorrectionType val) {
		super.setType(val);
		setDominantType();
		return this;
	}

	public void setAqcuExtendedCorrectionType(ExtendedCorrectionType val) {
		aqcuExtendedCorrectionType = val;
		setDominantType();
	}

	protected void setDominantType() {
		if(aqcuExtendedCorrectionType != null) {
			dominantType = aqcuExtendedCorrectionType.toString();
		} else if(getType() != null) {
			dominantType = getType().toString();
		} else {
			dominantType = null;
		}
	}
}
