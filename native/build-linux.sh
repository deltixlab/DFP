#!/bin/bash
# Need 4 arguments:
# Project name(overrides the one specified in the makefile), Build configuration (Debug/Release),
# Architecture word size (32/64), Full arch name for ICC (ia32/intel64)
source /opt/intel/bin/compilervars.sh $4 -platform linux
rm -rf ./obj/*
make ProjectName=$1 Configuration=$2 Architecture=$3 Build

