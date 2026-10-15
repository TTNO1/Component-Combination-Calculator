package me.ttno1.componentcalculator.bigdecimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A {@link BigDecimal} whose {@code equals()} method is consistent with its
 * {@code compareTo()} method, favoring {@code compareTo()}, thus allowing usage
 * in sorted collections.<br>
 * Behaves identically to {@link BigDecimal} except that
 * {@link SBigDecimal#equals(Object)} will behave as described below.
 */
public class SBigDecimal extends BigDecimal {

	private static final long serialVersionUID = 1L;

	public static final SBigDecimal NEGATIVE_ONE = new SBigDecimal(-1);
	public static final SBigDecimal ZERO = new SBigDecimal(BigDecimal.ZERO);
	public static final SBigDecimal ONE = new SBigDecimal(BigDecimal.ONE);
	public static final SBigDecimal TWO = new SBigDecimal(2);
	public static final SBigDecimal TEN = new SBigDecimal(BigDecimal.TEN);
	
	public SBigDecimal(BigInteger unscaledVal, int scale, MathContext mc) {
		super(unscaledVal, scale, mc);
	}

	public SBigDecimal(BigInteger unscaledVal, int scale) {
		super(unscaledVal, scale);
	}

	public SBigDecimal(BigInteger val, MathContext mc) {
		super(val, mc);
	}

	public SBigDecimal(BigInteger val) {
		super(val);
	}

	public SBigDecimal(char[] in, int offset, int len, MathContext mc) {
		super(in, offset, len, mc);
	}

	public SBigDecimal(char[] in, int offset, int len) {
		super(in, offset, len);
	}

	public SBigDecimal(char[] in, MathContext mc) {
		super(in, mc);
	}

	public SBigDecimal(char[] in) {
		super(in);
	}

	public SBigDecimal(double val, MathContext mc) {
		super(val, mc);
	}

	public SBigDecimal(double val) {
		super(val);
	}

	public SBigDecimal(int val, MathContext mc) {
		super(val, mc);
	}

	public SBigDecimal(int val) {
		super(val);
	}

	public SBigDecimal(long val, MathContext mc) {
		super(val, mc);
	}

	public SBigDecimal(long val) {
		super(val);
	}

	public SBigDecimal(String val, MathContext mc) {
		super(val, mc);
	}

	public SBigDecimal(String val) {
		super(val);
	}

	public SBigDecimal(BigDecimal val) {
		super(val.unscaledValue(), val.scale());
	}
	
	/**
	 * If {@code obj} is an {@link SBigDecimal}, returns true if the value of
	 * {@code this} and {@code obj} are mathematically equal.<br>
	 * If {@code obj} is a {@link BigDecimal}, returns the equivalent of
	 * {@code obj.equals(this)}.
	 * 
	 * @return as specified above
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SBigDecimal sbdobj && compareTo(sbdobj) == 0) {
			return true;
		}
		return super.equals(obj);
	}
	
	@Override
	public SBigDecimal add(BigDecimal augend) {
		return new SBigDecimal(super.add(augend));
	}

	@Override
	public SBigDecimal add(BigDecimal augend, MathContext mc) {
		return new SBigDecimal(super.add(augend, mc));
	}

	@Override
	public SBigDecimal subtract(BigDecimal subtrahend) {
		return new SBigDecimal(super.subtract(subtrahend));
	}

	@Override
	public SBigDecimal subtract(BigDecimal subtrahend, MathContext mc) {
		return new SBigDecimal(super.subtract(subtrahend, mc));
	}

	@Override
	public SBigDecimal multiply(BigDecimal multiplicand) {
		return new SBigDecimal(super.multiply(multiplicand));
	}

	@Override
	public SBigDecimal multiply(BigDecimal multiplicand, MathContext mc) {
		return new SBigDecimal(super.multiply(multiplicand, mc));
	}

	@Deprecated
	@Override
	public SBigDecimal divide(BigDecimal divisor, int scale, int roundingMode) {
		return new SBigDecimal(super.divide(divisor, scale, roundingMode));
	}

	@Override
	public SBigDecimal divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
		return new SBigDecimal(super.divide(divisor, scale, roundingMode));
	}

	@Deprecated
	@Override
	public SBigDecimal divide(BigDecimal divisor, int roundingMode) {
		return new SBigDecimal(super.divide(divisor, roundingMode));
	}

	@Override
	public SBigDecimal divide(BigDecimal divisor, RoundingMode roundingMode) {
		return new SBigDecimal(super.divide(divisor, roundingMode));
	}

	@Override
	public SBigDecimal divide(BigDecimal divisor) {
		return new SBigDecimal(super.divide(divisor));
	}

	@Override
	public SBigDecimal divide(BigDecimal divisor, MathContext mc) {
		return new SBigDecimal(super.divide(divisor, mc));
	}

	@Override
	public SBigDecimal divideToIntegralValue(BigDecimal divisor) {
		return new SBigDecimal(super.divideToIntegralValue(divisor));
	}

	@Override
	public SBigDecimal divideToIntegralValue(BigDecimal divisor, MathContext mc) {
		return new SBigDecimal(super.divideToIntegralValue(divisor, mc));
	}

	@Override
	public SBigDecimal remainder(BigDecimal divisor) {
		return new SBigDecimal(super.remainder(divisor));
	}

	@Override
	public SBigDecimal remainder(BigDecimal divisor, MathContext mc) {
		return new SBigDecimal(super.remainder(divisor, mc));
	}

	private SBigDecimal[] wrapArray(BigDecimal[] array) {
		SBigDecimal[] result = new SBigDecimal[array.length];
		for(int i = 0; i < array.length; i++) {
			result[i] = new SBigDecimal(array[i]);
		}
		return result;
	}
	
	@Override
	public SBigDecimal[] divideAndRemainder(BigDecimal divisor) {
		return wrapArray(super.divideAndRemainder(divisor));
	}

	@Override
	public SBigDecimal[] divideAndRemainder(BigDecimal divisor, MathContext mc) {
		return wrapArray(super.divideAndRemainder(divisor, mc));
	}

	@Override
	public SBigDecimal sqrt(MathContext mc) {
		return new SBigDecimal(super.sqrt(mc));
	}

	@Override
	public SBigDecimal pow(int n) {
		return new SBigDecimal(super.pow(n));
	}

	@Override
	public SBigDecimal pow(int n, MathContext mc) {
		return new SBigDecimal(super.pow(n, mc));
	}

	@Override
	public SBigDecimal abs() {
		return new SBigDecimal(super.abs());
	}

	@Override
	public SBigDecimal abs(MathContext mc) {
		return new SBigDecimal(super.abs(mc));
	}

	@Override
	public SBigDecimal negate() {
		return new SBigDecimal(super.negate());
	}

	@Override
	public SBigDecimal negate(MathContext mc) {
		return new SBigDecimal(super.negate(mc));
	}

	@Override
	public SBigDecimal plus() {
		return new SBigDecimal(super.plus());
	}

	@Override
	public SBigDecimal plus(MathContext mc) {
		return new SBigDecimal(super.plus(mc));
	}

	@Override
	public int signum() {
		return super.signum();
	}

	@Override
	public int scale() {
		return super.scale();
	}

	@Override
	public int precision() {
		return super.precision();
	}

	@Override
	public BigInteger unscaledValue() {
		return super.unscaledValue();
	}

	@Override
	public SBigDecimal round(MathContext mc) {
		return new SBigDecimal(super.round(mc));
	}

	@Override
	public SBigDecimal setScale(int newScale, RoundingMode roundingMode) {
		return new SBigDecimal(super.setScale(newScale, roundingMode));
	}

	@Deprecated
	@Override
	public SBigDecimal setScale(int newScale, int roundingMode) {
		return new SBigDecimal(super.setScale(newScale, roundingMode));
	}

	@Override
	public SBigDecimal setScale(int newScale) {
		return new SBigDecimal(super.setScale(newScale));
	}

	@Override
	public SBigDecimal movePointLeft(int n) {
		return new SBigDecimal(super.movePointLeft(n));
	}

	@Override
	public SBigDecimal movePointRight(int n) {
		return new SBigDecimal(super.movePointRight(n));
	}

	@Override
	public SBigDecimal scaleByPowerOfTen(int n) {
		return new SBigDecimal(super.scaleByPowerOfTen(n));
	}

	@Override
	public SBigDecimal stripTrailingZeros() {
		return new SBigDecimal(super.stripTrailingZeros());
	}

	@Override
	public int compareTo(BigDecimal val) {
		return super.compareTo(val);
	}

	@Override
	public SBigDecimal min(BigDecimal val) {
		return new SBigDecimal(super.min(val));
	}

	@Override
	public SBigDecimal max(BigDecimal val) {
		return new SBigDecimal(super.max(val));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String toEngineeringString() {
		return super.toEngineeringString();
	}

	@Override
	public String toPlainString() {
		return super.toPlainString();
	}

	@Override
	public BigInteger toBigInteger() {
		return super.toBigInteger();
	}

	@Override
	public BigInteger toBigIntegerExact() {
		return super.toBigIntegerExact();
	}

	@Override
	public long longValue() {
		return super.longValue();
	}

	@Override
	public long longValueExact() {
		return super.longValueExact();
	}

	@Override
	public int intValue() {
		return super.intValue();
	}

	@Override
	public int intValueExact() {
		return super.intValueExact();
	}

	@Override
	public short shortValueExact() {
		return super.shortValueExact();
	}

	@Override
	public byte byteValueExact() {
		return super.byteValueExact();
	}

	@Override
	public float floatValue() {
		return super.floatValue();
	}

	@Override
	public double doubleValue() {
		return super.doubleValue();
	}

	@Override
	public SBigDecimal ulp() {
		return new SBigDecimal(super.ulp());
	}

}
