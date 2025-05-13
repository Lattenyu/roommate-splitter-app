package SplitterApp;

import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private List<Roommate> roommates; // List of roommates
    private List<Expense> expenses; // List of expenses
    private int nextRoommateId; // Counter for roommate IDs
    private int nextExpenseId; // Counter for expense IDs

    public ExpenseManager() {
        this.roommates = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.nextRoommateId = 1;
        this.nextExpenseId = 1;
    }

    // Add roommate name to the list 
    public Roommate addRoommate(String name) {
        Roommate roommate = new Roommate(nextRoommateId++, name);
        roommates.add(roommate);
        return roommate;
    }

    // Keep track of the total amount of each bill
    public Expense addExpense(String description, double amount, Roommate payer) {
        if (!roommates.contains(payer)) {
            throw new IllegalArgumentException("Payer must be a registered roommate");
        }
        Expense expense = new Expense(nextExpenseId++, description, amount, payer);
        expenses.add(expense);
        return expense;
    }

    // Get the list of expenses
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    // Get the list of roommates
    public List<Roommate> getRoommates() {
        return new ArrayList<>(roommates);
    }

    // Calculate how much each roommate needs to pay the payer
    public List<Split> calculateSplits() {
        return SplitCalculator.calculateSplits(roommates, expenses);
    }

    // Search roommate by name 
    public Roommate findRoommateByName(String name) {
        return roommates.stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Set the next expense ID (used when loading expenses to avoid ID conflicts)
    public void setNextExpenseId(int id) {
        if (id >= nextExpenseId) {
            nextExpenseId = id + 1;
        }
    }
}