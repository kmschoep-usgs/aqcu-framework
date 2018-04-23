package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionType;

public class ExtendedCorrectionTest {

	@Test
	public void extendedTypeAssignmentTest() {
		assertEquals(new ExtendedCorrection(new Correction()).getAqcuExtendedCorrectionType(), null);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.Offset)).getAqcuExtendedCorrectionType(), null);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.DeleteRegion)).getAqcuExtendedCorrectionType(), null);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.UsgsMultiPoint)).getAqcuExtendedCorrectionType(), null);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.CopyPaste)).getAqcuExtendedCorrectionType(), null);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("freehand")).getAqcuExtendedCorrectionType(), ExtendedCorrectionType.Freehand);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("FREEHAND")).getAqcuExtendedCorrectionType(), ExtendedCorrectionType.Freehand);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("freeHAND")).getAqcuExtendedCorrectionType(), ExtendedCorrectionType.Freehand);
		assertEquals(new ExtendedCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("text including the word freehand")).getAqcuExtendedCorrectionType(), ExtendedCorrectionType.Freehand);
	}

	@Test
	public void dominantTypeTest() {
		ExtendedCorrection corr = new ExtendedCorrection();		
		assertEquals(corr.getDominantType(), null);
		corr.setType(CorrectionType.Offset);
		assertEquals(corr.getDominantType(), CorrectionType.Offset.toString());
		corr.setAqcuExtendedCorrectionType(ExtendedCorrectionType.Freehand);
		assertEquals(corr.getDominantType(), ExtendedCorrectionType.Freehand.toString());
		corr.setAqcuExtendedCorrectionType(null);
		assertEquals(corr.getDominantType(), CorrectionType.Offset.toString());
		corr.setAqcuExtendedCorrectionType(ExtendedCorrectionType.Freehand);
		corr.setType(null);
		assertEquals(corr.getDominantType(), ExtendedCorrectionType.Freehand.toString());
		corr.setAqcuExtendedCorrectionType(null);
		assertEquals(corr.getDominantType(), null);
	}
}
