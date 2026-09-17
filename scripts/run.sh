#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
javac --release 17 -d build @sources.txt
java -cp build campuskit.Main "${1:-data}"
