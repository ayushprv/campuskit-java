param([string]$DataDirectory = 'data')
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
javac --release 17 -d build '@sources.txt'
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp build campuskit.Main $DataDirectory
exit $LASTEXITCODE
