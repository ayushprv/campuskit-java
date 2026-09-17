package campuskit;

import java.io.*;
import java.util.Scanner;

public final class ConsoleApp {
    private final LendingService service;
    private final Scanner input;
    private final PrintStream out;
    public ConsoleApp(LendingService service, Scanner input, PrintStream out) {
        this.service = service; this.input = input; this.out = out;
    }
    private String ask(String prompt) { out.print(prompt + ": "); return input.nextLine(); }
    private int number(String prompt) { return Integer.parseInt(ask(prompt).strip()); }
    public void run() {
        out.println("CampusKit | Equipment Lending Manager");
        while (true) {
            out.println("\n1 Add equipment   2 List inventory   3 Register member   4 List members");
            out.println("5 Borrow          6 Return           7 Loan history      8 Report   0 Exit");
            if (!input.hasNextLine()) return;
            try {
                switch (ask("Choice").strip()) {
                    case "0" -> { out.println("Goodbye. All successful changes are saved."); return; }
                    case "1" -> out.println("Equipment ID: " + service.addEquipment(ask("Name"), number("Quantity")));
                    case "2" -> {
                        out.println("ID | Name | Available / Total");
                        for (Equipment e : service.snapshot().equipment.values())
                            out.println(e.id() + " | " + e.name() + " | " + service.available(e.id()) + " / " + e.quantity());
                    }
                    case "3" -> out.println("Member ID: " + service.addMember(ask("Member name")));
                    case "4" -> service.snapshot().members.values().forEach(m -> out.println(m.id() + " | " + m.name()));
                    case "5" -> out.println("Loan ID: " + service.borrow(number("Equipment ID"), number("Member ID"), number("Days (1-30)")));
                    case "6" -> { service.returnLoan(number("Loan ID")); out.println("Return recorded."); }
                    case "7" -> {
                        out.println("Loan | Equipment | Member | Borrowed | Due | Returned");
                        service.snapshot().loans.values().forEach(l -> out.println(l.id() + " | " + l.equipmentId() + " | " + l.memberId()
                            + " | " + l.borrowed() + " | " + l.due() + " | " + (l.active() ? "ACTIVE" : l.returned())));
                    }
                    case "8" -> out.print(Reports.summary(service.snapshot(), service.today()));
                    default -> out.println("Choose a menu number from 0 to 8.");
                }
            } catch (IllegalArgumentException ex) { out.println("Input error: " + ex.getMessage()); }
              catch (IOException ex) { out.println("Save failed; operation not applied: " + ex.getMessage()); }
              catch (java.util.NoSuchElementException ex) { out.println("Input ended; incomplete operation cancelled."); return; }
        }
    }
}
