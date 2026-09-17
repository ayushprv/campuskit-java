"""Optional documentation tool: pip install reportlab; python scripts/build_report.py."""
from pathlib import Path
from xml.sax.saxutils import escape
from reportlab.pdfgen import canvas
from reportlab.lib.colors import HexColor, white
from reportlab.lib.styles import ParagraphStyle
from reportlab.platypus import Paragraph
import math

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'output/pdf/CampusKit-Project-Report.pdf'
OUT.parent.mkdir(parents=True, exist_ok=True)
c = canvas.Canvas(str(OUT), pagesize=(595,842))
c.setTitle('CampusKit - Programming in Java Project Report')
c.setAuthor('Ayush Yadav | 25BAI10946')
navy=HexColor('#123047'); teal=HexColor('#087e8b'); pale=HexColor('#edf5f7')
style=ParagraphStyle('body',fontName='Helvetica',fontSize=10.5,leading=16,textColor=navy)
page=0; y=740
def start(title):
    global page,y
    if page: c.showPage()
    page+=1; y=738
    c.setFillColor(navy); c.rect(0,784,595,58,fill=1,stroke=0)
    c.setFillColor(white); c.setFont('Helvetica-Bold',15); c.drawString(42,807,'CAMPUSKIT / PROJECT REPORT')
    c.setFillColor(navy); c.setFont('Helvetica-Bold',21); c.drawString(42,y,title); y-=34
    c.setFont('Helvetica',8); c.drawString(42,27,'Ayush Yadav | 25BAI10946 | Programming in Java')
    c.drawRightString(553,27,str(page))
def para(text):
    global y
    p=Paragraph(text,style); w,h=p.wrap(511,700)
    if y-h<55: raise ValueError('Page overflow: '+text[:60])
    p.drawOn(c,42,y-h); y-=h+12
def head(text):
    global y
    c.setFillColor(teal); c.setFont('Helvetica-Bold',12); c.drawString(42,y,text); y-=23
def box(x,y,w,h,text):
    c.setFillColor(pale); c.setStrokeColor(teal); c.roundRect(x,y,w,h,7,fill=1,stroke=1)
    lines=text.split('|'); c.setFillColor(navy)
    for i,line in enumerate(lines):
        c.setFont('Helvetica-Bold' if i==0 else 'Helvetica',9)
        c.drawCentredString(x+w/2,y+h/2+(len(lines)-1)*6-i*12-3,line)
def arrow(x1,y1,x2,y2,label=''):
    c.setStrokeColor(teal); c.setLineWidth(1); c.line(x1,y1,x2,y2)
    a=math.atan2(y2-y1,x2-x1)
    for d in (-.45,.45): c.line(x2,y2,x2-7*math.cos(a+d),y2-7*math.sin(a+d))
    if label:
        c.setFillColor(navy); c.setFont('Helvetica',8); c.drawCentredString((x1+x2)/2,(y1+y2)/2+6,label)

start('Campus equipment lending manager')
y=645
c.setFillColor(teal); c.setFont('Helvetica-Bold',40); c.drawString(42,y,'CampusKit'); y-=44
para('A terminal application for inventory, borrowers, circulation and overdue reporting.')
y-=25
head('Course and student')
para('<b>Course:</b> Programming in Java<br/><b>Name:</b> Ayush Yadav<br/><b>Registration number:</b> 25BAI10946<br/><b>Report date:</b> 18 September 2026')
head('Project summary')
para('Ten Java application source files implement four functional modules with local file persistence. The application requires only JDK 17 or newer and runs without a graphical setup or external Java dependencies.')
head('Authorship and review note')
para('The implementation and this report were prepared with AI assistance. The student should review, understand and personalize the work and ensure that its use follows the course policy. This report documents implemented behavior and observed checks; it does not assert unaided authorship or invented personal learning experiences.')
head('Contents')
para('2 Requirements and scope<br/>3 Architecture and use cases<br/>4 Workflow and sequence<br/>5 Class and storage design<br/>6 Implementation and decisions<br/>7 Execution results and testing<br/>8 Challenges, learning topics and references')

