#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p build/core-tests
javac -d build/core-tests app/src/main/java/dev/engram/daily/Planner.java tests/PlannerTest.java
java -cp build/core-tests dev.engram.daily.PlannerTest
