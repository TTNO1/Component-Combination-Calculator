## Component Combination Calculator
This is a tool that finds all combinations of components in series or parallel or some combination of the two for a desired equivalent value.

### Usage
Download Jar file from releases.
```
java -jar componentcalculator.jar
Ready. Type 'help' for usage.
help
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

11.25K 3
5.69k + 5.56k
5.76k + 5.49k
5.83k + 5.42k
6.2k + 5.05k

[... (results omitted for brevity)]

30k || 18k
105k || 12.6k
180k || 12k
7.5k + (7.5k || 7.5k)
3.9k + 3.7k + 3.65k
7.59k + (7.32k || 7.32k)
7.41k + (7.68k || 7.68k)
3.9k + 3.74k + 3.61k
3.9k + 3.83k + 3.52k

[...]

5.1k + (24.6k || 8.2k)
1.5k + (21k || 18.2k)
750 + (21k || 21k)
1.35k + (22k || 18k)

[...]

210k || 210k || 12.6k
11.7k || (210k + 82.5k)
11.7k || (221k + 71.5k)
11.7k || (226k + 66.5k)
234k || 130k || 13k
255k || 34k || 18k
252k || 180k || 12.6k
11.7k || (267k + 25.5k)
11.7k || (271k + 21.5k)

Found 2547 results in 290ms

```