start('Requirements and scope')
head('1. Introduction and problem statement')
para('Campus clubs and laboratory desks share calculators, cameras and similar equipment. A paper register can make availability and overdue returns difficult to track. CampusKit records each loan against a member and equipment type and derives current availability from active loans.')
head('2. Objectives and target users')
para('Provide a reliable, understandable terminal workflow for lending-desk operators. Preserve records across restarts, reject invalid borrowing requests and identify late returns. Borrowers are registered members; they do not log in to the application.')
head('3. Functional requirements')
para('<b>Inventory:</b> register named equipment types with positive quantities and list availability.<br/><b>Membership:</b> register and list members using generated IDs.<br/><b>Circulation:</b> issue loans for 1-30 days, allow at most three active loans per member, reject overdue borrowers, record returns and retain history.<br/><b>Reporting:</b> show members, equipment types, total/available units, active loans and overdue loans with days late.')
head('4. Non-functional requirements')
para('<b>Portability:</b> Java 17 target and standard library only.<br/><b>Reliability:</b> atomic snapshot replacement; failed writes leave live state unchanged.<br/><b>Integrity:</b> validate IDs, dates, references and stock on load.<br/><b>Usability:</b> numbered menus, validation messages and safe end-of-input handling.<br/><b>Maintainability:</b> distinct UI, service, domain and repository responsibilities.<br/><b>Concurrency:</b> one process per data directory using a file lock.')
head('5. Boundaries')
para('Intended for small local registers. No login, payments, fines, reservations, network server, deletion or serial-number tracking is implemented. Quantities represent interchangeable units. Performance at large scale has not been benchmarked.')

start('Architecture and use cases')
head('6. Layered architecture')
box(42,605,130,55,'ConsoleApp|Prompts and output'); box(232,605,130,55,'LendingService|Rules and state'); box(422,605,130,55,'FileRepository|TSV persistence')
arrow(172,632,232,632); arrow(362,632,422,632)
box(232,510,130,55,'State|Equipment / Member / Loan'); arrow(297,605,297,565)
box(42,510,130,55,'Reports|Read-only summaries'); arrow(107,605,107,565)
y=480
para('Main creates the data directory and obtains an exclusive lock before loading data. LendingService depends on the Repository interface; FileRepository implements the persistence contract. Reports consumes a copied state and a date.')
head('7. Use case diagram')
box(42,268,120,60,'Desk operator')
for by,txt in [(385,'Register / list equipment'),(315,'Register / list members'),(245,'Issue / return equipment'),(175,'History / overdue reports')]:
    c.setStrokeColor(teal); c.setFillColor(pale); c.ellipse(265,by,550,by+45,fill=1,stroke=1)
    c.setFillColor(navy); c.setFont('Helvetica',10); c.drawCentredString(407,by+18,txt)
    arrow(162,298,265,by+22)
y=140
para('The operator performs all use cases on behalf of members. Stock and borrower validation are included in the issue-equipment workflow.')

start('Workflow and sequence')
head('8. Request workflow')
for x,by,w,txt in [(42,625,145,'Lock and load'),(222,625,145,'Read menu / input'),(402,625,150,'Validate request'),(402,535,150,'Copy and mutate'),(222,535,145,'Atomic save'),(42,535,145,'Publish / confirm')]: box(x,by,w,45,txt)
arrow(187,647,222,647); arrow(367,647,402,647); arrow(477,625,477,580)
arrow(402,557,367,557); arrow(222,557,187,557)
y=507
para('Validation errors return to the menu. A save failure reports an error and retains the old state. Read-only requests display output without saving. Exit or end-of-input releases the lock. Startup load failures stop execution without overwriting the data.')
head('9. Successful borrow sequence')
xs=[72,218,370,521]
for x,t in zip(xs,['Operator','ConsoleApp','LendingService','FileRepository']):
    c.setFillColor(navy); c.setFont('Helvetica-Bold',9); c.drawCentredString(x,400,t)
    c.setDash(3,3); c.setStrokeColor(teal); c.line(x,388,x,145); c.setDash()
