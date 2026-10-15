package me.ttno1.componentcalculator.bigdecimal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class Calculator {
	
	private final ForkJoinPool forkJoinPool;
	
	public Calculator(ForkJoinPool forkJoinPool) {
		
		this.forkJoinPool = forkJoinPool;
		
	}
	
	/**
	 * Searches for combinations of {@code maxComponents} values in {@code valueSet}
	 * with replacement that have the given equivalent value.
	 * 
	 * @param equivalentValue
	 * @param maxComponents
	 * @param baseValueSet
	 * @return
	 */
	public NavigableSet<ComponentSet> getCombinations(SBigDecimal equivalentValue, int maxComponents, NavigableSet<SBigDecimal> baseValueSet) {
		
		if(Calcs.equalWithinEpsilon(equivalentValue, SBigDecimal.ZERO)) {
			throw new IllegalArgumentException("Cannot find additive combinations for an equivalent value of zero");
		}
		if(maxComponents <= 0) {
			throw new IllegalArgumentException("Max components must be greater than zero");
		}
		
		NavigableSet<ComponentSet> solutions = forkJoinPool.submit(new CombinationSearch(equivalentValue, maxComponents, baseValueSet)).join();
		forkJoinPool.submit(new CombinationSearch(Calcs.reciprocal(equivalentValue), maxComponents, ValueSets.getInverseValueSet(baseValueSet))).join().stream().map(ComponentSet::invert).forEach(solutions::add);
		
		return solutions;
		
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
	private ComponentSet getHigherAdditiveCombination(SBigDecimal equivalentValue, int maxComponents, NavigableSet<SBigDecimal> valueSet) {
		
		if(maxComponents <= 0) {
			throw new IllegalArgumentException("Cannot find higher additive combination for max components <= 0");
		}
		
		SBigDecimal min = valueSetHigher(equivalentValue, valueSet);
		ComponentSet minComponentSet = ComponentSet.single(min);
		
		if(maxComponents == 1) {
			return minComponentSet;
		}
		
		SBigDecimal minSearchValue = equivalentValue.divide(new SBigDecimal(maxComponents), Calcs.MC);
		SBigDecimal maxSearchValue = valueSetLower(equivalentValue, valueSet);
		
		Iterator<SBigDecimal> valueSetIterator = valueSetIterator(minSearchValue, maxSearchValue, valueSet);
		
		while(valueSetIterator.hasNext()) {
			SBigDecimal augend = valueSetIterator.next();
			SBigDecimal addend = equivalentValue.subtract(augend);
			ComponentSet addendComponentSet = getHigherAdditiveCombination(addend, maxComponents - 1, valueSet);
			SBigDecimal sum = augend.add(addendComponentSet.equivalentValue());
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
	private SBigDecimal getMinParallelCompliment(SBigDecimal equivalentValue, int maxComponents, NavigableSet<SBigDecimal> parallelValueSet) {
		
		return equivalentValue.subtract(Calcs.reciprocal(getHigherAdditiveCombination(Calcs.reciprocal(equivalentValue), maxComponents, parallelValueSet).equivalentValue()));
		
		//return equivalentValue/100;
		
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
	private NavigableSet<ComponentSet> getAdditiveCombinations(SBigDecimal equivalentValue, int maxComponents, SBigDecimal maxSearchValue, NavigableSet<SBigDecimal> valueSet) {
		
		if(maxComponents <= 0) {
			return Collections.emptyNavigableSet();
		}
		
		NavigableSet<ComponentSet> solutions = new TreeSet<>();
		
		if(maxComponents == 1) {
			Iterator<SBigDecimal> valueIterator = valueSetIterator(equivalentValue, equivalentValue, valueSet);
			if (Calcs.compareWithinEpsilon(equivalentValue, maxSearchValue) <= 0 && valueIterator.hasNext()) {
				solutions.add(ComponentSet.single(valueIterator.next()));
			}
			/*SBigDecimal eqValLow = equivalentValue.subtract(Calcs.EPSILON);
			SBigDecimal eqValHigh = equivalentValue.add(Calcs.EPSILON);
			int eqValExponent = Math.max(Calcs.exponent(eqValLow), Calcs.exponent(eqValHigh));
			SBigDecimal eqValLowMantissa = eqValLow.movePointLeft(eqValExponent);
			SBigDecimal eqValHighMantissa = eqValHigh.movePointLeft(eqValExponent);
			if (Calcs.compareWithinEpsilon(equivalentValue, maxSearchValue) <= 0 && valueSet.subSet(eqValLowMantissa, true, eqValHighMantissa, true).size() > 0) {
				solutions.add(ComponentSet.single(equivalentValue));
			}*/
			return solutions;
		}
		
		SBigDecimal minAddSearchValue = equivalentValue.divide(new SBigDecimal(maxComponents), Calcs.MC);
		SBigDecimal minParallelSearchValue = getMinParallelCompliment(equivalentValue, maxComponents - 1, ValueSets.getInverseValueSet(valueSet));
		
		Iterator<SBigDecimal> valueIterator = valueSetIterator(minAddSearchValue.min(minParallelSearchValue), maxSearchValue, valueSet);
		
		while(valueIterator.hasNext()) {
			
			SBigDecimal candidateValue = valueIterator.next();
			SBigDecimal compliment = equivalentValue.subtract(candidateValue);
			
			if(Calcs.equalWithinEpsilon(compliment, SBigDecimal.ZERO)) {
				solutions.add(ComponentSet.single(candidateValue));
				continue;
			}
			
			if(Calcs.compareWithinEpsilon(candidateValue, minAddSearchValue) >= 0) {
				
				SBigDecimal newMaxSearchValue = candidateValue.min(compliment);
				
				NavigableSet<ComponentSet> complimentSets = getAdditiveCombinations(compliment, maxComponents - 1, newMaxSearchValue, valueSet);
				
				for(ComponentSet complimentSet : complimentSets) {
					solutions.add(ComponentSet.additive(ComponentSet.single(candidateValue), complimentSet));
				}
				
			}
			
			if(Calcs.compareWithinEpsilon(candidateValue, minParallelSearchValue) >= 0 && maxComponents > 2) {
				
				SBigDecimal newMaxSearchValue = Calcs.reciprocal(compliment);
				
				NavigableSet<ComponentSet> complimentSets = getAdditiveCombinations(Calcs.reciprocal(compliment), maxComponents - 1, newMaxSearchValue, ValueSets.getInverseValueSet(valueSet));
				complimentSets = complimentSets.stream().filter(componentSet -> !componentSet.isSingle()).map(ComponentSet::invert).collect(Collectors.toCollection(TreeSet::new));
				
				for(ComponentSet complimentSet : complimentSets) {
					solutions.add(ComponentSet.additive(ComponentSet.single(candidateValue), complimentSet));
				}
				
			}
			
		}
		
		return solutions;
		
	}
	
	private class CombinationSearch extends RecursiveTask<NavigableSet<ComponentSet>> {
		
		private static final long serialVersionUID = -6767085144416744641L;
		
		private final SBigDecimal equivalentValue;
		private final int maxComponents;
		private final SBigDecimal maxSearchValue;
		private final NavigableSet<SBigDecimal> valueSet;
		private SBigDecimal previousCompliment;
		
		public CombinationSearch(SBigDecimal equivalentValue, int maxComponents, NavigableSet<SBigDecimal> valueSet) {
			this(equivalentValue, maxComponents, equivalentValue, valueSet, null);
		}
		
		private CombinationSearch(SBigDecimal equivalentValue, int maxComponents, SBigDecimal maxSearchValue, NavigableSet<SBigDecimal> valueSet, SBigDecimal previousCompliment) {
			this.equivalentValue = equivalentValue;
			this.maxComponents = maxComponents;
			this.maxSearchValue = maxSearchValue;
			this.valueSet = valueSet;
			this.previousCompliment = previousCompliment;
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
				Iterator<SBigDecimal> valueIterator = valueSetIterator(equivalentValue, equivalentValue, valueSet);
				if (Calcs.compareWithinEpsilon(equivalentValue, maxSearchValue) <= 0 && valueIterator.hasNext()) {
					solutions.add(ComponentSet.single(valueIterator.next()));
				}
				/*SBigDecimal eqValLow = equivalentValue.subtract(Calcs.EPSILON);
				SBigDecimal eqValHigh = equivalentValue.add(Calcs.EPSILON);
				int eqValExponent = Math.max(Calcs.exponent(eqValLow), Calcs.exponent(eqValHigh));
				SBigDecimal eqValLowMantissa = eqValLow.movePointLeft(eqValExponent);
				SBigDecimal eqValHighMantissa = eqValHigh.movePointLeft(eqValExponent);
				if (Calcs.compareWithinEpsilon(equivalentValue, maxSearchValue) <= 0 && valueSet.subSet(eqValLowMantissa, true, eqValHighMantissa, true).size() > 0) {
					solutions.add(ComponentSet.single(equivalentValue));
				}*/
				return solutions;
			}
			
			SBigDecimal minAddSearchValue = equivalentValue.divide(new SBigDecimal(maxComponents), Calcs.MC);
			SBigDecimal minParallelSearchValue = getMinParallelCompliment(equivalentValue, maxComponents - 1, ValueSets.getInverseValueSet(valueSet));
			
			Iterator<SBigDecimal> valueIterator = valueSetIterator(minAddSearchValue.min(minParallelSearchValue), maxSearchValue, valueSet);
			
			Set<CombinationSearch> addSearches = new HashSet<>();
			Set<CombinationSearch> parallelSearches = new HashSet<>();
			
			while(valueIterator.hasNext()) {
				
				SBigDecimal candidateValue = valueIterator.next();
				SBigDecimal compliment = equivalentValue.subtract(candidateValue);
				
				if(Calcs.equalWithinEpsilon(compliment, SBigDecimal.ZERO)) {
					solutions.add(ComponentSet.single(candidateValue));
					continue;
				}
				
				if(Calcs.compareWithinEpsilon(candidateValue, minAddSearchValue) >= 0) {
					SBigDecimal newMaxSearchValue = candidateValue.min(compliment);
					
					CombinationSearch complimentSearch = new CombinationSearch(compliment, maxComponents - 1, newMaxSearchValue, valueSet, candidateValue);
					complimentSearch.invokeOrFork();
					addSearches.add(complimentSearch);
				}
				
				if(Calcs.compareWithinEpsilon(candidateValue, minParallelSearchValue) >= 0 && maxComponents > 2) {
					SBigDecimal newMaxSearchValue = Calcs.reciprocal(compliment);
					
					CombinationSearch complimentSearch = new CombinationSearch(Calcs.reciprocal(compliment), maxComponents - 1, newMaxSearchValue, ValueSets.getInverseValueSet(valueSet), candidateValue);
					complimentSearch.invokeOrFork();
					parallelSearches.add(complimentSearch);
				}
				
			}
			
			for(CombinationSearch addSearch : addSearches) {
				addSearch.join().stream().map(componentSet -> ComponentSet.additive(ComponentSet.single(addSearch.previousCompliment), componentSet)).forEach(solutions::add);;
			}

			for(CombinationSearch parallelSearch : parallelSearches) {
				parallelSearch.join().stream().filter(componentSet -> !componentSet.isSingle()).map(ComponentSet::invert).map(componentSet -> ComponentSet.additive(ComponentSet.single(parallelSearch.previousCompliment), componentSet)).forEach(solutions::add);;
			}
			
			return solutions;
			
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
	private Iterator<SBigDecimal> valueSetIterator(SBigDecimal minValue, SBigDecimal maxValue, NavigableSet<SBigDecimal> valueSet) {
		
		maxValue = maxValue.add(Calcs.EPSILON);
		minValue = minValue.subtract(Calcs.EPSILON);
		
		if(minValue.compareTo(maxValue) > 0) {
			throw new IllegalArgumentException("Max value cannot be less than min value");
		}
		
		SBigDecimal adjustedMaxValue = valueSetFloor(maxValue, valueSet);
		SBigDecimal adjustedMinValue = valueSetCeiling(minValue, valueSet);
		
		if(adjustedMinValue.compareTo(adjustedMaxValue) > 0) {
			return Collections.emptyIterator();
		}
		
		final SBigDecimal maxMantissa = Calcs.mantissa(adjustedMaxValue);
		final int maxExponent = Calcs.exponent(adjustedMaxValue);
		
		final SBigDecimal minMantissa = Calcs.mantissa(adjustedMinValue);
		final int minExponent = Calcs.exponent(adjustedMinValue);
		
		return new Iterator<SBigDecimal>() {
			private int exponent = maxExponent;
			private Iterator<SBigDecimal> mantissaIterator;
			
			{
				generateMantissaIterator();
			}
			
			private void generateMantissaIterator() {
				NavigableSet<SBigDecimal> loopSet = valueSet;
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
			
			public SBigDecimal next() {
				if(!mantissaIterator.hasNext()) {
					if(exponent < minExponent) {
						throw new NoSuchElementException();
					}
					exponent--;
					generateMantissaIterator();
				}
				return mantissaIterator.next().movePointRight(exponent);
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
	private SBigDecimal valueSetLower(SBigDecimal value, NavigableSet<SBigDecimal> valueSet) {
		value = value.subtract(Calcs.EPSILON);
		SBigDecimal mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		SBigDecimal lowerMantissa = valueSet.lower(mantissa);
		if(lowerMantissa == null) {
			lowerMantissa = valueSet.last();
			exponent--;
		}
		return lowerMantissa.movePointRight(exponent);
	}
	
	/**
	 * Gets the least value that is greater than {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet the ValueSet
	 */
	private SBigDecimal valueSetHigher(SBigDecimal value, NavigableSet<SBigDecimal> valueSet) {
		value = value.add(Calcs.EPSILON);
		SBigDecimal mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		SBigDecimal higherMantissa = valueSet.higher(mantissa);
		if(higherMantissa == null) {
			higherMantissa = valueSet.first();
			exponent++;
		}
		return higherMantissa.movePointRight(exponent);
	}
	
	/**
	 * Gets the greatest value that is less than or equal to {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet
	 */
	private SBigDecimal valueSetFloor(SBigDecimal value, NavigableSet<SBigDecimal> valueSet) {
		value = value.add(Calcs.EPSILON);
		SBigDecimal mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		SBigDecimal floorMantissa = valueSet.floor(mantissa);
		if(floorMantissa == null) {
			floorMantissa = valueSet.last();
			exponent--;
		}
		return floorMantissa.movePointRight(exponent);
	}
	
	/**
	 * Gets the least value that is greater than or equal to {@code value} given the
	 * value set of mantissas.
	 * 
	 * @param value
	 * @param valueSet the ValueSet
	 */
	private SBigDecimal valueSetCeiling(SBigDecimal value, NavigableSet<SBigDecimal> valueSet) {
		value = value.subtract(Calcs.EPSILON);
		SBigDecimal mantissa = Calcs.mantissa(value);
		int exponent = Calcs.exponent(value);
		SBigDecimal ceilingMantissa = valueSet.ceiling(mantissa);
		if(ceilingMantissa == null) {
			ceilingMantissa = valueSet.first();
			exponent++;
		}
		return ceilingMantissa.movePointRight(exponent);
	}
	
}
