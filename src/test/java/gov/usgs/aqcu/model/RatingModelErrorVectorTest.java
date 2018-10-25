package gov.usgs.aqcu.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class RatingModelErrorVectorTest {

	@Test
	public void constructorTest() {
		BigDecimal A = BigDecimal.valueOf(1.0D);
		BigDecimal B = BigDecimal.valueOf(2.0D);
        BigDecimal C = BigDecimal.valueOf(3.0D);
        RatingModelErrorVector result = new RatingModelErrorVector(A,B,C);
        assertEquals(result.getMaxErrorValue(), A);
        assertEquals(result.getValue(), B);
        assertEquals(result.getMinErrorValue(), C);
	}

	@Test
	public void getAsListTest() {
		BigDecimal A = BigDecimal.valueOf(1.0D);
		BigDecimal B = BigDecimal.valueOf(2.0D);
        BigDecimal C = BigDecimal.valueOf(3.0D);
        RatingModelErrorVector result = new RatingModelErrorVector(A,B,C);
        assertEquals(result.getAsList().get(0), result.getMaxErrorValue()); 
        assertEquals(result.getAsList().get(1), result.getValue()); 
        assertEquals(result.getAsList().get(2), result.getMinErrorValue()); 
	}
}
