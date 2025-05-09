package SplitterApp;

//It manages the list of roommate and the total amount of money for each group 

import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private List<Roommate> roommates; // List of roommate
    private List<Expense> expenses; // List of how much money everyone owes
    private int nextRoommateId; // Counting how many roommate
    private int nextExpenseId; // Counting 

    
    public ExpenseManager() {
        this.roommates = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.nextRoommateId = 1;
        this.nextExpenseId = 1;
    }

    // Add roommate name in the list 
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

    // Get the list of expense
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    // Get the list of roommates
    public List<Roommate> getRoommates() {
        return new ArrayList<>(roommates);
    }

    // Calculate how much each roommate need to pay the payer
    public List<Split> calculateSplits() {
        return SplitCalculator.calculateSplits(roommates, expenses);
    }

    // Search roommate name 
    public Roommate findRoommateByName(String name) {
        return roommates.stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}