package me.ttno1.componentcalculator.javadouble;

import java.util.Collections;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@Deprecated
public class Calculator {
	
	private final NavigableSet<Double> baseValueSet;
	
	private final NavigableSet<Double> inverseValueSet;
	
	public Calculator(NavigableSet<Double> valueSet) {
		
		baseValueSet = valueSet;
		
		inverseValueSet = valueSet.stream().map(num -> Calcs.mantissa(1/num)).collect(Collectors.collectingAndThen(Collectors.toCollection(TreeSet::new), Collections::unmodifiableNavigableSet));
		
		/*double val = 11.25E3;
		double inv = 1/val;
		ComponentSet higher = getHigherAdditiveCombination(val, 3, baseValueSet);
		ComponentSet invHigher = getHigherAdditiveCombination(inv, 3, inverseValueSet);
		
		System.out.println(MetricNumbers.toMetricString(val));
		System.out.println(higher.toExpressionString());
		System.out.println(MetricNumbers.toMetricString(higher.equivalentValue()));
		System.out.println();
		System.out.println(MetricNumbers.toMetricString(inv));
		System.out.println(invHigher.toExpressionString());
		System.out.println(MetricNumbers.toMetricString(invHigher.equivalentValue()));
		System.out.println();
		System.out.println(MetricNumbers.toMetricString(1/inv));
		System.out.println();
		System.out.println(MetricNumbers.toMetricString(1/invHigher.equivalentValue()));
		System.out.println();
		System.out.println(MetricNumbers.toMetricString(val - 1/invHigher.equivalentValue()));
		System.exit(0);*/
		
	}
	
	/**
	 * Returns the additive combination with the smallest equivalent value that is
	 * greater than {@code equivalentValue} and that does not include
	 * {@code equivalentValue}
	 * 
	 * @param equivalentValue
	 * @param maxComponents
	 * @param valueSet
	 * @return
	 * @throws IllegalArgumentException if maxComponents is less than 1
	 */
	private ComponentSet getHigherAdditiveCombination(double equivalentValue, int maxComponents, NavigableSet<Double> valueSet) {
		
		if(maxComponents <= 0) {
			throw new IllegalArgumentException("Cannot find higher additive combination for max components <= 0");
		}
		
		double min = valueSetHigher(equivalentValue, valueSet);
		ComponentSet minComponentSet = ComponentSet.single(min);
		
		if(maxComponents == 1) {
			return minComponentSet;
		}
		
		double minSearchValue = equivalentValue/maxComponents;
		double maxSearchValue = valueSetLower(equivalentValue, valueSet);
		
		Iterator<Double> valueSetIterator = valueSetIterator(minSearchValue, maxSearchValue, valueSet);
		
		while(valueSetIterator.hasNext()) {
			double augend = valueSetIterator.next();
			double addend = equivalentValue - augend;
			ComponentSet addendComponentSet = getHigherAdditiveCombination(addend, maxComponents - 1, valueSet);
			double sum = augend + addendComponentSet.equivalentValue();
			if(Calcs.compareWithinEpsilon(sum, min) < 0) {
				min = sum;
				minComponentSet = ComponentSet.additive(ComponentSet.single(augend), addendComponentSet);
			}
		}
		
		return minComponentSet;
		
	}
	
	/**
	 * Gets the compliment of the greatest value smaller than
	 * {@code equivalentValue} that can be formed with a parallel combination.
	 * 
	 * @param equivalentValue
	 * @param maxComponents
	 * @return
	 */
	private double getMinParallelCompliment(double equivalentValue, int maxComponents, NavigableSet<Double> valueSet) {
		
		return equivalentValue - 1/getHigherAdditiveCombination(1/equivalentValue, maxComponents, valueSet).equivalentValue();
		
		//return equivalentValue/100;
		
	}
	
