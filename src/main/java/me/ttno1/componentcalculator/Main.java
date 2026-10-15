package me.ttno1.componentcalculator;

import java.math.RoundingMode;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import me.ttno1.componentcalculator.bigdecimal.Calculator;
import me.ttno1.componentcalculator.bigdecimal.ComponentSet;
import me.ttno1.componentcalculator.bigdecimal.MetricNumbers;
import me.ttno1.componentcalculator.bigdecimal.SBigDecimal;
import me.ttno1.componentcalculator.bigdecimal.ValueSets;



public class Main {
	
	private static Main instance;
	
	public static void main(String[] args) {
		
		instance = new Main();
		
		instance.runCLILoop();
		
	}
	
	private static final String[] exitWords = {"exit", "quit", "stop", "end", "shutdown"};
	
	private static final String[] helpWords = {"help", "h", "-h", "--help", "usage"};
	
	private final ForkJoinPool forkJoinPool;
	
	private final Calculator calculator;
	
	private final Scanner stdInScanner;
	
	private Main() {
		
		forkJoinPool = new ForkJoinPool(4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, false, 6, 32, 2, null, 60, TimeUnit.SECONDS);
		
		calculator = new Calculator(forkJoinPool);
		
		stdInScanner = new Scanner(System.in);
		
	}
	
	private void runCLILoop() {
		
		System.out.println("Ready. Type 'help' for usage.");
		
		main: while(!Thread.interrupted()) {
			
			String inLine = stdInScanner.nextLine();
			String trimmedLowerInLine = inLine.trim().toLowerCase();
			
			for(String helpWord : helpWords) {
				if(helpWord.equals(trimmedLowerInLine)) {
					printUsage();
					continue main;
				}
			}
			
			for(String exitWord : exitWords) {
				if(exitWord.equals(trimmedLowerInLine)) {
					break main;
				}
			}
			
			String[] args = inLine.split(" ");
			
			if(args.length < 2 || args.length > 3) {
				System.out.println("Invalid number of arguments");
				System.out.println("Type 'help' for usage");
				continue;
			}
			
			SBigDecimal equivalentValue;
			try {
				equivalentValue = MetricNumbers.parseMetricNumber(args[0]);
			} catch(NumberFormatException e) {
				System.out.println("Invalid number format for equivalent_value: " + args[0]);
				System.out.println("Type 'help' for usage");
				continue;
			}
			if(equivalentValue.compareTo(SBigDecimal.ZERO) <= 0) {
				System.out.println("equivalent_value must be greter than zero");
				continue;
			}
			
			int maxComponents;
			try {
				maxComponents = Integer.parseInt(args[1]);
			} catch(NumberFormatException e) {
				System.out.println("Invalid integer format for max_components: " + args[1]);
				System.out.println("Type 'help' for usage");
				continue;
			}
			if(maxComponents <= 0) {
				System.out.println("max_components must be greter than zero");
				continue;
			}
			
			NavigableSet<SBigDecimal> valueSet = ValueSets.E192_PLUS_E24;
			if(args.length > 2) {
				try {
					valueSet = ValueSets.getValueSetFromStringName(args[2]);
				} catch(IllegalArgumentException e) {
					System.out.println("Invalid value_set name: " + args[2]);
					System.out.println("Type 'help' for usage");
					continue;
				}
			}
			
			printSolutions(equivalentValue, maxComponents, valueSet);
			
		}
		
		forkJoinPool.close();
		
	}
	
	private void printSolutions(SBigDecimal equivalentValue, int maxComponents, NavigableSet<SBigDecimal> valueSet) {
		
		long startTimeNanos = System.nanoTime();
		
		NavigableSet<ComponentSet> solutions = calculator.getCombinations(equivalentValue, maxComponents, valueSet);
		
		long elapsedTimeNanos = System.nanoTime() - startTimeNanos;
		SBigDecimal elapsedTimeSeconds = new SBigDecimal(elapsedTimeNanos).movePointLeft(9);
		
		for(ComponentSet solution : solutions) {
			System.out.println(solution.toExpressionString());
		}
		
		System.out.println();
		System.out.println("Found " + solutions.size() + " results in " + MetricNumbers.toMetricString(elapsedTimeSeconds.setScale(2, RoundingMode.HALF_UP)) + "s");
		System.out.println();
		
	}
	
	private void printUsage() {
		
		System.out.println("""
				Usage: equivalent_value max_components [value_set]
				
					equivalent_value: the value to find combinations for
						examples: 1k, 1E3, 2.25M, 2.25E6, 100n, 100E-6, 3E, 3E18
					
					max_components: the maximum number of components in a combination
						note: values above 3 may take very long to calculate
					
					value_set: the E series from which to search for combinations
						possible values: E3, E6, E12, E24, E48, E96, E192, E96+24, E192+24
						default: E192+24
				
					[exit | quit | stop | end | shutdown] to exit
					[help | h | -h | --help | usage] for usage
				""");
		
	}

}
