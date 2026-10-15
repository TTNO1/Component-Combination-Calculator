package me.ttno1.componentcalculator.javadouble;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import me.ttno1.componentcalculator.bigdecimal.SBigDecimal;

public final class MetricNumbers {

	private MetricNumbers() {}
	
	private static final Map<String, Integer> prefixMap = new HashMap<>(24, 1f);
	
	private static final NavigableMap<Integer, String> exponentMap = new TreeMap<>();
	
	static {
		prefixMap.put("Q", 30);
		prefixMap.put("R", 27);
		prefixMap.put("Y", 24);
		prefixMap.put("Z", 21);
		prefixMap.put("E", 18);
		prefixMap.put("P", 15);
		prefixMap.put("T", 12);
		prefixMap.put("G", 9);
		prefixMap.put("M", 6);
		prefixMap.put("K", 3);
		prefixMap.put("k", 3);
		//prefixMap.put("h", 2);
		//prefixMap.put("da", 1);
		prefixMap.put("", 0);
		//prefixMap.put("d", -1);
		//prefixMap.put("c", -2);
		prefixMap.put("m", -3);
		prefixMap.put("u", -6);
		prefixMap.put("μ", -6);
		prefixMap.put("n", -9);
		prefixMap.put("p", -12);
		prefixMap.put("f", -15);
		prefixMap.put("a", -18);
		prefixMap.put("z", -21);
		prefixMap.put("y", -24);
		prefixMap.put("r", -27);
		prefixMap.put("q", -30);
		
		for(Entry<String, Integer> entry : prefixMap.entrySet()) {
			exponentMap.put(entry.getValue(), entry.getKey());
		}
		exponentMap.put(3, "k");
		exponentMap.put(-6, "μ");
		
	}
	
	/**
	 * Parses a number that may have a metric prefix with no unit.
	 * @param numberString the unit-less string representation of the number with a metric prefix symbol
	 * @return the double representation of the number
	 * @throws IllegalArgumentException if number is an empty string
	 * @throws NumberFormatException if the string is not a valid number
	 */
	public static double parseMetricNumber(String numberString) {
		
		if(numberString.isEmpty()) {
			throw new IllegalArgumentException("Cannot parse empty string");
		}
		
		int strLength = numberString.length();
		
		Integer exponent = null;
		
		if(strLength >= 2) {
			exponent = prefixMap.get(numberString.substring(strLength - 2, strLength));
		}
		
		if(exponent == null) {
			exponent = prefixMap.get(numberString.substring(strLength - 1, strLength));
		} else {
			numberString = numberString.substring(0, strLength - 1);
		}
		
		if(exponent == null) {
			exponent = 0;
		} else {
			numberString = numberString.substring(0, strLength - 1);
		}
		
		return Double.parseDouble(numberString) * Calcs.powTen(exponent);
		
	}
	
	/**
	 * Converts the number into a string representation with the fewest possible
	 * digits using a metric prefix.
	 * 
	 * @param number the number to convert
	 * @return the string representation with a metric prefix
	 */
	public static String toMetricString(double number) {
		
		Integer exponent = Calcs.exponent(number);
		
		Integer metricExponent = exponentMap.floorKey(exponent);
		if (metricExponent == null) {
			metricExponent = exponentMap.firstKey();
		}
		
		return new SBigDecimal(number, new MathContext(10)).movePointLeft(metricExponent).stripTrailingZeros().toPlainString() + exponentMap.get(metricExponent);
		
	}

}
