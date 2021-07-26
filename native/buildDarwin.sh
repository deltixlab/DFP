#!/usr/bin/env bash
set -ex

CCOMPILER=${2:-clang}
CXXCOMPILER=${2:-clang++}
VERBOSE=${3:-ON}

rm -rf ./build
mkdir build
cd build

$CCOMPILER --version

cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_VERBOSE_MAKEFILE=$VERBOSE -DCMAKE_C_COMPILER=$CCOMPILER -DCMAKE_CXX_COMPILER=$CXXCOMPILER -DCMAKE_INSTALL_PREFIX=../bin/Release/Darwin/x86_64 ../
make install

cd ..
rm -rf ./build
