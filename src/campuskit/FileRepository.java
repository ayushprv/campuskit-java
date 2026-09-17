package campuskit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/** Versioned snapshot; names are Base64 encoded so tabs and Unicode round-trip safely. */
public final class FileRepository implements Repository {
    private final Path file;
    public FileRepository(Path directory) { file = directory.resolve("campuskit.tsv"); }
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
    private static <T> void put(Map<Integer, T> map, int id, T value) {
        if (map.putIfAbsent(id, value) != null) throw new IllegalArgumentException("Duplicate ID");
    }
    @Override public State load() throws IOException {
        State state = new State();
        if (!Files.exists(file)) return state;
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).equals("CAMPUSKIT\t1"))
            throw new IOException("Unsupported or empty data file: " + file);
        int line = 1;
        try {
            for (; line < lines.size(); line++) {
                String[] p = lines.get(line).split("\t", -1);
                int id = Integer.parseInt(p[1]);
                switch (p[0]) {
                    case "E" -> {
                        if (p.length != 4) throw new IllegalArgumentException("Equipment columns");
                        put(state.equipment, id, new Equipment(id, decode(p[2]), Integer.parseInt(p[3])));
                    }
                    case "M" -> {
                        if (p.length != 3) throw new IllegalArgumentException("Member columns");
                        put(state.members, id, new Member(id, decode(p[2])));
                    }
                    case "L" -> {
                        if (p.length != 7) throw new IllegalArgumentException("Loan columns");
                        put(state.loans, id, new Loan(id, Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                            LocalDate.parse(p[4]), LocalDate.parse(p[5]), p[6].isEmpty() ? null : LocalDate.parse(p[6])));
                    }
                    default -> throw new IllegalArgumentException("Unknown record type");
                }
            }
            for (Loan loan : state.loans.values())
                if (!state.equipment.containsKey(loan.equipmentId()) || !state.members.containsKey(loan.memberId()))
                    throw new IllegalArgumentException("Loan references missing member or equipment");
            for (Equipment item : state.equipment.values())
                if (state.loans.values().stream().filter(l -> l.active() && l.equipmentId() == item.id()).count() > item.quantity())
                    throw new IllegalArgumentException("Active loans exceed stock");
        } catch (RuntimeException ex) {
            throw new IOException("Invalid data near line " + (line + 1) + ": " + ex.getMessage(), ex);
        }
        return state;
    }
    @Override public void save(State state) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        StringBuilder out = new StringBuilder("CAMPUSKIT\t1\n");
        for (Equipment e : state.equipment.values())
            out.append("E\t").append(e.id()).append('\t').append(encode(e.name())).append('\t').append(e.quantity()).append('\n');
        for (Member m : state.members.values())
            out.append("M\t").append(m.id()).append('\t').append(encode(m.name())).append('\n');
        for (Loan l : state.loans.values())
            out.append("L\t").append(l.id()).append('\t').append(l.equipmentId()).append('\t').append(l.memberId())
               .append('\t').append(l.borrowed()).append('\t').append(l.due()).append('\t')
               .append(l.returned() == null ? "" : l.returned()).append('\n');
        Path temp = Files.createTempFile(file.toAbsolutePath().getParent(), "snapshot-", ".tmp");
        try {
            Files.writeString(temp, out, StandardCharsets.UTF_8);
            Files.move(temp, file.toAbsolutePath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally { Files.deleteIfExists(temp); }
    }
}
