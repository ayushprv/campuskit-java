package campuskit;

import java.util.*;

public final class State {
    final Map<Integer, Equipment> equipment = new LinkedHashMap<>();
    final Map<Integer, Member> members = new LinkedHashMap<>();
    final Map<Integer, Loan> loans = new LinkedHashMap<>();
    public State copy() {
        State copy = new State();
        copy.equipment.putAll(equipment);
        copy.members.putAll(members);
        copy.loans.putAll(loans);
        return copy;
    }
    static int nextId(Map<Integer, ?> map) {
        return Math.addExact(map.keySet().stream().mapToInt(Integer::intValue).max().orElse(0), 1);
    }
}
