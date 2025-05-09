package SplitterApp;
//Keep track of the roommate it's bill

public class Split {
    private Roommate roommate;
    private double balance;

    //Constructor 
    public Split(Roommate roommate, double balance) {
        this.roommate = roommate;
        this.balance = balance;
    }

    // Get roommate name
    public Roommate getRoommate() {
        return roommate;
    }

    // Get the total amount of money the roommate owes or owed
    public double getBalance() {
        return balance;
    }

    // Check if this roommate need to pay or need to demand money from other roommate
    @Override
    public String toString() {
        if (balance > 0) {
            return roommate.getName() + " is owed $" + String.format("%.2f", balance);
        } else if (balance < 0) {
            return roommate.getName() + " owes $" + String.format("%.2f", -balance);
        } else {
            return roommate.getName() + " is settled.";
        }
    }
}