cmake_minimum_required (VERSION 2.8.11)
project (DFPMathLibrary)

set(CALL_BY_REF "0" CACHE STRING "The numerical arguments and results are passed by reference")
set(GLOBAL_RND "1" CACHE STRING "The rounding mode is a global variable _IDEC_glbround")
set(GLOBAL_FLAGS "1" CACHE STRING "The exception status flags are represented by a global variable _IDEC_glbflags")

add_definitions(-DDECIMAL_CALL_BY_REFERENCE=${CALL_BY_REF})
add_definitions(-DDECIMAL_GLOBAL_ROUNDING=${GLOBAL_RND})
add_definitions(-DDECIMAL_GLOBAL_EXCEPTION_FLAGS=${GLOBAL_FLAGS})

add_subdirectory (LIBRARY)
# add_subdirectory (Demo)
