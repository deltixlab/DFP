#!/usr/bin/env bash
set -ex

CCOMPILER=${2:-clang}
CXXCOMPILER=${2:-clang++}
VERBOSE=${3:-ON}

rm -rf ./build
mkdir build
cd build

$CCOMPILER --version

cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_VERBOSE_MAKEFILE=$VERBOSE -DCMAKE_C_COMPILER=$CCOMPILER -DCMAKE_CXX_COMPILER=$CXXCOMPILER -DTARGET=arm-linux-gnueabihf -DTARGET_EX='-march=armv7a -mfloat-abi=hard' -DSYSROOT=../../../llvm/clang+llvm-12.0.0-armv7a-linux-gnueabihf -DCMAKE_INSTALL_PREFIX=../bin/Release/Linux/arm ../
make install
mkdir -p ../binDemo/Release/Linux/arm
cp ./EXAMPLES/demo ../binDemo/Release/Linux/arm

rm -rf ./*
cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_VERBOSE_MAKEFILE=$VERBOSE -DCMAKE_C_COMPILER=$CCOMPILER -DCMAKE_CXX_COMPILER=$CXXCOMPILER -DTARGET=aarch64-linux-gnu -DSYSROOT=../../../llvm/clang+llvm-12.0.0-aarch64-linux-gnu -DCMAKE_INSTALL_PREFIX=../bin/Release/Linux/aarch64 ../
make install
mkdir -p ../bin/Release/Linux/aarch64
cp ./EXAMPLES/demo ../bin/Release/Linux/aarch64

rm -rf ./*
cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_VERBOSE_MAKEFILE=$VERBOSE -DCMAKE_C_COMPILER=$CCOMPILER -DCMAKE_CXX_COMPILER=$CXXCOMPILER -DTARGET=i686-linux-gnu -DSYSROOT=../../../llvm/clang+llvm-12.0.0-i386-unknown-freebsd12 -DCMAKE_INSTALL_PREFIX=../bin/Release/Linux/i386 ../
make install
mkdir -p ../bin/Release/Linux/i386
cp ./EXAMPLES/demo ../bin/Release/Linux/i386

rm -rf ./*
cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_VERBOSE_MAKEFILE=$VERBOSE -DCMAKE_C_COMPILER=$CCOMPILER -DCMAKE_CXX_COMPILER=$CXXCOMPILER -DCMAKE_INSTALL_PREFIX=../bin/Release/Linux/amd64 ../
make install
mkdir -p ../bin/Release/Linux/amd64
cp ./EXAMPLES/demo ../bin/Release/Linux/amd64

cd ..
rm -rf ./build
