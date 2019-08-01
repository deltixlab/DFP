# Decimal Floating Point Arithmetic for Java/.NET

## Description/Usage
### *TODO*


## Building from source
Note that you need to build native libraries for your target platform(s) before you can build platform-agnostic libraries for Java / .NET.
\
If you want to be able to target Windows, Linux, Mac OS simultaneously, you must build the native libraries on all 3 platforms before building Java / .NET libraries that wrap them together. This is typically done with a CI script that is currently not present in this release.
\
Native libraries can be built for a single OS only but then the resulting Java / .NET libraries will only work on that OS as well..
\
You may build the Java/.NET libraries on a different platform than the one that was used to build native libraries.

### Requirements
+ Intel C compiler at for one of the supported platforms at least (Windows, Linux, Mac OS).
+ zstandard command line compressor
+ Java version: OpenJDK 8+ or Oracle JDK 8+, Gradle build tool
+ .NET Version: .NET Framework 4.0+ or .NET Core or Mono runtime supporting .NET Standard 2.0, Cake build tool
+ Invoking Cake on non-Windows systems may require Mono runtime.

### Fetch the source code
```
git clone --recurse-submodules https://github.com/deltixlab/DFP
cd DFP
```
Note that the current open source release uses (https://github.com/deltixlab/NativeUtils) as a submodule for building both versions of the library(Java and C#).

### Building native resources
The build tasks for native libraries reside in the common Cake script in `csharp` directory for convenience. It is possible to build them without Cake, or change the source code to work without Intel C compiler, but this is left as an exercise for the reader.
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

### Building .NET library
After building and compressing native libraries as described above, from `csharp` directory, assuming Windows OS:
```
powershell -file build.ps1
```
Versions for .NET4.0 & .NET Standard 2.0 will appear in `/csharp/Deltix.DFP/bin/Release`

## License
This library is released under Apache 2.0 license. See ([license](LICENSE))
