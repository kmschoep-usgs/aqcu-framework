package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Correction;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.CorrectionType;

public class ExtendedCorrectionTypeTest {

	@Test
	public void extendedTypeFreehandAssignmentTest() {
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
	public void extendedTypeNullTest() {
		assertEquals(ExtendedCorrectionType.fromCorrection(null), null);
		assertEquals(ExtendedCorrectionType.fromCorrection(new Correction()), null);
	}

}