	/**
	 * Searches for combinations of {@code maxComponents} values in {@code valueSet}
	 * with replacement that have the given equivalent value.
	 * 
	 * @param equivalentValue
	 * @param maxComponents
	 * @return
	 */
	public NavigableSet<ComponentSet> getCombinations(double equivalentValue, int maxComponents) {
		
		if(Calcs.equalWithinEpsilon(equivalentValue, 0.0)) {
			throw new IllegalArgumentException("Cannot find additive combinations for an equivalent value of zero");
		}
		if(maxComponents <= 0) {
			throw new IllegalArgumentException("Max components must be greater than zero");
		}
		
		double maxSearchValue = equivalentValue;
		
		return getAdditiveCombinations(equivalentValue, maxComponents, maxSearchValue, baseValueSet);
		
	}
	
	/**
	 * Searches for combinations of {@code maxComponents} values in {@code valueSet}
	 * with replacement that have the given equivalent value and are additive at the
	 * top level and for which the largest value in the combination is less than or
	 * equal to {@code maxSearchValue}.
	 * 
	 * @param equivalentValue
	 * @param maxComponents
	 * @param minSearchValue
	 * @param maxSearchValue
	 * @return
	 */
	private NavigableSet<ComponentSet> getAdditiveCombinations(double equivalentValue, int maxComponents, double maxSearchValue, NavigableSet<Double> valueSet) {
		
		if(maxComponents <= 0) {
			return Collections.emptyNavigableSet();
		}
		
		NavigableSet<ComponentSet> solutions = new TreeSet<>();
		
		if(maxComponents == 1) {
			double eqValLow = equivalentValue - Calcs.EPSILON;
			double eqValHigh = equivalentValue + Calcs.EPSILON;
			int eqValExponent = Math.max(Calcs.exponent(eqValLow), Calcs.exponent(eqValHigh));
			double eqValLowMantissa = eqValLow / Calcs.powTen(eqValExponent);
			double eqValHighMantissa = eqValHigh / Calcs.powTen(eqValExponent);
			if (Calcs.compareWithinEpsilon(equivalentValue, maxSearchValue) <= 0 && valueSet.subSet(eqValLowMantissa, true, eqValHighMantissa, true).size() > 0) {
				solutions.add(ComponentSet.single(equivalentValue));
			}
			return solutions;
		}
		
		double minAddSearchValue = equivalentValue/maxComponents;
		double minParallelSearchValue = getMinParallelCompliment(equivalentValue, maxComponents - 1, getInverseValueSet(valueSet));
		
		Iterator<Double> valueIterator = valueSetIterator(Math.min(minAddSearchValue, minParallelSearchValue), maxSearchValue, valueSet);
		
		while(valueIterator.hasNext()) {
			
			double candidateValue = valueIterator.next();
			double compliment = equivalentValue - candidateValue;
			
			if(Calcs.equalWithinEpsilon(compliment, 0.0)) {
				solutions.add(ComponentSet.single(candidateValue));
				continue;
			}
			
			if(Calcs.compareWithinEpsilon(candidateValue, minAddSearchValue) >= 0) {
				
				double newMaxSearchValue = Math.min(candidateValue, compliment);
				
				NavigableSet<ComponentSet> complimentSets = getAdditiveCombinations(compliment, maxComponents - 1, newMaxSearchValue, valueSet);
				
				for(ComponentSet complimentSet : complimentSets) {
					solutions.add(ComponentSet.additive(ComponentSet.single(candidateValue), complimentSet));
				}
				
			}
			
			if(Calcs.compareWithinEpsilon(candidateValue, minParallelSearchValue) >= 0 && maxComponents > 2) {
				
				double newMaxSearchValue = 1/compliment;
				
				NavigableSet<ComponentSet> complimentSets = getAdditiveCombinations(1/compliment, maxComponents - 1, newMaxSearchValue, getInverseValueSet(valueSet));
				complimentSets = complimentSets.stream().map(ComponentSet::invert).collect(Collectors.toCollection(TreeSet::new));
				
				for(ComponentSet complimentSet : complimentSets) {
					solutions.add(ComponentSet.additive(ComponentSet.single(candidateValue), complimentSet));
				}
				
			}
			
		}
		
		return solutions;
		
	}
	
	private class CombinationSearch extends RecursiveTask<NavigableSet<ComponentSet>> {
		
