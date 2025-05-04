package SplitterAppGui;

import java.util.HashMap;
import java.util.Map;

public class Bill {
    private String id; // Unique bill ID
    private String name; // Bill name (e.g., "Electricity")
    private double totalAmount; // Total bill amount
    private String creator; // User who created the bill
    private Map<String, Double> splits; // User email -> amount owed

    public Bill(String id, String name, double totalAmount, String creator) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount;
        this.creator = creator;
        this.splits = new HashMap<>();
    }

    public void addSplit(String user, double amount) {
        splits.put(user, amount);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getCreator() {
        return creator;
    }

    public Map<String, Double> getSplits() {
        return splits;
    }
}