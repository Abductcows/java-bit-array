# BitArray
[![Maven Central](https://img.shields.io/maven-central/v/io.github.abductcows/bit-array.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.abductcows%22%20AND%20a:%22bit-array%22)&nbsp;&nbsp;
[![Contributors][contributors-shield]][contributors-url]&nbsp;&nbsp;
[![Total alerts](https://img.shields.io/lgtm/alerts/g/Abductcows/java-bit-array.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Abductcows/java-bit-array/alerts/)&nbsp;&nbsp;
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Abductcows/java-bit-array.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Abductcows/java-bit-array/context:java)

### The cooler ArrayList\<Boolean\>
- [Documentation](https://abductcows.github.io/java-bit-array/gr/geompokon/bitarray/BitArray.html) 
- [Release](#getting-started)
- [Benchmarks](https://github.com/Abductcows/bit-array-benchmarks)

### Motivation
This class was conceived as a replacement for the `ArrayList<Boolean>` type, when working with one of its interfaces. It boasts higher performance in CRUD operations and requires less memory to store the same elements. As its own type, it introduces new specialized methods that leverage its implementation to deliver more performance. 

### Caveats
- **No nulls:** The internal representation of the elements, namely the bits, have only 2 states, so null values cannot be accommodated. Null values will throw a NullPointerException. 
- **No references to the inserted objects are retained:** This should not concern you if you are using Java ≥ 9. But if for some twisted reason you are using new Boolean objects and == comparison, the Boolean you just put in could return as a different object. If so, this class is not for you.

### Performance
For the performance difference, check out the [benchmark repository](https://github.com/Abductcows/bit-array-benchmarks). It includes results from my runs and the benchmark files should you want to run them yourself. A general rule is that the performance margin widens as elements increase. Additionally, operations which move a lot of elements such as `add()/remove()` at the head scale even better than those which don't, such as `get()/set()`. Also, it can easily handle `INTEGER.MAX_VALUE` elements, but cannot hold more. 

### Thread safety / synchronization
BitArray, being a List, is not inherently thread safe. It is also not synchronized, as it would harm performance. Synchronized access can be achieved as usual with [`Collections.synchronizedList(bitArray)`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#synchronizedList(java.util.List))

### Few implementation details
Internally the array stores the boolean elements in an array of `long` primitives. These primitives essentially form a sequence of bits; their chained bit representation in 2's complement. Boolean elements are converted to and from their bit equivalent (i.e. `false ⟷ 0` and `true ⟷ 1`) to perform insertions, deletions etc. With the appropriate bitwise operations new elements can be added at a specific index and elements already in the array can be shifted to preserve the previous order. Thanks to that, element shifting and array resizing is much cheaper, all while the elements themselves occupy less space in memory.

# Getting Started
You can grab the class from Maven or download the files yourself from the latest [release](https://github.com/Abductcows/java-bit-array/releases/latest).

### Maven
```xml
<dependency>
    <groupId>io.github.abductcows</groupId>
    <artifactId>bit-array</artifactId>
    <version>2.0.0</version>
</dependency>
```

# Project info

### Versioning
The project uses [SemVer 2.0.0](https://semver.org/) for versioning.

### License
This Project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

### Contributions and future of this project
I would love to work on this project with anyone willing to contribute. My estimation of the remaining actions is  

- Optimizing: 'cause why not. Maybe override a few of the AbstractList's default implementations
- Add new methods, but not too many

If you want to contribute, check out [CONTRIBUTING.md](CONTRIBUTING.md) for more info.

[lgtm-alerts-url]: https://lgtm.com/projects/g/Abductcows/java-bit-array/?mode=list
[lgtm-alerts-shield]: https://img.shields.io/lgtm/alerts/github/abductcows/java-bit-array
[lgtm-quality-url]: https://lgtm.com/projects/g/Abductcows/java-bit-array/?mode=list
[lgtm-quality-shield]: https://img.shields.io/lgtm/grade/java/github/abductcows/java-bit-array
[contributors-url]: https://github.com/Abductcows/java-bit-array/graphs/contributors
[contributors-shield]: https://img.shields.io/github/contributors/abductcows/java-bit-array
