package campuskit;

import java.io.IOException;
import java.time.*;
import java.util.*;

public final class LendingService {
    private final Repository repository;
    private final Clock clock;
    private State state;
    public LendingService(Repository repository, Clock clock) throws IOException {
        this.repository = repository; this.clock = clock; state = repository.load();
    }
    private void commit(State next) throws IOException { repository.save(next); state = next; }
    public State snapshot() { return state.copy(); }
    public LocalDate today() { return LocalDate.now(clock); }
    private String name(String value) {
        if (value == null || value.isBlank() || value.strip().length() > 100 || value.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Name must contain 1-100 characters without control characters.");
        return value.strip();
    }
    public int addEquipment(String name, int quantity) throws IOException {
        State next = state.copy(); int id = State.nextId(next.equipment);
        next.equipment.put(id, new Equipment(id, name(name), quantity)); commit(next); return id;
    }
    public int addMember(String name) throws IOException {
        State next = state.copy(); int id = State.nextId(next.members);
        next.members.put(id, new Member(id, name(name))); commit(next); return id;
    }
    public int available(int equipmentId) {
        Equipment e = state.equipment.get(equipmentId);
        if (e == null) throw new IllegalArgumentException("Equipment ID not found.");
        return e.quantity() - (int) state.loans.values().stream().filter(l -> l.active() && l.equipmentId() == equipmentId).count();
    }
    public int borrow(int equipmentId, int memberId, int days) throws IOException {
        if (days < 1 || days > 30) throw new IllegalArgumentException("Loan duration must be 1-30 days.");
        if (!state.members.containsKey(memberId)) throw new IllegalArgumentException("Member ID not found.");
        if (available(equipmentId) == 0) throw new IllegalArgumentException("No stock available.");
        long active = state.loans.values().stream().filter(l -> l.active() && l.memberId() == memberId).count();
        if (active >= 3) throw new IllegalArgumentException("Member already has three active loans.");
        if (state.loans.values().stream().anyMatch(l -> l.memberId() == memberId && l.overdue(today())))
            throw new IllegalArgumentException("Return overdue equipment before borrowing.");
        State next = state.copy(); int id = State.nextId(next.loans);
        next.loans.put(id, new Loan(id, equipmentId, memberId, today(), today().plusDays(days), null));
        commit(next); return id;
    }
    public void returnLoan(int id) throws IOException {
        Loan l = state.loans.get(id);
        if (l == null || !l.active()) throw new IllegalArgumentException("Active loan ID not found.");
        State next = state.copy();
        next.loans.put(id, new Loan(id, l.equipmentId(), l.memberId(), l.borrowed(), l.due(), today())); commit(next);
    }
}
