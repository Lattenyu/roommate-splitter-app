package SplitterApp;

import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private List<Roommate> roommates;
    private List<Expense> expenses;
    private int nextRoommateId;
    private int nextExpenseId;

    public ExpenseManager() {
        this.roommates = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.nextRoommateId = 1;
        this.nextExpenseId = 1;
    }

    public Roommate addRoommate(String name) {
        Roommate roommate = new Roommate(nextRoommateId++, name);
        roommates.add(roommate);
        return roommate;
    }

    public Expense addExpense(String description, double amount, Roommate payer) {
        if (!roommates.contains(payer)) {
            throw new IllegalArgumentException("Payer must be a registered roommate");
        }
        Expense expense = new Expense(nextExpenseId++, description, amount, payer);
        expenses.add(expense);
        return expense;
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Roommate> getRoommates() {
        return new ArrayList<>(roommates);
    }

    public List<Split> calculateSplits() {
        return SplitCalculator.calculateSplits(roommates, expenses);
    }

    public Roommate findRoommateByName(String name) {
        return roommates.stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}