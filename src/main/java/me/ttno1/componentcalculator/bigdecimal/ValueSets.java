package me.ttno1.componentcalculator.bigdecimal;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import ch.obermuhlner.math.big.BigDecimalMath;

public final class ValueSets {

	private ValueSets() {}
	
	private static final MathContext PRECISION_3 = new MathContext(3, RoundingMode.HALF_UP);
	private static final MathContext INVERSE_MC = new MathContext(Calcs.MC.getPrecision() + 10);
	
	private static final SBigDecimal SBIG_DECIMAL_192 = new SBigDecimal(192);
	
	private static final String[] E24_VALUES = new String[] {
				"1.0", "1.1", "1.2", "1.3", "1.5", "1.6", "1.8", "2.0", "2.2", "2.4", "2.7","3.0",
				"3.3", "3.6", "3.9", "4.3", "4.7", "5.1", "5.6", "6.2", "6.8", "7.5", "8.2", "9.1"
			};
	
	private static final NavigableSet<SBigDecimal> E3_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E6_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E12_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E24_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E48_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E96_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E192_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E192_PLUS_E24_SET = new TreeSet<>();
	private static final NavigableSet<SBigDecimal> E96_PLUS_E24_SET = new TreeSet<>();
	
	public static final NavigableSet<SBigDecimal> E3 = Collections.unmodifiableNavigableSet(E3_SET);
	public static final NavigableSet<SBigDecimal> E6 = Collections.unmodifiableNavigableSet(E6_SET);
	public static final NavigableSet<SBigDecimal> E12 = Collections.unmodifiableNavigableSet(E12_SET);
	public static final NavigableSet<SBigDecimal> E24 = Collections.unmodifiableNavigableSet(E24_SET);
	public static final NavigableSet<SBigDecimal> E48 = Collections.unmodifiableNavigableSet(E48_SET);
	public static final NavigableSet<SBigDecimal> E96 = Collections.unmodifiableNavigableSet(E96_SET);
	public static final NavigableSet<SBigDecimal> E192 = Collections.unmodifiableNavigableSet(E192_SET);
	public static final NavigableSet<SBigDecimal> E96_PLUS_E24 = Collections.unmodifiableNavigableSet(E96_PLUS_E24_SET);
	public static final NavigableSet<SBigDecimal> E192_PLUS_E24 = Collections.unmodifiableNavigableSet(E192_PLUS_E24_SET);
	
	private static final Map<NavigableSet<SBigDecimal>, NavigableSet<SBigDecimal>> inverseMap = new HashMap<>();
	
	static {
		
		for(int i = 0; i < E24_VALUES.length; i++) {
			String numStr = E24_VALUES[i];
			SBigDecimal num = new SBigDecimal(numStr);
			
			E24_SET.add(num);
			if(i % 2 == 0) {
				E12_SET.add(num);
			}
			if(i % 4 == 0) {
				E6_SET.add(num);
			}
			if(i % 8 == 0) {
				E3_SET.add(num);
			}
			
		}
		
		for(int i = 0; i < 192; i++) {
			
			SBigDecimal num = new SBigDecimal(BigDecimalMath.root(SBigDecimal.TEN.pow(i), SBIG_DECIMAL_192, PRECISION_3));
			
			if(i == 185) {
				// See https://en.wikipedia.org/wiki/E_series_of_preferred_numbers#E192_subsets
				num = new SBigDecimal("9.20");
			}
			
			E192_SET.add(num);
			if(i % 2 == 0) {
				E96_SET.add(num);
			}
			if(i % 4 == 0) {
				E48_SET.add(num);
			}
			
		}
		
		E96_PLUS_E24_SET.addAll(E96_SET);
		E96_PLUS_E24_SET.addAll(E24_SET);
		
		E192_PLUS_E24_SET.addAll(E192_SET);
		E192_PLUS_E24_SET.addAll(E24_SET);
		
		generateInverseValueSet(E3);
		generateInverseValueSet(E6);
		generateInverseValueSet(E12);
		generateInverseValueSet(E24);
		generateInverseValueSet(E48);
		generateInverseValueSet(E96);
		generateInverseValueSet(E192);
		generateInverseValueSet(E96_PLUS_E24);
		generateInverseValueSet(E192_PLUS_E24);
		
	}
	
	private static void generateInverseValueSet(NavigableSet<SBigDecimal> baseValueSet) {
		
		NavigableSet<SBigDecimal> inverseValueSet = baseValueSet.stream().map(value -> Calcs.mantissa(Calcs.reciprocal(value, INVERSE_MC))).collect(Collectors.collectingAndThen(Collectors.toCollection(TreeSet::new), Collections::unmodifiableNavigableSet));
		
		inverseMap.put(baseValueSet, inverseValueSet);
		inverseMap.put(inverseValueSet, baseValueSet);
		
	}
	
	public static NavigableSet<SBigDecimal> getInverseValueSet(NavigableSet<SBigDecimal> baseValueSet) {
		if(!inverseMap.containsKey(baseValueSet)) {
			throw new IllegalArgumentException("Invalid base value set");
		}
		
		return inverseMap.get(baseValueSet);
	}
	
	public static NavigableSet<SBigDecimal> getValueSetFromStringName(String name) {
		return switch (name) {
		case "E3":
			yield E3;
		case "E6":
			yield E6;
		case "E12":
			yield E12;
		case "E24":
			yield E24;
		case "E48":
			yield E48;
		case "E96":
			yield E96;
		case "E192":
			yield E192;
		case "E96+24":
			yield E96_PLUS_E24;
		case "E192+24":
			yield E192_PLUS_E24;
		default:
			throw new IllegalArgumentException("Invalid value set name: " + name);
		};
	}
	
}
