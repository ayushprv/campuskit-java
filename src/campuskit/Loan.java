package campuskit;

import java.time.LocalDate;

public record Loan(int id, int equipmentId, int memberId, LocalDate borrowed,
                   LocalDate due, LocalDate returned) {
    public Loan {
        if (id < 1 || equipmentId < 1 || memberId < 1 || borrowed == null || due == null
                || due.isBefore(borrowed) || (returned != null && returned.isBefore(borrowed)))
            throw new IllegalArgumentException("Invalid loan dates or identifiers.");
    }
    public boolean active() { return returned == null; }
    public boolean overdue(LocalDate today) { return active() && due.isBefore(today); }
}
