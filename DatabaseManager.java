package SplitterApp;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String GROUPS_FILE = "groups.txt";
    private static final String EXPENSES_FILE = "expenses.txt";

    // Save groups and their members to groups.txt
    public void saveGroups(List<Group> groups) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GROUPS_FILE))) {
            for (Group group : groups) {
                StringBuilder members = new StringBuilder();
                for (String member : group.getMembers()) {
                    if (members.length() > 0) members.append(",");
                    members.append(member);
                }
                writer.write(String.format("%s|%s", group.getName(), members));
                writer.newLine();
            }
        }
    }

    // Load groups and their members from groups.txt
    public List<Group> loadGroups() throws IOException {
        List<Group> groups = new ArrayList<>();
        File file = new File(GROUPS_FILE);
        if (!file.exists()) {
            return groups; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String groupName = parts[0];
                    String[] members = parts[1].split(",");
                    List<String> memberList = new ArrayList<>();
                    for (String member : members) {
                        if (!member.trim().isEmpty()) {
                            memberList.add(member.trim());
                        }
                    }
                    groups.add(new Group(groupName, memberList));
                }
            }
        }
        return groups;
    }

    // Save expenses for a specific group to expenses.txt
    public void saveExpenses(Group group) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSES_FILE, true))) {
            for (Expense expense : group.getExpenseManager().getExpenses()) {
                writer.write(String.format("%s|%d|%s|%.2f|%s|%s",
                        group.getName(),
                        expense.getId(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getPayer().getName(),
                        expense.getDate().toString()));
                writer.newLine();
            }
        }
    }

    // Load expenses for a specific group from expenses.txt
    public void loadExpenses(List<Group> groups) throws IOException {
        File file = new File(EXPENSES_FILE);
        if (!file.exists()) {
            return; // No expenses to load
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    String groupName = parts[0];
                    int expenseId = Integer.parseInt(parts[1]);
                    String description = parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    String payerName = parts[4];
                    LocalDate date = LocalDate.parse(parts[5]);

                    // Find the group
                    Group targetGroup = groups.stream()
                            .filter(g -> g.getName().equals(groupName))
                            .findFirst()
                            .orElse(null);

                    if (targetGroup != null) {
                        Roommate payer = targetGroup.getExpenseManager().findRoommateByName(payerName);
                        if (payer != null) {
                            // Update nextExpenseId to avoid ID conflicts
                            targetGroup.getExpenseManager().setNextExpenseId(expenseId);
                            targetGroup.getExpenseManager().addExpense(description, amount, payer);
                        }
                    }
                }
            }
        }
    }
}