for a,b,by,t in [(0,1,365,'Enter IDs and days'),(1,2,325,'borrow(...)'),(2,3,265,'save(nextState)'),(3,2,225,'Atomic save complete'),(2,1,185,'Publish state; return loan ID'),(1,0,150,'Confirmation')]: arrow(xs[a],by,xs[b],by,t)
c.setFillColor(navy); c.setFont('Helvetica',8); c.drawString(290,291,'Validate eligibility; copy state')
y=113
para('Failure path: validation or IOException propagates to the console; successful state publication occurs only after repository.save returns.')

start('Class and storage design')
head('10. Class / component diagram')
box(42,610,150,65,'ConsoleApp|run(), ask(), number()'); box(222,610,150,65,'LendingService|borrow(), returnLoan()|addEquipment(), addMember()')
arrow(192,642,222,642)
box(402,610,150,65,'Repository (interface)|load(), save(State)'); arrow(372,642,402,642)
box(402,510,150,55,'FileRepository|implements Repository'); arrow(477,565,477,610,'implements')
box(222,510,150,55,'State|Maps of immutable records'); arrow(297,610,297,565)
y=480
para('Equipment, Member and Loan are immutable Java records. State copies its maps while sharing these immutable values. Reports formats read-only summaries. Main wires dependencies and owns the lock lifecycle. Repository supports alternate persistence implementations and simulated failures in tests.')
head('11. Logical ER diagram')
box(42,298,145,100,'Equipment|id (PK)|name|quantity'); box(225,278,145,140,'Loan|id (PK)|equipmentId (FK)|memberId (FK)|borrowed / due|returned (nullable)'); box(407,298,145,100,'Member|id (PK)|name')
arrow(187,348,225,348,'1 : many'); arrow(407,348,370,348,'1 : many')
y=248
head('12. Storage schema')
para('UTF-8 file: campuskit.tsv. Header: CAMPUSKIT followed by a tab and version 1. E rows store id, encoded name and quantity; M rows store id and encoded name; L rows store id, equipmentId, memberId, borrowed, due and returned. All fields use tabs. Names use Base64; dates use ISO YYYY-MM-DD. An empty returned field marks an active loan.')
para('Load checks reject duplicate IDs, invalid dates, missing referenced records and active loans exceeding stock. No SQL engine is required. Data is not encrypted; local filesystem permissions govern access.')

start('Implementation and decisions')
head('13. Java concepts demonstrated')
para('<b>Encapsulation:</b> LendingService keeps state private and exposes copied snapshots.<br/><b>Abstraction and polymorphism:</b> Repository defines load/save; the file implementation and a test double share the contract.<br/><b>Records and collections:</b> immutable entities live in ordered LinkedHashMap collections.<br/><b>Exception handling:</b> validation uses IllegalArgumentException; persistence uses IOException; the console recovers from ordinary input errors.<br/><b>File handling:</b> NIO paths, temporary files, atomic moves and process locks.<br/><b>Date processing:</b> LocalDate, ChronoUnit and injectable Clock support deterministic due-date logic.')
head('14. Decisions and rationale')
para('Availability is derived from active loans instead of maintaining a separate mutable stock counter. A complete copied state is saved before being published in memory, preventing disk failures from leaving the session ahead of the saved file. Names are encoded to avoid delimiter ambiguity. CLI-only operation satisfies terminal execution requirements and avoids UI framework setup.')
head('15. Complexity and tradeoffs')
para('Memory and snapshot storage grow as O(E+M+L). A save and state copy are linear in the number of records. Borrow eligibility scans loans: O(L). Listing inventory is O(E*L) because each equipment type calculates availability from loans. These choices suit small registers but motivate indexed storage for larger deployments.')
head('16. Setup and execution')
para('Install JDK 17 or newer and make java and javac available on PATH. No third-party Java dependencies or configuration secrets are needed. From the repository root:<br/><font face="Courier" size="9">javac --release 17 -d build &quot;@sources.txt&quot;<br/>java -cp build campuskit.Main</font><br/>An optional final argument selects the data directory. See README.md for Windows and POSIX scripts, a first-session walkthrough and recovery guidance.')

