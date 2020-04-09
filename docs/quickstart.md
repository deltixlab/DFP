# Overview

"...it is a bad idea to use floating point to try to represent exact quantities like monetary amounts. Using floating point for dollars-and-cents calculations is a recipe for disaster. Floating point numbers are best reserved for values such as measurements, whose values are fundamentally inexact to begin with." -- [Brian Goetz](https://www.ibm.com/developerworks/library/j-jtp0114/index.html)

Java lacks built-in type to represent Money or Quantity properties frequently used in financial domain.  

Ideal data type for this purpose:

* Use base-10 (rather than base-2) to accurately represent monetary values 
* Support wide range of values (ranging from hundred of billions of that represent portfolio values to fractions of 6 to 8 below decimal point to represent smallest tick sizes)
* Allow GC-free arithmetic (Garbage Collection pauses are evil in low-latency systems). This most likely implies using *primitive* data types.
* fast (as fast as non-builtin numeric data type could be)
* Support efficient conversion to String and double

# Quick Start

DFP uses Java long to represent base-10 floating point numbers. DFP is based on [IEEE-754 standard](https://en.wikipedia.org/wiki/IEEE_754).

Here is a very short example of DFP:

```java
import deltix.dfp.Decimal64Utils;

@Decimal long commission = Decimal64Utils.fromFixedPoint(8, 4) // 8 basis points, 0.0008
@Decimal long price = Decimal64Utils.fromDouble (123.45)
@Decimal long adjustedPrice = Decimal64Utils.add (price, Decimal64Utils.multiply (price, commission));
```

This may look a bit bulky, but if you are familiar with similar libraries like Joda Money, or BigDecimal, you will find it reasonable. 

Key points:

* Everything related to DFP is provided by helper class Decimal64Utils.
* Use 'fromXXX' factory methods to construct DFP number. For example
  * fromDouble
  * fromLong
  * fromFixedPoint
  * parse(String)
* There is a helper method for every possible arithmetic operation.
* Special `@Decimal` annotation should be used to mark long values that represent DFP values.

Another example. This time we illustrate conversion from string and back:

```java
@Decimal long parseQuantity (String quantityText) {
   try {
      @Decimal long result = Decimal64Util.parse(quantityText);
      System.out.println("Processed quantity: " + Decimal64Util.toString (result));
      // for lbraries that have built-in support for DFP:
      // logger.info("Processed quantity %s).withDecimal(result);
   } catch (NumberFormatException e) {
      throw new IllegalArgumentException ("Bad quantity: \" + quantityText + '"');
   }  
   return result; 
}
```

# Decimal vs. lava.lang.double vs. java.lang.BigDecimal

```java
double d1 = 0.3;
double d2 = 0.2;
System.out.println("Double: 0,3 - 0,2 = " + (d1 - d2)); // Prints: Double: 0,3 - 0,2 = 0.09999999999999998 <== PROBLEM!

float f1 = 0.3f;
float f2 = 0.2f;
System.out.println("Float: 0,3 - 0,2 = " + (f1 - f2)); // Prints: Float: 0,3 - 0,2 = 0.10000001 <== PROBLEM!

BigDecimal bd1 = new BigDecimal("0.3");
BigDecimal bd2 = new BigDecimal("0.2");
System.out.println("BigDecimal: 0,3 - 0,2 = " + (bd1.subtract(bd2))); // Prints: BigDecimal: 0,3 - 0,2 = 0.1

@Decimal long b1 = Decimal64Utils.parse("0.3");
@Decimal long b2 = Decimal64Utils.parse("0.2");
@Decimal long b3 = Decimal64Utils.subtract(b1, b2);
System.out.println("Decimal64: 0,3 - 0,2 = " + (Decimal64Utils.toString(b3))); // Prints: Decimal64: 0,3 - 0,2 = 0.1
```
# Decimal64 Value Type

For people who prefer strong type safety and want to avoid mixing `long` values with `@Decimal long` Deltix offers Decimal64 value type. 
General idea is that DFP values are represented by immutable instances of class Decimal64. Special runtime agent converts usages of Decimal64 to primitive `long` using nifty bytecode modification technique. 

Decimal64 gives you both type safe Decimal operations and runtime effectiveness of primitive data type.


With Decimal64 the previous example can be written as:

```java
Decimal64 c1 = Decimal64.parse("0.3");
Decimal64 c2 = Decimal64.parse("0.2");
System.out.println("Decimal64 obj: 0,3 - 0,2 = " + (c1.subtract(c2))); // Prints: Decimal64 obj: 0,3 - 0,2 = 0.1

```
See [ValueTypes4Java](https://github.com/deltixlab/ValueTypes4Java/blob/master/docs/INSTALL.md) for more information.


# NaN, infinity

DFP concept of Not-a-Number (NaN) and infinity is somewhat similar to Java double's (and dictated by IEEE 754 spec).

* You must use `isNaN()` to check for NaN. Never check for NaN using comparison operator `==`.
* Similarly use `isPositiveInfinity()` and `isNegativeInfinity` functions for infinity checks.
* One of NaN values is distinguished as decimal NULL.


(Common pitfalls)[pitfalls.md]
