cmake_minimum_required (VERSION 2.8.12)
project (native)

if (TARGET)
   set(CMAKE_C_FLAGS "--target=${TARGET} ${TARGET_EX} --sysroot=${SYSROOT} -I${SYSROOT}/include/c++/v1 -I/usr/${TARGET}/include")
endif()

#set(CMAKE_C_FLAGS "--target=arm-linux-gnueabihf -march=armv7a -mfloat-abi=hard \
#                   --sysroot=/home/user/llvm/clang+llvm-12.0.0-armv7a-linux-gnueabihf \
#                   -I/home/user/llvm/clang+llvm-12.0.0-armv7a-linux-gnueabihf/include/c++/v1 \
#                   -I/usr/arm-linux-gnueabihf/include")

#set(CMAKE_C_FLAGS "--target=aarch64-linux-gnu \
#                   --sysroot=/home/user/llvm/clang+llvm-12.0.0-aarch64-linux-gnu \
#                   -I/home/user/llvm/clang+llvm-12.0.0-aarch64-linux-gnu/include/c++/v1 \
#                   -I/usr/aarch64-linux-gnu/include")

#set(CMAKE_C_FLAGS "--target=i686-linux-gnu \
#                   --sysroot=/home/user/llvm/clang+llvm-12.0.0-i386-unknown-freebsd12 \
#                   -I/home/user/llvm/clang+llvm-12.0.0-i386-unknown-freebsd12/include/c++/v1 \
#                   -I/usr/i686-linux-gnu/include")

set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS}")

message(STATUS "CMAKE_SYSTEM_PROCESSOR: " ${CMAKE_SYSTEM_PROCESSOR})
message(STATUS "CMAKE_C_COMPILER_TARGET: " ${CMAKE_C_COMPILER_TARGET})
message(STATUS "CMAKE_CXX_COMPILER_TARGET: " ${CMAKE_CXX_COMPILER_TARGET})
message(STATUS "CMAKE_CROSSCOMPILING: " ${CMAKE_CROSSCOMPILING})
message(STATUS "CMAKE_C_FLAGS: " ${CMAKE_C_FLAGS})
message(STATUS "CMAKE_CXX_FLAGS: " ${CMAKE_CXX_FLAGS})

set(CALL_BY_REF "0" CACHE STRING "The numerical arguments and results are passed by reference")
set(GLOBAL_RND "1" CACHE STRING "The rounding mode is a global variable _IDEC_glbround")
set(GLOBAL_FLAGS "1" CACHE STRING "The exception status flags are represented by a global variable _IDEC_glbflags")

if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE Release)
endif()

if(UNIX)
	add_definitions(-DLINUX)
	add_definitions(-Dlinux)
elseif(WIN32)
	add_definitions(-DWINDOWS)
	add_definitions(-Dwinnt)
	add_definitions(-MT)
elseif(APPLE)
	add_definitions(-DAPPLE)
endif()
add_definitions(-DMINIMAL_SILENT_MODE_EXCEPTION_HANDLER)
add_definitions(-DUSE_COMPILER_F128_TYPE=0)
add_definitions(-DUSE_COMPILER_F80_TYPE=0)
add_definitions(-DUSE_NATIVE_QUAD_TYPE=0)
add_definitions(-Defi2)
add_definitions(-DDECIMAL_CALL_BY_REFERENCE=${CALL_BY_REF})
add_definitions(-DDECIMAL_GLOBAL_ROUNDING=${GLOBAL_RND})
add_definitions(-DDECIMAL_GLOBAL_EXCEPTION_FLAGS=${GLOBAL_FLAGS})

#set(CMAKE_C_VISIBILITY_PRESET default)
#set(CMAKE_CXX_VISIBILITY_PRESET default)
#set(CMAKE_VISIBILITY_INLINES_HIDDEN 0)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_subdirectory (../thirdparty/IntelRDFPMathLib20U2/LIBRARY LIBRARY)
add_subdirectory (../thirdparty/IntelRDFPMathLib20U2/EXAMPLES EXAMPLES)

add_definitions(-DAPI_PREFIX=${API_PREFIX}_)
add_library(dfp SHARED NativeImpl.c)
target_link_libraries (dfp LINK_PUBLIC bid)
set_target_properties(dfp PROPERTIES OUTPUT_NAME ${API_PREFIX})

install(TARGETS dfp DESTINATION ./)
