package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionType;

public enum ExtendedCorrectionType  {	
	Freehand;
	
	public static ExtendedCorrectionType fromCorrection(Correction correction) {
		if(correction.getType().equals(CorrectionType.CopyPaste) &&  correction.getComment() != null && correction.getComment().toLowerCase().contains(Freehand.toString().toLowerCase())) {
			return Freehand;
		}
		
		return null;
	}
}
