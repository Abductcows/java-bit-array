# Java BitArray

[![Latest release][latest-release-shield]][latest-release-url]&nbsp;&nbsp;
[![Open Issues][open-issues-shield]][open-issues-url]&nbsp;&nbsp;
[![Contributors][contributors-shield]][contributors-url]

### Motivation
This class is a replacement for the `ArrayList<Boolean>` type when working with its `List` interface. It boasts higher performance in add, remove and set operations and requires less memory for storing the same elements. 

The BitArray is by all means an array; it is random access and all elements have contiguous indices at all times. Its behaviour is meant to be indistinguishable from ArrayList in order for programmers to be able to substitute it for the latter and benefit from its performance advantage. 

### Few details
Internally the array stores the boolean elements in an array of long primitives. These long primitives essentially form a sequence of bits; their chained bit representation in 2's complement. Boolean elements are converted to and from their bit equivalent to perform insertions, deletions etc. With the appropriate bitwise operations new elements can be added at a specific index and elements already in the array can be shifted to preserve the previous order. Thanks to that hack, element shifting and array resizing is much cheaper, all while the elements themselves occupy less space in memory.

### Performance
With regard to the difference in performance, I have a [temporary benchmark](https://github.com/Abductcows/java-bit-array/blob/dev/src/test/java/gr/geompokon/bitarray/BitArrayVsArrayListBenchmarkTest.java) file for you to test. I am looking into creating a more trustworthy benchmark using a benchmark framework like [JMH](https://github.com/openjdk/jmh) in order to be able to publish some results with confidence. If you have experience doing that and want to contribute, feel free to start an [issue](https://github.com/Abductcows/java-bit-array/issues).

### Disclaimer
As you can tell from the project version and creation date this class is very new and not battle-tested. As such I would discourage you from using it in a serious application just yet.

# Getting Started
You will need the class and source files. You can grab the [latest release](https://github.com/Abductcows/java-bit-array/releases/latest) (built for jdk-11) or download the project and run `mvn package/install` yourself. Releases contain a zip file with separate jars for classes, sources and javadoc. Include at least the class jar in your project and you will be able to use the BitArray. Looks like you are good to go.

### Versioning
The project uses [SemVer](https://semver.org/) for versioning.

### Contributions and future of this project
I would like to work on this project with anyone willing to contribute. My estimation of the rough priority of actions needed is:

- Testing/debugging: Write better and well documented tests to enhance confidence
- Benchmarking: Give ArrayList a run for their money
- Optimizing: 'cause why not. Maybe override a few of the AbstractList's default implementations
- New features: Not sure what to add, suggestions very welcome

If you want to contribute, check out [CONTRIBUTING.md](https://github.com/Abductcows/java-bit-array/blob/master/CONTRIBUTING.md) for more info.

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
