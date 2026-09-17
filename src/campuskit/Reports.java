package campuskit;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Reports {
    private Reports() { }
    public static String summary(State state, LocalDate today) {
        long active = state.loans.values().stream().filter(Loan::active).count();
        long overdue = state.loans.values().stream().filter(l -> l.overdue(today)).count();
        int total = state.equipment.values().stream().mapToInt(Equipment::quantity).sum();
        StringBuilder out = new StringBuilder("CampusKit report - " + today + "\n")
            .append("Members: ").append(state.members.size()).append(" | Equipment types: ").append(state.equipment.size())
            .append("\nTotal units: ").append(total).append(" | Available: ").append(total - active)
            .append(" | Active loans: ").append(active).append(" | Overdue: ").append(overdue).append('\n');
        for (Loan l : state.loans.values()) if (l.overdue(today))
            out.append("OVERDUE loan #").append(l.id()).append(" | ").append(state.members.get(l.memberId()).name())
               .append(" | ").append(state.equipment.get(l.equipmentId()).name()).append(" | ")
               .append(ChronoUnit.DAYS.between(l.due(), today)).append(" day(s) late\n");
        return out.toString();
    }
}
