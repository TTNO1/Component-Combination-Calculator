package me.ttno1.componentcalculator.javadouble;

import java.math.RoundingMode;

public final class Calcs {

	private Calcs() {}
	
	public static final double EPSILON = 1E-10D;
	
	public static double parallel(double a, double b) {
		return (a*b)/(a+b);
	}
	
	public static double parallelCompliment(double equivalentValue, double value1) {
		return (equivalentValue*value1)/(value1-equivalentValue);
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
	public static boolean inRange(double value, double min, boolean minInclusive, double max, boolean maxInclusive) {
		boolean aboveMin;
		boolean belowMax;
		if(minInclusive) {
			aboveMin = value + EPSILON >= min;
		} else {
			aboveMin = value + EPSILON > min;
		}
		if(maxInclusive) {
			belowMax = value - EPSILON <= max;
		} else {
			belowMax = value - EPSILON < max;
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
	public static boolean inRange(double value, double min, double max) {
		return inRange(value, min, true, max, true);
	}
	
	/**
	 * Rounds {@code num} to the nearest {@code decimalPlaces} digits to the right
	 * of the decimal using {@linkplain RoundingMode#HALF_UP}.
	 * 
	 * @param num
	 * @param decimalPlaces
	 * @return {@code num} rounded to the specified decimal places
	 */
	public static double round(double num, int decimalPlaces) {
		double scale = powTen(decimalPlaces);
		return Math.floor(num*scale + 0.5)/scale;
	}
	
	/**
	 * Calculates 10 to the power of {@code exponent}
	 * @param exponent
	 * @return 10^{@code exponent}
	 */
	public static double powTen(int exponent) {
		double result = 1.0;
		if(exponent < 0) {
			for(int i = 0; i > exponent; i--) {
				result /= 10;
			}
		} else {
			for(int i = 0; i < exponent; i++) {
				result *= 10;
			}
		}
		return result;
		//return Math.pow(10, exponent);
	}
	
	/**
	 * Gets the exponent of the number if it were represented in scientific notation
	 * with one digit to the left of the decimal point.<br>
	 * If {@code num} is zero, returns zero.
	 * 
	 * @param num
	 * @return the exponent of {@code num}
	 */
	public static int exponent(double num) {
		if(num == 0) {
			return 0;
		}
		/*num = Math.abs(num);
		int exponent = 0;
		while(num >= 10) {
			num /= 10;
			exponent++;
		}
		while(num < 1) {
			num *= 10;
			exponent--;
		}
		return exponent;*/
		return (int) Math.floor(Math.log10(num));
	}
	
	/**
	 * Gets the mantissa of the number if it were represented in scientific notation
	 * with one digit to the left of the decimal point.
	 * 
	 * @param num
	 * @return the mantissa
	 */
	public static double mantissa(double num) {
		return num/powTen(exponent(num));
	}
	
	public static boolean equalWithinError(double a, double b, double error) {
		return Math.abs(a - b) <= error;
	}
	
	public static boolean equalWithinEpsilon(double a, double b) {
		return equalWithinError(a, b, EPSILON);
	}
	
	public static int compareWithinEpsilon(double a, double b) {
		if(equalWithinEpsilon(a, b)) {
			return 0;
		}
		return Double.compare(a, b);
	}
	
}
