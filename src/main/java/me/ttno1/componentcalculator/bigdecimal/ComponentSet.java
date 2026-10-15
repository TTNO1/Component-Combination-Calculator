package me.ttno1.componentcalculator.bigdecimal;

import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Represents a collection of component values that are additive, parallel, or
 * some combination of both.
 */
public abstract class ComponentSet implements Comparable<ComponentSet> {
	
	private static int nextID = 0;
	
	private final int id = nextID++;
	
	protected final NavigableSet<ComponentSet> components;
	
	private ComponentSet(boolean single) {
		components = single ? null : new TreeSet<>();
	}

	/**
	 * @return true if this set's components are parallel, false if additive
	 */
	public abstract boolean isParallel();
	
	/**
	 * Returns true if this component set will always have a size of 1.
	 * 
	 * @return whether this ComponentSet represents only one component
	 */
	public abstract boolean isSingle();
	
	/**
	 * Calculates the equivalent value of all of the components.
	 * 
	 * @return the equivalent value
	 */
	public abstract SBigDecimal equivalentValue();
	
	/**
	 * If this ComponentSet is additive, converts this ComponentSet to a parallel
	 * ComponentSet with inverted subcomponents.<br>
	 * If this ComponentSet is parallel, converts this ComponentSet to an additive
	 * ComponentSet with inverted subcomponents.<br>
	 * If this ComponentSet is single, returns a single ComponentSet with the
	 * reciprocal value.
	 * 
	 * @return
	 */
	public abstract ComponentSet invert();
	
	/**
	 * Adds the given component set into this one.<br>
	 * If the given ComponentSet is of the same operation as this ComponentSet, the
	 * subcomponents of the given set are each added individually to this one;
	 * otherwise, the given set is added as a whole unit.<br>
	 * If this ComponentSet is parallel, the given components are added in parallel.
	 * 
	 * @param subcomponents the ComponentSet to add into this one
	 * @throws UnsupportedOperationException if the implementation does not support
	 *                                       adding components (single component
	 *                                       sets)
	 */
	public void add(ComponentSet subcomponents) {
		if(!subcomponents.isSingle() && subcomponents.isParallel() == this.isParallel()) {
			components.addAll(subcomponents.components);
		} else {
			components.add(subcomponents);
		}
	}
	
	/**
	 * Adds a single ComponentSet specified by the given value into this set.<br>
	 * This is equivalent to {@code add(ComponentSet.single(value))}
	 * 
	 * @param value the value of the single ComponentSet to add
	 * @throws UnsupportedOperationException if the implementation does not support
	 *                                       adding components (single component
	 *                                       sets)
	 */
	public void add(SBigDecimal value) {
		add(ComponentSet.single(value));
	}
	
	/**
	 * Returns an unmodifiable view of the set of all subcomponents that make up
	 * this component set.
	 * 
	 * @return the set of constituent subcomponents
	 * @throws UnsupportedOperationException if the implementation does not have
	 *                                       subcomponents (single component sets)
	 */
	//NavigableSet<ComponentSet> getSubcomponents();
	
	/**
	 * Returns the total number of components in this ComponentSet including components in subsets.
	 * 
	 * @return the number of components in this set
	 */
	public int size() {
		int size = 0;
		for(ComponentSet component : components) {
			size += component.size();
		}
		return size;
	}
	
	/**
	 * Gets the maximum single component value in this ComponentSet
	 * 
	 * @return the highest single component value
	 */
	public SBigDecimal getMaxComponentValue() {
		SBigDecimal max = components.first().getMaxComponentValue();
		for(ComponentSet subcomponent : components) {
			max = max.max(subcomponent.getMaxComponentValue());
		}
		return max;
	}
	
	/**
	 * Gets the minimum single component value in this ComponentSet
	 * 
	 * @return the lowest single component value
	 */
	public SBigDecimal getMinComponentValue() {
		SBigDecimal min = components.first().getMinComponentValue();
		for(ComponentSet subcomponent : components) {
			min = min.min(subcomponent.getMinComponentValue());
		}
		return min;
	}
	
	/**
	 * Calculates the maximum difference between any two single component values in
	 * this ComponentSet.
	 * 
	 * @return the largest difference between any two components
	 */
	public SBigDecimal getMaxComponentDifference() {
		return getMaxComponentValue().subtract(getMinComponentValue());
	}
	
	/**
	 * Converts this ComponentSet into a readable expression representing all of the
	 * individual components that make it up and how they relate to each other.
	 * 
	 * @return a string representation of this ComponentSet
	 */
	public String toExpressionString() {
		String opString = this.isParallel() ? "||" : "+";
		StringBuilder builder = new StringBuilder();
		for(ComponentSet component : components) {
			if(component.size() > 1) {
				builder.append("(" + component.toExpressionString() + ")");
			} else {
				builder.append(component.toExpressionString());
			}
			builder.append(" " + opString + " ");
		}
		builder.delete(builder.length() - (2 + opString.length()), builder.length());
		return builder.toString();
	}
	
