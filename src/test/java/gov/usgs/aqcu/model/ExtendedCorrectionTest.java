package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionType;

import org.junit.Before;
import org.junit.Test;

public class ExtendedCorrectionTest {
	@Before
	public void setup() {
	}

	@Test
	public void extendedTypeAssignmentTest() {
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.Offset)), null);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.DeleteRegion)), null);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.UsgsMultiPoint)), null);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.CopyPaste)), null);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("freehand")), ExtendedCorrectionType.Freehand);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("FREEHAND")), ExtendedCorrectionType.Freehand);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("freeHAND")), ExtendedCorrectionType.Freehand);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction().setType(CorrectionType.CopyPaste).setComment("text including the word freehand")), ExtendedCorrectionType.Freehand);
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
