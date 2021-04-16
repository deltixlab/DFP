#!/usr/bin/env bash
set -e

mkdir buildx86_64
cd buildx86_64
cmake -G "Unix Makefiles" -DAPI_PREFIX=$1 -DCMAKE_C_COMPILER=clang -DCMAKE_CXX_COMPILER=clang ../
make
mkdir ../bin/Release/Linux/x86_64
cp ./*.so ./bin/Release/Linux/x86_64
cd ..

