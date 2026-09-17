# CampusKit

A command-line equipment lending manager for **Programming in Java**. Prepared for Ayush Yadav (25BAI10946).

CampusKit helps a campus club track shared equipment, members, loans, returns and overdue items. It uses ten Java source files, immutable records, collections, an interface-based persistence layer, exception handling, Java NIO file operations and the Java time API.

## Features
- Register equipment types and quantities; show live availability.
- Register and list members with generated numeric IDs.
- Borrow for 1-30 days, with a three-active-loan limit per member.
- Block borrowing when stock is exhausted or the member has an overdue item.
- Return equipment and retain historical loans.
- Report total/available units and overdue items with days late.
- Save every successful change and validate saved records on restart.

## Environment and dependencies
Install a **JDK 17 or newer**, not just a JRE. Set `JAVA_HOME` to your JDK directory if required by your installation, and add its `bin` directory to `PATH`. Open a new terminal and verify:

```text
java -version
javac -version
```

Both commands must be available and report version 17 or newer. No Maven, Gradle, database, GUI setup, API keys, packages or network connection is required to build and run. Git is only needed to clone or publish the repository. Downloading and extracting the repository ZIP also works.

## Build and run (all platforms)
Open a terminal in the repository root (the directory containing this README and `sources.txt`). In PowerShell use the quoted argument shown below; quoting also works in other common shells:

```text
javac --release 17 -d build "@sources.txt"
java -cp build campuskit.Main
```

Alternatively, on Windows: `powershell -ExecutionPolicy Bypass -File scripts/run.ps1`. On Linux/macOS: `sh scripts/run.sh`.

## Configuration and storage
The default data directory is `data` relative to the working directory. To choose another location:

```text
java -cp build campuskit.Main my-club-data
```

The application creates the directory automatically. It needs write permission and a local filesystem supporting atomic file replacement. Data is stored in `campuskit.tsv`; `.lock` prevents concurrent CLI sessions. Names are Base64 encoded for delimiter safety, **not encrypted**. Use fictitious members for demonstrations. Back up the data directory while the program is closed. If loading fails, the program stops without replacing the file; restore a known-good backup. Do not manually edit live data. Data and compiled files are excluded from Git.

## First session
1. Choose `1`; enter `Scientific Calculator`, then quantity `2`. Note equipment ID `1`.
2. Choose `3`; enter `Demo Student`. Note member ID `1`.
3. Choose `5`; enter equipment `1`, member `1`, duration `7`. Note loan ID `1`.
4. Choose `2` to see `1 / 2` available and `8` to see the summary.
5. Choose `6`; enter loan `1`. The equipment becomes available again.
6. Choose `7` to inspect retained history. Choose `0` to exit.
7. Restart with the same directory to verify persistence.

IDs above assume an empty directory. Menu options are displayed at every step. Names must have 1-100 characters and no control characters. A loan is overdue beginning the day after its due date, using the computer's local calendar date.

## Run tests
From the repository root:

```text
javac --release 17 -d build "@sources.txt" tests/campuskit/ProjectTests.java
java -cp build campuskit.ProjectTests
```

Or use `powershell -ExecutionPolicy Bypass -File scripts/test.ps1` / `sh scripts/test.sh`. Tests require no JUnit dependency and throw an assertion failure on a failed check. They use a fixed clock and a temporary directory that is removed afterward. Coverage includes stock limits, member limits, overdue boundaries, invalid input, returns, persistence, corrupt data and simulated save failure. Actual captured results are in [docs/test-results.txt](docs/test-results.txt).

For a repeatable CLI demo on PowerShell (use a new directory for each run):

```powershell
Get-Content docs/demo-input.txt | java -cp build campuskit.Main demo-data
```

On Linux/macOS: `java -cp build campuskit.Main demo-data < docs/demo-input.txt`. See [captured output](docs/demo-output.txt).

## Project layout
```text
src/campuskit/      Ten application classes/records/interfaces
tests/campuskit/    Dependency-free regression checks
scripts/           Windows and POSIX launchers; report builder
docs/              Design, sample input, actual test and demo results
output/pdf/        Project report
sources.txt        Portable javac source-file list
statement.md       Problem, scope, requirements and acceptance criteria
```

See [design and storage documentation](docs/design.md) and [report](output/pdf/CampusKit-Project-Report.pdf).

## Limitations
Single-operator local application; no login or encrypted storage. No equipment editing/deletion, serial-number tracking, reservations or fines. All records are loaded into memory and a complete snapshot is saved on each mutation. Storage must support atomic move; otherwise the operation fails safely. Tests were run on Windows using JDK 26 with `--release 17`; Linux/macOS and a Java 17 runtime were not directly tested.

## Authorship and submission
This project and documentation were generated with AI assistance. Review, understand, test and personalize the work, and follow your course's rules on permitted assistance; do not represent generated work as entirely unaided work. The report does not claim personal experiences that have not been supplied.

Repository root URL: **https://github.com/ayushprv/campuskit-java**. Submit this URL and upload the PDF report separately. Do not submit a `/tree/` or `/blob/` link. Verify public access before final submission.

## Rebuilding the report (optional)
The application does not need Python. To regenerate the documentation PDF only, install Python 3 and run `python -m pip install reportlab`, followed by `python scripts/build_report.py` from the repository root. The builder reads the captured test and demo output under `docs/`.