		final double equivalentValue;
		final int maxComponents;
		final double maxSearchValue;
		final boolean invertResult;
		final NavigableSet<Double> valueSet;
		
		public CombinationSearch(double equivalentValue, int maxComponents, double maxSearchValue, boolean invertResult, NavigableSet<Double> valueSet) {
			this.equivalentValue = equivalentValue;
			this.maxComponents = maxComponents;
			this.maxSearchValue = maxSearchValue;
			this.invertResult = invertResult;
			this.valueSet = valueSet;
		}
		
		/**
		 * Automatically forks this task if it is big enough or invokes it if it is
		 * small enough.
		 */
		private void invokeOrFork() {
			if(maxComponents >= 2) {
				this.fork();
			} else {
				this.invoke();
			}
		}
		
		@Override
		protected NavigableSet<ComponentSet> compute() {
			
			if(maxComponents <= 0) {
				return Collections.emptyNavigableSet();
			}
			
			NavigableSet<ComponentSet> solutions = new TreeSet<>();
			
			if(maxComponents == 1) {
				double eqValLow = equivalentValue - Calcs.EPSILON;
				double eqValHigh = equivalentValue + Calcs.EPSILON;
				int eqValExponent = Math.max(Calcs.exponent(eqValLow), Calcs.exponent(eqValHigh));
				double eqValLowMantissa = eqValLow / Calcs.powTen(eqValExponent);
				double eqValHighMantissa = eqValHigh / Calcs.powTen(eqValExponent);
				if (Calcs.compareWithinEpsilon(equivalentValue, maxSearchValue) <= 0 && valueSet.subSet(eqValLowMantissa, true, eqValHighMantissa, true).size() > 0) {
					solutions.add(ComponentSet.single(equivalentValue));
				}
				return solutions;
			}
			
			double minAddSearchValue = equivalentValue/maxComponents;
			double minParallelSearchValue = getMinParallelCompliment(equivalentValue, maxComponents - 1, getInverseValueSet(valueSet));
			
			Iterator<Double> valueIterator = valueSetIterator(Math.min(minAddSearchValue, minParallelSearchValue), maxSearchValue, valueSet);
			
			while(valueIterator.hasNext()) {
				
				double candidateValue = valueIterator.next();
				double compliment = equivalentValue - candidateValue;
				
				if(Calcs.equalWithinEpsilon(compliment, 0.0)) {
					solutions.add(ComponentSet.single(candidateValue));
					continue;
				}
				
				if(Calcs.compareWithinEpsilon(candidateValue, minAddSearchValue) >= 0) {
					
					double newMaxSearchValue = Math.min(candidateValue, compliment);
					
					CombinationSearch complimentSearch = new CombinationSearch(compliment, maxComponents - 1, newMaxSearchValue, false, valueSet);
					complimentSearch.invokeOrFork();
					NavigableSet<ComponentSet> complimentSets = complimentSearch.join();
					
					for(ComponentSet complimentSet : complimentSets) {
						solutions.add(ComponentSet.additive(ComponentSet.single(candidateValue), complimentSet));
					}
					
				}
				
				if(Calcs.compareWithinEpsilon(candidateValue, minParallelSearchValue) >= 0 && maxComponents > 2) {
					
					double newMaxSearchValue = 1/compliment;
					
					CombinationSearch complimentSearch = new CombinationSearch(1/compliment, maxComponents - 1, newMaxSearchValue, true, getInverseValueSet(valueSet));
					complimentSearch.invokeOrFork();
					NavigableSet<ComponentSet> complimentSets = complimentSearch.join();
					
					for(ComponentSet complimentSet : complimentSets) {
						solutions.add(ComponentSet.additive(ComponentSet.single(candidateValue), complimentSet));
					}
					
				}
				
			}
			
			if(invertResult) {
				return solutions.stream().map(ComponentSet::invert).collect(Collectors.toCollection(TreeSet::new));
			}
			
			return solutions;
			
		}
		
	}
	
