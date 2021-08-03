# Decimal Floating Point Arithmetic for Java/.NET


"...it is a bad idea to use floating point to try to represent exact quantities like monetary amounts. Using floating point for dollars-and-cents calculations is a recipe for disaster. Floating point numbers are best reserved for values such as measurements, whose values are fundamentally inexact to begin with." -- [Brian Goetz](https://www.ibm.com/developerworks/library/j-jtp0114/index.html)

Java lacks built-in type to represent Money or Quantity properties frequently used in financial domain.  

Ideal data type for this purpose:

* Use base-10 (rather than base-2) to accurately represent monetary values 
* Support wide range of values (ranging from hundred of billions of that represent portfolio values to fractions of 6 to 8 below decimal point to represent smallest tick sizes)
* Allow GC-free arithmetic (Garbage Collection pauses are evil in low-latency systems). This most likely implies using *primitive* data types.
* fast (as fast as non-builtin numeric data type could be)
* Support efficient conversion to String and double


DFP uses Java long to represent base-10 floating point numbers. DFP is based on [IEEE-754 standard](https://en.wikipedia.org/wiki/IEEE_754).

## Description/Usage

* [Quick Start Guide](docs/quickstart.md)
* [Tips and Trick](docs/TipsNTricks.md)
* [FAQ](docs/FAQ.md) 



## Building

[How to build this project](docs/build.md)

## Credits

This project was developed by **Vitali Haravy**. Special thanks to **Boris Chuprin** for helping with native code and testing of this library

## License
This library is released under Apache 2.0 license. See ([license](LICENSE))
