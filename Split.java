package SplitterApp;

public class Split {
    private Roommate roommate;
    private double balance;

    public Split(Roommate roommate, double balance) {
        this.roommate = roommate;
        this.balance = balance;
    }

    public Roommate getRoommate() {
        return roommate;
    }

    public double getBalance() {
        return balance;
    }

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