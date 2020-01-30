# Decimal Floating Point Arithmetic for Java/.NET

## Description/Usage
### *TODO*


## Building from source
Note that you need to build native libraries for your target platform(s) before you can build platform-agnostic libraries for Java / .NET.
\
Both .NET and Java versions use same set of native dynamic libraries that has to be built for at least one of the supported platforms and architectures before trying to build C#/Java libraries.
\
If you want to be able to target Windows, Linux, Mac OS simultaneously, you must build the native libraries on all 3 platforms before building Java / .NET libraries that wrap them together. This is typically done with a CI script that is currently not present in this release.
\
Native libraries can be built for a single OS only but then the resulting Java / .NET libraries will only work with that OS as well.
\
You may build the Java/.NET libraries on a different platform than the one that was used to build the native libraries.


### Requirements
+ Intel C compiler for one of the supported platforms at least (Windows, Linux, Mac OS). Needed to build intel-provided DFP library.
+ zstandard command line compressor, though zstd compression is optional
+ Java version: OpenJDK 8+ or Oracle JDK 8+, uses Gradle V5.6.4
+ .NET Version: .NET Framework 4.0+ or .NET Core or Mono runtime supporting .NET Standard 2.0, uses Cake build tool
+ Invoking Cake on non-Windows systems may require Mono runtime.

### Fetch the source code
```
git clone --recurse-submodules https://github.com/deltixlab/DFP
cd DFP
```
or, after checkout: `git submodule update --init --recursive` 

Note that the current open source release uses (https://github.com/deltixlab/NativeUtils) as a submodule for building both versions of the library(Java and C#).

### Building native resources
Arch/OS-specific native libraries are compressed with ZStandard compressor, though the build scripts can be modified to not use compression. 

The build tasks for native libraries reside in the common Cake script in `csharp` directory for convenience. It is possible to build them without Cake, ZStd, or change the source code to work without Intel C compiler, but this is left as an exercise for the reader.
#### Windows
From `/csharp` subdirectory:
```
powershell -file build.ps1 --target BuildAndCompressNativeWindowsLibs
```
#### Linux/Mac OS
From `/csharp` subdirectory:
```
sh ./build.sh --target BuildAndCompressNativeLinuxLibs
```

### Building Java library
After building and compressing native libraries as described above, from project root directory:
```
./gradlew shadowJar
```
The build product (standalone JAR library) will appear in `/java/build/libs`

`test` task will run unit tests, `jmh` task will run some benchmarks. 


### Building .NET library
After building and compressing native libraries as described above, from `csharp` directory, assuming Windows OS:
```
powershell -file build.ps1
```
Versions for .NET4.0 & .NET Standard 2.0 will appear in `/csharp/Deltix.DFP/bin/Release`

## License
This library is released under Apache 2.0 license. See ([license](LICENSE))