start('Execution results and testing')
head('17. Observed terminal results')
para('The captured demo registered two Scientific Calculator units and one Demo Student, issued one seven-day loan, displayed availability, recorded its return and retained loan history. The following lines are from the actual terminal run:')
demo=(ROOT/'docs/demo-output.txt').read_text(encoding='utf-8-sig')
selected=[line for line in demo.splitlines() if 'Total units:' in line or 'Scientific Calculator |' in line or 'Return recorded.' in line]
for line in selected: para('<font face="Courier" size="9">'+escape(line)+'</font>')
head('18. Test method and outcome')
results=(ROOT/'docs/test-results.txt').read_text(encoding='utf-8-sig')
para('<b>'+escape(results.strip().splitlines()[-1])+'</b> Tests were executed on Windows with JDK 26, compiling against the Java 17 API target. A fixed UTC clock makes overdue tests reproducible; temporary directories isolate file tests. Assertions fail the test process on an unexpected result.')
para('Checks cover empty startup, invalid names and quantities, unknown IDs, duration limits, exhausted stock, due-date boundaries, overdue restrictions, returns, the three-loan cap, reload, Unicode persistence, snapshot isolation, failed-save rollback, console recovery, dangling references and corrupt-file preservation.')
para('Evidence: docs/test-results.txt and docs/demo-output.txt. Run:<br/><font face="Courier" size="9">javac --release 17 -d build &quot;@sources.txt&quot; tests/campuskit/ProjectTests.java<br/>java -cp build campuskit.ProjectTests</font>')
head('19. Validation limits')
para('The test suite is targeted rather than exhaustive. It does not simulate power loss or benchmark large datasets. Atomic replacement is filesystem-dependent. A GitHub Actions workflow is included to compile and test on Ubuntu with Java 17; its actual remote result must be inspected after publication.')

start('Reflection and references')
head('20. Engineering challenges addressed')
para('Stock consistency was addressed by calculating availability from loan records. Failed saves were addressed by copying state and publishing it only after persistence succeeds. Date-dependent testing was addressed with Clock injection. Corrupt input files trigger a startup error instead of silent recovery that could discard records. These describe engineering concerns handled by the implementation, not a claim about the student\'s personal development history.')
head('21. Learning topics and key takeaways')
para('Review how interface-based design enables failure injection, why immutable records simplify copying, how derived state avoids duplicated counters, and how exception boundaries separate user errors from storage failures. Before evaluation, the student should be able to explain the borrow/return flow, demonstrate tests and discuss the tradeoffs of flat-file persistence.')
head('22. Future enhancements')
para('Add serial-number tracking, reservations, controlled editing, CSV export and indexed database storage. A multi-user version would require authentication, authorization, transactional persistence and conflict handling. Add platform-specific tests and fault-injection checks before wider deployment.')
head('23. References')
para('1. VITyarthi, Build Your Own Project: General Project Instructions &amp; Submission Guidelines (user-provided PDF).<br/>2. Java SE 17 API documentation: https://docs.oracle.com/en/java/javase/17/docs/api/<br/>3. Java language specification, Java SE 17: https://docs.oracle.com/javase/specs/jls/se17/html/<br/>4. Project source, README.md, statement.md, docs/design.md and test evidence in this repository.')
head('24. Submission checklist')
para('Use a public GitHub repository containing the root README, statement, source files, tests and design documentation. Submit the repository root URL rather than a tree/blob link. Upload this PDF separately through the course portal. Review the course policy on AI assistance and complete any required personal review before submission.')
c.save()
print(OUT)
