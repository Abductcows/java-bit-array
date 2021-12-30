# Java BitArray

[![Latest release][latest-release-shield]][latest-release-url]&nbsp;&nbsp;
[![Open Issues][open-issues-shield]][open-issues-url]&nbsp;&nbsp;
[![Contributors][contributors-shield]][contributors-url]

### TL;DR: Faster List&lt;Boolean&gt; than ArrayList with the same exact behaviour. 
- [Documentation](https://abductcows.github.io/java-bit-array/gr/geompokon/bitarray/BitArray.html) 
- [Release](https://github.com/Abductcows/java-bit-array/releases/latest)
- [Benchmarks](https://github.com/Abductcows/bit-array-benchmarks)
### Motivation
This class is a replacement for the `ArrayList<Boolean>` type when working with its `List` interface. It boasts higher performance in add, remove and set operations and requires less memory for storing the same elements. 

The BitArray is by all means an array; it is random access and all elements have contiguous indices at all times. Its behaviour is meant to be indistinguishable from ArrayList in order for programmers to be able to substitute it for the latter and benefit from its performance advantage. 

### Few details
Internally the array stores the boolean elements in an array of long primitives. These long primitives essentially form a sequence of bits; their chained bit representation in 2's complement. Boolean elements are converted to and from their bit equivalent to perform insertions, deletions etc. With the appropriate bitwise operations new elements can be added at a specific index and elements already in the array can be shifted to preserve the previous order. Thanks to this "hack", element shifting and array resizing is much cheaper, all while the elements themselves occupy less space in memory.

### Thread safety
The class is not currently thread-safe, I will look into it properly at some point. For the time being you can use `Collections.synchronizedList()`

### Null values
Unfortunately, due to the implementation I have not been able to accommodate null values in the array. Null insertions or updates will throw NullPointerException. 

### Performance
For the performance difference, check out the [benchmark repository](https://github.com/Abductcows/bit-array-benchmarks). It includes results from my runs and the benchmark files should you want to run them yourself. A TLDR version is that it gets much faster the more the elements are in add/remove. The performance difference stems wholly from resizes and moves. For example an insertion at random indices of 1000 elements with an initial capacity of 10 runs at 2x the speed. Same scenario but for 1.5M elements and the BitArray runs 13x faster. But for already resized arrays and insertions at the tail, the difference is miniscule. The numbers mentioned are quite conservative for safety. Also, it can easily handle `INTEGER.MAX_VALUE` elements, but cannot hold more. 

# Getting Started
You will need the class and source files. You can grab the [latest release](https://github.com/Abductcows/java-bit-array/releases/latest) (built with jdk-11) or download the project and run `mvn install` yourself. Releases contain a zip file with separate jars for classes, sources and javadoc. Include at least the class jar in your project, and you will be able to use the BitArray. Looks like you are good to go.

### Versioning
The project uses [SemVer](https://semver.org/) for versioning.

### Contributions and future of this project
I would like to work on this project with anyone willing to contribute. My estimation of the rough priority of actions needed is:

- Testing: Improve tests to enhance confidence
- Optimizing: 'cause why not. Maybe override a few of the AbstractList's default implementations
- New features: Not sure if there is anything to add, suggestions very welcome

I would also appreciate you sharing your opinion on this class and the project as a whole. If you want to contribute, check out [CONTRIBUTING.md](https://github.com/Abductcows/java-bit-array/blob/master/CONTRIBUTING.md) for more info.

### License
This Project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

### Authors
- *George Bouroutzoglou* (geompokon@csd.auth.gr)


[open-issues-url]: https://github.com/Abductcows/java-bit-array/issues
[open-issues-shield]: https://img.shields.io/github/issues/abductcows/java-bit-array
[contributors-url]: https://github.com/Abductcows/java-bit-array/graphs/contributors
[contributors-shield]: https://img.shields.io/github/contributors/abductcows/java-bit-array
[latest-release-shield]: https://img.shields.io/github/v/release/abductcows/java-bit-array?sort=semver
[latest-release-url]: https://github.com/Abductcows/java-bit-array/releases/latest
