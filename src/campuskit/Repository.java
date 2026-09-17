package campuskit;

import java.io.IOException;

public interface Repository {
    State load() throws IOException;
    void save(State state) throws IOException;
}