	private NavigableSet<Double> getInverseValueSet(NavigableSet<Double> valueSet) {
		if(valueSet == baseValueSet) {
			return inverseValueSet;
		} else if(valueSet == inverseValueSet) {
			return baseValueSet;
		} else {
			throw new IllegalArgumentException("Inavlid value set");
		}
	}
	
	/**
	 * Returns an iterator for every value between {@code minValue} and
	 * {@code maxValue} (inclusive) for the given value set in descending order.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @param valueSet
	 */
	private Iterator<Double> valueSetIterator(double minValue, double maxValue, NavigableSet<Double> valueSet) {
		
		maxValue += Calcs.EPSILON;
		minValue -= Calcs.EPSILON;
		
		if(minValue > maxValue) {
			throw new IllegalArgumentException("Max value cannot be less than min value");
		}
		
		double adjustedMaxValue = valueSetFloor(maxValue, valueSet);
		double adjustedMinValue = valueSetCeiling(minValue, valueSet);
		
		if(adjustedMinValue > adjustedMaxValue) {
			return Collections.emptyIterator();
		}
		
		final double maxMantissa = Calcs.mantissa(adjustedMaxValue);
		final int maxExponent = Calcs.exponent(adjustedMaxValue);
		
		final double minMantissa = Calcs.mantissa(adjustedMinValue);
		final int minExponent = Calcs.exponent(adjustedMinValue);
		
		return new Iterator<Double>() {
			private int exponent = maxExponent;
			private Iterator<Double> mantissaIterator;
			
			{
				generateMantissaIterator();
			}
			
			private void generateMantissaIterator() {
				NavigableSet<Double> loopSet = valueSet;
				if(exponent == maxExponent) {
					loopSet = loopSet.headSet(maxMantissa, true);
				}
				if(exponent == minExponent) {
					loopSet = loopSet.tailSet(minMantissa, true);
				}
				mantissaIterator = loopSet.descendingIterator();
			}
			
			public boolean hasNext() {
				return exponent > minExponent || mantissaIterator.hasNext();
			}
			
			public Double next() {
				if(!mantissaIterator.hasNext()) {
					if(exponent < minExponent) {
						throw new NoSuchElementException();
					}
					exponent--;
					generateMantissaIterator();
				}
				return mantissaIterator.next() * Calcs.powTen(exponent);
			}
		};
		
	}
	
	/**
	 * Gets the greatest value that is less than {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet
	 */
	private double valueSetLower(double value, NavigableSet<Double> valueSet) {
		value -= Calcs.EPSILON;
		double mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		Double lowerMantissa = valueSet.lower(mantissa);
		if(lowerMantissa == null) {
			lowerMantissa = valueSet.last();
			exponent--;
		}
		return lowerMantissa * Calcs.powTen(exponent);
	}
	
	/**
	 * Gets the least value that is greater than {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet the ValueSet
	 */
	private double valueSetHigher(double value, NavigableSet<Double> valueSet) {
		value += Calcs.EPSILON;
		double mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		Double higherMantissa = valueSet.higher(mantissa);
		if(higherMantissa == null) {
			higherMantissa = valueSet.first();
			exponent++;
		}
		return higherMantissa * Calcs.powTen(exponent);
	}
	
	/**
	 * Gets the greatest value that is less than or equal to {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet
	 */
	private double valueSetFloor(double value, NavigableSet<Double> valueSet) {
		value += Calcs.EPSILON;
		double mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		Double floorMantissa = valueSet.floor(mantissa);
		if(floorMantissa == null) {
			floorMantissa = valueSet.last();
			exponent--;
		}
		return floorMantissa * Calcs.powTen(exponent);
	}
	
	/**
	 * Gets the least value that is greater than or equal to {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet the ValueSet
	 */
	private double valueSetCeiling(double value, NavigableSet<Double> valueSet) {
		value -= Calcs.EPSILON;
		double mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		Double ceilingMantissa = valueSet.ceiling(mantissa);
		if(ceilingMantissa == null) {
			ceilingMantissa = valueSet.first();
			exponent++;
		}
		return ceilingMantissa * Calcs.powTen(exponent);
	}
	
}
