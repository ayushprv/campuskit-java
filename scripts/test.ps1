$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
javac --release 17 -d build '@sources.txt' tests/campuskit/ProjectTests.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp build campuskit.ProjectTests
exit $LASTEXITCODE
