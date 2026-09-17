package campuskit;

public record Equipment(int id, String name, int quantity) {
    public Equipment {
        if (id < 1 || name == null || name.isBlank() || quantity < 1)
            throw new IllegalArgumentException("Equipment requires a positive ID, name and quantity.");
    }
}
