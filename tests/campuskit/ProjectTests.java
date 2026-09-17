package campuskit;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public final class ProjectTests {
    private static int passed;
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-18T12:00:00Z"), ZoneOffset.UTC);
    interface Action { void run() throws Exception; }
    static void check(boolean value, String label) {
        if (!value) throw new AssertionError(label);
        passed++; System.out.println("PASS " + label);
    }
    static void rejects(Action action, String label) throws Exception {
        try { action.run(); } catch (IllegalArgumentException | IOException ex) { check(true, label); return; }
        throw new AssertionError("Expected rejection: " + label);
    }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("campuskit-test-");
        try {
            FileRepository repository = new FileRepository(dir);
            LendingService s = new LendingService(repository, CLOCK);
            check(s.snapshot().loans.isEmpty(), "new repository is empty");
            rejects(() -> s.addEquipment("Camera", 0), "zero stock rejected");
            rejects(() -> s.addMember(" "), "blank member rejected");
            rejects(() -> s.addMember("A\tB"), "control characters rejected");
            int e = s.addEquipment("Camera \u03b1", 1), m = s.addMember("Test Student");
            rejects(() -> s.borrow(e, 999, 2), "unknown member rejected");
            rejects(() -> s.borrow(999, m, 2), "unknown equipment rejected");
            rejects(() -> s.borrow(e, m, 0), "zero duration rejected");
            rejects(() -> s.borrow(e, m, 31), "excess duration rejected");
            int loan = s.borrow(e, m, 1);
            check(s.available(e) == 0, "borrow reduces available stock");
            rejects(() -> s.borrow(e, m, 1), "stock exhaustion rejected");
            check(!s.snapshot().loans.get(loan).overdue(LocalDate.of(2026, 9, 19)), "due date is not overdue");
            LendingService later = new LendingService(repository, Clock.offset(CLOCK, Duration.ofDays(2)));
            check(Reports.summary(later.snapshot(), later.today()).contains("Overdue: 1"), "overdue report uses injected clock");
            int spare = later.addEquipment("Tripod", 5);
            rejects(() -> later.borrow(spare, m, 2), "overdue member cannot borrow");
            later.returnLoan(loan);
            check(later.available(e) == 1, "return restores stock");
            rejects(() -> later.returnLoan(loan), "duplicate return rejected");
            later.borrow(spare, m, 1); later.borrow(spare, m, 1); later.borrow(spare, m, 1);
            rejects(() -> later.borrow(spare, m, 1), "three-loan limit enforced");
            LendingService reopened = new LendingService(repository, CLOCK);
            check(reopened.snapshot().loans.size() == 4, "history survives reload");
            check(reopened.snapshot().equipment.get(e).name().equals("Camera \u03b1"), "Unicode round trip");
            State copy = reopened.snapshot(); copy.members.clear();
            check(reopened.snapshot().members.size() == 1, "snapshot is isolated");
            Repository failing = new Repository() {
                public State load() { return new State(); }
                public void save(State state) throws IOException { throw new IOException("Simulated disk failure"); }
            };
            LendingService unsafeDisk = new LendingService(failing, CLOCK);
            rejects(() -> unsafeDisk.addMember("A"), "save failure reported");
            check(unsafeDisk.snapshot().members.isEmpty(), "failed save does not mutate live state");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            new ConsoleApp(reopened, new Scanner("wrong\n1\nLens\nabc\n8\n0\n"), new PrintStream(output)).run();
            check(output.toString().contains("Input error:") && output.toString().contains("CampusKit report"), "CLI recovers from invalid input");
            Files.writeString(dir.resolve("campuskit.tsv"), "CAMPUSKIT\t1\nL\t1\t99\t99\t2026-09-18\t2026-09-19\t\n");
            rejects(repository::load, "dangling loan rejected");
            Files.writeString(dir.resolve("campuskit.tsv"), "broken");
            rejects(repository::load, "corrupt snapshot rejected");
            check(Files.readString(dir.resolve("campuskit.tsv")).equals("broken"), "corrupt file preserved");
            System.out.println("All " + passed + " checks passed.");
        } finally {
            try (var paths = Files.walk(dir)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(path);
            }
        }
    }
}
