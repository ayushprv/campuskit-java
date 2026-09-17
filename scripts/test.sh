#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
javac --release 17 -d build @sources.txt tests/campuskit/ProjectTests.java
java -cp build campuskit.ProjectTests