	/**
	 * Sorts from smallest to largest size, then from additive to parallel, then
	 * from smallest to largest maximum difference, then from largest to smallest
	 * equivalent value, then from single to not single, then from oldest to newest.
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(ComponentSet other) {
		int sizeComp = Integer.compare(this.size(), other.size());
		if(sizeComp != 0) {
			return sizeComp;
		}
		if(this.isParallel() != other.isParallel()) {
			return this.isParallel() ? 1 : -1;
		}
		int maxDiffComp = Calcs.compareWithinEpsilon(this.getMaxComponentDifference(), other.getMaxComponentDifference());
		if(maxDiffComp != 0) {
			return maxDiffComp;
		}
		int eqValComp = Calcs.compareWithinEpsilon(other.equivalentValue(), this.equivalentValue());
		if (eqValComp != 0) {
			return eqValComp;
		}
		if(this.isSingle() != other.isSingle()) {
			return this.isSingle() ? -1 : 1;
		}
		return Integer.compare(this.id, other.id);
	}
	
	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ComponentSet other) {
			return this.id == other.id;
		}
		return false;
	}
	
	public static ComponentSet single(SBigDecimal value) {
		return new SingleComponentSet(value);
	}
	
	public static ComponentSet additive(ComponentSet ... components) {
		if(components.length == 0) {
			throw new IllegalArgumentException("Cannot create empty ComponentSet");
		}
		AdditiveComponentSet componentSet = new AdditiveComponentSet();
		for(ComponentSet component : components) {
			componentSet.add(component);
		}
		return componentSet;
	}
	
	public static ComponentSet additive(SBigDecimal ... values) {
		if(values.length == 0) {
			throw new IllegalArgumentException("Cannot create empty ComponentSet");
		}
		AdditiveComponentSet componentSet = new AdditiveComponentSet();
		for(SBigDecimal value : values) {
			componentSet.add(ComponentSet.single(value));
		}
		return componentSet;
	}
	
	public static ComponentSet parallel(ComponentSet ... components) {
		if(components.length == 0) {
			throw new IllegalArgumentException("Cannot create empty ComponentSet");
		}
		ParallelComponentSet componentSet = new ParallelComponentSet();
		for(ComponentSet component : components) {
			componentSet.add(component);
		}
		return componentSet;
	}
	
	public static ComponentSet parallel(SBigDecimal ... values) {
		if(values.length == 0) {
			throw new IllegalArgumentException("Cannot create empty ComponentSet");
		}
		ParallelComponentSet componentSet = new ParallelComponentSet();
		for(SBigDecimal value : values) {
			componentSet.add(ComponentSet.single(value));
		}
		return componentSet;
	}
	
	public static class SingleComponentSet extends ComponentSet {
		
		private final SBigDecimal value;
		
		private SingleComponentSet(SBigDecimal value) {
			super(true);
			this.value = value;
		}

		@Override
		public boolean isParallel() {
			return false;
		}
		
		@Override
		public boolean isSingle() {
			return true;
		}
		
		@Override
		public SBigDecimal equivalentValue() {
			return value;
		}
		
		@Override
		public ComponentSet invert() {
			return ComponentSet.single(Calcs.reciprocal(value));
		}

		@Override
		public void add(ComponentSet subComponents) {
			throw new UnsupportedOperationException("Cannot add to single ComponentSet");
		}
		
		@Override
		public void add(SBigDecimal value) {
			throw new UnsupportedOperationException("Cannot add to single ComponentSet");
		}

		@Override
		public int size() {
			return 1;
		}
		
		@Override
		public SBigDecimal getMaxComponentValue() {
			return value;
		}
		
		@Override
		public SBigDecimal getMinComponentValue() {
			return value;
		}
		
		@Override
		public SBigDecimal getMaxComponentDifference() {
			return SBigDecimal.ZERO;
		}
		
		@Override
		public String toExpressionString() {
			return MetricNumbers.toMetricString(value);
		}
		
	}
	
	public static class AdditiveComponentSet extends ComponentSet {
		
		private AdditiveComponentSet() {
			super(false);
		}

		@Override
		public boolean isParallel() {
			return false;
		}
		
		@Override
		public boolean isSingle() {
			return false;
		}
		
		@Override
		public SBigDecimal equivalentValue() {
			SBigDecimal eqValue = SBigDecimal.ZERO;
			for(ComponentSet component : components) {
				eqValue = eqValue.add(component.equivalentValue());
			}
			return eqValue;
		}
		
		@Override
		public ComponentSet invert() {
			return ComponentSet.parallel((ComponentSet[]) components.stream().map(ComponentSet::invert).toArray((size) -> new ComponentSet[size]));
		}
		
	}
	
	public static class ParallelComponentSet extends ComponentSet {
		
		private ParallelComponentSet() {
			super(false);
		}

		@Override
		public boolean isParallel() {
			return true;
		}
		
		@Override
		public boolean isSingle() {
			return false;
		}
		
		@Override
		public SBigDecimal equivalentValue() {
			SBigDecimal eqValue = components.first().equivalentValue();
			for(ComponentSet component : components.tailSet(components.first(), false)) {
				eqValue = Calcs.parallel(eqValue, component.equivalentValue());
			}
			return eqValue;
		}
		
		@Override
		public ComponentSet invert() {
			return ComponentSet.additive((ComponentSet[]) components.stream().map(ComponentSet::invert).toArray((size) -> new ComponentSet[size]));
		}
		
	}

}
