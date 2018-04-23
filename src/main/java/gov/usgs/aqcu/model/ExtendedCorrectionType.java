package gov.usgs.aqcu.model;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionType;

public enum ExtendedCorrectionType {	
	Freehand;

	public static ExtendedCorrectionType fromCorrection(Correction correction) {
		//If the correction is not null and has a type to map to an extended type..
		if(correction!= null && correction.getType() != null) {
			//1. Map `CopyPaste` with a comment containing `freehand` to `Freehand`
			if(correction.getType().equals(CorrectionType.CopyPaste) && correction.getComment() != null
					&& correction.getComment().toLowerCase().contains(Freehand.toString().toLowerCase())) {
				return Freehand;
			}
		}

		return null;
	}
}
