#!/usr/bin/env bash
set -e

COMPILER=${3:-clang}

mkdir build$2
cd build$2
cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_C_COMPILER=$COMPILER -DCMAKE_CXX_COMPILER=$COMPILER ../
make
mkdir -p ../bin/Release/Linux/$2
cp ./*.so ../bin/Release/Linux/$2
cd ..
