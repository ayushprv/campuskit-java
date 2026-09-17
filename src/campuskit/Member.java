package campuskit;

public record Member(int id, String name) {
    public Member {
        if (id < 1 || name == null || name.isBlank())
            throw new IllegalArgumentException("Member requires a positive ID and name.");
    }
}
