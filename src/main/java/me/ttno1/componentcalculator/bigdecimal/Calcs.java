package me.ttno1.componentcalculator.bigdecimal;

import java.math.MathContext;

import ch.obermuhlner.math.big.BigDecimalMath;

public final class Calcs {

	private Calcs() {}
	
	public static final MathContext MC = new MathContext(96);
	public static final SBigDecimal EPSILON = new SBigDecimal("1E-64");
	
	public static final SBigDecimal ABS_MIN_VAL = SBigDecimal.ONE.movePointLeft(32);
	public static final SBigDecimal ABS_MAX_VAL = SBigDecimal.ONE.movePointRight(32);
	
	public static SBigDecimal parallel(SBigDecimal a, SBigDecimal b, MathContext mc) {
		return a.multiply(b).divide(a.add(b), mc);
	}
	
	public static SBigDecimal parallel(SBigDecimal a, SBigDecimal b) {
		return parallel(a, b, MC);
	}
	
	public static SBigDecimal parallelCompliment(SBigDecimal equivalentValue, SBigDecimal value1, MathContext mc) {
		return equivalentValue.multiply(value1).divide(value1.subtract(equivalentValue), mc);
	}
	
	public static SBigDecimal parallelCompliment(SBigDecimal equivalentValue, SBigDecimal value1) {
		return parallelCompliment(equivalentValue, value1, MC);
	}
	
	/**
	 * Determines if {@code value} is between {@code min} and {@code max} within
	 * {@linkplain #EPSILON} error.
	 * 
	 * @param value
	 * @param min
	 * @param minInclusive
	 * @param max
	 * @param maxInclusive
	 * @return
	 */
	public static boolean inRange(SBigDecimal value, SBigDecimal min, boolean minInclusive, SBigDecimal max, boolean maxInclusive) {
		boolean aboveMin;
		boolean belowMax;
		if(minInclusive) {
			aboveMin = value.add(EPSILON).compareTo(min) >= 0;
		} else {
			aboveMin = value.add(EPSILON).compareTo(min) > 0;
		}
		if(maxInclusive) {
			belowMax = value.subtract(EPSILON).compareTo(max) <= 0;
		} else {
			belowMax = value.subtract(EPSILON).compareTo(max) < 0;
		}
		
		return aboveMin && belowMax;
	}
	
	/**
	 * Determines if {@code value} is between {@code min} and {@code max}
	 * (inclusive) within {@linkplain #EPSILON} error.
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean inRange(SBigDecimal value, SBigDecimal min, SBigDecimal max) {
		return inRange(value, min, true, max, true);
	}
	
	public static SBigDecimal reciprocal(SBigDecimal value, MathContext mc) {
		return SBigDecimal.ONE.divide(value, mc);
	}
	
	public static SBigDecimal reciprocal(SBigDecimal value) {
		return reciprocal(value, MC);
	}
	
	public static SBigDecimal mantissa(SBigDecimal value) {
		return new SBigDecimal(BigDecimalMath.mantissa(value));
	}
	
	public static int exponent(SBigDecimal value) {
		return BigDecimalMath.exponent(value);
	}
	
	public static boolean equalWithinError(SBigDecimal a, SBigDecimal b, SBigDecimal error) {
		return a.subtract(b).abs().compareTo(error) <= 0;
	}
	
	public static boolean equalWithinEpsilon(SBigDecimal a, SBigDecimal b) {
		return equalWithinError(a, b, EPSILON);
	}
	
	public static int compareWithinEpsilon(SBigDecimal a, SBigDecimal b) {
		if(equalWithinEpsilon(a, b)) {
			return 0;
		}
		return a.compareTo(b);
	}
	
}
