package me.ttno1.componentcalculator.javadouble;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

public final class ValueSets {

	private ValueSets() {}
	
	private static final double[] E24_VALUES = new double[] {
				1.0, 1.1, 1.2, 1.3, 1.5, 1.6, 1.8, 2.0, 2.2, 2.4, 2.7,3.0,
				3.3, 3.6, 3.9, 4.3, 4.7, 5.1, 5.6, 6.2, 6.8, 7.5, 8.2, 9.1
			};
	
	private static final NavigableSet<Double> E3_SET = new TreeSet<>();
	private static final NavigableSet<Double> E6_SET = new TreeSet<>();
	private static final NavigableSet<Double> E12_SET = new TreeSet<>();
	private static final NavigableSet<Double> E24_SET = new TreeSet<>();
	private static final NavigableSet<Double> E48_SET = new TreeSet<>();
	private static final NavigableSet<Double> E96_SET = new TreeSet<>();
	private static final NavigableSet<Double> E192_SET = new TreeSet<>();
	private static final NavigableSet<Double> E192_PLUS_E24_SET = new TreeSet<>();
	private static final NavigableSet<Double> E96_PLUS_E24_SET = new TreeSet<>();
	
	public static final NavigableSet<Double> E3 = Collections.unmodifiableNavigableSet(E3_SET);
	public static final NavigableSet<Double> E6 = Collections.unmodifiableNavigableSet(E6_SET);
	public static final NavigableSet<Double> E12 = Collections.unmodifiableNavigableSet(E12_SET);
	public static final NavigableSet<Double> E24 = Collections.unmodifiableNavigableSet(E24_SET);
	public static final NavigableSet<Double> E48 = Collections.unmodifiableNavigableSet(E48_SET);
	public static final NavigableSet<Double> E96 = Collections.unmodifiableNavigableSet(E96_SET);
	public static final NavigableSet<Double> E192 = Collections.unmodifiableNavigableSet(E192_SET);
	public static final NavigableSet<Double> E192_PLUS_E24 = Collections.unmodifiableNavigableSet(E192_PLUS_E24_SET);
	public static final NavigableSet<Double> E96_PLUS_E24 = Collections.unmodifiableNavigableSet(E96_PLUS_E24_SET);
	
	static {
		
		for(int i = 0; i < E24_VALUES.length; i++) {
			double num = E24_VALUES[i];
			
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
			
			double num = Calcs.round(Math.pow(Calcs.powTen(i), 1.0/192), 2);
			
			if(i == 185) {
				// See https://en.wikipedia.org/wiki/E_series_of_preferred_numbers#E192_subsets
				num = 9.20;
			}
			
			E192_SET.add(num);
			if(i % 2 == 0) {
				E96_SET.add(num);
			}
			if(i % 4 == 0) {
				E48_SET.add(num);
			}
			
		}
		
		E192_PLUS_E24_SET.addAll(E192_SET);
		E192_PLUS_E24_SET.addAll(E24_SET);
		
		E96_PLUS_E24_SET.addAll(E96_SET);
		E96_PLUS_E24_SET.addAll(E24_SET);
		
	}
	
}
