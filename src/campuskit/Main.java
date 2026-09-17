package campuskit;

import java.nio.file.*;
import java.nio.channels.*;
import java.time.Clock;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        if (args.length > 1 || (args.length == 1 && args[0].equals("--help"))) {
            System.out.println("Usage: java -cp build campuskit.Main [data-directory]"); return;
        }
        Path directory = Path.of(args.length == 0 ? "data" : args[0]);
        try {
            Files.createDirectories(directory);
            try (FileChannel channel = FileChannel.open(directory.resolve(".lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock lock = channel.tryLock()) {
                if (lock == null) throw new java.io.IOException("Data directory is in use by another CampusKit process.");
                LendingService service = new LendingService(new FileRepository(directory), Clock.systemDefaultZone());
                new ConsoleApp(service, new Scanner(System.in), System.out).run();
            }
        } catch (Exception ex) {
            System.err.println("Cannot start CampusKit: " + ex.getMessage()); System.exit(1);
        }
    }
}
