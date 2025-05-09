package SplitterApp;
// Keeps track of the expense such as who payed and what is the total amount. 
import java.time.LocalDate;

public class Expense {
    private int id; 
    private String description;
    private double amount;
    private Roommate payer;
    private LocalDate date;

    public Expense(int id, String description, double amount, Roommate payer) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.date = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public Roommate getPayer() {
        return payer;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", payer=" + payer.getName() +
                ", date=" + date +
                '}';
    }
}